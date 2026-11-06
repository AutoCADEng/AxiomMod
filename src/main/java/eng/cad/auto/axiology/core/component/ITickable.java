package eng.cad.auto.axiology.core.component;

import eng.cad.auto.axiology.core.machine.TickingMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

public interface ITickable {
    void serverTick(Level level, BlockPos pos, BlockState state);

    default TickingMachine validateMachine(Level level, BlockPos pos, String name){
        if(level.isClientSide()) return null;
        if (!(level.getBlockEntity(pos) instanceof TickingMachine blockEntity)) return null;
        if (!(Objects.equals(blockEntity.getName(), name))) return null;
        return blockEntity;
    }
}
