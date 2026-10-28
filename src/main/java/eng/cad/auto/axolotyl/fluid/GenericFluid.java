package eng.cad.auto.axolotyl.fluid;

import eng.cad.auto.axolotyl.fluid.client.FluidRenderProperties;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import java.util.function.Supplier;

public class GenericFluid {

    public GenericFluid() {
    }

    public static FluidRenderProperties getFluidRenderer(int color) {
        return new FluidRenderProperties(color);
    }


    public static class Block extends LiquidBlock{
        public Block (Supplier<? extends FlowingFluid> fluid, Properties properties) {
            super(fluid, properties);
        }
    }

    public static class Source extends ForgeFlowingFluid.Source {
        public Source(ForgeFlowingFluid.Properties properties) {
            super(properties);
        }
    }

    public static class Flowing extends ForgeFlowingFluid.Flowing {
        public Flowing(ForgeFlowingFluid.Properties properties) {
            super(properties);
        }
    }
}
