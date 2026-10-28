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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Supplier;

public class FluidRegistry {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, AxiomMod.MODID);
    public static final DeferredRegister<FluidType> FLUIDTYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, AxiomMod.MODID);
    public static final HashMap<String, RegistryObject<? extends Fluid>> FLUIDLIST = new HashMap<>();
    public static final HashMap<String, FluidHolder> Fluid_Holders = new HashMap<>();

    public static void init(){
        Fluid_Holders.put(Ethanol.Dilute.name, new FluidHolder(Ethanol.Dilute.name, Ethanol.Dilute.color, MapColor.COLOR_BLACK, null));
        Fluid_Holders.put(Ethanol.name, new FluidHolder(Ethanol.name, Ethanol.color, MapColor.COLOR_BLACK, null));

        Fluid_Holders.put(NitricAcid.Dilute.name, new FluidHolder(NitricAcid.Dilute.name, NitricAcid.Dilute.color, MapColor.COLOR_BLACK, null));
        Fluid_Holders.put(NitricAcid.name, new FluidHolder(NitricAcid.name, NitricAcid.color, MapColor.COLOR_BLACK, null));
        Fluid_Holders.put(NitricAcid.Nital.name, new FluidHolder(NitricAcid.Nital.name, NitricAcid.Nital.color, MapColor.COLOR_BLACK, null));

        Fluid_Holders.put(Deathwater.name, new FluidHolder(Deathwater.name, Deathwater.color, MapColor.COLOR_BLACK, new Deathwater.FluidBehaviour()));
        Fluid_Holders.put(Blazewater.name, new FluidHolder(Blazewater.name, Blazewater.color, MapColor.COLOR_BLACK, new Blazewater.FluidBehaviour()));
        Fluid_Holders.put(Fluxfluid.name, new FluidHolder(Fluxfluid.name, Fluxfluid.color, MapColor.COLOR_BLACK, new Fluxfluid.FluidBehaviour()));
    }


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
