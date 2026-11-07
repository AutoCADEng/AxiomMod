package eng.cad.auto.axiology.core;

import eng.cad.auto.axiology.core.fluid.FluidStackHandler;
import eng.cad.auto.axiology.core.network.AxiologyNetwork;
import eng.cad.auto.axiology.core.network.packet.MachineFluidSyncPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface ISlotFluid {
    FluidStackHandler getFluidSlots();

    default void syncWithClient(int tank, Level level, BlockPos worldPosition, ISlotFluid fluidMachine) {
        AxiologyNetwork.sendToClientWithChunk(level, worldPosition, new MachineFluidSyncPacket(worldPosition, tank, fluidMachine.getFluidSlots().getFluidInTank(tank)));
    }

    default void onFluidContentsChanged(int slot) {
        if (!(this instanceof BlockEntity blockEntity)) return;
        blockEntity.setChanged();
        Level level = blockEntity.getLevel();
        if (level != null && !level.isClientSide) {
            syncWithClient(slot, level, blockEntity.getBlockPos(), this);
        }
    };
}
