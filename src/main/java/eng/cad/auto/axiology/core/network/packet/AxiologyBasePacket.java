package eng.cad.auto.axiology.core.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class AxiologyBasePacket<T extends AxiologyBasePacket<T>> {
    public abstract void encode(FriendlyByteBuf byteBuf);

    public abstract void handle(Supplier<NetworkEvent.Context> context);

    public abstract T decode(FriendlyByteBuf buffer);
}
