package eng.cad.auto.axiology.items.toolsets;

import eng.cad.auto.axiology.core.item.tools.*;
import eng.cad.auto.axiology.modulartools.components.IModularTool;
import eng.cad.auto.axiology.modulartools.components.ModularToolAugment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EtchedTools {
    public static String name = "etched_iron";
    public static void addSummaryTooltip(List<Component> tooltip, int modules, int maxModules) {
        tooltip.add(Component.translatable("tooltip.axiology.modulartools.tooltip", modules, maxModules).withStyle(ChatFormatting.WHITE));
    }
    public static void addModuleTooltip(List<Component> tooltip, ModularToolAugment module, int count, int moduleLimit, ChatFormatting color) {
        tooltip.add(Component.translatable("tooltip.axiology.modulartools.tooltip.module", module.getDisplayName(), count, moduleLimit).withStyle(color));
    }
    public static boolean isShiftDown(ItemStack stack, Level level){
        if (level == null) return false;
        if (!level.isClientSide) return false;
        return Screen.hasShiftDown();
    }

    public static class Pickaxe extends CustomPickaxe implements IModularTool {
        public Pickaxe(Tier tier, int attackDamageModifier, float attackSpeedModifier, Properties properties) {
            super(tier, attackDamageModifier, attackSpeedModifier, properties);
        }
        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {

            addSummaryTooltip(components, IModularTool.getAugmentCount(stack), IModularTool.getMaxAugments(stack));
            if (isShiftDown(stack, level)){
                for (ModularToolAugment module:IModularTool.getAugments(stack).keySet()){
                    addModuleTooltip(components, module, IModularTool.getAugments(stack).get(module), module.maxStackable(), module.getColor());
                }
            }

            super.appendHoverText(stack, level, components, flag);
        }

    }

    public static class Axe extends CustomAxe implements IModularTool {
        public Axe(Tier tier, int attackDamageModifier, float attackSpeedModifier, Properties properties) {
            super(tier, attackDamageModifier, attackSpeedModifier, properties);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {

            addSummaryTooltip(components, IModularTool.getAugmentCount(stack), IModularTool.getMaxAugments(stack));
            if (isShiftDown(stack, level)){
                for (ModularToolAugment module:IModularTool.getAugments(stack).keySet()){
                    addModuleTooltip(components, module, IModularTool.getAugments(stack).get(module), module.maxStackable(), module.getColor());
                }
            }

            super.appendHoverText(stack, level, components, flag);
        }
    }

    public static class Hoe extends CustomHoe implements IModularTool {
        public Hoe(Tier tier, int attackDamageModifier, float attackSpeedModifier, Properties properties) {
            super(tier, attackDamageModifier, attackSpeedModifier, properties);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {

            addSummaryTooltip(components, IModularTool.getAugmentCount(stack), IModularTool.getMaxAugments(stack));
            if (isShiftDown(stack, level)){
                for (ModularToolAugment module:IModularTool.getAugments(stack).keySet()){
                    addModuleTooltip(components, module, IModularTool.getAugments(stack).get(module), module.maxStackable(), module.getColor());
                }
            }

            super.appendHoverText(stack, level, components, flag);
        }
    }

    public static class Sword extends CustomSword implements IModularTool {
        public Sword(Tier tier, int attackDamageModifier, float attackSpeedModifier, Properties properties) {
            super(tier, attackDamageModifier, attackSpeedModifier, properties);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {

            addSummaryTooltip(components, IModularTool.getAugmentCount(stack), IModularTool.getMaxAugments(stack));
            if (isShiftDown(stack, level)){
                for (ModularToolAugment module:IModularTool.getAugments(stack).keySet()){
                    addModuleTooltip(components, module, IModularTool.getAugments(stack).get(module), module.maxStackable(), module.getColor());
                }
            }

            super.appendHoverText(stack, level, components, flag);
        }
    }

    public static class Shovel extends CustomShovel implements IModularTool {
        public Shovel(Tier tier, int attackDamageModifier, float attackSpeedModifier, Properties properties) {
            super(tier, attackDamageModifier, attackSpeedModifier, properties);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {

            addSummaryTooltip(components, IModularTool.getAugmentCount(stack), IModularTool.getMaxAugments(stack));
            if (isShiftDown(stack, level)){
                for (ModularToolAugment module:IModularTool.getAugments(stack).keySet()){
                    addModuleTooltip(components, module, IModularTool.getAugments(stack).get(module), module.maxStackable(), module.getColor());
                }
            }

            super.appendHoverText(stack, level, components, flag);
        }
    }

    public static class Shears extends CustomShears implements IModularTool {
        public Shears(Properties properties) {
            super(properties);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {

            addSummaryTooltip(components, IModularTool.getAugmentCount(stack), IModularTool.getMaxAugments(stack));
            if (isShiftDown(stack, level)){
                for (ModularToolAugment module:IModularTool.getAugments(stack).keySet()){
                    addModuleTooltip(components, module, IModularTool.getAugments(stack).get(module), module.maxStackable(), module.getColor());
                }
            }

            super.appendHoverText(stack, level, components, flag);
        }
    }
}
