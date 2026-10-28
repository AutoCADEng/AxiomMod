package eng.cad.auto.axiom.machines;

import eng.cad.auto.axolotyl.client.machine.core.IMachineClientLogic;
import eng.cad.auto.axolotyl.crafting.core.CustomCraftingContainer;
import eng.cad.auto.axolotyl.crafting.core.GenericRecipe;
import eng.cad.auto.axolotyl.crafting.util.CraftingUtil;
import eng.cad.auto.axolotyl.item.CustomBucket;
import eng.cad.auto.axolotyl.machine.core.GenericBlockEntity;
import eng.cad.auto.axolotyl.machine.core.MachineHolder;
import eng.cad.auto.axolotyl.machine.core.slot.SlotIORestriction;
import eng.cad.auto.axolotyl.machine.core.slot.SlotType;
import eng.cad.auto.axolotyl.machine.core.interfaces.IMachineLogic;
import eng.cad.auto.axolotyl.machine.core.interfaces.IRightClickBehaviour;
import eng.cad.auto.axolotyl.machine.core.ui.GenericUI;
import eng.cad.auto.axiom.registry.MachineRegistry;
import eng.cad.auto.axiom.registry.RecipeRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import java.util.Objects;
import java.util.Optional;

public class SoakingDrum {
    MachineHolder INSTANCE;
    public static final String name = "soaking_drum";
    public SoakingDrum() {
        this.INSTANCE = MachineRegistry.registerMachine(new MachineHolder
                .Builder(name)
                .addItemSlots(1)
                .addDataSlots(1)
                .addFluidSlots(1)
                .modifySlot(0, SlotType.ITEM,1, SlotIORestriction.IN_OUT)
                .modifySlot(0,SlotType.FLUID,8000,SlotIORestriction.IN_OUT)
                .addServerLogic(new Logic.Server())
                .addClientLogic(new Logic.Client())
                .noGui(new Logic.RightClickLogic())
                .build());
    }
    public static class Logic{

        public static final int progress = 0;
        public static final int item_slot = 0;
        public static final int fluid_tank = 0;
        public static final int MAX_PROGRESS = 24000/60;

        public static class Server implements IMachineLogic {

            @Override
            public void serverTick(Level level, BlockPos pos, BlockState state) {
                if(level.isClientSide()) return;
                if (!(level.getBlockEntity(pos) instanceof GenericBlockEntity blockEntity)) return;
                if (!(Objects.equals(blockEntity.machineConfig.name(), SoakingDrum.name))) return;

                GenericRecipe activeRecipe = CraftingUtil.getActiveRecipe(
                        new CustomCraftingContainer(blockEntity)
                                .addTank(blockEntity.machine.tanks.getFluidInTank(fluid_tank))
                                .addSlot(blockEntity.machine.inventory.getStackInSlot(item_slot)),
                        RecipeRegistry.SOAKING_DRUM.get(),
                        level,
                        blockEntity);

                if (activeRecipe == null){
                    resetProgress(blockEntity);
                    return;
                }

                if (blockEntity.machine.metadata.get(progress) >= MAX_PROGRESS) {
                    if (CraftingUtil.validate(blockEntity, activeRecipe)) CraftingUtil.craft(blockEntity, activeRecipe);
                    level.playSound(null, pos, SoundEvents.DISPENSER_DISPENSE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    resetProgress(blockEntity);
                    return;
                }

                if (level.random.nextFloat() < 0.1F) level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0F, 1.0F);
                incrementProgress(blockEntity,1);
            }


            @Override
            public void saveInventory(GenericBlockEntity entity, CompoundTag tag) {
                if (entity.machine.inventory != null) {
                    tag.put("Inventory", entity.machine.inventory.serializeNBT());
                }
                if (entity.machine.tanks != null) {
                    tag.put("Tanks", entity.machine.tanks.serializeNBT());
                }
                tag.putInt("Progress", entity.machine.metadata.get(progress));
            }

            @Override
            public void loadInventory(GenericBlockEntity entity, CompoundTag tag) {
                if (entity.machine.inventory != null && tag.contains("Inventory")) {
                    entity.machine.inventory.deserializeNBT(tag.getCompound("Inventory"));
                }
                if (entity.machine.tanks != null && tag.contains("Tanks")) {
                    entity.machine.tanks.deserializeNBT(tag.getCompound("Tanks"));
                }
                if (tag.contains("Progress")) entity.machine.metadata.set(progress, tag.getInt("Progress"));
            }

            @Override
            public void resetProgress(GenericBlockEntity blockEntity) {
                blockEntity.machine.metadata.set(progress, 0);
            }

