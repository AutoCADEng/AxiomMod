package eng.cad.auto.axiom.registry;

import eng.cad.auto.axiom.AxiomMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;

public class TagRegistry {
    public static final TagKey<Block> HOT_BLOCKS =
            TagKey.create(
                    Registries.BLOCK,
                    ResourceLocation.fromNamespaceAndPath(AxiomMod.MODID, "hot_blocks")
            );
    public static final TagKey<Fluid> HOT_FLUIDS =
            TagKey.create(
                    Registries.FLUID,
                    ResourceLocation.fromNamespaceAndPath(AxiomMod.MODID, "hot_fluids")
            );
    public static void register(IEventBus modEventBus){
        modEventBus.register(HOT_BLOCKS);
        modEventBus.register(HOT_FLUIDS);
    }
}
