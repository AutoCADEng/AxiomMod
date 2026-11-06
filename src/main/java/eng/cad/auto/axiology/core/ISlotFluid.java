package eng.cad.auto.axiology.core;

import eng.cad.auto.engine.fluid.FluidStackHandler;
import eng.cad.auto.engine.network.AxiologyNetwork;
import eng.cad.auto.engine.network.packet.MachineFluidSyncPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface ISlotFluid {
    FluidStackHandler getFluidSlots();

    default void syncWithClient(int tank, Level level, BlockPos worldPosition, ISlotFluid fluidMachine) {
        AxiologyNetwork.sendToClientWithChunk(
                level,
                worldPosition,
                new MachineFluidSyncPacket(worldPosition, tank, fluidMachine.getFluidSlots().getFluidInTank(tank))
        );
    }


}
