package eng.cad.auto.axiology.registry;

import eng.cad.auto.axiology.AxiomMod;

import eng.cad.auto.axiology.crafting.machines.InfusionRecipe;
import eng.cad.auto.axiology.crafting.machines.*;
import eng.cad.auto.axiology.machines.*;
import eng.cad.auto.axiology.machines.multiblock.infusion.ArcaneImbuer;
import eng.cad.auto.axiology.machines.multiblock.Pyrolyzer;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RecipeRegistry {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, AxiomMod.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, AxiomMod.MODID);

    public static final RegistryObject<RecipeType<FermenterRecipe>> FERMENTATION = RecipeRegistry.RECIPE_TYPES.register(Fermenter.name, () -> FermenterRecipe.FermenterType.INSTANCE);
    public static final RegistryObject<RecipeSerializer<FermenterRecipe>> FERMENTATION_SER = RecipeRegistry.RECIPE_SERIALIZERS.register(Fermenter.name, () -> FermenterRecipe.FermenterSerializer.INSTANCE);

    public static final RegistryObject<RecipeType<SoakerRecipe>> SOAKING_DRUM = RecipeRegistry.RECIPE_TYPES.register(SoakingDrum.name, () -> SoakerRecipe.SoakerType.INSTANCE);
    public static final RegistryObject<RecipeSerializer<SoakerRecipe>> SOAKING_DRUM_SER = RecipeRegistry.RECIPE_SERIALIZERS.register(SoakingDrum.name, () -> SoakerRecipe.SoakerSerializer.INSTANCE);

    public static final RegistryObject<RecipeType<AlembicRecipe>> ALEMBIC = RecipeRegistry.RECIPE_TYPES.register(Alembic.name, () -> AlembicRecipe.AlembicType.INSTANCE);
    public static final RegistryObject<RecipeSerializer<AlembicRecipe>> ALEMBIC_SER = RecipeRegistry.RECIPE_SERIALIZERS.register(Alembic.name, () -> AlembicRecipe.AlembicSerializer.INSTANCE);

    public static final RegistryObject<RecipeType<ChemicalReactorRecipe>> CHEMICAL_REACTOR = RecipeRegistry.RECIPE_TYPES.register(ChemicalReactor.name, () -> ChemicalReactorRecipe.ChemicalReactorType.INSTANCE);
    public static final RegistryObject<RecipeSerializer<ChemicalReactorRecipe>> CHEMICAL_REACTOR_SER = RecipeRegistry.RECIPE_SERIALIZERS.register(ChemicalReactor.name, () -> ChemicalReactorRecipe.ChemicalReactorSerializer.INSTANCE);

    public static final RegistryObject<RecipeType<PyrolyzerRecipe>> PYROLYZER = RecipeRegistry.RECIPE_TYPES.register(Pyrolyzer.name, () -> PyrolyzerRecipe.PyrolyzerType.INSTANCE);
    public static final RegistryObject<RecipeSerializer<PyrolyzerRecipe>> PYROLYZER_SER = RecipeRegistry.RECIPE_SERIALIZERS.register(Pyrolyzer.name, () -> PyrolyzerRecipe.PyrolyzerSerializer.INSTANCE);

    public static final RegistryObject<RecipeType<InfusionRecipe>> INFUSION = RecipeRegistry.RECIPE_TYPES.register(ArcaneImbuer.name, () -> InfusionRecipe.InfusionType.INSTANCE);
    public static final RegistryObject<RecipeSerializer<InfusionRecipe>> INFUSION_SER = RecipeRegistry.RECIPE_SERIALIZERS.register(ArcaneImbuer.name, () -> InfusionRecipe.InfusionSerializer.INSTANCE);
}
