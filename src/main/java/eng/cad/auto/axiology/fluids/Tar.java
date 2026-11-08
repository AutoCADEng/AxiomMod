package eng.cad.auto.axiology.fluids;

import net.minecraft.core.BlockPos;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class Tar {
    public static final String name = "tar";
    public static int color = 0xFF000000;

    public static class CustomFluidBehaviour extends AbstractFluidBehaviour {
        @Override
        public void onEntityInside(Level level, BlockPos pos, Entity entity) {
            if (!level.isClientSide && entity instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10, 10));
            }
        }

        @Override
        public void applyAsProjectile(Level level, Entity entity) {
            if (!level.isClientSide && entity instanceof LivingEntity living) {
                living.hurt(level.damageSources().magic(), 1.0F);
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10, 10));
            }
        }
    }
}
