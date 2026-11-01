package eng.cad.auto.axiology.fluids;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class Cursewater {
    public static int color = 0xFF330033;
    public static String name = "cursewater";

    public static class CustomFluidBehaviour extends AbstractFluidBehaviour {
        @Override
        public void onEntityInside(Level level, BlockPos pos, Entity entity) {
            if (!level.isClientSide && entity instanceof LivingEntity living) {
                living.hurt(level.damageSources().magic(), 1.0F);
            }
        }

        @Override
        public void applyAsProjectile(Level level, Entity entity){
            if (!level.isClientSide && entity instanceof LivingEntity living) {
                living.hurt(level.damageSources().magic(), 5.0F);
            }
        }
    }

}
