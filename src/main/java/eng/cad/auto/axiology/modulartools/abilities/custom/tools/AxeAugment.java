package eng.cad.auto.axiology.modulartools.abilities.custom.tools;

import eng.cad.auto.axiology.modulartools.components.tools.IModularAxe;
import net.minecraft.network.chat.Component;

import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class AxeAugment extends AbstractToolAugment implements IModularAxe {

    @Override
    public Component getDisplayName() {
        return Component.translatable("axiology.modulartools.axe_expansion");
    }

    @Override
    public AxeItem getAxe(ItemStack stackInHand) {
        return new AxeItem(getMaterial(stackInHand), 0, 0, new Item.Properties());
    }
}
