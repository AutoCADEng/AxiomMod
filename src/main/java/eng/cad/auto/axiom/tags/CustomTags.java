package eng.cad.auto.axiom.tags;

import eng.cad.auto.axiom.AxiomMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class CustomTags {
    public static class Blocks{
            public static final TagKey<Block> ELDERITCH_BLOCK = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(AxiomMod.MODID, "elderitch_tag"));
    }
}
