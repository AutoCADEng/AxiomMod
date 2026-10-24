package eng.cad.auto.axiom.common.fluid;

import net.minecraft.core.BlockPos;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.MapColor;

public class Ethanol {
    public static int color = 0xCCF5DEB3;
    public static class FluidBehaviour implements IFluidBehaviour {
        @Override
        public void onEntityInside(Level level, BlockPos pos, Entity entity) {
            if (!level.isClientSide && entity instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(MobEffects.CONFUSION,200,1));
            }
        }
    }
}
