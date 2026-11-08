package eng.cad.auto.axiology.modulartools.abilities.custom.tools;

import eng.cad.auto.axiology.modulartools.components.ModularToolAugment;
import eng.cad.auto.axiology.modulartools.components.tools.IModularHoe;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class HoeAugment extends AbstractToolAugment implements IModularHoe {

    @Override
    public Component getDisplayName() {
        return Component.translatable("axiology.modulartools.hoe_expansion");
    }

    @Override
    public boolean isCompatible(ModularToolAugment ability) {
        if (ability instanceof ShovelAugment) return false;
        return true;
    }

    @Override
    public HoeItem getHoe(ItemStack stackInHand) {
        return new HoeItem(getMaterial(stackInHand), 0, 0, new Item.Properties());
    }
}
