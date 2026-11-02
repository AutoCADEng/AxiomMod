package eng.cad.auto.axiology.client.effects;

import eng.cad.auto.engine.client.render.AbstractBeamEffect;
import eng.cad.auto.engine.client.render.Wave;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import javax.annotation.Nullable;

public class InfusionBeamEffect extends AbstractBeamEffect<InfusionBeamEffect>{

    @Override
    public InfusionBeamEffect createBeam(@Nullable ResourceLocation texture, Vector3f castFrom, Vector3f castTo, Level level) {
        return new InfusionBeamEffect()
            .addPos(castFrom)
            .addPos(new Vec3(castFrom).add(0,1,0))
            .addPos(new Vec3(castTo).add(0,15,0))
            .addPos(castTo)
            .setWidth(0.1f)
            .setTint(0xFFFFFFFF)
            .minSegments(10)
            .maxSegments(300)
            .setLifespan(200)
            .setSpeed(0.005f)
            .setTime(level.getGameTime())
            .addWave(new Wave()
                .setAmplitude(0.35f)
                .setWavelength(0.7f)
                .setOffset(0.0f))
            .addWave(new Wave()
                    .setAmplitude(0.09f)
                    .setWavelength(0.33f)
                    .setOffset(0.1f))
            .addWave(new Wave()
                    .setAmplitude(0.05f)
                    .setWavelength(0.44f)
                    .setOffset(1.0f));
    }

}
