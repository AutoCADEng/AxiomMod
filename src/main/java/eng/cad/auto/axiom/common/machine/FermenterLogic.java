package eng.cad.auto.axiom.common.machine;

import eng.cad.auto.axiom.common.machine.core.GenericBlockEntity;
import eng.cad.auto.axiom.common.machine.core.GenericUI;
import eng.cad.auto.axiom.common.machine.core.IMachineClientLogic;
import eng.cad.auto.axiom.common.machine.core.IMachineLogic;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class FermenterLogic implements IMachineLogic {
    static int Processing_Ticks = 200;
    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (!(level.getBlockEntity(pos) instanceof GenericBlockEntity blockEntity)) return;
        if (blockEntity.machine.metadata.get(0) >= Processing_Ticks) {
            //Do crafting
            resetProgress(blockEntity);
            return ;
        }
        blockEntity.machine.metadata.set(1,1);
        incrementProgress(blockEntity, 2);
    }

    public void resetProgress(GenericBlockEntity blockEntity){
        blockEntity.machine.metadata.set(0,0);
    }

    public void incrementProgress(GenericBlockEntity blockEntity, int value){
        blockEntity.machine.metadata.set(0,Math.min(Processing_Ticks, blockEntity.machine.metadata.get(0) + value));
    }

    public static class Client implements IMachineClientLogic{

        @Override
        public void renderLogic(GuiGraphics guiGraphics, float tick, int mouse_x, int mouse_y, GenericUI.Screen screen) {
            float progressPercentage = (float) screen.getMenu().data.get(0)/ (float) Processing_Ticks;
            int progressWidth = (int) (59*progressPercentage);
            if (screen.getMenu().data.get(1) == 1) {
                guiGraphics.blit(screen.GUI, screen.getGuiLeft() + 46, screen.getGuiTop() + 57, 1, 167, 83, 14);
            }

            if (progressWidth > 0) {
                guiGraphics.blit(screen.GUI,screen.getGuiLeft() + 42, screen.getGuiTop() + 17,1, 182, progressWidth, 27);
            }
        }
    }
}
