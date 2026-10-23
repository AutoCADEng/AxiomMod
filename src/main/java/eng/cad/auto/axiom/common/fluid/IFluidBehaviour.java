package eng.cad.auto.axiom.common.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

@FunctionalInterface
public interface IFluidBehaviour {
    void onEntityInside(Level level, BlockPos pos, Entity entity);
}