package eng.cad.auto.axiology.modulartools.abilities.custom.tools;

import eng.cad.auto.axiology.modulartools.components.tools.IModularShovel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;

public class ShovelAugment extends AbstractToolAugment implements IModularShovel {

    @Override
    public Component getDisplayName() { return Component.translatable("axiology.modulartools.hoe_expansion"); }

    @Override
    public ShovelItem getShovel(ItemStack stackInHand) {
        return new ShovelItem(getMaterial(stackInHand), 0, 0, new Item.Properties());
    }
}
