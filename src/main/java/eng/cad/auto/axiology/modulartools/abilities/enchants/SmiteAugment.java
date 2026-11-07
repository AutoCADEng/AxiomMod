package eng.cad.auto.axiology.modulartools.abilities.enchants;

import eng.cad.auto.axiology.modulartools.ItemEnchantment;
import eng.cad.auto.axiology.modulartools.components.IModularEnchantment;
import eng.cad.auto.axiology.modulartools.components.ModularToolAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class SmiteAugment extends ModularToolAbility implements IModularEnchantment {

    @Override
    public Component getDisplayName() {
        return Component.translatable("axiology.modulartools.smite");
    }

    @Override
    public List<ItemEnchantment> getEnchantments() {
        ItemEnchantment enchantment = new ItemEnchantment(ForgeRegistries.ENCHANTMENTS.getValue(ResourceLocation.parse("minecraft:smite")),1);
        return List.of(enchantment);
    }

    @Override
    public boolean isCompatible(ModularToolAbility ability) {
        if (ability instanceof SharpnessAugment) return false;
        if (ability instanceof BaneOfArthropodsAugment) return false;
        return true;
    }
}
