package eng.cad.auto.axiology.core.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class GuiMachine<T extends GuiMachine<T>> extends BaseMachine<T> implements MenuProvider {
    public GuiMachine(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }
}