            @Override
            public void incrementProgress(GenericBlockEntity blockEntity, int count) {
                blockEntity.machine.metadata.set(progress, blockEntity.machine.metadata.get(progress)>= MAX_PROGRESS? MAX_PROGRESS : blockEntity.machine.metadata.get(progress) + count);
            }
        }
        public static class Client implements IMachineClientLogic {
            @Override
            public void renderLogic(GuiGraphics guiGraphics, float tick, int mouse_x, int mouse_y, GenericUI.Screen screen) {
                // This does nothing as the machine does not have a UI
            }

            @Override
            public void clientTick(Level level, BlockPos pos, BlockState state, GenericBlockEntity blockEntity) {
                if (!level.isClientSide()) return;
                if (blockEntity.machine.metadata.get(progress) <= 0) return;
                if (level.random.nextFloat() < 0.33F) {

                    double x = pos.getX() + 0.5D + (level.random.nextDouble() - 0.5D) * 0.5D;
                    double y = pos.getY() + 1.0D;
                    double z = pos.getZ() + 0.5D + (level.random.nextDouble() - 0.5D) * 0.5D;

                    double xSpeed = 0.0D;
                    double ySpeed = 0.05D;
                    double zSpeed = 0.0D;

                    level.addParticle(ParticleTypes.SMOKE, x, y, z, xSpeed, ySpeed, zSpeed);
                }
            }
        }
        public static class RightClickLogic implements IRightClickBehaviour {

            @Override
            public InteractionResult useMachine(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult result) {
                if (!(level.getBlockEntity(blockPos) instanceof GenericBlockEntity machine)) return InteractionResult.PASS;
                if (!machine.machineConfig.name().equals(SoakingDrum.name)) return InteractionResult.PASS;
                boolean success = onRightClick(machine, blockState, level, blockPos, player, interactionHand, result);
                return success? InteractionResult.SUCCESS : InteractionResult.PASS;
            }

            private boolean onRightClick(GenericBlockEntity blockEntity, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult result) {
                if (player.isShiftKeyDown()) return false;
                if (interactionHand == InteractionHand.OFF_HAND) return false;
                if (bucketInHand(blockEntity, blockState, level, blockPos, player, interactionHand, result)) return true;
                ItemStack playerHand = player.getItemInHand(interactionHand);
                ItemStack machineItem = blockEntity.machine.inventory.getStackInSlot(item_slot);
                if (machineItem.isEmpty()) {
                    if (playerHand.isEmpty()) return false;
                    ItemStack toInsert = playerHand.copy();
                    toInsert.setCount(1);
                    ItemStack remainder = blockEntity.machine.inventory.insertItem(item_slot, toInsert, false);
                    if (remainder.getCount() < toInsert.getCount()) {
                        level.playSound(null, blockPos, SoundEvents.COMPOSTER_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                        playerHand.shrink(1);
                    }
                    return true;
                }

                if (playerHand.isEmpty() || playerHand.is(machineItem.getItem())) {
                    int maxHandLimit = playerHand.isEmpty() ? machineItem.getMaxStackSize() : playerHand.getMaxStackSize();
                    int currentHandCount = playerHand.isEmpty() ? 0 : playerHand.getCount();
                    int maxTransferable = maxHandLimit - currentHandCount;
                    if (maxTransferable <= 0) return false;
                    ItemStack simulatedExtract = blockEntity.machine.inventory.extractItem(item_slot, maxTransferable, true);
                    if (simulatedExtract.isEmpty()) return false;
                    ItemStack extracted = blockEntity.machine.inventory.extractItem(item_slot, simulatedExtract.getCount(), false);
                    level.playSound(null, blockPos, SoundEvents.COMPOSTER_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                    if (playerHand.isEmpty()) {
                        player.setItemInHand(interactionHand, extracted);
                    } else {
                        playerHand.grow(extracted.getCount());
                    }
                }
                return false;
            }

            private boolean bucketInHand(GenericBlockEntity blockEntity, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult result) {
                ItemStack playerHand = player.getItemInHand(interactionHand);
                Optional<IFluidHandlerItem> bucketHandler = playerHand.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve();
                if (bucketHandler.isPresent()) {
                    if (bucketHandler.get().getFluidInTank(0).isEmpty()) {
                        CustomBucket.fluidToItem(blockEntity, player, interactionHand);
                    } else {
                        CustomBucket.fluidFromItem(blockEntity, player, interactionHand);
                    }
                    return true;
                }
                return false;
            }
        }
    }
}
