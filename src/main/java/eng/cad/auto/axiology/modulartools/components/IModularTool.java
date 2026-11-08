package eng.cad.auto.axiology.modulartools.components;

import eng.cad.auto.axiology.core.events.BlockDropEvent;
import eng.cad.auto.axiology.modulartools.ItemEnchantment;
import eng.cad.auto.axiology.registry.AugmentsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

import java.util.HashMap;
import java.util.Map;

public interface IModularTool {
    String NBT_KEY = "Augments";

    static int getMaxAugments(ItemStack tool) {
        return 16;
    }

    static Map<ModularToolAugment, Integer> getAugments(ItemStack stack) {
        Map<ModularToolAugment, Integer> out = new HashMap<>();
        CompoundTag root = stack.getTag();
        if (root == null || !root.contains(NBT_KEY, Tag.TAG_COMPOUND)) return out;

        CompoundTag augments = root.getCompound(NBT_KEY);
        for (String key : augments.getAllKeys()) {
            ResourceLocation id = ResourceLocation.tryParse(key);
            if (id == null) continue;
            ModularToolAugment augment = AugmentsRegistry.REGISTRY.get().getValue(id);
            if (augment != null) out.put(augment, augments.getInt(key));
        }
        return out;
    }

    static int getLevel(ItemStack stack, ModularToolAugment augment) {
        ResourceLocation id = AugmentsRegistry.REGISTRY.get().getKey(augment);
        CompoundTag root = stack.getTag();
        if (id == null || root == null) return 0;
        return root.getCompound(NBT_KEY).getInt(id.toString());
    }

    static void setLevel(ItemStack stack, ModularToolAugment augment, int level) {
        ResourceLocation id = AugmentsRegistry.REGISTRY.get().getKey(augment);
        if (id == null) return;
        CompoundTag root = stack.getOrCreateTag();
        CompoundTag augments = root.getCompound(NBT_KEY);
        if (level <= 0) augments.remove(id.toString());
        else augments.putInt(id.toString(), level);
        root.put(NBT_KEY, augments);
        refreshTool(stack);
    }

    static void refreshTool(ItemStack stack) {
        stack.removeTagKey("Enchantments");
        stack.removeTagKey("StoredEnchantments");
        for (ModularToolAugment augment : getAugments(stack).keySet()) {
            if (!(augment instanceof IModularEnchantment modularEnchantment)) return;
            for (ItemEnchantment enchant : modularEnchantment.getEnchantments()) {
                stack.enchant(enchant.enchantment(), getAugments(stack).get(modularEnchantment));
            }
        }
    }


    static int getAugmentCount(ItemStack tool) {
        return getAugments(tool).entrySet().stream().mapToInt(set -> set.getKey().getCost() * set.getValue()).sum();
    }

    static boolean addModule(ItemStack tool, ModularToolAugment augment) {
        if (augment == null) return false;
        if (getAugmentCount(tool) >= getMaxAugments(tool)) return false;

        Map<ModularToolAugment, Integer> current = getAugments(tool);
        int level = current.getOrDefault(augment, 0);
        if (level >= augment.maxStackable()) return false;

        for (ModularToolAugment existing : current.keySet()) {
            if (!existing.isCompatible(augment) || !augment.isCompatible(existing)) return false;
        }

        setLevel(tool, augment, level + 1);
        return true;
    }

    static boolean isBroken(ItemStack stack) {
        return stack.isDamageableItem() && stack.getDamageValue() >= stack.getMaxDamage();
    }

    static void applyHit(ItemStack item, LivingEntity target, LivingEntity attacker) {
        for (Map.Entry<ModularToolAugment, Integer> entry : getAugments(item).entrySet()) {
            if (entry.getKey() instanceof IModularHitAbility hit) {
                hit.onHit(item, target, attacker, entry.getValue());
            }
        }
    }

    static void applyDig(ItemStack item, Level level, BlockState state, BlockPos pos, LivingEntity miner) {
        for (Map.Entry<ModularToolAugment, Integer> entry : getAugments(item).entrySet()) {
            if (entry.getKey() instanceof IModularDigAbility dig) {
                dig.onDig(item, level, state, pos, miner, entry.getValue());
            }
        }
    }

    static void onBlockDrop(BlockDropEvent event) {
        ItemStack held = event.getMiner().getMainHandItem();
        if (!(held.getItem() instanceof IModularTool)) return;
        for (ModularToolAugment augment : getAugments(held).keySet()) {
            if (augment instanceof IModularDigAbility dig) dig.digDropEvent(event);
        }
    }

    static void onLivingDrops(LivingDropsEvent event) {
        if (!(event.getSource().getEntity() instanceof LivingEntity killer)) return;
        if (killer.level().isClientSide) return;
        ItemStack held = killer.getMainHandItem();
        if (!(held.getItem() instanceof IModularTool)) return;
        for (ModularToolAugment augment : getAugments(held).keySet()) {
            if (augment instanceof IModularHitAbility hit) hit.killDropsEvent(event);
        }
    }

    default boolean canBeDepleted(Player player) {
        return getAugments(player.getMainHandItem()).keySet().stream()
                .noneMatch(augment -> augment instanceof IModularDurability);
    }

}
