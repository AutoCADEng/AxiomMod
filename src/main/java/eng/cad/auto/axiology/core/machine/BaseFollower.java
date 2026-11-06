package eng.cad.auto.axiology.core.machine;

import eng.cad.auto.axiology.core.component.IMultiblockFollower;
import eng.cad.auto.axiology.core.component.IMultiblockMaster;
import eng.cad.auto.axiology.core.component.ISubscribeBlockBreak;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;

public abstract class BaseFollower<H extends BaseFollower<H>> extends BaseMachine<H> implements ISubscribeBlockBreak, IMultiblockFollower {
    IMultiblockMaster master;

    public BaseFollower(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    public void fireBlockBreak(BlockEvent.BreakEvent event) {
        if (level == null) return;
        if (!(getMaster() instanceof BaseMachine<?> master)) return;
        getMaster().validateStructure(master.getBlockPos(), level);
    }

    @Override
    public IMultiblockMaster getMaster() {
        return master;
    }

    @Override
    public void setMaster(IMultiblockMaster master) {
        this.master = master;
    }
}
