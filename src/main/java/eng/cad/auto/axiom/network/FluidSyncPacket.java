package eng.cad.auto.axiom.network;

import eng.cad.auto.axiom.common.machine.core.GenericBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class FluidSyncPacket {
    private final BlockPos pos;
    private final ResourceLocation fluidResource;
    private final int slot;
    private final int amount;

    public FluidSyncPacket(BlockPos pos, int tank, FluidStack stack) {
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

    public static FluidSyncPacket decode(FriendlyByteBuf byteBuf) {
        return new FluidSyncPacket(
                byteBuf.readBlockPos(),
                byteBuf.readVarInt(),
                byteBuf.readFluidStack()
        );
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(
                        Dist.CLIENT,
                        () -> () -> {
                            Level level = Minecraft.getInstance().level;
                            if (level == null) return;

                            BlockEntity blockEntity = level.getBlockEntity(pos);
                            if (!(blockEntity instanceof GenericBlockEntity genericBlockEntity)) return;
                            if (genericBlockEntity.machineConfig.fluidSlots() <= 0) return;
                            if (genericBlockEntity.machine.tanks == null) return;
                            if (slot < 0 || slot >= genericBlockEntity.machine.tanks.getTanks()) return;

                            FluidStack stack = FluidStack.EMPTY;

                            if (fluidResource != null && amount > 0) {
                                Fluid fluid = ForgeRegistries.FLUIDS.getValue(fluidResource);
                                if (fluid != null) {
                                    stack = new FluidStack(fluid, amount);
                                }
                            }

                            genericBlockEntity.machine.tanks.setStackInSlot(slot, stack);
                        }
                )
        );

        context.get().setPacketHandled(true);
    }
}
