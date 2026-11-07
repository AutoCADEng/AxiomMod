package eng.cad.auto.axiology.items.toolsets;

import eng.cad.auto.axiology.core.events.BlockDropEvent;
import eng.cad.auto.axiology.core.item.tools.*;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BlazingTools {
    public static String name = "blazing";
    public static int BURN_TIME = 5;

    public static void addSmeltingTooltip(List<Component> tooltip) {
        tooltip.add(Component.translatable("tooltip.axiology.blazing_tools.smelt").withStyle(ChatFormatting.RED));
    }
    public static void addBurningTooltip(List<Component> tooltip) {
        tooltip.add(Component.translatable("tooltip.axiology.blazing_tools.burn", BURN_TIME).withStyle(ChatFormatting.GOLD));
    }

    public static void onBlockDrop(BlockDropEvent event) {
        if (!(event.getEntity() instanceof ItemEntity drop)) return;
        if (drop.level().isClientSide) return;
        Player miner = event.getMiner();
        if ((!(miner.getMainHandItem().getItem() instanceof BlazingTools.Sword))
                && (!(miner.getMainHandItem().getItem() instanceof BlazingTools.Shears))
                && (!(miner.getMainHandItem().getItem() instanceof BlazingTools.Shovel))
                && (!(miner.getMainHandItem().getItem() instanceof BlazingTools.Hoe))
                && (!(miner.getMainHandItem().getItem() instanceof BlazingTools.Pickaxe))
                && (!(miner.getMainHandItem().getItem() instanceof BlazingTools.Axe))) return;
        ItemStack smeltedItem = smelt(event.getLevel(), drop.getItem());
        drop.setItem(smeltedItem);
    }
    public static void burnEffect(LivingEntity target){
        target.setSecondsOnFire(BURN_TIME);
    }
    private static ItemStack smelt(Level level, ItemStack stack) {
        Optional<SmeltingRecipe> recipe = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(stack), level);
        if (recipe.isEmpty()) return stack;
        ItemStack result = recipe.get().getResultItem(level.registryAccess()).copy();
        result.setCount(result.getCount() * stack.getCount());
        return result;
    }

    public static class Pickaxe extends CustomPickaxe {
        public Pickaxe(Tier tier, int attackDamageModifier, float attackSpeedModifier, Properties properties) {
            super(tier, attackDamageModifier, attackSpeedModifier, properties);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
            BlazingTools.addBurningTooltip(tooltip);
            BlazingTools.addSmeltingTooltip(tooltip);
            super.appendHoverText(stack, level, tooltip, flag);
        }

        @Override
        public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
            burnEffect(target);
            return super.hurtEnemy(stack, target, attacker);
        }
    }

    public static class Axe extends CustomAxe {
        public Axe(Tier tier, int attackDamageModifier, float attackSpeedModifier, Properties properties) {
            super(tier, attackDamageModifier, attackSpeedModifier, properties);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
            BlazingTools.addBurningTooltip(tooltip);
            BlazingTools.addSmeltingTooltip(tooltip);
            super.appendHoverText(stack, level, tooltip, flag);
        }

        @Override
        public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
            burnEffect(target);
            return super.hurtEnemy(stack, target, attacker);
        }
    }

    public static class Hoe extends CustomHoe {
        public Hoe(Tier tier, int attackDamageModifier, float attackSpeedModifier, Properties properties) {
            super(tier, attackDamageModifier, attackSpeedModifier, properties);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
            BlazingTools.addBurningTooltip(tooltip);
            BlazingTools.addSmeltingTooltip(tooltip);
            super.appendHoverText(stack, level, tooltip, flag);
        }

        @Override
        public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
            burnEffect(target);
            return super.hurtEnemy(stack, target, attacker);
        }
    }

    public static class Sword extends CustomSword {
        public Sword(Tier tier, int attackDamageModifier, float attackSpeedModifier, Properties properties) {
            super(tier, attackDamageModifier, attackSpeedModifier, properties);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
            BlazingTools.addBurningTooltip(tooltip);
            BlazingTools.addSmeltingTooltip(tooltip);
            super.appendHoverText(stack, level, tooltip, flag);
        }

        @Override
        public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
            burnEffect(target);
            return super.hurtEnemy(stack, target, attacker);
        }
    }

    public static class Shovel extends CustomShovel{
        public Shovel(Tier tier, int attackDamageModifier, float attackSpeedModifier, Properties properties) {
            super(tier, attackDamageModifier, attackSpeedModifier, properties);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
            BlazingTools.addBurningTooltip(tooltip);
            BlazingTools.addSmeltingTooltip(tooltip);
            super.appendHoverText(stack, level, tooltip, flag);
        }

        @Override
        public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
            burnEffect(target);
            return super.hurtEnemy(stack, target, attacker);
        }
    }

    public static class Shears extends CustomShears{
        public Shears(Properties properties) {
            super(properties);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
            BlazingTools.addBurningTooltip(tooltip);
            BlazingTools.addSmeltingTooltip(tooltip);
            super.appendHoverText(stack, level, tooltip, flag);
        }

        @Override
        public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
            burnEffect(target);
            return super.hurtEnemy(stack, target, attacker);
        }
    }
}
