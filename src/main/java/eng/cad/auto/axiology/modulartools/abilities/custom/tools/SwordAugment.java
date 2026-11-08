package eng.cad.auto.axiology.modulartools.abilities.custom.tools;

import eng.cad.auto.axiology.modulartools.components.tools.IModularSword;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;


public class SwordAugment extends AbstractToolAugment implements IModularSword {

    @Override
    public Component getDisplayName() {
        return Component.translatable("axiology.modulartools.pickaxe_expansion");
    }

    @Override
    public SwordItem getSword(ItemStack stackInHand) {
        return new SwordItem(getMaterial(stackInHand), 0, 0, new Item.Properties());
    }
}
