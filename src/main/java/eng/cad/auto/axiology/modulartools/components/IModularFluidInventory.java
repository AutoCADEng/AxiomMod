package eng.cad.auto.axiology.modulartools.components;

import eng.cad.auto.axiology.core.fluid.FluidStackHandler;
import net.minecraft.nbt.CompoundTag;

public interface IModularFluidInventory {
    FluidStackHandler getTanks();

    default void saveInventory(CompoundTag tag){
            tag.put("Tanks", getTanks().serializeNBT());
    }

    default void loadInventory(CompoundTag tag) {
        if (tag.contains("Tanks")) getTanks().deserializeNBT(tag.getCompound("Tanks"));
    }

    void fluidChanged(Integer integer);
}
