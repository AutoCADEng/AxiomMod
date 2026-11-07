package eng.cad.auto.axiology.core.fluid;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public interface IRangedFluidBehaviour {
   default void applyAsProjectile(Level level, Entity entity){
       if (!level.isClientSide && entity instanceof LivingEntity living) {
           living.hurt(level.damageSources().magic(), 1.0F);
           living.setSecondsOnFire(0);
       }
   }
}
