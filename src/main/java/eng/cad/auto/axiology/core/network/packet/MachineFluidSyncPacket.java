package eng.cad.auto.axiology.core.network.packet;

import eng.cad.auto.axiology.core.ISlotFluid;
import eng.cad.auto.axiology.core.machine.TickingMachine;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class MachineFluidSyncPacket extends AxiologyBasePacket<MachineFluidSyncPacket> {
    private final BlockPos pos;
    private final ResourceLocation fluidResource;
    private final int slot;
    private final int amount;

    public MachineFluidSyncPacket() {
        this.pos = new BlockPos(0, 0, 0);
        this.fluidResource = null;
        this.slot = 0;
        this.amount = 0;
    }

    public MachineFluidSyncPacket(BlockPos pos, int tank, FluidStack stack) {
        this.pos = pos;
        this.fluidResource = stack.isEmpty() ? null : ForgeRegistries.FLUIDS.getKey(stack.getFluid());
        this.slot = tank;
        this.amount = stack.isEmpty() ? 0 : stack.getAmount();
    }

    public void encode(FriendlyByteBuf byteBuf) {
        byteBuf.writeBlockPos(pos);
        byteBuf.writeVarInt(slot);
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(fluidResource);

        if (fluid == null || amount <= 0) {
            byteBuf.writeFluidStack(FluidStack.EMPTY);
        } else {
            byteBuf.writeFluidStack(new FluidStack(fluid, amount));
        }
    }

    @Override
    public MachineFluidSyncPacket decode(FriendlyByteBuf byteBuf) {
        return new MachineFluidSyncPacket(
                byteBuf.readBlockPos(),
                byteBuf.readVarInt(),
                byteBuf.readFluidStack()
        );
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(
                        Dist.CLIENT,
                        () -> () -> {
                            Level level = Minecraft.getInstance().level;
                            if (level == null) return;

                            BlockEntity blockEntity = level.getBlockEntity(pos);
                            if (!(blockEntity instanceof TickingMachine genericBlockEntity)) return;
                            if (!(genericBlockEntity instanceof ISlotFluid fluidMachine)) return;
                            if (fluidMachine.getFluidSlots().getTanks() <= 0) return;
                            if (fluidMachine.getFluidSlots() == null) return;
                            if (slot < 0 || slot >= fluidMachine.getFluidSlots().getTanks()) return;

                            FluidStack stack = FluidStack.EMPTY;

                            if (fluidResource != null && amount > 0) {
                                Fluid fluid = ForgeRegistries.FLUIDS.getValue(fluidResource);
                                if (fluid != null) {
                                    stack = new FluidStack(fluid, amount);
                                }
                            }

                            fluidMachine.getFluidSlots().setStackInSlot(slot, stack);
                        }
                )
        );

        context.get().setPacketHandled(true);
    }
}
