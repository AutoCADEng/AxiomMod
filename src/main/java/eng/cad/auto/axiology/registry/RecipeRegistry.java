package eng.cad.auto.axiology.registry;

import eng.cad.auto.axiology.AxiomMod;

import eng.cad.auto.axiology.crafting.GenericMachineRecipe;
import eng.cad.auto.axiology.machines.*;
import eng.cad.auto.axiology.machines.multiblock.Pyrolyzer;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RecipeRegistry {

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, AxiomMod.MODID);

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, AxiomMod.MODID);

    public static final RegistryObject<RecipeSerializer<GenericMachineRecipe>> GENERIC_SERIALIZER = RECIPE_SERIALIZERS.register("generic", () -> GenericMachineRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeType<GenericMachineRecipe>> FERMENTATION =
            RecipeRegistry.RECIPE_TYPES.register(Fermenter.name, GenericMachineRecipe.Type::new);
    public static final RegistryObject<RecipeType<GenericMachineRecipe>> SOAKING_DRUM =
            RecipeRegistry.RECIPE_TYPES.register(SoakingDrum.name, GenericMachineRecipe.Type::new);
    public static RegistryObject<RecipeType<GenericMachineRecipe>> ALEMBIC =
            RecipeRegistry.RECIPE_TYPES.register(Alembic.name, GenericMachineRecipe.Type::new);
    public static RegistryObject<RecipeType<GenericMachineRecipe>> CHEMICAL_REACTOR =
            RecipeRegistry.RECIPE_TYPES.register(ChemicalReactor.name, GenericMachineRecipe.Type::new);
    public static RegistryObject<RecipeType<GenericMachineRecipe>> PYROLYZER =
            RecipeRegistry.RECIPE_TYPES.register(Pyrolyzer.name, GenericMachineRecipe.Type::new);
    public static RegistryObject<RecipeType<GenericMachineRecipe>> INFUSION =
            RecipeRegistry.RECIPE_TYPES.register(ArcaneImbuer.name, GenericMachineRecipe.Type::new);
}
