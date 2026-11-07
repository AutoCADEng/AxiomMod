package eng.cad.auto.axiology.client.effects;

import com.mojang.blaze3d.platform.NativeImage;
import eng.cad.auto.axiology.client.render.AbstractBeamEffect;
import eng.cad.auto.axiology.client.render.Wave;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class FluidBeamEffect extends AbstractBeamEffect<FluidBeamEffect>{

    static final HashMap<ResourceLocation, Integer> FluidColorCache = new HashMap<>();
    @Override
    public FluidBeamEffect createBeam(ResourceLocation texture, int tint, Vector3f castFrom, Vector3f castTo, Level level, UUID id) {
        return new FluidBeamEffect()
                .setTexture(texture)
                .setUUID(id)
                .addPos(castFrom.add(0.1f,0,-0.1f))
                .addPos(castTo)
                .setWidth(0.1f)
                .minSegments(100)
                .maxSegments(300)
                .setLifespan(20)
                .setSpeed(0f)
                .setTime(level.getGameTime())
                .doFade()
                .addWave(new Wave()
                    .setAmplitude(0.03f)
                    .setWavelength(1f)
                    .setOffset(0.5f));
    }

    public FluidBeamEffect setTexture(ResourceLocation var){
        setTint(getAverageColour(var));
        return this;
    }

    private static int getAverageColour(ResourceLocation texture) {
        if (FluidColorCache.containsKey(texture)) return FluidColorCache.get(texture);

        Fluid fluid = ForgeRegistries.FLUIDS.getValue(texture);
        if (fluid == null || fluid == Fluids.EMPTY) return 0xFFFFFFFF;
        IClientFluidTypeExtensions ext = IClientFluidTypeExtensions.of(fluid);
        ResourceLocation still = ext.getStillTexture();

        NativeImage image = Minecraft.getInstance()
                .getResourceManager()
                .getResource(ResourceLocation.fromNamespaceAndPath(
                        still.getNamespace(), "textures/" + still.getPath() + ".png"))
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

        int tint = ext.getTintColor();
        if (((tint >> 24) & 0xFF) == 0) tint |= 0xFF000000;
        int a_tint = (int)(a / pixels) * ((tint >> 24) & 0xFF) / 255;
        int r_tint = (int)(r / pixels) * ((tint >> 16) & 0xFF) / 255;
        int g_tint = (int)(g / pixels) * ((tint >> 8)  & 0xFF) / 255;
        int b_tint = (int)(b / pixels) * ( tint & 0xFF) / 255;
        int color = (a_tint << 24) | (r_tint << 16) | (g_tint << 8) | b_tint;
        FluidColorCache.put(texture, color);
        return color;
    }

}
