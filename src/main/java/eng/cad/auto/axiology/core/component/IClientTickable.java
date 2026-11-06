package eng.cad.auto.axiology.core.component;

import eng.cad.auto.axiology.core.machine.TickingMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IClientTickable {
    void clientTick(Level level, BlockPos pos, BlockState state, TickingMachine blockEntity);
}
