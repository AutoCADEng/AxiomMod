package eng.cad.auto.axiology.modulartools.abilities.enchants;

import eng.cad.auto.axiology.modulartools.components.IModularEnchantment;
import eng.cad.auto.axiology.modulartools.components.ModularToolAugment;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class FortuneAugment extends ModularToolAugment implements IModularEnchantment {

    @Override
    public Component getDisplayName() {
        return Component.translatable("axiology.modulartools.fortune");
    }

    @Override
    public List<ItemEnchantment> getEnchantments() {
        ItemEnchantment enchantment = new ItemEnchantment(ForgeRegistries.ENCHANTMENTS.getValue(ResourceLocation.parse("minecraft:fortune")), 1);
        return List.of(enchantment);
    }

    @Override
    public boolean isCompatible(ModularToolAugment ability) {
        return !(ability instanceof SilktouchAugment);
    }

    @Override
    public int getCost() {
        return 3;
    }
}
