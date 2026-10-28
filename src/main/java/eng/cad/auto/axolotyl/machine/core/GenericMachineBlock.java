package eng.cad.auto.axolotyl.machine.core;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class GenericMachineBlock extends BaseEntityBlock {
    RegistryObject<BlockEntityType<? extends GenericMachineEntity>> blockEntity;
    MachineCore.Config config;
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public GenericMachineBlock(Properties properties, RegistryObject<BlockEntityType<? extends GenericMachineEntity>> blockEntity, MachineCore.Config config) {
        super(properties);
        this.blockEntity = blockEntity;
        this.config = config;
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override @ParametersAreNonnullByDefault
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return blockEntity.get().create(blockPos, blockState);
    }

    @Override @ParametersAreNonnullByDefault @MethodsReturnNonnullByDefault
    public RenderShape getRenderShape(BlockState p_50950_) {
        return RenderShape.MODEL;
    }

    @Override @ParametersAreNonnullByDefault @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return !level.isClientSide() ? createTickerHelper(blockEntityType, blockEntity.get(), GenericMachineEntity::tick) : createTickerHelper(blockEntityType, blockEntity.get(), GenericMachineEntity::clientTick);
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult result) {
        if (!(level.getBlockEntity(blockPos) instanceof GenericMachineEntity genericBlockEntity)) return InteractionResult.FAIL;
        return genericBlockEntity.machineConfig.rightClickLogic().useMachine(blockState, level, blockPos, player, interactionHand, result);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49915_) {
        p_49915_.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return context.getPlayer().isShiftKeyDown()?
                defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite())
                : defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }
}