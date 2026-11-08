package eng.cad.auto.axiology.modulartools.abilities.custom;

import eng.cad.auto.axiology.modulartools.components.IModularEnergyInventory;
import eng.cad.auto.axiology.modulartools.components.ModularToolAugment;
import net.minecraft.network.chat.Component;
import net.minecraftforge.energy.EnergyStorage;

public class BatteryAugment extends ModularToolAugment implements IModularEnergyInventory {
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
