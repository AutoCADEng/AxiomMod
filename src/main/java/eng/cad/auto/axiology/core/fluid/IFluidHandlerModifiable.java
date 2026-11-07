package eng.cad.auto.axiology.core.fluid;

import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public interface IFluidHandlerModifiable {
    void setStackInSlot(int slot, @NotNull FluidStack stack);
}
