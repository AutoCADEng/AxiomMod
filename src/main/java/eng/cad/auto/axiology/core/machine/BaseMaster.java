package eng.cad.auto.axiology.core.machine;

import eng.cad.auto.axiology.core.DataSlotHandler;
import eng.cad.auto.axiology.core.ISlotData;
import eng.cad.auto.axiology.core.component.IClientTickable;
import eng.cad.auto.axiology.core.component.ITickable;
import eng.cad.auto.engine.fluid.FluidStackHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

public abstract class BaseMaster extends BaseMachine {
    public BaseMaster(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }
}
