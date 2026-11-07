package eng.cad.auto.axiology.modulartools.abilities.custom;

import eng.cad.auto.axiology.modulartools.ModularTool;
import eng.cad.auto.axiology.modulartools.components.IModularBattery;
import eng.cad.auto.axiology.modulartools.components.IModularDurability;
import eng.cad.auto.axiology.modulartools.components.ModularToolAugment;
import net.minecraft.network.chat.Component;

public class ElectricAugment extends ModularToolAugment implements IModularDurability {
    @Override
    public Component getDisplayName() {
        return Component.translatable("axiology.modulartools.fuel.electric");
    }

    @Override
    public int getDurability(ModularTool tool) {
        return tool.getModules().keySet().stream()
            .filter(key-> key instanceof IModularBattery battery)
            .map(key-> (IModularBattery) key)
            .mapToInt(battery-> battery.getBattery().getEnergyStored())
            .sum();
    }
}
