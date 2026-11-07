package eng.cad.auto.axiology.core.item;

import eng.cad.auto.axiology.registry.ItemRegistry;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;

public class ToolTiers {
    public static final Tier CURSED = new ForgeTier(3, 1000, 10f, 5.0f, 15, BlockTags.NEEDS_DIAMOND_TOOL, () -> Ingredient.of(ItemRegistry.CURSED_INGOT.get()));
    public static final Tier FLUXED = new ForgeTier(3, 1000, 15f, 3.0f, 30, BlockTags.NEEDS_DIAMOND_TOOL, () -> Ingredient.of(ItemRegistry.FLUXED_INGOT.get()));
    public static final Tier BLAZING = new ForgeTier(3, 1000, 10f, 4.0f, 20, BlockTags.NEEDS_DIAMOND_TOOL, () -> Ingredient.of(ItemRegistry.BLAZING_INGOT.get()));
    public static final Tier ETCHED = new ForgeTier(2, 312, 7.5f, 2.5f, 17, BlockTags.NEEDS_IRON_TOOL, () -> Ingredient.of(ItemRegistry.ETCHED_IRON_INGOT.get()));
}