package eng.cad.auto.axiom.axiomancy.block;

import eng.cad.auto.axiom.axiomancy.blockentitiy.AlembicBlockEntity;
import eng.cad.auto.axiom.registry.BlockEntityRegistry;
import eng.cad.auto.axiom.ui.AlembicMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

public class AlembicBlock extends BaseEntityBlock {

    public AlembicBlock(Properties properties) {
        super(properties);
    }

    @Override @ParametersAreNonnullByDefault
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new AlembicBlockEntity(blockPos, blockState);
    }
    
    @Override @ParametersAreNonnullByDefault @MethodsReturnNonnullByDefault
    public RenderShape getRenderShape(BlockState p_50950_) {
        return RenderShape.MODEL;
    }

    @Override @ParametersAreNonnullByDefault @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if(pLevel.isClientSide()) {
            return null;
        }

        return createTickerHelper(pBlockEntityType, BlockEntityRegistry.ALEMBIC.get(),
                (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.serverTick(pLevel1, pPos, pState1));
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult result) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        if (level.getBlockEntity(blockPos) instanceof AlembicBlockEntity alembic){
            if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResult.CONSUME;
            NetworkHooks.openScreen(serverPlayer, alembic, blockPos);
        }
        return InteractionResult.CONSUME;
    }
}
