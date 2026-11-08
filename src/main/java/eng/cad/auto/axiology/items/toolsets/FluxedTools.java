package eng.cad.auto.axiology.items.toolsets;

import eng.cad.auto.axiology.core.events.BlockDropEvent;
import eng.cad.auto.axiology.core.item.tools.*;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.List;
/**
 * TODO MIGRATE THIS TO THE MODULAR SYSTEM AT SOME POINT
 */
public class FluxedTools {
    public static String name = "fluxed";

    public static void onLivingDrops(LivingDropsEvent event) {
        if (!(event.getSource().getEntity() instanceof LivingEntity killer)) return;

        if (killer.level().isClientSide) return;

        if ((!(killer.getMainHandItem().getItem() instanceof FluxedTools.Sword))
                && (!(killer.getMainHandItem().getItem() instanceof FluxedTools.Axe))) return;

        for (ItemEntity drop : event.getDrops()) {
            if (!(event.getSource().getEntity() instanceof Player player)) {
                drop.setPos(killer.position());
                drop.setDeltaMovement(Vec3.ZERO);
                continue;
            }
            ItemHandlerHelper.giveItemToPlayer(player, drop.getItem());
            drop.remove(Entity.RemovalReason.DISCARDED);
        }
    }

    public static void onBlockDrop(BlockDropEvent event) {
        if (!(event.getEntity() instanceof ItemEntity drop)) return;
        if (drop.level().isClientSide) return;
        Player miner = event.getMiner();
        if ((!(miner.getMainHandItem().getItem() instanceof FluxedTools.Sword))
                && (!(miner.getMainHandItem().getItem() instanceof FluxedTools.Shears))
                && (!(miner.getMainHandItem().getItem() instanceof FluxedTools.Shovel))
                && (!(miner.getMainHandItem().getItem() instanceof FluxedTools.Hoe))
                && (!(miner.getMainHandItem().getItem() instanceof FluxedTools.Pickaxe))
                && (!(miner.getMainHandItem().getItem() instanceof FluxedTools.Axe))) return;

        ItemHandlerHelper.giveItemToPlayer(miner, drop.getItem());
        drop.remove(Entity.RemovalReason.DISCARDED);
    }

    public static void addFluxedTooltip(List<Component> tooltip) {
        tooltip.add(Component.translatable("tooltip.axiology.fluxed_tools.auto_collect").withStyle(ChatFormatting.DARK_PURPLE));
    }

    public static class Pickaxe extends CustomPickaxe {
        public Pickaxe(Tier tier, int attackDamageModifier, float attackSpeedModifier, Properties properties) {
            super(tier, attackDamageModifier, attackSpeedModifier, properties);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
            FluxedTools.addFluxedTooltip(tooltip);
            super.appendHoverText(stack, level, tooltip, flag);
        }
    }

    public static class Axe extends CustomAxe {
        public Axe(Tier tier, int attackDamageModifier, float attackSpeedModifier, Properties properties) {
            super(tier, attackDamageModifier, attackSpeedModifier, properties);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
            FluxedTools.addFluxedTooltip(tooltip);
            super.appendHoverText(stack, level, tooltip, flag);
        }
    }

    public static class Hoe extends CustomHoe {
        public Hoe(Tier tier, int attackDamageModifier, float attackSpeedModifier, Properties properties) {
            super(tier, attackDamageModifier, attackSpeedModifier, properties);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
            FluxedTools.addFluxedTooltip(tooltip);
            super.appendHoverText(stack, level, tooltip, flag);
        }
    }

    public static class Sword extends CustomSword {
        public Sword(Tier tier, int attackDamageModifier, float attackSpeedModifier, Properties properties) {
            super(tier, attackDamageModifier, attackSpeedModifier, properties);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
            FluxedTools.addFluxedTooltip(tooltip);
            super.appendHoverText(stack, level, tooltip, flag);
        }
    }

    public static class Shovel extends CustomShovel {
        public Shovel(Tier tier, int attackDamageModifier, float attackSpeedModifier, Properties properties) {
            super(tier, attackDamageModifier, attackSpeedModifier, properties);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
            FluxedTools.addFluxedTooltip(tooltip);
            super.appendHoverText(stack, level, tooltip, flag);
        }
    }

    public static class Shears extends CustomShears {
        public Shears(Properties properties) {
            super(properties);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
            FluxedTools.addFluxedTooltip(tooltip);
            super.appendHoverText(stack, level, tooltip, flag);
        }
    }
}
