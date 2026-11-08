package eng.cad.auto.axiology.fluids;

import net.minecraft.core.BlockPos;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class Creosote {
    public static final String name = "creosote";
    public static int color = 0xFF783F04;

    public static class CustomFluidBehaviour extends AbstractFluidBehaviour {
        @Override
        public void onEntityInside(Level level, BlockPos pos, Entity entity) {
            if (!level.isClientSide && entity instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(MobEffects.POISON, 50, 1));
            }
        }

        @Override
        public void applyAsProjectile(Level level, Entity entity) {
            if (!level.isClientSide && entity instanceof LivingEntity living) {
                living.hurt(level.damageSources().magic(), 1.0F);
                living.addEffect(new MobEffectInstance(MobEffects.POISON, 50, 1));
            }
        }
    }
}
