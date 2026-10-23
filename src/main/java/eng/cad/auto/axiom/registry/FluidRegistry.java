package eng.cad.auto.axiom.registry;

import eng.cad.auto.axiom.AxiomMod;

import eng.cad.auto.axiom.common.fluid.FluidHolder;
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
