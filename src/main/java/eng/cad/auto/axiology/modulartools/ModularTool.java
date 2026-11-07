package eng.cad.auto.axiology.modulartools;

import eng.cad.auto.axiology.core.events.BlockDropEvent;
import eng.cad.auto.axiology.modulartools.components.*;
import eng.cad.auto.axiology.registry.AugmentsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

import java.util.HashMap;
import java.util.Map;

public class ModularTool extends Item implements IModularTool {

    public ModularTool(Properties properties) {
        super(properties);
    }

    public static Map<ModularToolAugment, Integer> getAugments(ItemStack stack) {
        HashMap<ModularToolAugment, Integer> out = new HashMap<>();
        if (!stack.hasTag()) return out;
        CompoundTag tag = stack.getTag();
        for(String key:tag.getAllKeys()){
            ResourceLocation moduleLoc = ResourceLocation.tryParse(key);
            if (moduleLoc == null) continue;
            ModularToolAugment module = AugmentsRegistry.REGISTRY.get().getValue(moduleLoc);
            if (module != null) out.put(module, tag.getInt(key));
        }
        return out;
    }
    public static void onBlockDrop(BlockDropEvent event) {
        if(!(event.getMiner().getMainHandItem().getItem() instanceof ModularTool tool)) return;
        for(ModularToolAugment ability:ModularTool.getAugments(event.getMiner().getMainHandItem()).keySet()){
            if (!(ability instanceof IModularDigAbility onDigAbility)) continue;
            onDigAbility.digDropEvent(event);
        }
    }

    public static void onLivingDrops(LivingDropsEvent event) {
        if (!(event.getSource().getEntity() instanceof LivingEntity killer)) return;
        if (killer.level().isClientSide) return;
        if(!(killer.getMainHandItem().getItem() instanceof ModularTool tool)) return;
        for(ModularToolAugment ability:ModularTool.getAugments(killer.getMainHandItem()).keySet()){
            if (!(ability instanceof IModularHitAbility onHitAbility)) continue;
            onHitAbility.killDropsEvent(event);
        }
    }

    @Override
    public boolean hurtEnemy(ItemStack item, LivingEntity target, LivingEntity player) {
        if (getDamage(item) >= getMaxDamage(item)) return false;
        Map<ModularToolAugment, Integer> modules = ModularTool.getAugments(player.getMainHandItem());
        for(ModularToolAugment ability:modules.keySet()){
            if (!(ability instanceof IModularHitAbility onHitAbility)) continue;
            onHitAbility.onHit(item, target, player, modules.get(onHitAbility));
        }
        return super.hurtEnemy(item, target, player);
    }

    @Override
    public boolean mineBlock(ItemStack item, Level level, BlockState blockState, BlockPos blockPos, LivingEntity player) {
        if (getDamage(item) >= getMaxDamage(item)) return false;
        Map<ModularToolAugment, Integer> modules = ModularTool.getAugments(player.getMainHandItem());
        for(ModularToolAugment ability:modules.keySet()){
            if (!(ability instanceof IModularDigAbility onDigAbility)) continue;
            onDigAbility.onDig(item, level, blockState,blockPos, player, modules.get(ability));
        }
        return super.mineBlock(item, level, blockState, blockPos, player);
    }

    @Override
    public boolean canBeDepleted(Player player) {
        Map<ModularToolAugment, Integer> modules = ModularTool.getAugments(player.getMainHandItem());
        return modules.keySet().stream().noneMatch(key -> key instanceof IModularDurability);
    }

    public static boolean addModule(ItemStack tool, ModularToolAugment ability) {
        Map<ModularToolAugment, Integer> modules = ModularTool.getAugments(tool);
        if (getAugmentCount(tool) < IModularTool.getMaxAugments(tool)){
            if (modules.keySet().stream().allMatch(existing -> existing.isCompatible(ability))){
                if (modules.containsKey(ability)){
                    modules.put(ability, modules.get(ability) +1);
                }else{
                    modules.put(ability, 1);
                }
                return true;
            }
        }
        return false;
    }

    private static int getAugmentCount(ItemStack tool) {
        Map<ModularToolAugment, Integer> modules = ModularTool.getAugments(tool);
        int sumOf = modules.values().stream().mapToInt(Integer::intValue).sum();
        return sumOf;
    }
}
