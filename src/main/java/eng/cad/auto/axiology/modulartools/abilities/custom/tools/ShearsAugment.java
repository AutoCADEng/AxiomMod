package eng.cad.auto.axiology.modulartools.abilities.custom.tools;

import eng.cad.auto.axiology.modulartools.components.tools.IModularShear;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;

public class ShearsAugment extends AbstractToolAugment implements IModularShear {

    @Override
    public Component getDisplayName() {
        return Component.translatable("axiology.modulartools.shear_expansion");
    }

    @Override
    public ShearsItem getShear(ItemStack stackInHand) {
        return new ShearsItem(new Item.Properties());
    }
}
