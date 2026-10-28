package eng.cad.auto.axolotyl.machine.multiblock;

import eng.cad.auto.axolotyl.machine.core.GenericMachineEntity;
import eng.cad.auto.axolotyl.machine.core.MachineCore;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class GenericFollowerEntity extends GenericMachineEntity {
    public GenericFollowerEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, MachineCore.Config machineConfig) {
        super(type, pos, state, machineConfig);
    }
}