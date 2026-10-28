package eng.cad.auto.axiom.registry;

import eng.cad.auto.axiom.AxiomMod;

import eng.cad.auto.axolotyl.fluid.FluidHolder;
import eng.cad.auto.axiom.fluids.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.function.Supplier;

public class FluidRegistry {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, AxiomMod.MODID);
    public static final DeferredRegister<FluidType> FLUIDTYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, AxiomMod.MODID);
    public static final HashMap<String, RegistryObject<? extends Fluid>> FLUIDLIST = new HashMap<>();

    public static final FluidHolder DILUTE_ETHANOL = new FluidHolder(Ethanol.Dilute.name, Ethanol.Dilute.color, MapColor.COLOR_BLACK, null);
    public static final FluidHolder ETHANOL = new FluidHolder(Ethanol.name, Ethanol.color, MapColor.COLOR_BLACK, new Ethanol.FluidBehaviour());

    public static final FluidHolder DILUTE_NITRIC_ACID = new FluidHolder(NitricAcid.Dilute.name, NitricAcid.Dilute.color, MapColor.COLOR_BLACK, null);
    public static final FluidHolder NITRIC_ACID = new FluidHolder(NitricAcid.name, NitricAcid.color, MapColor.COLOR_BLACK, null);
    public static final FluidHolder NITAL = new FluidHolder(NitricAcid.Nital.name, NitricAcid.Nital.color, MapColor.COLOR_BLACK, null);

    public static final FluidHolder DEATHWATER = new FluidHolder("deathwater", Deathwater.color, MapColor.COLOR_BLACK, new Deathwater.FluidBehaviour());
    public static final FluidHolder BLAZEWATER = new FluidHolder("blazewater", Blazewater.color, MapColor.COLOR_BLACK, new Blazewater.FluidBehaviour());
    public static final FluidHolder FLUXFLUID = new FluidHolder("fluxfluid", Fluxfluid.color, MapColor.COLOR_BLACK, new Fluxfluid.FluidBehaviour());

    private void registerAxiomFluid(String name, int color){
        new FluidHolder(name, color, MapColor.COLOR_BLACK);
    }

    public static <T extends Fluid> RegistryObject<T> registerFluid(String name, Supplier<T> fluid){
        RegistryObject<T> newFluid = FLUIDS.register(name, fluid);
        FLUIDLIST.put(name, newFluid);
        return newFluid;
    }

    public static <T extends Block> RegistryObject<T> registerFluidBlock(String name, Supplier<T> block){
        RegistryObject<T> blockObject = BlockRegistry.BLOCKS.register(name, block);
        return blockObject;
    }

}
