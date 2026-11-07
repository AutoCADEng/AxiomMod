package eng.cad.auto.axiology.modulartools.abilities.enchants;

import eng.cad.auto.axiology.modulartools.ItemEnchantment;
import eng.cad.auto.axiology.modulartools.components.IModularEnchantment;
import eng.cad.auto.axiology.modulartools.components.ModularToolAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class SilktouchAugment extends ModularToolAbility implements IModularEnchantment {

    @Override
    public Component getDisplayName() {
        return Component.translatable("axiology.modulartools.silk_touch");
    }

    @Override
    public List<ItemEnchantment> getEnchantments() {
        ItemEnchantment enchantment = new ItemEnchantment(ForgeRegistries.ENCHANTMENTS.getValue(ResourceLocation.parse("minecraft:silk_touch")),1);
        return List.of(enchantment);
    }

    @Override
    public boolean isCompatible(ModularToolAbility ability) {
        if (ability instanceof FortuneAugment) return false;
        return true;
    }
}
