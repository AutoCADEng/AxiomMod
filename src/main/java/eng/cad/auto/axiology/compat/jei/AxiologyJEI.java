package eng.cad.auto.axiology.compat.jei;

import eng.cad.auto.axiology.AxiomMod;
import eng.cad.auto.axiology.crafting.machines.AlembicRecipe;
import eng.cad.auto.axiology.crafting.machines.ChemicalReactorRecipe;
import eng.cad.auto.axiology.crafting.machines.FermenterRecipe;
import eng.cad.auto.axiology.crafting.machines.SoakerRecipe;
import eng.cad.auto.axiology.machines.ChemicalReactor;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class AxiologyJEI implements IModPlugin {
    private static IJeiRuntime jeiRuntime;
    public static final RecipeType<FermenterRecipe> FERMENTER_RECIPE_TYPE = new RecipeType<FermenterRecipe>(ResourceLocation.fromNamespaceAndPath(AxiomMod.MODID,"fermenter_recipe"), FermenterRecipe.class);
    public static final RecipeType<SoakerRecipe> SOAKER_RECIPE_TYPE = new RecipeType<SoakerRecipe>(ResourceLocation.fromNamespaceAndPath(AxiomMod.MODID,"soaker_recipe"), SoakerRecipe.class);
    public static final RecipeType<AlembicRecipe> ALEMBIC_RECIPE_TYPE = new RecipeType<AlembicRecipe>(ResourceLocation.fromNamespaceAndPath(AxiomMod.MODID,"alembic_recipe"), AlembicRecipe.class);
    public static final RecipeType<ChemicalReactorRecipe> CHEMICAL_REACTOR_RECIPE_TYPE = new RecipeType<ChemicalReactorRecipe>(ResourceLocation.fromNamespaceAndPath(AxiomMod.MODID,"chemical_reactor_recipe"), ChemicalReactorRecipe.class);



    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(AxiomMod.MODID, "axiom_jei_plugin");
    }
    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new FermenterRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new SoakingDrumRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new AlembicRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new ChemicalReactorRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }
    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        jeiRuntime = runtime;
    }

    public static IJeiRuntime getJeiRuntime() {
        return jeiRuntime;
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.level == null) {
            AxiomMod.LOGGER.warn("Cannot register recipes: client level is null");
            return;
        }

        registration.addRecipes(FERMENTER_RECIPE_TYPE, minecraft.level.getRecipeManager().getAllRecipesFor(FermenterRecipe.FermenterType.INSTANCE));
        registration.addRecipes(SOAKER_RECIPE_TYPE, minecraft.level.getRecipeManager().getAllRecipesFor(SoakerRecipe.SoakerType.INSTANCE));
        registration.addRecipes(ALEMBIC_RECIPE_TYPE, minecraft.level.getRecipeManager().getAllRecipesFor(AlembicRecipe.AlembicType.INSTANCE));
        registration.addRecipes(CHEMICAL_REACTOR_RECIPE_TYPE, minecraft.level.getRecipeManager().getAllRecipesFor(ChemicalReactorRecipe.ChemicalReactorType.INSTANCE));
    }
}