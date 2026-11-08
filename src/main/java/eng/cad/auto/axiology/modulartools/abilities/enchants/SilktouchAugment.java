package eng.cad.auto.axiology.modulartools.abilities.enchants;

import eng.cad.auto.axiology.modulartools.ItemEnchantment;
import eng.cad.auto.axiology.modulartools.components.IModularEnchantment;
import eng.cad.auto.axiology.modulartools.components.ModularToolAugment;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class SilktouchAugment extends ModularToolAugment implements IModularEnchantment {

    @Override
    public Component getDisplayName() {
        return Component.translatable("axiology.modulartools.silk_touch");
    }

    @Override
    public List<ItemEnchantment> getEnchantments() {
        ItemEnchantment enchantment = new ItemEnchantment(ForgeRegistries.ENCHANTMENTS.getValue(ResourceLocation.parse("minecraft:silk_touch")), 1);
        return List.of(enchantment);
    }

    @Override
    public boolean isCompatible(ModularToolAugment ability) {
        return !(ability instanceof FortuneAugment);
    }

    @Override
    public int maxStackable() {
        return 1;
    }

    @Override
    public int getCost() {
        return 8;
    }
}
