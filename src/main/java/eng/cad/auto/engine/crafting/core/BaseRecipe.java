package eng.cad.auto.engine.crafting.core;


import net.minecraft.core.NonNullList;
import net.minecraft.world.level.Level;

public abstract class BaseRecipe<C extends CustomCraftingContainer, T extends AbstractRecipe<C, T>>{
    public abstract boolean recipesContain(CraftingIngredient target, Level level);

    public abstract boolean matchesTypeAndQuantity(CustomCraftingContainer machineInventory, Level level);

    public abstract NonNullList<CraftingIngredient> getCraftingIngredients();

    public abstract NonNullList<CraftingIngredient> getCraftingOutput();
}
