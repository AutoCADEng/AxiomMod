package eng.cad.auto.engine.network;

import eng.cad.auto.axiology.client.effects.FluidBeamEffect;
import eng.cad.auto.axiology.registry.render.BeamRegistry;
import eng.cad.auto.engine.client.render.BeamEffectRenderer;
import eng.cad.auto.engine.network.packet.AbstractBeamPacket;
import eng.cad.auto.engine.network.packet.AxiologyBasePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import org.joml.Vector3f;

import java.util.UUID;
import java.util.function.Supplier;

public class KillBeamPacket extends AxiologyBasePacket<KillBeamPacket> {
    UUID id;
    public KillBeamPacket() {
        super();
    }

    public KillBeamPacket(UUID id){
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
