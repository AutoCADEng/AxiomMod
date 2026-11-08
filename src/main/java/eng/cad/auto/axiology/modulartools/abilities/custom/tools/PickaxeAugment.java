package eng.cad.auto.axiology.modulartools.abilities.custom.tools;

import eng.cad.auto.axiology.modulartools.components.tools.IModularPickaxe;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;


public class PickaxeAugment extends AbstractToolAugment implements IModularPickaxe {

    @Override
    public Component getDisplayName() {
        return Component.translatable("axiology.modulartools.pickaxe_expansion");
    }

    @Override
    public PickaxeItem getPickaxe(ItemStack stackInHand) {
        return new PickaxeItem(getMaterial(stackInHand), 0, 0, new Item.Properties());
    }
}
