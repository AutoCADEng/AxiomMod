package eng.cad.auto.axiom.registry;

import eng.cad.auto.axiom.AxiomMod;

import eng.cad.auto.axiom.common.fluid.MortisFluid;
import eng.cad.auto.axiom.common.util.FluidRenderProperties;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class FluidRegistry {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, AxiomMod.MODID);
    public static final DeferredRegister<FluidType> FLUIDTYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, AxiomMod.MODID);
    public static final RegistryObject<Fluid> MORTIS = registerFluid("mortis",
            MortisFluid.Source::new);
    public static final RegistryObject<Fluid> MORTIS_FLOWING = registerFluid("mortis_flowing",
            MortisFluid.Flowing::new);
    public static final RegistryObject<FluidType> MORTIS_TYPE = registerFluidType("mortis",
            standardProps(1000,1000), MortisFluid.renderProperties);


    private static RegistryObject<FluidType> registerFluidType(String name, FluidType.Properties props, FluidRenderProperties renderProperties) {
        return FLUIDTYPES.register(name, () -> new FluidType(props){
            @Override
            public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                consumer.accept(renderProperties);
            }
        });
    }

    private static FluidType.Properties standardProps(int density, int viscosity) {
        return FluidType.Properties.create()
                .density(density)
                .viscosity(viscosity)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH);
    }

    public static <T extends Fluid> RegistryObject<T> registerFluid(String name, Supplier<T> fluid){
        return FLUIDS.register(name, fluid);
    }

}
