package eng.cad.auto.axiom.fluids;

import eng.cad.auto.axolotyl.fluid.IFluidBehaviour;
import net.minecraft.core.BlockPos;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class Ethanol {
    public static final String name = "ethanol";
    public static int color = 0xCCF5DEB3;
    public static class FluidBehaviour implements IFluidBehaviour {
        @Override
        public void onEntityInside(Level level, BlockPos pos, Entity entity) {
            if (!level.isClientSide && entity instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(MobEffects.CONFUSION,200,1));
            }
        }
    }

    public static class Dilute{
        public static final String name = "dilute_ethanol";
        public static int color = 0x55FAEDD4;
    }
}
