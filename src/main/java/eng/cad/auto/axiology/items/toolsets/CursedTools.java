package eng.cad.auto.axiology.items.toolsets;

import eng.cad.auto.axiology.core.item.tools.*;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
/**
 * TODO MIGRATE THIS TO THE MODULAR SYSTEM AT SOME POINT
 */
public class CursedTools {
    // Effective Hit Points (Base Durability of 1000) (95CI) = (1731, 2269)
    public static int CHANCE = 10;
    public static int REPAIR_AMOUNT = 5;
    public static int DURATION = 5;
    public static int LEVEL = 1;
    public static String name = "cursed";

    public static void vampireEffect(Level level, ItemStack stack) {
        if (level.getRandom().nextIntBetweenInclusive(0, 100) > CHANCE) return;
        if (stack.getDamageValue() <= 0) return;
        stack.setDamageValue(Math.max(stack.getDamageValue() - REPAIR_AMOUNT, 0));
    }

    public static void cursedEffect(Level level, LivingEntity target) {
        target.addEffect(new MobEffectInstance(MobEffects.WITHER, DURATION, LEVEL));
    }

    public static void addVampireTooltip(List<Component> tooltip) {
        tooltip.add(Component.translatable("tooltip.axiology.cursed_tool.vampire", CHANCE, REPAIR_AMOUNT).withStyle(ChatFormatting.DARK_RED));
    }

    public static void addCursedTooltip(List<Component> tooltip) {
        tooltip.add(Component.translatable("tooltip.axiology.cursed_tool.curse", DURATION, LEVEL).withStyle(ChatFormatting.RED));
    }

    public static class Pickaxe extends CustomPickaxe {
        public Pickaxe(Tier tier, int attackDamageModifier, float attackSpeedModifier, Properties properties) {
            super(tier, attackDamageModifier, attackSpeedModifier, properties);
        }

        @Override
        public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
            vampireEffect(attacker.level(), stack);
            cursedEffect(attacker.level(), target);
            return super.hurtEnemy(stack, target, attacker);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
            CursedTools.addVampireTooltip(tooltip);
            CursedTools.addCursedTooltip(tooltip);
            super.appendHoverText(stack, level, tooltip, flag);
        }
    }

    public static class Axe extends CustomAxe {
        public Axe(Tier tier, int attackDamageModifier, float attackSpeedModifier, Properties properties) {
            super(tier, attackDamageModifier, attackSpeedModifier, properties);
        }

        @Override
        public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
            vampireEffect(attacker.level(), stack);
            cursedEffect(attacker.level(), target);
            return super.hurtEnemy(stack, target, attacker);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
            CursedTools.addVampireTooltip(tooltip);
            CursedTools.addCursedTooltip(tooltip);
            super.appendHoverText(stack, level, tooltip, flag);
        }
    }

    public static class Hoe extends CustomHoe {
        public Hoe(Tier tier, int attackDamageModifier, float attackSpeedModifier, Properties properties) {
            super(tier, attackDamageModifier, attackSpeedModifier, properties);
        }

        @Override
        public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
            vampireEffect(attacker.level(), stack);
            cursedEffect(attacker.level(), target);
            return super.hurtEnemy(stack, target, attacker);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
            CursedTools.addVampireTooltip(tooltip);
            CursedTools.addCursedTooltip(tooltip);
            super.appendHoverText(stack, level, tooltip, flag);
        }
    }

    public static class Sword extends CustomSword {
        public Sword(Tier tier, int attackDamageModifier, float attackSpeedModifier, Properties properties) {
            super(tier, attackDamageModifier, attackSpeedModifier, properties);
        }

        @Override
        public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
            vampireEffect(attacker.level(), stack);
            cursedEffect(attacker.level(), target);
            return super.hurtEnemy(stack, target, attacker);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
            CursedTools.addVampireTooltip(tooltip);
            CursedTools.addCursedTooltip(tooltip);
            super.appendHoverText(stack, level, tooltip, flag);
        }
    }

    public static class Shovel extends CustomShovel {
        public Shovel(Tier tier, int attackDamageModifier, float attackSpeedModifier, Properties properties) {
            super(tier, attackDamageModifier, attackSpeedModifier, properties);
        }

        @Override
        public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
            vampireEffect(attacker.level(), stack);
            cursedEffect(attacker.level(), target);
            return super.hurtEnemy(stack, target, attacker);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
            CursedTools.addVampireTooltip(tooltip);
            CursedTools.addCursedTooltip(tooltip);
            super.appendHoverText(stack, level, tooltip, flag);
        }
    }

    public static class Shears extends CustomShears {
        public Shears(Properties properties) {
            super(properties);
        }

        @Override
        public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
            vampireEffect(attacker.level(), stack);
            cursedEffect(attacker.level(), target);
            return super.hurtEnemy(stack, target, attacker);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
            CursedTools.addVampireTooltip(tooltip);
            CursedTools.addCursedTooltip(tooltip);
            super.appendHoverText(stack, level, tooltip, flag);
        }
    }
}
