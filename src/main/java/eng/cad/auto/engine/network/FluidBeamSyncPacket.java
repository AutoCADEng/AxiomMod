package eng.cad.auto.engine.network;

import eng.cad.auto.axiology.client.effects.FluidBeamEffect;
import eng.cad.auto.axiology.registry.render.BeamRegistry;
import eng.cad.auto.engine.network.packet.AbstractBeamPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

public class FluidBeamSyncPacket extends AbstractBeamPacket<FluidBeamSyncPacket, FluidBeamEffect> {

    public FluidBeamSyncPacket() {
        super();
    }

    public FluidBeamSyncPacket(ResourceLocation texture, int tint, Vector3f castFrom, Vector3f castTo, Level level) {
        super(castFrom, castTo, texture, tint, level);
    }

    @Override
    public FluidBeamEffect getBeam() {
        return BeamRegistry.FLUID_RAY.get();
    }

    @Override
    public FluidBeamSyncPacket decode(FriendlyByteBuf buffer) {
        return new FluidBeamSyncPacket(
                buffer.readResourceLocation(),
                buffer.readVarInt(),
                buffer.readVector3f(),
                buffer.readVector3f(),
                Minecraft.getInstance().level
        );
    }
}
