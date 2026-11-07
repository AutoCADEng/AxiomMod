package eng.cad.auto.axiology.core.machine;

import eng.cad.auto.axiology.core.*;
import eng.cad.auto.axiology.core.component.IClientTickable;
import eng.cad.auto.axiology.core.component.ITickable;
import eng.cad.auto.axiology.core.fluid.FluidStackHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class TickingMachine<T extends TickingMachine<T>> extends BaseMachine<T> {
    MachineFactory factory;

    public TickingMachine(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        items = new ExtendedItemStackHandler();
        fluids = new FluidStackHandler();
        data = new DataSlotHandler();
        refreshCapabilities();
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, TickingMachine<?> blockEntity){
        if (blockEntity instanceof ITickable serverTicker) {
            serverTicker.serverTick(level, blockPos, blockState);
            if (!(blockEntity instanceof ISlotData data)) return;
            data.updateDataIfChanged(level, blockPos);
        }
        if (blockEntity instanceof IClientTickable clientTicker) {
            clientTicker.clientTick(level, blockPos, blockState, blockEntity);
        }
    }
}
