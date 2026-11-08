package eng.cad.auto.axiology.fluids;

import net.minecraft.core.BlockPos;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class Ethanol {
    public static final String name = "ethanol";
    public static int color = 0xCCF5DEB3;

    public static class CustomFluidBehaviour extends AbstractFluidBehaviour {
        @Override
        public void onEntityInside(Level level, BlockPos pos, Entity entity) {
            if (!level.isClientSide && entity instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 1));
                living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 1));
            }
        }

        @Override
        public void applyAsProjectile(Level level, Entity entity) {
            if (!level.isClientSide && entity instanceof LivingEntity living) {
                living.hurt(level.damageSources().magic(), 1.0F);
                living.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 1));
                living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 1));
            }
        }
    }

    public static class Dilute {
        public static final String name = "dilute_ethanol";
        public static int color = 0x55FAEDD4;
    }
}
