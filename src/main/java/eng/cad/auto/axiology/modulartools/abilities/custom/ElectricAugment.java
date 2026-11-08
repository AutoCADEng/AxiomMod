package eng.cad.auto.axiology.modulartools.abilities.custom;

import eng.cad.auto.axiology.modulartools.components.IModularEnergyInventory;
import eng.cad.auto.axiology.modulartools.components.IModularDurability;
import eng.cad.auto.axiology.modulartools.components.IModularTool;
import eng.cad.auto.axiology.modulartools.components.ModularToolAugment;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class ElectricAugment extends ModularToolAugment implements IModularDurability {
    @Override
    public Component getDisplayName() {
        return Component.translatable("axiology.modulartools.fuel.electric");
    }

    @Override
    public int getDurability(ItemStack stack) {
        return IModularTool.getAugments(stack).keySet().stream()
            .filter(key-> key instanceof IModularEnergyInventory battery)
            .map(key-> (IModularEnergyInventory) key)
            .mapToInt(battery-> battery.getBattery().getEnergyStored())
            .sum();
    }
}
