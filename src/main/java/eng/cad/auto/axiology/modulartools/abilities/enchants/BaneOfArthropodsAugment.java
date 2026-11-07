package eng.cad.auto.axiology.modulartools.abilities.enchants;

import eng.cad.auto.axiology.modulartools.ItemEnchantment;
import eng.cad.auto.axiology.modulartools.components.IModularEnchantment;
import eng.cad.auto.axiology.modulartools.components.ModularToolAugment;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class BaneOfArthropodsAugment extends ModularToolAugment implements IModularEnchantment {

    @Override
    public Component getDisplayName() {
        return Component.translatable("axiology.modulartools.arthropods");
    }

    @Override
    public List<ItemEnchantment> getEnchantments() {
        ItemEnchantment enchantment = new ItemEnchantment(ForgeRegistries.ENCHANTMENTS.getValue(ResourceLocation.parse("minecraft:bane_of_arthropods")),1);
        return List.of(enchantment);
    }

    @Override
    public boolean isCompatible(ModularToolAugment ability) {
        if (ability instanceof SharpnessAugment) return false;
        if (ability instanceof SmiteAugment) return false;
        return true;
    }
}
