package eng.cad.auto.axiology.modulartools.abilities.custom;

import eng.cad.auto.axiology.core.fluid.FluidStackHandler;
import eng.cad.auto.axiology.core.slot.SlotConfig;
import eng.cad.auto.axiology.core.slot.SlotIORestriction;
import eng.cad.auto.axiology.modulartools.components.IModularFluidInventory;
import eng.cad.auto.axiology.modulartools.components.ModularToolAugment;
import net.minecraft.network.chat.Component;

public class TankAugment extends ModularToolAugment implements IModularFluidInventory {
    FluidStackHandler fluidInventory = new FluidStackHandler(new SlotConfig[]{new SlotConfig(SlotIORestriction.IN_OUT, 8000)}, this::fluidChanged);

    @Override
    public Component getDisplayName() {
        return Component.translatable("axiology.modulartools.fluid_tank");
    }

    @Override
    public int maxStackable() {
        return 1;
    }

    @Override
    public FluidStackHandler getTanks() {
        return fluidInventory;
    }

    @Override
    public void fluidChanged(Integer integer) {
        // not sure yet!
    }
}
