package eng.cad.auto.axiology.machines;

import eng.cad.auto.axiology.machines.util.CommonRightClickBehaviour;
import eng.cad.auto.axiology.registry.MachineRegistry;
import eng.cad.auto.engine.machine.core.GenericMachineEntity;
import eng.cad.auto.engine.machine.core.MachineCore;
import eng.cad.auto.engine.machine.core.interfaces.IMachineLogic;
import eng.cad.auto.engine.machine.core.interfaces.IRightClickBehaviour;
import eng.cad.auto.engine.machine.core.slot.SlotIORestriction;
import eng.cad.auto.engine.machine.core.slot.SlotType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class ArcanePedestal {
    MachineCore INSTANCE;
    public static final String name = "arcane_pedestal";
    public ArcanePedestal(){
        this.INSTANCE = MachineRegistry.registerMachine(new MachineCore.Builder(name)
                .addItemSlots(1)
                .addDataSlots(1)
                .modifySlot(0, SlotType.ITEM,1, SlotIORestriction.IN_OUT)
                .noGui()
                .rightClickLogic(new Logic.RightClickLogic())
                .addServerLogic(Logic.Server::new)
                .build());
    }

    public static class Logic{
        public static class Server implements IMachineLogic {

            @Override
            public void serverTick(Level level, BlockPos pos, BlockState state) {

            }

            @Override
            public void saveInventory(GenericMachineEntity entity, CompoundTag tag) {
                if (entity.get().inventory != null) {
                    tag.put("Inventory", entity.get().inventory.serializeNBT());
                }
                if (entity.get().tanks != null) {
                    tag.put("Tanks", entity.get().tanks.serializeNBT());
                }
                tag.putInt("Progress", entity.get().metadata.get(0));
            }

            @Override
            public void loadInventory(GenericMachineEntity entity, CompoundTag tag) {
                if (entity.get().inventory != null && tag.contains("Inventory")) {
                    entity.get().inventory.deserializeNBT(tag.getCompound("Inventory"));
                }
                if (entity.get().tanks != null && tag.contains("Tanks")) {
                    entity.get().tanks.deserializeNBT(tag.getCompound("Tanks"));
                }
                if (tag.contains("Progress")) entity.get().metadata.set(0, tag.getInt("Progress"));
            }

            @Override
            public void resetProgress(GenericMachineEntity blockEntity) {

            }

            @Override
            public void incrementProgress(GenericMachineEntity blockEntity, int count) {

            }
        }
        public static class RightClickLogic implements IRightClickBehaviour {
            @Override
            public InteractionResult useMachine(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult result) {
                if (!(level.getBlockEntity(blockPos) instanceof GenericMachineEntity machine)) return InteractionResult.PASS;
                if (!machine.machineConfig.name().equals(ArcanePedestal.name)) return InteractionResult.PASS;
                boolean success = onRightClick(machine, blockState, level, blockPos, player, interactionHand, result);
                if (success) {
                    machine.get().metadata.set(0,0);
                    machine.syncData();
                }
                return success? InteractionResult.SUCCESS : InteractionResult.PASS;
            }

            private boolean onRightClick(GenericMachineEntity blockEntity, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult result) {
                if (player.isShiftKeyDown()) return false;
                if (interactionHand == InteractionHand.OFF_HAND) return false;
                return CommonRightClickBehaviour.itemInHand(0, blockEntity, blockState, level, blockPos, player, interactionHand, result);
            }
        }
    }
}
