package eng.cad.auto.axiology.core.network.packet;

import eng.cad.auto.axiology.client.effects.InfusionBeamEffect;
import eng.cad.auto.axiology.registry.render.BeamRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

import java.util.UUID;

public class InfusionSyncPacket extends AbstractBeamPacket<InfusionSyncPacket, InfusionBeamEffect> {

    public InfusionSyncPacket() {
        super();
    }

    public InfusionSyncPacket(UUID id, ResourceLocation texture, int tint, Vector3f castFrom, Vector3f castTo, Level level) {
        super(id, castFrom, castTo, texture, tint, level);
    }

    @Override
    public InfusionBeamEffect getBeam() {
        return BeamRegistry.INFUSION.get();
    }

    @Override
    public InfusionSyncPacket decode(FriendlyByteBuf buffer) {
        return new InfusionSyncPacket(
                buffer.readUUID(),
                buffer.readResourceLocation(),
                buffer.readVarInt(),
                buffer.readVector3f(),
                buffer.readVector3f(),
                Minecraft.getInstance().level
        );
    }
}
