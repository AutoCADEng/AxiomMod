package eng.cad.auto.axiom.common.machine.core;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class GenericMachineBlock extends BaseEntityBlock {
    RegistryObject<BlockEntityType<GenericBlockEntity>> blockEntity;
    MachineHolder.Config config;
    public GenericMachineBlock(Properties properties, RegistryObject<BlockEntityType<GenericBlockEntity>> blockEntity, MachineHolder.Config config) {
        super(properties);
        this.blockEntity = blockEntity;
        this.config = config;
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
        if (level.isClientSide()) return null;
        return createTickerHelper(blockEntityType, blockEntity.get(), GenericBlockEntity::tick);
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult result) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        if (level.getBlockEntity(blockPos) instanceof GenericBlockEntity machine){
            if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResult.CONSUME;
            NetworkHooks.openScreen(serverPlayer, machine, blockPos);
        }
        return InteractionResult.CONSUME;
    }
}