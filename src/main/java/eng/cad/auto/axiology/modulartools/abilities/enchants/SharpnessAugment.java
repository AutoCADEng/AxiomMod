package eng.cad.auto.axiology.modulartools.abilities.enchants;

import eng.cad.auto.axiology.modulartools.ItemEnchantment;
import eng.cad.auto.axiology.modulartools.components.IModularEnchantment;
import eng.cad.auto.axiology.modulartools.components.ModularToolAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class SharpnessAugment extends ModularToolAbility implements IModularEnchantment {

    @Override
    public Component getDisplayName() {
        return Component.translatable("axiology.modulartools.sharpness");
    }

    @Override
    public List<ItemEnchantment> getEnchantments() {
        ItemEnchantment enchantment = new ItemEnchantment(ForgeRegistries.ENCHANTMENTS.getValue(ResourceLocation.parse("minecraft:sharpness")),1);
        return List.of(enchantment);
    }

    @Override
    public boolean isCompatible(ModularToolAbility ability) {
        if (ability instanceof SmiteAugment) return false;
        if (ability instanceof BaneOfArthropodsAugment) return false;
        return true;
    }
}
