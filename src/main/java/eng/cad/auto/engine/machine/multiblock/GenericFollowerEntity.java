package eng.cad.auto.engine.machine.multiblock;

import eng.cad.auto.engine.crafting.core.CustomCraftingContainer;
import eng.cad.auto.engine.machine.core.GenericMachineEntity;
import eng.cad.auto.engine.machine.core.MachineCore;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class GenericFollowerEntity extends GenericMachineEntity {
    public GenericFollowerEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, MachineCore.Config machineConfig) {
        super(type, pos, state, machineConfig);
    }

    public CustomCraftingContainer getCraftingContainer(GenericFollowerEntity entity){
        return this.machineConfig.followerLogic().getCraftingContainer(entity);
    }
}