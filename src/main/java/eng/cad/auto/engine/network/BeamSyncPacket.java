package eng.cad.auto.engine.network;

import eng.cad.auto.axiology.client.effects.InfusionBeamEffect;
import eng.cad.auto.axiology.registry.render.BeamRegistry;
import eng.cad.auto.engine.network.packet.AbstractBeamPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

public class BeamSyncPacket extends AbstractBeamPacket<BeamSyncPacket, InfusionBeamEffect> {

    public BeamSyncPacket() {
        super();
    }

    public BeamSyncPacket(ResourceLocation texture, int tint, Vector3f castFrom, Vector3f castTo, Level level) {
        super(castFrom, castTo, tint, level);
    }

    @Override
    public InfusionBeamEffect getBeam() {
        return BeamRegistry.INFUSION.get();
    }

    @Override
    public BeamSyncPacket decode(FriendlyByteBuf buffer) {
        return new BeamSyncPacket(
                buffer.readResourceLocation(),
                buffer.readVarInt(),
                buffer.readVector3f(),
                buffer.readVector3f(),
                Minecraft.getInstance().level
        );
    }
}
