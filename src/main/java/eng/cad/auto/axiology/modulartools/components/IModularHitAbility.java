package eng.cad.auto.axiology.modulartools.components;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

public interface IModularHitAbility {
    void onHit(ItemStack item, LivingEntity target, LivingEntity player, Integer integer);

    void killDropsEvent(LivingDropsEvent event);
}
