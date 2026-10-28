package eng.cad.auto.axiom.fluids;

import eng.cad.auto.axolotyl.fluid.IFluidBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class Fluxfluid {
    public static int color = 0xEE5D36FF;

    public static class FluidBehaviour implements IFluidBehaviour {
        @Override
        public void onEntityInside(Level level, BlockPos pos, Entity entity) {
            if (!level.isClientSide && entity instanceof LivingEntity living) {
                double X = living.getX() + (living.getRandom().nextDouble() - 0.5D) * 16.0D;
                double Y = living.getY() + (double)(living.getRandom().nextInt(8) - 4);
                double Z = living.getZ() + (living.getRandom().nextDouble() - 0.5D) * 16.0D;

                living.randomTeleport(X, Y, Z, true);
            }
        }
    }
}
