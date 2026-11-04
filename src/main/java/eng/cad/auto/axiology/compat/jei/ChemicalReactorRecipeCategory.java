package eng.cad.auto.axiology.compat.jei;

import eng.cad.auto.axiology.AxiomMod;
import eng.cad.auto.axiology.crafting.machines.ChemicalReactorRecipe;
import eng.cad.auto.axiology.machines.SoakingDrum;
import eng.cad.auto.axiology.registry.MachineRegistry;
import eng.cad.auto.engine.crafting.core.CraftingIngredient;
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

public class ChemicalReactorRecipeCategory implements IRecipeCategory<ChemicalReactorRecipe> {

    private static final ResourceLocation LOCATION = ResourceLocation.fromNamespaceAndPath(AxiomMod.MODID, "menu/chemical_reactor_menu.png");

    private static final int TRIM = 4;
    private static final int WIDTH = 176-TRIM*2;
    private static final int HEIGHT = 80-TRIM;

    private static final Vector2i TANK_0 = new Vector2i(50, 22).sub(TRIM, TRIM);
    private static final Vector2i TANK_1 = new Vector2i(69, 22).sub(TRIM, TRIM);
    private static final Vector2i TANK_2 = new Vector2i(110, 22).sub(TRIM, TRIM);

    private static final Vector2i TANK_SIZE = new Vector2i(16, 32);
    private static final Vector2i GLASS_UV = new Vector2i(0, 166);


    private final IDrawable icon;
    private final IDrawable background;
    private final IDrawable glassOverlay;

    public ChemicalReactorRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(MachineRegistry.MACHINELIST.get(SoakingDrum.name).blockItem.get().getDefaultInstance());
        this.background = guiHelper.createDrawable(LOCATION, TRIM, TRIM, WIDTH, HEIGHT);
        this.glassOverlay = guiHelper.createDrawable(LOCATION, GLASS_UV.x, GLASS_UV.y, TANK_SIZE.x, TANK_SIZE.y);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ChemicalReactorRecipe recipe, IFocusGroup focuses) {
        NonNullList<CraftingIngredient> inputs = recipe.craftingIngredients;
        NonNullList<CraftingIngredient> outputs = recipe.craftingOutputs;

        builder.addInputSlot(TANK_0.x, TANK_0.y)
            .setFluidRenderer(inputs.get(0).getFluid().getAmount(), false, TANK_SIZE.x, TANK_SIZE.y)
            .setOverlay(glassOverlay, 0, 0)
            .addFluidStack(inputs.get(0).getFluid().getFluid(), inputs.get(0).getFluid().getAmount());

        builder.addOutputSlot(TANK_1.x, TANK_1.y)
            .setFluidRenderer(inputs.get(1).getFluid().getAmount(), false, TANK_SIZE.x, TANK_SIZE.y)
            .setOverlay(glassOverlay, 0, 0)
            .addFluidStack(inputs.get(1).getFluid().getFluid(), inputs.get(1).getFluid().getAmount());

        builder.addOutputSlot(TANK_2.x, TANK_2.y)
                .setFluidRenderer(outputs.get(0).getFluid().getAmount(), false, TANK_SIZE.x, TANK_SIZE.y)
                .setOverlay(glassOverlay, 0, 0)
                .addFluidStack(outputs.get(0).getFluid().getFluid(), outputs.get(0).getFluid().getAmount());

    }

    @Override
    public void draw(ChemicalReactorRecipe recipe, IRecipeSlotsView slotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics);
    }

    @Override public RecipeType<ChemicalReactorRecipe> getRecipeType() { return AxiologyJEI.CHEMICAL_REACTOR_RECIPE_TYPE; }
    @Override public Component getTitle() { return Component.translatable("axiology.jei.chemical.reactor"); }
    @Override public IDrawable getIcon() { return icon; }
    @Override public int getWidth() { return WIDTH; }
    @Override public int getHeight() { return HEIGHT; }
}