package eng.cad.auto.axiology.registry;

import eng.cad.auto.axiology.AxiomMod;

import eng.cad.auto.axiology.machines.*;
import eng.cad.auto.axiology.machines.multiblock.Pyrolyzer;
import eng.cad.auto.engine.crafting.core.GenericRecipe;
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
            RecipeRegistry.RECIPE_TYPES.register(Fermenter.name, GenericRecipe.Type::new);
    public static final RegistryObject<RecipeType<GenericRecipe>> SOAKING_DRUM =
            RecipeRegistry.RECIPE_TYPES.register(SoakingDrum.name, GenericRecipe.Type::new);
    public static RegistryObject<RecipeType<GenericRecipe>> ALEMBIC =
            RecipeRegistry.RECIPE_TYPES.register(Alembic.name, GenericRecipe.Type::new);
    public static RegistryObject<RecipeType<GenericRecipe>> CHEMICAL_REACTOR =
            RecipeRegistry.RECIPE_TYPES.register(ChemicalReactor.name, GenericRecipe.Type::new);
    public static RegistryObject<RecipeType<GenericRecipe>> PYROLYZER =
            RecipeRegistry.RECIPE_TYPES.register(Pyrolyzer.name, GenericRecipe.Type::new);
    public static RegistryObject<RecipeType<GenericRecipe>> INFUSION =
            RecipeRegistry.RECIPE_TYPES.register(ArcaneImbuer.name, GenericRecipe.Type::new);
}
