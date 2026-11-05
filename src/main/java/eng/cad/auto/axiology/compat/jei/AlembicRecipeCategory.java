package eng.cad.auto.axiology.compat.jei;

import eng.cad.auto.axiology.AxiomMod;
import eng.cad.auto.axiology.crafting.machines.AlembicRecipe;
import eng.cad.auto.axiology.machines.Alembic;
import eng.cad.auto.axiology.machines.SoakingDrum;
import eng.cad.auto.axiology.registry.MachineRegistry;
import eng.cad.auto.axiology.core.slot.UniversalSlot;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector2i;

public class AlembicRecipeCategory implements IRecipeCategory<AlembicRecipe> {

    private static final ResourceLocation LOCATION = ResourceLocation.fromNamespaceAndPath(AxiomMod.MODID, "menu/alembic_menu.png");

    private static final int TRIM = 4;
    private static final int WIDTH = 176-TRIM*2;
    private static final int HEIGHT = 80-TRIM;

    private static final Vector2i TANK_0 = new Vector2i(59, 27).sub(TRIM, TRIM);
    private static final Vector2i TANK_1 = new Vector2i(98, 27).sub(TRIM, TRIM);

    private static final Vector2i TANK_SIZE = new Vector2i(16, 32);
    private static final Vector2i GLASS_UV = new Vector2i(0, 166);


    private final IDrawable icon;
    private final IDrawable background;
    private final IDrawable glassOverlay;

    public AlembicRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(MachineRegistry.MACHINELIST.get(Alembic.name).blockItem.get().getDefaultInstance());
        this.background = guiHelper.createDrawable(LOCATION, TRIM, TRIM, WIDTH, HEIGHT);
        this.glassOverlay = guiHelper.createDrawable(LOCATION, GLASS_UV.x, GLASS_UV.y, TANK_SIZE.x, TANK_SIZE.y);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AlembicRecipe recipe, IFocusGroup focuses) {
        NonNullList<UniversalSlot> inputs = recipe.craftingIngredients;
        NonNullList<UniversalSlot> outputs = recipe.craftingOutputs;

        builder.addInputSlot(TANK_0.x, TANK_0.y)
            .setFluidRenderer(inputs.get(0).getFluid().getAmount(), false, TANK_SIZE.x, TANK_SIZE.y)
            .setOverlay(glassOverlay, 0, 0)
            .addFluidStack(inputs.get(0).getFluid().getFluid(), inputs.get(0).getFluid().getAmount());

        builder.addOutputSlot(TANK_1.x, TANK_1.y)
            .setFluidRenderer(outputs.get(0).getFluid().getAmount(), false, TANK_SIZE.x, TANK_SIZE.y)
            .setOverlay(glassOverlay, 0, 0)
            .addFluidStack(outputs.get(0).getFluid().getFluid(), outputs.get(0).getFluid().getAmount());

    }

    @Override
    public void draw(AlembicRecipe recipe, IRecipeSlotsView slotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics);
    }

    @Override public RecipeType<AlembicRecipe> getRecipeType() { return AxiologyJEI.ALEMBIC_RECIPE_TYPE; }
    @Override public Component getTitle() { return Component.translatable("axiology.jei.alembic"); }
    @Override public IDrawable getIcon() { return icon; }
    @Override public int getWidth() { return WIDTH; }
    @Override public int getHeight() { return HEIGHT; }
}