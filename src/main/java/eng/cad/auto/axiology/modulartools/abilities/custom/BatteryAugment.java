package eng.cad.auto.axiology.modulartools.abilities.custom;

import eng.cad.auto.axiology.core.fluid.FluidStackHandler;
import eng.cad.auto.axiology.core.slot.SlotConfig;
import eng.cad.auto.axiology.core.slot.SlotIORestriction;
import eng.cad.auto.axiology.modulartools.components.IModularBattery;
import eng.cad.auto.axiology.modulartools.components.IModularFluidInventory;
import eng.cad.auto.axiology.modulartools.components.ModularToolAbility;
import net.minecraft.network.chat.Component;
import net.minecraftforge.energy.EnergyStorage;

public class BatteryAugment extends ModularToolAbility implements IModularBattery {
    EnergyStorage battery = new EnergyStorage(100000);
    @Override
    public Component getDisplayName() {
        return Component.translatable("axiology.modulartools.battery");
    }

    @Override
    public int maxStackable() {
        return 1;
    }

    @Override
    public EnergyStorage getBattery() {
        return battery;
    }
}
