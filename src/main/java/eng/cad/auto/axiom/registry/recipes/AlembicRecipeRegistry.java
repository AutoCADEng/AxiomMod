package eng.cad.auto.axiom.registry.recipes;

import eng.cad.auto.axiom.AxiomMod;
import eng.cad.auto.axiom.recipes.AlembicRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.*;

public class AlembicRecipeRegistry {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZER = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, AxiomMod.MODID);
    public static final RegistryObject<RecipeSerializer<AlembicRecipe>> ALEMBIC_RECIPE_SERIALIZER = SERIALIZER.register(
            "alembic_recipe", () -> AlembicRecipe.Serializer.INSTANCE
    );
    public static boolean contains(ItemStack targetItem) {
        return false;
    }

}
