package eng.cad.auto.axiology.machines.multiblock.followers;

import eng.cad.auto.axiology.core.machine.component.IMachinePersistable;
import eng.cad.auto.axiology.core.machine.BaseFollower;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class Hatch<F extends Hatch<F>> extends BaseFollower<Hatch<F>> implements IMachinePersistable {

    public Hatch(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }
}
