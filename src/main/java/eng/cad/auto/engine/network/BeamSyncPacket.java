package eng.cad.auto.engine.network;

import eng.cad.auto.axiology.registry.render.BeamRegistry;
import eng.cad.auto.engine.client.render.AbstractBeamEffect;
import eng.cad.auto.engine.client.render.BeamEffectRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import org.joml.Vector3f;

import java.util.function.Supplier;

public class BeamSyncPacket extends AxiologyPacket<BeamSyncPacket> {
    private final Vector3f castFrom;
    private final Vector3f castTo;
    private final ResourceLocation fluidResource;
    private final ResourceLocation beamEffect;
    private final int tint;
    private AbstractBeamEffect beam;
    public BeamSyncPacket(){
        this.castFrom = new Vector3f(0, 0, 0);
        this.castTo = new Vector3f(0, 0, 0);
        this.fluidResource = null;
        this.beamEffect = null;
        this.tint = 0xFFFFFFFF;
        this.beam = null;
    };
    public BeamSyncPacket(int tint, Vector3f castFrom, Vector3f castTo, ResourceLocation fluidResource, ResourceLocation beamEffect, Level level) {
        this.castFrom = castFrom;
        this.castTo = castTo;
        this.fluidResource = fluidResource;
        this.beamEffect = beamEffect;
        this.tint = tint;
        beam = BeamRegistry.BEAMS.getEntries().stream()
                .filter(holder -> holder.getId().equals(beamEffect))
                .map(Supplier::get)
                .findFirst()
                .orElse(null);
    }
    public BeamSyncPacket(int tint, Vec3 castFrom, Vec3 castTo, ResourceLocation fluidResource, ResourceLocation beamEffect, Level level) {
       this(tint,castFrom.toVector3f(),castTo.toVector3f(), fluidResource, beamEffect, level);
    }


    public void encode(FriendlyByteBuf byteBuf) {
        byteBuf.writeVarInt(tint);
        byteBuf.writeVector3f(castFrom);
        byteBuf.writeVector3f(castTo);
        byteBuf.writeResourceLocation(fluidResource);
        byteBuf.writeResourceLocation(beamEffect);
    }
    @Override
    public BeamSyncPacket decode(FriendlyByteBuf byteBuf) {
        return new BeamSyncPacket(
            byteBuf.readVarInt(),
            byteBuf.readVector3f(),
            byteBuf.readVector3f(),
            byteBuf.readResourceLocation(),
            byteBuf.readResourceLocation(),
            Minecraft.getInstance().level
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
                            AbstractBeamEffect newBeam = beam.createBeam(castFrom, castTo, level);
                            if (newBeam == null) return;
                            BeamEffectRenderer.addBeam(newBeam);
                        }
                )
        );
        context.get().setPacketHandled(true);
    }
}
