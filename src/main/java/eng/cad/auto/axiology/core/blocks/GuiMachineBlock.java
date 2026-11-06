package eng.cad.auto.axiology.core.blocks;

import eng.cad.auto.axiology.core.machine.GuiMachine;
import eng.cad.auto.axiology.core.machine.TickingMachine;
import eng.cad.auto.axiology.core.component.IGuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

public class GuiMachineBlock extends BaseMachineBlock {
    public GuiMachineBlock(Properties properties, RegistryObject<BlockEntityType<? extends TickingMachine>> blockEntity) {
        super(properties, blockEntity);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityTicker) {
        return super.getTicker(level, blockState, blockEntityTicker);
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult result) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!(level.getBlockEntity(blockPos) instanceof GuiMachine guiMachine)) return InteractionResult.PASS;
        if (!(level.getBlockEntity(blockPos) instanceof IGuiGraphics gui)) return InteractionResult.PASS;
        return gui.openScreen(guiMachine, blockState, level, blockPos, player, interactionHand, result);
    }
}