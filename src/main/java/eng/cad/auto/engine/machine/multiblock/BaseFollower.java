package eng.cad.auto.engine.machine.multiblock;

import eng.cad.auto.axiology.core.machine.BaseMachine;
import eng.cad.auto.engine.crafting.core.CustomCraftingContainer;
import eng.cad.auto.axiology.core.machine.MachineRegistryObject;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BaseFollower extends BaseMachine {
    public BaseFollower(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
}