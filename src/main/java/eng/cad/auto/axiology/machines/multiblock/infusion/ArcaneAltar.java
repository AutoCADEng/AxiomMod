package eng.cad.auto.axiology.machines.multiblock.infusion;

import eng.cad.auto.axiology.AxiomMod;
import eng.cad.auto.axiology.core.DataSlotHandler;
import eng.cad.auto.axiology.core.ExtendedItemStackHandler;
import eng.cad.auto.axiology.core.ISlotData;
import eng.cad.auto.axiology.core.ISlotItem;
import eng.cad.auto.axiology.core.blocks.BaseMachineBlock;
import eng.cad.auto.axiology.core.machine.BaseMachine;
import eng.cad.auto.axiology.core.machine.BaseMaster;
import eng.cad.auto.axiology.core.machine.component.*;
import eng.cad.auto.axiology.core.network.AxiologyNetwork;
import eng.cad.auto.axiology.core.network.packet.InfusionSyncPacket;
import eng.cad.auto.axiology.core.network.packet.KillBeamPacket;
import eng.cad.auto.axiology.core.slot.SlotConfig;
import eng.cad.auto.axiology.core.slot.SlotIORestriction;
import eng.cad.auto.axiology.core.slot.UniversalSlot;
import eng.cad.auto.axiology.crafting.core.CustomCraftingContainer;
import eng.cad.auto.axiology.crafting.machines.InfusionRecipe;
import eng.cad.auto.axiology.magic.rebounds.Rebounds;
import eng.cad.auto.axiology.magic.rebounds.core.AbstractRebound;
import eng.cad.auto.axiology.magic.rebounds.core.InfusionContext;
import eng.cad.auto.axiology.modulartools.components.IModularTool;
import eng.cad.auto.axiology.registry.RecipeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.*;

public class ArcaneAltar extends BaseMaster<ArcaneAltar> implements ISlotItem, ISlotData, IMachineProgress, ITickable, IRightClickable, IRecipeMachine<InfusionRecipe> {
    public static String name = "arcane_altar";
    public static int MAX_PEDESTAL_RADIUS = 5;
    int[] lastSyncData;
    long lastSyncTime;
    HashMap<BlockPos, UUID> beamUUIDs = new HashMap<>();

    public ArcaneAltar(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        this.data = new DataSlotHandler("progress", "recipeCompleted", "isActive");
        this.items = new ExtendedItemStackHandler(new SlotConfig[]{
                new SlotConfig(SlotIORestriction.IN_OUT, 1)
        }
        );
        refreshCapabilities();
    }

