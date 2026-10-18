package eng.cad.auto.axiom.recipes.api;

import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

public interface CustomAlchemyRecipe<T extends Container> extends Recipe<T> {
    CustomResult getOutput();
    Ingredient getInput();
}
