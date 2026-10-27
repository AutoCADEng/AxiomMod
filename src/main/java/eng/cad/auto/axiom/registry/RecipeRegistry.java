package eng.cad.auto.axiom.registry;

import eng.cad.auto.axiom.AxiomMod;

import eng.cad.auto.axiom.common.crafting.core.GenericRecipe;
import io.netty.util.Attribute;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RecipeRegistry {

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, AxiomMod.MODID);

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, AxiomMod.MODID);

    public static final RegistryObject<RecipeSerializer<GenericRecipe>> GENERIC_SERIALIZER = RECIPE_SERIALIZERS.register("generic", () -> GenericRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeType<GenericRecipe>> FERMENTATION =
            RecipeRegistry.RECIPE_TYPES.register("fermenter", GenericRecipe.Type::new);
    public static final RegistryObject<RecipeType<GenericRecipe>> SOAKING_DRUM =
            RecipeRegistry.RECIPE_TYPES.register("soaking_drum", GenericRecipe.Type::new);
    public static RegistryObject<RecipeType<GenericRecipe>> ALEMBIC =
            RecipeRegistry.RECIPE_TYPES.register("alembic", GenericRecipe.Type::new);
    public static RegistryObject<RecipeType<GenericRecipe>> CHEMICAL_REACTOR =
            RecipeRegistry.RECIPE_TYPES.register("chemical_reactor", GenericRecipe.Type::new);
}
