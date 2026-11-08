package eng.cad.auto.axiology.modulartools.abilities.enchants;

import eng.cad.auto.axiology.modulartools.components.IModularEnchantment;
import eng.cad.auto.axiology.modulartools.components.ModularToolAugment;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class UnbreakingAugment extends ModularToolAugment implements IModularEnchantment {
    @Override
    public Component getDisplayName() {
        return Component.translatable("axiology.modulartools.unbreaking");
    }

    @Override
    public List<ItemEnchantment> getEnchantments() {
        ItemEnchantment enchantment = new ItemEnchantment(ForgeRegistries.ENCHANTMENTS.getValue(ResourceLocation.parse("minecraft:unbreaking")), 1);
        return List.of(enchantment);
    }
}
