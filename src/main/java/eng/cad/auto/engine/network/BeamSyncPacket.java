package eng.cad.auto.engine.network;

import eng.cad.auto.engine.client.render.BeamEffect;
import eng.cad.auto.engine.client.render.BeamEffectRenderer;
import eng.cad.auto.engine.machine.core.GenericMachineEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Vector3f;

import java.util.function.Supplier;

public class BeamSyncPacket extends AxiologyPacket<BeamSyncPacket> {
    private final Vector3f castFrom;
    private final Vector3f castTo;
    private final ResourceLocation fluidResource;

    public BeamSyncPacket(){
        this.castFrom = new Vector3f(0, 0, 0);
        this.castTo = new Vector3f(0, 0, 0);
        this.fluidResource = null;
    };
    public BeamSyncPacket(Vector3f castFrom, Vector3f castTo, ResourceLocation fluidResource) {
        this.castFrom = castFrom;
        this.castTo = castTo;
        this.fluidResource = fluidResource;
    }
    public BeamSyncPacket(Vec3 castFrom, Vec3 castTo, ResourceLocation fluidResource) {
        this.castFrom = castFrom.toVector3f();
        this.castTo = castTo.toVector3f();
        this.fluidResource = fluidResource;
    }

    public void encode(FriendlyByteBuf byteBuf) {
        byteBuf.writeVector3f(castFrom);
        byteBuf.writeVector3f(castTo);
        byteBuf.writeResourceLocation(fluidResource);
    }
    @Override
    public BeamSyncPacket decode(FriendlyByteBuf byteBuf) {
        return new BeamSyncPacket(
            byteBuf.readVector3f(),
            byteBuf.readVector3f(),
            byteBuf.readResourceLocation()
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
                            BeamEffectRenderer.addBeam(
                                    new BeamEffect(level.getGameTime())
                                    .addPos(castFrom)
                                    .addPos(new Vec3(castFrom).add(0, 15, 0))
                                    .addPos(new Vec3(castTo).add(0, 3, 0))
                                    .addPos(castTo)
                                    .setTint(0xAAFFAAFF)
                                    .width(0.1f)
                                    .minSegments(10)
                                    .maxSegments(30)
                                    .setLifespan(200)
                                    .addWave(new BeamEffect.Wave()
                                            .setAmplitude(0.25f)
                                            .setWavelength(0.7f)
                                            .setOffset(0.0f))
                                    .addWave(new BeamEffect.Wave()
                                            .setAmplitude(0.10f)
                                            .setWavelength(0.33f)
                                            .setOffset(0.1f))
                                    .addWave(new BeamEffect.Wave()
                                            .setAmplitude(0.05f)
                                            .setWavelength(0.44f)
                                            .setOffset(1.0f))
                            );
                        }
                )
        );
        context.get().setPacketHandled(true);
    }
}