    public static HashSet<BlockPos> findPedestals(Level level, BlockPos center, int radius) {
        HashSet<BlockPos> pedestals = new HashSet<>();
        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-radius, -radius, -radius),
                center.offset(radius, radius, radius))) {
            if (!level.isLoaded(pos)) continue;
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ArcanePedestal) pedestals.add(pos.immutable());
        }
        return pedestals;
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) return;
        if (!(level.getBlockEntity(pos) instanceof ArcaneAltar altar)) return;
        if (level.getGameTime() % 20 == 0) validateStructure(pos, level);

        if (getDataSlots().get("isActive") == 0) {
            for (BlockPos beamPos : new ArrayList<>(beamUUIDs.keySet())) {
                removePedestalBeam(beamPos, getBlockPos(), level);
            }
            resetProgress(this);
            resetPedestals();
            return;
        }

        if (checkPedestalCompletion(level)) {
            CustomCraftingContainer infusionContainers = new CustomCraftingContainer(this).addSlot(getItemSlots().getStackInSlot(0));
            for (BaseMachine<? extends IMultiblockFollower> follower : getFollowers(level)) {
                if (follower instanceof ArcanePedestal pedestal) {
                    infusionContainers.addSlot(pedestal.getItemSlots().getStackInSlot(0));
                }
                // TODO add a fluid equivalent if/when we make a tank variant of ArcanePedestal
            }
            InfusionRecipe activeRecipe = findRecipe(this, level, infusionContainers);
            if (activeRecipe != null) {
                if (activeRecipe.getCraftingOutput().get(0).is(UniversalSlot.Kind.MODULE)) performModuleRitual(activeRecipe, this, level);
            }
            getDataSlots().set("isActive", 0);
            return;
        }

        incrementProgress(this, 1);

        for (BaseMachine<? extends IMultiblockFollower> pedestalMachine : getFollowers(level)) {
            if (!(pedestalMachine instanceof ArcanePedestal pedestal)) continue;
            BlockPos pedestalPos = pedestal.getBlockPos();

            if (progressCompleted(this) && pedestal.getDataSlots().get("isItemHidden") == 0 && !pedestal.getItemSlots().getStackInSlot(0).isEmpty()) {
                resetProgress(this);
                pedestal.getDataSlots().set("isItemHidden", 1);
                level.playSound(
                        null,
                        getBlockPos(),
                        SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("axiology", "infuser_consume")),
                        SoundSource.BLOCKS,
                        0.1F,
                        1.1F + level.getRandom().nextFloat() * 0.2f
                );
                pedestal.setChanged();
                level.sendBlockUpdated(pedestalPos, pedestal.getBlockState(), pedestal.getBlockState(), 3);
            }

            if (pedestal.getItemSlots().getStackInSlot(0).isEmpty() || pedestal.getDataSlots().get("isItemHidden") == 1) {
                removePedestalBeam(pedestalPos, pos, level);
            } else if (!beamUUIDs.containsKey(pedestalPos)) {
                addPedestalBeam(pedestalPos, pos, level);
            }
        }
    }

    private void performModuleRitual(InfusionRecipe activeRecipe, ArcaneAltar entity, Level level) {
        var followers = getFollowers(level);
        if (activeRecipe != null && activeRecipe.validate(entity, followers, activeRecipe)) {
            ArrayList<BaseMachine<? extends IMultiblockFollower>> followerList = new ArrayList<>(followers);
            boolean failed = false;
            ItemStack copyOfItem = entity.getItemSlots().getStackInSlot(0).copy();
            activeRecipe.consumeIngredients(entity, followerList, activeRecipe);
            if (activeRecipe.getCraftingOutput().get(0).is(UniversalSlot.Kind.MODULE)) {
                if (copyOfItem.getItem() instanceof IModularTool moddableTool) {
                    if (!IModularTool.addModule(copyOfItem, activeRecipe.getCraftingOutput().get(0).getModule().getModule(), activeRecipe.getCraftingOutput().get(0).getModule().getSoftCap())) {
                        failed = true;
                        applyRebound(entity);
                    }
                }
            }

            if (!failed) {

                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.playSound(
                            null,
                            entity.getBlockPos(),
                            SoundEvents.GENERIC_EXPLODE,
                            SoundSource.BLOCKS,
                            0.5F,
                            0.9F + level.getRandom().nextFloat() * 0.2f
                    );
                    serverLevel.sendParticles(ParticleTypes.EXPLOSION, getBlockPos().getX() + 0.5f, getBlockPos().getY() + 1.0f, getBlockPos().getZ() + 0.5f, 1, 0, 0, 0, 0);
                    serverLevel.sendParticles(ParticleTypes.POOF, getBlockPos().getX() + 0.5f, getBlockPos().getY() + 1.0f, getBlockPos().getZ() + 0.5f, 20, 0.3, 0.3, 0.3, 0.05);
                }

            }
            entity.getItemSlots().setStackInSlot(0, copyOfItem);

            for (BaseMachine<? extends IMultiblockFollower> follower : followers) {
                if (!(follower instanceof ArcanePedestal pedestal)) continue;
                pedestal.setChanged();
                level.sendBlockUpdated(pedestal.getBlockPos(), pedestal.getBlockState(), pedestal.getBlockState(), 3);
            }
            entity.syncWithClients(level, entity.getBlockPos());
        } else {
            applyRebound(entity);

            for (BaseMachine<? extends IMultiblockFollower> follower : followers) {
                if (!(follower instanceof ArcanePedestal pedestal)) continue;
                pedestal.getItemSlots().setStackInSlot(0, ItemStack.EMPTY);
                pedestal.getDataSlots().set("isItemHidden", 0);
                pedestal.setChanged();
                level.sendBlockUpdated(pedestal.getBlockPos(), pedestal.getBlockState(), pedestal.getBlockState(), 3);
            }
        }

        entity.getDataSlots().set("isActive", 0);
    }

    public boolean checkPedestalCompletion(Level level) {
        if (getFollowerPositions().isEmpty()) return false;
        var pedestals = getFollowers(level).stream()
                .filter(follower -> level.getBlockEntity(follower.getBlockPos()) instanceof ArcanePedestal)
                .map(follower -> (ArcanePedestal) follower)
                .filter(obj -> true)
                .filter(machine -> !machine.getItemSlots().getStackInSlot(0).isEmpty())
                .toList();
        return !pedestals.isEmpty() && pedestals.stream().allMatch(machine -> machine.getDataSlots().get("isItemHidden") == 1);
    }

    public void addPedestalBeam(BlockPos followerPos, BlockPos pos, Level level) {
        if (!(level.getBlockEntity(followerPos) instanceof ArcanePedestal machine)) return;
        UUID id = UUID.randomUUID();
        beamUUIDs.put(followerPos, id);
        AxiologyNetwork.INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(pos.getX(), pos.getY(), pos.getZ(), 128, level.dimension())),
                new InfusionSyncPacket(id,
                        ForgeRegistries.ITEMS.getKey(machine.getItemSlots().getStackInSlot(0).getItem()),
                        0,
                        pos.getCenter().toVector3f(),
                        followerPos.getCenter().toVector3f(),
                        level));
    }

    public boolean removePedestalBeam(BlockPos followerPos, BlockPos pos, Level level) {
        if (!beamUUIDs.containsKey(followerPos)) return false;
        AxiologyNetwork.INSTANCE.send(PacketDistributor.NEAR.with(() ->
                        new PacketDistributor.TargetPoint(pos.getX(), pos.getY(), pos.getZ(), 128, level.dimension())),
                new KillBeamPacket(beamUUIDs.get(followerPos)));
        beamUUIDs.remove(followerPos);
        return true;
    }

    @Override
    public void validateStructure(BlockPos masterPos, Level level) {
        clearFollowers(level);
        getFollowerPositions().addAll(findPedestals(level, masterPos, MAX_PEDESTAL_RADIUS));
    }

    public void applyRebound(ArcaneAltar entity) {
        AbstractRebound effect = Rebounds.randomEffect();
        AxiomMod.LOGGER.warn("FAILED A RECIPE FOR INFUSER applying {}", effect.toString());
        for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, new AABB(entity.getBlockPos()).inflate(MAX_PEDESTAL_RADIUS * 2))) {
            effect.apply(new InfusionContext(level, entity.getBlockPos(), player));
        }
        level.playSound(
                null,
                entity.getBlockPos(),
                SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("axiology", "infuser_fail")),
                SoundSource.BLOCKS,
                0.5F,
                0.9F
        );
    }

    public void resetPedestals() {
        for (BaseMachine<? extends IMultiblockFollower> follower : getFollowers(level)) {
            if (follower instanceof ArcanePedestal pedestal) {
                if (level == null) continue;
                pedestal.getDataSlots().set("isItemHidden", 0);
                pedestal.syncWithClients(level, pedestal.getBlockPos());
                level.sendBlockUpdated(pedestal.getBlockPos(), pedestal.getBlockState(), pedestal.getBlockState(), 3);
            }
        }
    }

    @Override
    public void updateStructureValidity(boolean validity) {
    }

    @Override
    public boolean isStructureValid() {
        return true;
    }

    @Override
    public DataSlotHandler getDataSlots() {
        return this.data;
    }

    @Override
    public int[] getLastSyncedData() {
        return lastSyncData;
    }

    @Override
    public void setLastSyncedData(int[] data) {
        this.lastSyncData = data;
    }

    @Override
    public long getLastSyncTime() {
        return lastSyncTime;
    }

    @Override
    public void setLastSyncTime(long time) {
        lastSyncTime = time;
    }

    @Override
    public ExtendedItemStackHandler getItemSlots() {
        return this.items;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ArcaneAltar getInstance() {
        return this;
    }

    @Override
    public int progressSlot() {
        return getDataSlots().indexOf("progress");
    }

    @Override
    public int maxProgress() {
        return (24000 / 60) / 16;
    }

    @Override
    public RecipeType<InfusionRecipe> recipeType() {
        return RecipeRegistry.INFUSION.get();
    }

    @Override
    public InteractionResult rightClick(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult result) {
        if (player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND) {
            if (!level.isClientSide() && getDataSlots().get("isActive") == 0) {
                validateStructure(blockPos, level);
                getDataSlots().set("isActive", 1);
                setChanged();
                syncWithClients(level, blockPos);
            }
            return InteractionResult.SUCCESS;
        }
        return IRightClickable.super.rightClick(blockState, level, blockPos, player, hand, result);
    }

    public static class MachineBlock extends BaseMachineBlock {
        public MachineBlock(Properties properties, RegistryObject<BlockEntityType<? extends BaseMachine<?>>> blockEntity) {
            super(properties, blockEntity);
        }
    }
}
