package eng.cad.auto.axiology.core.component;

import eng.cad.auto.axiology.core.machine.BaseMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

public interface ITickable {
    void serverTick(Level level, BlockPos pos, BlockState state);

    default BaseMachine validateMachine(Level level, BlockPos pos, String name){
        if(level.isClientSide()) return null;
        if (!(level.getBlockEntity(pos) instanceof BaseMachine blockEntity)) return null;
        if (!(Objects.equals(blockEntity.getName(), name))) return null;
        return blockEntity;
    }
}
