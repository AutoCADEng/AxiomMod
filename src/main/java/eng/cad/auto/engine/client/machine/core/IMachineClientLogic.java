package eng.cad.auto.engine.client.machine.core;

import eng.cad.auto.engine.machine.core.GenericMachineEntity;
import eng.cad.auto.engine.machine.core.ui.GenericUI;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IMachineClientLogic {
    public void renderLogic(GuiGraphics guiGraphics, float tick, int mouse_x, int mouse_y, GenericUI.Screen screen);
    public void clientTick(Level level, BlockPos pos, BlockState state, GenericMachineEntity blockEntity);
}
