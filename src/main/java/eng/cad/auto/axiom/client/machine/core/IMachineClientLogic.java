package eng.cad.auto.axiom.client.machine.core;

import eng.cad.auto.axiom.common.machine.ui.GenericUI;
import net.minecraft.client.gui.GuiGraphics;

public interface IMachineClientLogic {
    public void renderLogic(GuiGraphics guiGraphics, float tick, int mouse_x, int mouse_y, GenericUI.Screen screen);

}
