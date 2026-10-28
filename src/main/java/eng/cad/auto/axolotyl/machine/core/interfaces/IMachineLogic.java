package eng.cad.auto.axolotyl.machine.core.interfaces;

import eng.cad.auto.axolotyl.machine.core.GenericMachineEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IMachineLogic {
    void serverTick(Level level, BlockPos pos, BlockState state);
    void saveInventory(GenericMachineEntity blockEntity, CompoundTag tag);
    void loadInventory(GenericMachineEntity blockEntity, CompoundTag tag);
    void resetProgress(GenericMachineEntity blockEntity);
    void incrementProgress(GenericMachineEntity blockEntity, int count);
}
