package eng.cad.auto.axiology.modulartools.abilities.custom;

import eng.cad.auto.axiology.modulartools.components.IModularEnchantment;
import eng.cad.auto.axiology.modulartools.components.ModularToolAugment;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.HashSet;
import java.util.function.Supplier;

public class EnchantmentAugment extends ModularToolAugment implements IModularEnchantment{
    HashSet<Enchantment> enchantments = new HashSet<>();
    int cost = 1;
    int maxStack = 9;
    Component displayName = Component.empty();
    ChatFormatting color = ChatFormatting.WHITE;
    HashSet<ModularToolAugment> incompatibleWith = new HashSet<>();

    public EnchantmentAugment(Component component){
        displayName = component;
    }
    @Override
    public Component getDisplayName() {
        return displayName;
    }

    //~~I am having a bout of laziness.~~ Nevermind, I changed it anyway :(
    @Override
    public HashSet<Enchantment> getEnchantments() {
        return enchantments;
    }

    public EnchantmentAugment addEnchantment(Enchantment enchantment){
        enchantments.add(enchantment);
        return this;
    }

    public EnchantmentAugment setCost(int cost){
        this.cost = cost;
        return this;
    }

    public EnchantmentAugment setMaxStack(int max){
        this.maxStack = max;
        return this;
    }
    public EnchantmentAugment incompatible(Supplier<ModularToolAugment> augment){
        incompatibleWith.add(augment.get());
        return this;
    }
    public EnchantmentAugment setColor(ChatFormatting color){
        this.color = color;
        return this;
    }

    @Override
    public int maxStackable() {
        return maxStack;
    }

    @Override
    public boolean isCompatible(ModularToolAugment ability) {
        return incompatibleWith.stream().anyMatch(x -> x.equals(ability));
    }

    @Override
    public ChatFormatting getColor() {
        return color;
    }

    @Override
    public int getCost() {
        return cost;
    }


}
