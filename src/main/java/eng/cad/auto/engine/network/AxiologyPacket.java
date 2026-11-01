package eng.cad.auto.engine.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class AxiologyPacket<T> {
    abstract void encode(FriendlyByteBuf byteBuf);
    abstract void handle(Supplier<NetworkEvent.Context> context);
    abstract T decode(FriendlyByteBuf buffer);
}
