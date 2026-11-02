package eng.cad.auto.axiology.machines.multiblock.followers;

import eng.cad.auto.axiology.machines.util.CommonRightClickBehaviour;
import eng.cad.auto.axiology.registry.MachineRegistry;
import eng.cad.auto.engine.client.machine.core.IMachineClientLogic;
import eng.cad.auto.engine.crafting.core.CustomCraftingContainer;
import eng.cad.auto.engine.machine.core.GenericMachineEntity;
import eng.cad.auto.engine.machine.core.MachineCore;
import eng.cad.auto.engine.machine.core.interfaces.IMachineLogic;
import eng.cad.auto.engine.machine.core.interfaces.IRightClickBehaviour;
import eng.cad.auto.engine.machine.core.slot.SlotIORestriction;
import eng.cad.auto.engine.machine.core.slot.SlotType;
import eng.cad.auto.engine.machine.core.ui.GenericUI;
import eng.cad.auto.engine.machine.multiblock.GenericFollowerEntity;
import eng.cad.auto.engine.machine.multiblock.IFollowerLogic;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class EtchedFluidOutput {
    MachineCore INSTANCE;
    public static final String name = "etched_fluid_output";
    public EtchedFluidOutput() {
        this.INSTANCE = MachineRegistry.registerMachine(new MachineCore
                .Builder(name)
                .addFluidSlots(1)
                .modifySlot(0, SlotType.FLUID,1000, SlotIORestriction.OUTPUT)
                .addServerLogic(new Logic.Server())
                .addClientLogic(new Logic.Client())
                .rightClickLogic(new Logic.RightClickLogic())
                .noGui()
                .isMultiblockFollower(new Logic.FollowerLogic())
                .build());
    }
    public static class Logic{
        public static class FollowerLogic implements IFollowerLogic {
            @Override
            public CustomCraftingContainer getCraftingContainer(GenericFollowerEntity entity) {
                return new CustomCraftingContainer(entity).addTank(entity.get().tanks.getFluidInTank(0));
            }
        }
        public static class Server implements IMachineLogic {

            @Override
            public void serverTick(Level level, BlockPos pos, BlockState state) {
                if(level.isClientSide()) return;
                if (!(level.getBlockEntity(pos) instanceof GenericFollowerEntity blockEntity)) return;

            }


            @Override
            public void saveInventory(GenericMachineEntity entity, CompoundTag tag) {
                if (!(entity instanceof GenericFollowerEntity blockEntity)) return;
                if (blockEntity.get().tanks != null) {
                    tag.put("Tanks", blockEntity.get().tanks.serializeNBT());
                }
            }

            @Override
            public void loadInventory(GenericMachineEntity entity, CompoundTag tag) {
                if (!(entity instanceof GenericFollowerEntity blockEntity)) return;
                if (blockEntity.get().tanks != null && tag.contains("Tanks")) {
                    blockEntity.get().tanks.deserializeNBT(tag.getCompound("Tanks"));
                }

            }

            @Override
            public void resetProgress(GenericMachineEntity blockEntity) {
            }

            @Override
            public void incrementProgress(GenericMachineEntity blockEntity, int count) {
            }
        }
        public static class Client implements IMachineClientLogic {
            @Override
            public void UIRenderLogic(GuiGraphics guiGraphics, float tick, int mouse_x, int mouse_y, GenericUI.Screen screen) {
            }

            @Override
            public void clientTick(Level level, BlockPos pos, BlockState state, GenericMachineEntity blockEntity) {
                if (!level.isClientSide()) return;

            }
        }
        public static class RightClickLogic implements IRightClickBehaviour {

            @Override
            public InteractionResult useMachine(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult result) {
                if (!(level.getBlockEntity(blockPos) instanceof GenericFollowerEntity machine)) return InteractionResult.PASS;
                if (!machine.machineConfig.name().equals(EtchedFluidOutput.name)) return InteractionResult.PASS;
                boolean success = onRightClick(machine, blockState, level, blockPos, player, interactionHand, result);
                return success? InteractionResult.SUCCESS : InteractionResult.PASS;
            }

            private boolean onRightClick(GenericMachineEntity blockEntity, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult result) {
                if (!(blockEntity instanceof GenericFollowerEntity machine)) return false;
                if (player.isShiftKeyDown()) return false;
                if (interactionHand == InteractionHand.OFF_HAND) return false;
                return CommonRightClickBehaviour.extractFluid(0, machine, blockState, level, blockPos, player, interactionHand, result);
            }
        }
    }
}
