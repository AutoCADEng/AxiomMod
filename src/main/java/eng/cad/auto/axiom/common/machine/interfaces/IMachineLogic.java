package eng.cad.auto.axiom.common.machine.interfaces;

import eng.cad.auto.axiom.common.crafting.core.GenericRecipe;
import eng.cad.auto.axiom.common.machine.core.GenericBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IMachineLogic {
    void serverTick(Level level, BlockPos pos, BlockState state);
    void saveInventory(GenericBlockEntity blockEntity, CompoundTag tag);
    void loadInventory(GenericBlockEntity blockEntity, CompoundTag tag);
    void resetProgress(GenericBlockEntity blockEntity);
    void incrementProgress(GenericBlockEntity blockEntity, int count);
}
