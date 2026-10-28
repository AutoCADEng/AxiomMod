package eng.cad.auto.axiom.fluids;

import eng.cad.auto.axolotyl.fluid.IFluidBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class Blazewater {
    public static int color = 0xEEFF7500;

    public static class FluidBehaviour implements IFluidBehaviour {
        @Override
        public void onEntityInside(Level level, BlockPos pos, Entity entity) {
            if (!level.isClientSide && entity instanceof LivingEntity living) {
                living.setSecondsOnFire(1);
            }
        }
    }
}
