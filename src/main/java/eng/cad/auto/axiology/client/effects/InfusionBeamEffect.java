package eng.cad.auto.axiology.client.effects;

import com.mojang.blaze3d.platform.NativeImage;
import eng.cad.auto.axiology.client.render.AbstractBeamEffect;
import eng.cad.auto.axiology.client.render.Wave;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.UUID;

public class InfusionBeamEffect extends AbstractBeamEffect<InfusionBeamEffect> {
    static final HashMap<ResourceLocation, Integer> ItemColorCache = new HashMap<>();

    private static int getAverageColour(ResourceLocation texture) {
        if (ItemColorCache.containsKey(texture)) return ItemColorCache.get(texture);

        Item item = ForgeRegistries.ITEMS.getValue(texture);
        if (item == null || item == Items.AIR) return 0xAAFFFFFF;
        TextureAtlasSprite sprite = Minecraft.getInstance().getItemRenderer()
                .getItemModelShaper().getItemModel(item).getParticleIcon();
        NativeImage image = sprite.contents().getOriginalImage();
        long r = 0, g = 0, b = 0, a = 0;
        int pixels = image.getWidth() * image.getHeight();
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int color = image.getPixelRGBA(x, y);

                r += (color >> 0) & 0xFF;
                g += (color >> 8) & 0xFF;
                b += (color >> 16) & 0xFF;
                a += (color >> 24) & 0xFF;
            }
        }
        int color = 0xAA000000
                | ((int) (r / pixels) << 16)
                | ((int) (g / pixels) << 8)
                | (int) (b / pixels);
        ItemColorCache.put(texture, color);
        return color;
    }

    @Override
    public InfusionBeamEffect createBeam(ResourceLocation texture, int tint, Vector3f castFrom, Vector3f castTo, Level level, UUID id) {
        return new InfusionBeamEffect()
                .setUUID(id)
                .addPos(castFrom)
                .addPos(new Vec3(castFrom).add(0, 1, 0))
                .addPos(new Vec3(castTo).add(0, 5, 0))
                .addPos(castTo)
                .setWidth(1.5f)
                .setTexture(texture)
                .minSegments(10)
                .maxSegments(50)
                .setLifespan(9999)
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

    public InfusionBeamEffect setTexture(ResourceLocation var) {
        setTint(getAverageColour(var));
        return this;
    }
}
