package eng.cad.auto.axiom.ui;

import eng.cad.auto.axiom.AxiomMod;
import eng.cad.auto.axiom.axiomancy.blockentitiy.AlembicBlockEntity;
import eng.cad.auto.axiom.compat.jei.AlembicRecipeCategory;
import eng.cad.auto.axiom.compat.jei.AxiologyJEICompat;
import eng.cad.auto.axiom.recipes.AlembicRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;


@OnlyIn(Dist.CLIENT)
public class AlembicScreen extends AbstractContainerScreen<AlembicMenu> {
    private static final ResourceLocation ALEMBIC_LOCATION = ResourceLocation.fromNamespaceAndPath(AxiomMod.MODID, "menu/alembic_menu.png");

    public AlembicScreen(AlembicMenu p_97741_, Inventory p_97742_, Component p_97743_) {
        super(p_97741_, p_97742_, p_97743_);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float tick, int mouse_x, int mouse_y) {
        int width = (this.width - this.imageWidth) / 2;
        int height = (this.height - this.imageHeight) / 2;
        float progressPercentage = (float) menu.data.get(0)/(float)AlembicBlockEntity.MAX_PROGRESS;
        int progressWidth = (int) (59 * progressPercentage);
        guiGraphics.blit(ALEMBIC_LOCATION, width, height, 0, 0, this.imageWidth, this.imageHeight);

        if (menu.data.get(1) == 1){
            guiGraphics.blit(ALEMBIC_LOCATION,width + 46, height + 57,1,167, 83,14);
        }

        if (progressWidth > 0) {
            guiGraphics.blit(ALEMBIC_LOCATION,width + 42, height + 17,1, 182, progressWidth, 27);
        }

    }

    @Override @ParametersAreNonnullByDefault
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);

    }

    @Override
    public void init(){
        super.init();
    }
}