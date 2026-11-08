package eng.cad.auto.axiology.core.network.packet;

import eng.cad.auto.axiology.client.render.BeamEffectRenderer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class KillBeamPacket extends AxiologyBasePacket<KillBeamPacket> {
    UUID id;

    public KillBeamPacket() {
        super();
    }

    public KillBeamPacket(UUID id) {
        this.id = id;
    }

    @Override
    public void encode(FriendlyByteBuf byteBuf) {
        byteBuf.writeUUID(id);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(
                        Dist.CLIENT,
                        () -> () -> {
                            BeamEffectRenderer.killBeam(id);
                        }));
        context.get().setPacketHandled(true);
    }

    @Override
    public KillBeamPacket decode(FriendlyByteBuf buffer) {
        return new KillBeamPacket(
                buffer.readUUID()
        );
    }
}
