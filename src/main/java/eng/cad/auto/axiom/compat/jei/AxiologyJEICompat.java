package eng.cad.auto.axiom.compat.jei;

import eng.cad.auto.axiom.AxiomMod;
import eng.cad.auto.axiom.recipes.AlembicRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

@JeiPlugin
public class AxiologyJEICompat implements IModPlugin {
    private static IJeiRuntime jeiRuntime;
    public static final RecipeType<AlembicRecipe> ALEMBIC_RECIPE_TYPE = new RecipeType<>(
            ResourceLocation.fromNamespaceAndPath(AxiomMod.MODID,"alembic_recipe"),
            AlembicRecipe.class
    );

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(AxiomMod.MODID, "axiom_jei");
    }
    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
                new AlembicRecipeCategory(
                        registration.getJeiHelpers().getGuiHelper()
                )
        );
    }
    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        // JEI calls this when the game finishes loading
        jeiRuntime = runtime;
    }

    public static IJeiRuntime getJeiRuntime() {
        return jeiRuntime;
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.level == null) {
            AxiomMod.LOGGER.warn(
                    "Cannot register Alembic recipes: client level is null"
            );
            return;
        }

        List<AlembicRecipe> recipes = minecraft.level
                .getRecipeManager()
                .getAllRecipesFor(AlembicRecipe.Type.INSTANCE);

        AxiomMod.LOGGER.info(
                "Found {} Alembic recipes for JEI",
                recipes.size()
        );

        registration.addRecipes(ALEMBIC_RECIPE_TYPE, recipes);
    }
}
