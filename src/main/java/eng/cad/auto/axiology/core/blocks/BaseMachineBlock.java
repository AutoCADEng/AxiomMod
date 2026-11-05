package eng.cad.auto.axiology.core.blocks;

import eng.cad.auto.axiology.core.component.IRightClickable;
import eng.cad.auto.axiology.core.machine.BaseMachine;
import eng.cad.auto.axiology.core.machine.MachineRegistryObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
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

public abstract class BaseMachineBlock extends BaseEntityBlock {
    protected final RegistryObject<BlockEntityType<? extends BaseMachine>> blockEntity;
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public BaseMachineBlock(Properties properties, RegistryObject<BlockEntityType<? extends BaseMachine>> blockEntity) {
        super(properties);
        this.blockEntity = blockEntity;
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return blockEntity.get().create(blockPos, blockState);
    }

    @Override
    public RenderShape getRenderShape(BlockState p_50950_) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult result) {
        if (!(level.getBlockEntity(blockPos) instanceof IRightClickable clickable)) return InteractionResult.CONSUME;
        return clickable.rightClick(blockState, level, blockPos, player, interactionHand, result);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return context.getPlayer().isShiftKeyDown()?
                defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite())
                : defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }



    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, blockEntity.get(), BaseMachine::tick);
    }
}