package eng.cad.auto.axiology.core.machine.component;

import eng.cad.auto.axiology.core.MachineUI;
import eng.cad.auto.axiology.core.machine.GuiMachine;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

public interface IGuiGraphics {
    void UIRenderLogic(GuiGraphics guiGraphics, float tick, int mouse_x, int mouse_y, MachineUI.Screen screen);

    default <T extends GuiMachine> InteractionResult openScreen(T machine, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult result) {
        if (machine == null) return InteractionResult.CONSUME;
        if (level.isClientSide()) return InteractionResult.CONSUME;
        if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResult.CONSUME;
        if (player.isShiftKeyDown()) return InteractionResult.CONSUME;
        NetworkHooks.openScreen(serverPlayer, machine, blockPos);
        return InteractionResult.SUCCESS;
    }

    String getScreenName();

    void loadMenuSlots(MachineUI.Menu menu);
}
