package eng.cad.auto.axiology.core.network.packet;

import eng.cad.auto.axiology.client.render.AbstractBeamEffect;
import eng.cad.auto.axiology.client.render.BeamEffectRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import org.joml.Vector3f;

import java.util.UUID;
import java.util.function.Supplier;

public abstract class AbstractBeamPacket<T extends AbstractBeamPacket<T, B>, B extends AbstractBeamEffect<B>> extends AxiologyBasePacket<T> {

    private final Vector3f castFrom;
    private final Vector3f castTo;
    private final int tint;
    private final ResourceLocation texture;
    private B beam;
    private final Level level;
    private final UUID id;

    protected AbstractBeamPacket() {
        this(null, new Vector3f(), new Vector3f(), null, 0xFFFFFFFF, null);
    }

    protected AbstractBeamPacket(UUID id, Vec3 castFrom, Vec3 castTo, ResourceLocation texture, int tint, Level level) {
        this(id, castFrom.toVector3f(), castTo.toVector3f(), texture, tint, level);
    }

    protected AbstractBeamPacket(UUID id, Vector3f castFrom, Vector3f castTo, ResourceLocation texture, int tint, Level level) {
        this.castFrom = castFrom;
        this.castTo = castTo;
        this.tint = tint;
        this.level = level;
        this.texture = texture;
        this.id = id;
    }

    public AbstractBeamPacket(UUID id, Vector3f castFrom, Vector3f castTo, int tint, Level level) {
        this(id, castFrom, castTo, null, tint, level);
    }

    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }

    public abstract B getBeam();

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(id);
        buffer.writeResourceLocation(texture == null ? ResourceLocation.parse("minecraft:air") : texture);
        buffer.writeVarInt(tint);
        buffer.writeVector3f(castFrom);
        buffer.writeVector3f(castTo);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(
                        Dist.CLIENT,
                        () -> () -> {
                            Level level = Minecraft.getInstance().level;
                            if (level == null) return;

                            B newBeam = getBeam().createBeam(
                                    texture,
                                    tint,
                                    castFrom,
                                    castTo,
                                    level,
                                    id
                            );

                            if (newBeam == null) return;
                            BeamEffectRenderer.addBeam(newBeam);
                        }
                )
        );

        context.get().setPacketHandled(true);
    }

}
