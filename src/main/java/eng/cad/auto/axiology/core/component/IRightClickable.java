package eng.cad.auto.axiology.core.component;

import eng.cad.auto.axiology.core.ISlotFluid;
import eng.cad.auto.axiology.core.machine.BaseMachine;
import eng.cad.auto.axiology.machines.util.CommonRightClickBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public interface IRightClickable {
   default InteractionResult rightClick(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult result){
      if (player.isShiftKeyDown()) return InteractionResult.CONSUME;
      if (hand == InteractionHand.OFF_HAND) return InteractionResult.CONSUME;
      if (!(level.getBlockEntity(blockPos) instanceof BaseMachine machine)) return InteractionResult.CONSUME;
      if (machine instanceof ISlotFluid fluid) {
         if (CommonRightClickBehaviour.bucketInHand(fluid.getFluidSlots().getFluidInTank(0).getAmount(), machine, blockState, level, blockPos, player, hand, result)) return InteractionResult.PASS;
      }
      if (machine instanceof ISlotFluid) {
         if (CommonRightClickBehaviour.itemInHand(0, machine, blockState, level, blockPos, player, hand, result))
            return InteractionResult.PASS;
      }
      return InteractionResult.CONSUME;
   }
}
