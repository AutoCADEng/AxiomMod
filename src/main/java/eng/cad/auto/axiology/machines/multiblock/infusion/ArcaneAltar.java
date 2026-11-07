package eng.cad.auto.axiology.machines.multiblock.infusion;

import eng.cad.auto.axiology.AxiomMod;
import eng.cad.auto.axiology.core.DataSlotHandler;
import eng.cad.auto.axiology.core.ExtendedItemStackHandler;
import eng.cad.auto.axiology.core.ISlotData;
import eng.cad.auto.axiology.core.ISlotItem;
import eng.cad.auto.axiology.core.blocks.BaseMachineBlock;
import eng.cad.auto.axiology.core.component.*;
import eng.cad.auto.axiology.core.machine.BaseMachine;
import eng.cad.auto.axiology.core.machine.BaseMaster;
import eng.cad.auto.axiology.core.slot.SlotConfig;
import eng.cad.auto.axiology.core.slot.SlotIORestriction;
import eng.cad.auto.axiology.crafting.machines.InfusionRecipe;
import eng.cad.auto.axiology.magic.rebounds.Rebounds;
import eng.cad.auto.axiology.magic.rebounds.core.AbstractRebound;
import eng.cad.auto.axiology.magic.rebounds.core.InfusionContext;
import eng.cad.auto.axiology.registry.RecipeRegistry;
import eng.cad.auto.engine.crafting.core.CustomCraftingContainer;
import eng.cad.auto.engine.network.AxiologyNetwork;
import eng.cad.auto.engine.network.packet.InfusionSyncPacket;
import eng.cad.auto.engine.network.packet.KillBeamPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import java.util.*;

public class ArcaneAltar extends BaseMaster<ArcaneAltar> implements ISlotItem, ISlotData, IMachineProgress, ITickable, IRightClickable, IRecipeMachine<InfusionRecipe> {
    public static String name = "arcane_altar";
    public static int MAX_PEDESTAL_RADIUS = 5;
    int[] lastSyncData;
    long lastSyncTime;
    boolean firstRun = true;
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

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if(level.isClientSide()) return;
        if (!(level.getBlockEntity(pos) instanceof ArcaneAltar altar)) return;
        if (firstRun) {
            validateStructure(pos, level);
        }

        if (getDataSlots().get("isActive") == 0){
            resetProgress(this);
            syncWithClients(level, pos);
            resetPedestals();
            return;
        }

        for (BaseMachine<? extends IMultiblockFollower> pedestalMachine:getFollowers(level)){
            if (beamUUIDs.containsKey(pedestalMachine.getBlockPos())){
                if (!(pedestalMachine instanceof ArcanePedestal pedestal)) continue;
                if (pedestal.getItemSlots().getStackInSlot(0).isEmpty() || getDataSlots().get("isActive") == 0 || pedestal.getDataSlots().get("isItemHidden") == 1) {
                    if (!removePedestalBeam(pedestal.getBlockPos(), pos, level)) continue;
                    if (pedestal.getDataSlots().get("isItemHidden") == 1) {
                        pedestal.setChanged();
                        level.sendBlockUpdated(pedestal.getBlockPos(), pedestal.getBlockState(), pedestal.getBlockState(), 3);
                    }
                }
            }else {
                addPedestalBeam(pedestalMachine.getBlockPos(), getBlockPos(), level);
            }
        }

        if(!progressCompleted(this)){
            incrementProgress(this, 1);
            syncWithClients(level, pos);
            if (level.getGameTime()%40 == 0){
            }
        }

        if (checkPedestalCompletion(level)) {
            CustomCraftingContainer infusionContainers = new CustomCraftingContainer(this).addSlot(getItemSlots().getStackInSlot(0));
            for (BaseMachine<? extends IMultiblockFollower> follower:getFollowers(level)){
                if (follower instanceof ArcanePedestal pedestal){
                    infusionContainers.addSlot(pedestal.getItemSlots().getStackInSlot(0));
                }
                // TODO add a fluid equivalent if/when we make a tank variant of ArcanePedestal
            }
            InfusionRecipe activeRecipe = findRecipe(this, level, infusionContainers);
            performRitual(activeRecipe, this, level);
        }
    }

    private void performRitual(InfusionRecipe activeRecipe, ArcaneAltar entity, Level level) {
        var followers = getFollowers(level);
        if (activeRecipe != null && activeRecipe.validate(entity, followers, activeRecipe)) {
            activeRecipe.craft(entity, activeRecipe);
        } else {
            AbstractRebound effect = Rebounds.randomEffect();
            AxiomMod.LOGGER.warn("FAILED A RECIPE FOR INFUSER applying {}", effect.toString());
            for(ServerPlayer player:level.getEntitiesOfClass(ServerPlayer.class, new AABB(entity.getBlockPos()).inflate(MAX_PEDESTAL_RADIUS*2))) {
                effect.apply(new InfusionContext(level, entity.getBlockPos(), player));
            }

            for(BaseMachine<? extends IMultiblockFollower> follower:followers){
                if (!(follower instanceof ArcanePedestal pedestal)) continue;
                pedestal.getItemSlots().setStackInSlot(0, ItemStack.EMPTY);
                pedestal.getDataSlots().set("isItemHidden", 0);
                pedestal.syncWithClients(level, pedestal.getBlockPos());
                pedestal.setChanged();
                level.sendBlockUpdated(pedestal.getBlockPos(), pedestal.getBlockState(), pedestal.getBlockState(), 3);
            }
        }

        level.playSound(
                null,
                entity.getBlockPos(),
                SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("axiology", "infuser_fail")),
                SoundSource.BLOCKS,
                0.5F,
                0.9F
        );

        entity.getDataSlots().set("isActive", 0);
    }

    public boolean checkPedestalCompletion(Level level){
        if (getFollowerPositions().isEmpty()) return false;
        return getFollowers(level).stream()
                .filter(follower -> level.getBlockEntity(follower.getBlockPos()) instanceof ArcanePedestal)
                .map(follower -> (ArcanePedestal) follower)
                .filter(Objects::nonNull)
                .filter(machine -> !machine.getItemSlots().getStackInSlot(0).isEmpty())
                .allMatch(machine -> machine.getDataSlots().get("isItemHidden") == 1);
    }

    public void addPedestalBeam(BlockPos followerPos, BlockPos pos, Level level){
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

    public boolean removePedestalBeam(BlockPos followerPos, BlockPos pos, Level level){
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

    public static HashSet<BlockPos> findPedestals(Level level, BlockPos center, int radius) {
        HashSet<BlockPos> pedestals = new HashSet<>();
        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-radius, -radius, -radius),
                center.offset(radius, radius, radius))) {
            if (!level.isLoaded(pos)) continue;
            BlockEntity be = level.getBlockEntity(pos);
            if (be != null) pedestals.add(pos);
        }
        return pedestals;
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
        return;
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
        return (24000/60)/4;
    }

    @Override
    public RecipeType<InfusionRecipe> recipeType() {
        return RecipeRegistry.INFUSION.get();
    }

    public static class MachineBlock extends BaseMachineBlock {
        public MachineBlock(Properties properties, RegistryObject<BlockEntityType<? extends BaseMachine<?>>> blockEntity) {
            super(properties, blockEntity);
        }
    }
}
