package eng.cad.auto.axiology.modulartools.components;

import eng.cad.auto.axiology.modulartools.ItemEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;

public interface IModularEnchantment {
    List<ItemEnchantment> getEnchantments();
}
