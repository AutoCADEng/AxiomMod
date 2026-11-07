package eng.cad.auto.axiology.modulartools;

import eng.cad.auto.axiology.core.events.BlockDropEvent;
import eng.cad.auto.axiology.modulartools.components.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

import java.util.HashMap;

public class ModularTool extends Item implements IModularTool {
    HashMap<ModularToolAbility, Integer> modules = new HashMap<>();
    public ModularTool(Properties properties) {
        super(properties);
    }

    @Override
    public HashMap<ModularToolAbility, Integer> getModules() {
        return modules;
    }

    public static void onBlockDrop(BlockDropEvent event) {
        if(!(event.getMiner().getMainHandItem().getItem() instanceof ModularTool tool)) return;
        for(ModularToolAbility ability:tool.modules.keySet()){
            if (!(ability instanceof IModularDigAbility onDigAbility)) continue;
            onDigAbility.digDropEvent(event);
        }
    }

    public static void onLivingDrops(LivingDropsEvent event) {
        if (!(event.getSource().getEntity() instanceof LivingEntity killer)) return;
        if (killer.level().isClientSide) return;
        if(!(killer.getMainHandItem().getItem() instanceof ModularTool tool)) return;
        for(ModularToolAbility ability:tool.modules.keySet()){
            if (!(ability instanceof IModularHitAbility onHitAbility)) continue;
            onHitAbility.killDropsEvent(event);
        }
    }

    @Override
    public boolean hurtEnemy(ItemStack item, LivingEntity target, LivingEntity player) {
        if (getDamage(item) >= getMaxDamage(item)) return false;
        for(ModularToolAbility ability:modules.keySet()){
            if (!(ability instanceof IModularHitAbility onHitAbility)) continue;
            onHitAbility.onHit(item, target, player, modules.get(ability));
        }
        return super.hurtEnemy(item, target, player);
    }

    @Override
    public boolean mineBlock(ItemStack item, Level level, BlockState blockState, BlockPos blockPos, LivingEntity player) {
        if (getDamage(item) >= getMaxDamage(item)) return false;
        for(ModularToolAbility ability:modules.keySet()){
            if (!(ability instanceof IModularDigAbility onDigAbility)) continue;
            onDigAbility.onDig(item, level, blockState,blockPos, player, modules.get(ability));
        }
        return super.mineBlock(item, level, blockState, blockPos, player);
    }

    @Override
    public boolean canBeDepleted() {
        return modules.keySet().stream().noneMatch(key -> key instanceof IModularDurability);
    }
}
