package eng.cad.auto.axiology.machines;

import eng.cad.auto.axiology.machines.util.CommonRightClickBehaviour;
import eng.cad.auto.engine.client.machine.core.IMachineClientLogic;
import eng.cad.auto.engine.crafting.core.CustomCraftingContainer;
import eng.cad.auto.axiology.crafting.GenericMachineRecipe;
import eng.cad.auto.engine.crafting.util.CraftingUtil;
import eng.cad.auto.engine.machine.core.GenericMachineEntity;
import eng.cad.auto.engine.machine.core.MachineCore;
import eng.cad.auto.engine.machine.core.slot.SlotIORestriction;
import eng.cad.auto.engine.machine.core.slot.SlotType;
import eng.cad.auto.engine.machine.core.interfaces.IMachineLogic;
import eng.cad.auto.engine.machine.core.interfaces.IRightClickBehaviour;
import eng.cad.auto.engine.machine.core.ui.GenericUI;
import eng.cad.auto.axiology.registry.MachineRegistry;
import eng.cad.auto.axiology.registry.RecipeRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Objects;

public class SoakingDrum {
    MachineCore INSTANCE;
    public static final String name = "soaking_drum";
    public SoakingDrum() {
        this.INSTANCE = MachineRegistry.registerMachine(new MachineCore
                .Builder(name)
                .addItemSlots(1)
                .addDataSlots(1)
                .addFluidSlots(1)
                .modifySlot(0, SlotType.ITEM,1, SlotIORestriction.IN_OUT)
                .modifySlot(0,SlotType.FLUID,8000,SlotIORestriction.IN_OUT)
                .addServerLogic(Logic.Server::new)
                .addClientLogic(new Logic.Client())
                .noGui()
                .rightClickLogic(new Logic.RightClickLogic())
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
                if (!(level.getBlockEntity(pos) instanceof GenericMachineEntity blockEntity)) return;
                if (!(Objects.equals(blockEntity.machineConfig.name(), SoakingDrum.name))) return;

                GenericMachineRecipe activeRecipe = CraftingUtil.getActiveRecipe(
                        new CustomCraftingContainer(blockEntity)
                                .addTank(blockEntity.get().tanks.getFluidInTank(fluid_tank))
                                .addSlot(blockEntity.get().inventory.getStackInSlot(item_slot)),
                        RecipeRegistry.SOAKING_DRUM.get(),
                        level,
                        blockEntity);

                if (activeRecipe == null){
                    resetProgress(blockEntity);
                    return;
                }

                if (blockEntity.get().metadata.get(progress) >= MAX_PROGRESS) {
                    if (CraftingUtil.validate(blockEntity, activeRecipe)) CraftingUtil.craft(blockEntity, activeRecipe);
                    level.playSound(null, pos, SoundEvents.DISPENSER_DISPENSE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    resetProgress(blockEntity);
                    return;
                }

                if (level.random.nextFloat() < 0.1F) level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0F, 1.0F);
                incrementProgress(blockEntity,1);
            }


            @Override
            public void saveInventory(GenericMachineEntity entity, CompoundTag tag) {
                if (entity.get().inventory != null) {
                    tag.put("Inventory", entity.get().inventory.serializeNBT());
                }
                if (entity.get().tanks != null) {
                    tag.put("Tanks", entity.get().tanks.serializeNBT());
                }
                tag.putInt("Progress", entity.get().metadata.get(progress));
            }

            @Override
            public void loadInventory(GenericMachineEntity entity, CompoundTag tag) {
                if (entity.get().inventory != null && tag.contains("Inventory")) {
                    entity.get().inventory.deserializeNBT(tag.getCompound("Inventory"));
                }
                if (entity.get().tanks != null && tag.contains("Tanks")) {
                    entity.get().tanks.deserializeNBT(tag.getCompound("Tanks"));
                }
                if (tag.contains("Progress")) entity.get().metadata.set(progress, tag.getInt("Progress"));
            }

            @Override
            public void resetProgress(GenericMachineEntity blockEntity) {
                blockEntity.get().metadata.set(progress, 0);
            }

            @Override
            public void incrementProgress(GenericMachineEntity blockEntity, int count) {
                blockEntity.get().metadata.set(progress, blockEntity.get().metadata.get(progress)>= MAX_PROGRESS? MAX_PROGRESS : blockEntity.get().metadata.get(progress) + count);
            }
        }
        public static class Client implements IMachineClientLogic {
            @Override
            public void UIRenderLogic(GuiGraphics guiGraphics, float tick, int mouse_x, int mouse_y, GenericUI.Screen screen) {
                // This does nothing as the machine does not have a UI
            }

            @Override
            public void clientTick(Level level, BlockPos pos, BlockState state, GenericMachineEntity blockEntity) {
                if (!level.isClientSide()) return;
                if (blockEntity.get().metadata.get(progress) <= 0) return;
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
                if (!(level.getBlockEntity(blockPos) instanceof GenericMachineEntity machine)) return InteractionResult.PASS;
                if (!machine.machineConfig.name().equals(SoakingDrum.name)) return InteractionResult.PASS;
                boolean success = onRightClick(machine, blockState, level, blockPos, player, interactionHand, result);
                return success? InteractionResult.SUCCESS : InteractionResult.PASS;
            }

            private boolean onRightClick(GenericMachineEntity blockEntity, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult result) {
                if (player.isShiftKeyDown()) return false;
                if (interactionHand == InteractionHand.OFF_HAND) return false;
                if (CommonRightClickBehaviour.bucketInHand(0, blockEntity, blockState, level, blockPos, player, interactionHand, result)) return true;
                return CommonRightClickBehaviour.itemInHand(0, blockEntity, blockState, level, blockPos, player, interactionHand, result);
            }
        }
    }
}
