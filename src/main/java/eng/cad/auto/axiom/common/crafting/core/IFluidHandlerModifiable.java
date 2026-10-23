package eng.cad.auto.axiom.common.crafting.core;

import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public interface IFluidHandlerModifiable {
    void setStackInSlot(int slot, @NotNull FluidStack stack);
}
