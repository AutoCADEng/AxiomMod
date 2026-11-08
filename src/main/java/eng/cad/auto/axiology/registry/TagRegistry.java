package eng.cad.auto.axiology.registry;

import eng.cad.auto.axiology.AxiomMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
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
    public static final TagKey<Fluid> TOOL_FUEL_FLUIDS =
            TagKey.create(
                    Registries.FLUID,
                    ResourceLocation.fromNamespaceAndPath(AxiomMod.MODID, "tool_fuel_fluids")
            );
    public static final TagKey<Item> MODULAR_TOOL =
            TagKey.create(
                    Registries.ITEM,
                    ResourceLocation.fromNamespaceAndPath(AxiomMod.MODID, "modular_tool")
            );

    public static void register(IEventBus modEventBus) {
        modEventBus.register(HOT_BLOCKS);
        modEventBus.register(HOT_FLUIDS);
        modEventBus.register(TOOL_FUEL_FLUIDS);
        modEventBus.register(MODULAR_TOOL);
    }
}
