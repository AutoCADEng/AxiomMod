package eng.cad.auto.engine.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public interface IFluidBehaviour {
    default void onEntityInside(Level level, BlockPos pos, Entity entity){
    }
}