package eng.cad.auto.axolotyl.machine.multiblock;

import eng.cad.auto.axolotyl.machine.core.GenericMachineEntity;
import eng.cad.auto.axolotyl.machine.core.GenericMachineBlock;
import eng.cad.auto.axolotyl.machine.core.MachineCore;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.registries.RegistryObject;

public class GenericFollowerBlock extends GenericMachineBlock {

    public GenericFollowerBlock(Properties properties, RegistryObject<BlockEntityType<? extends GenericMachineEntity>> blockEntity, MachineCore.Config config) {
        super(properties, blockEntity, config);
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult result) {
        if (!(level.getBlockEntity(blockPos) instanceof GenericFollowerEntity genericBlockEntity)) return InteractionResult.FAIL;
        return genericBlockEntity.machineConfig.rightClickLogic().useMachine(blockState, level, blockPos, player, interactionHand, result);
    }
}
