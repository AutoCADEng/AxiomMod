package eng.cad.auto.axiology.client.effects;

import com.mojang.blaze3d.platform.NativeImage;
import eng.cad.auto.engine.client.render.AbstractBeamEffect;
import eng.cad.auto.engine.client.render.Wave;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.io.IOException;

public class FluidBeamEffect extends AbstractBeamEffect<FluidBeamEffect>{

    @Override
    public FluidBeamEffect createBeam(ResourceLocation texture, Vector3f castFrom, Vector3f castTo, Level level) {
        return new FluidBeamEffect()
            .setTexture(texture)
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

    public FluidBeamEffect setTexture(ResourceLocation var){
        setTint(getAverageColour(var));
        return this;
    }

    private static int getAverageColour(ResourceLocation texture) {
        NativeImage image = Minecraft.getInstance()
                .getResourceManager()
                .getResource(texture)
                .map(resource -> {
                    try {
                        return NativeImage.read(resource.open());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .orElse(null);

        if (image == null) return 0xFFFFFFFF;

        long r = 0, g = 0, b = 0, a = 0;
        int pixels = image.getWidth() * image.getHeight();

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int color = image.getPixelRGBA(x, y);

                r += (color >> 0)  & 0xFF;
                g += (color >> 8)  & 0xFF;
                b += (color >> 16) & 0xFF;
                a += (color >> 24) & 0xFF;
            }
        }

        image.close();

        return  ((int)(a / pixels) << 24)
                | ((int)(r / pixels) << 16)
                | ((int)(g / pixels) << 8)
                | (int)(b / pixels);
    }

}
