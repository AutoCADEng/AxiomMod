package eng.cad.auto.axiology.modulartools.components;

import eng.cad.auto.axiology.core.events.BlockDropEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IModularDigAbility {
    void onDig(ItemStack item, Level level, BlockState blockState, BlockPos blockPos, LivingEntity player, Integer integer);
    void digDropEvent(BlockDropEvent event);
}
