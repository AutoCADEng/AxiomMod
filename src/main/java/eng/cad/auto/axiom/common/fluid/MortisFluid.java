package eng.cad.auto.axiom.common.fluid;


import eng.cad.auto.axiom.common.util.FluidRenderProperties;
import eng.cad.auto.axiom.registry.BlockRegistry;
import eng.cad.auto.axiom.registry.FluidRegistry;

import eng.cad.auto.axiom.registry.ItemRegistry;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class MortisFluid {
    public static FluidRenderProperties renderProperties = new FluidRenderProperties(0xAAAAAA);
    public MortisFluid(FluidType.Properties properties) {

    }

    private static ForgeFlowingFluid.Properties props() {
        return new ForgeFlowingFluid.Properties(
                FluidRegistry.MORTIS_TYPE, FluidRegistry.MORTIS, FluidRegistry.MORTIS_FLOWING
        ).block(BlockRegistry.MORTIS_FLUID_BLOCK).bucket(ItemRegistry.MORTIS_BUCKET).tickRate(2);
    }

    public static class Source extends ForgeFlowingFluid.Source {
        public Source() {
            super(props());
        }
    }
    public static class Flowing extends ForgeFlowingFluid.Flowing {
        public Flowing() {
            super(props());
        }
    }
}
