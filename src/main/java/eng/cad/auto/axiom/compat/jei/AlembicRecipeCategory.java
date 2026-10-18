package eng.cad.auto.axiom.compat.jei;

import eng.cad.auto.axiom.AxiomMod;
import eng.cad.auto.axiom.recipes.AlembicRecipe;
import eng.cad.auto.axiom.registry.BlockRegistry;
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
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class AlembicRecipeCategory implements IRecipeCategory<AlembicRecipe> {
    final IGuiHelper guiHelper;
    IDrawable icon, background;
    int hTrim = 8;
    int vTrim = 4;
    int playerInventoryOffset = 80;
    int width = 176;
    int height = 166;
    private static final ResourceLocation ALEMBIC_LOCATION = ResourceLocation.fromNamespaceAndPath(AxiomMod.MODID, "menu/alembic_menu.png");
    public AlembicRecipeCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        this.icon = guiHelper.createDrawableItemStack(BlockRegistry.ALEMBIC.get().asItem().getDefaultInstance());
        this.background = guiHelper.createDrawable(ALEMBIC_LOCATION,0,0, width, height);
    }

    /**
     * @return
     */
    @Override
    public RecipeType<AlembicRecipe> getRecipeType() {
        return AxiologyJEICompat.ALEMBIC_RECIPE_TYPE;
    }

    /**
     * @return
     */
    @Override
    public Component getTitle() {
        return Component.translatable("axiology.jei.alembic");
    }

    /**
     * @return
     */
    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    /**
     * @param builder
     * @param recipe
     * @param focuses
     */
    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AlembicRecipe recipe, IFocusGroup focuses) {
        builder.addInputSlot(31 - hTrim, 36 - vTrim).addIngredients(recipe.getInput());
        NonNullList<ItemStack> itemStacks = recipe.getOutput().outputItems;
        for (int i = 0; i < Math.min(itemStacks.size(), 4); i++) {
            int x = 102 - hTrim + (i % 3) * 18;
            int y = 18 - vTrim + (i / 3) * 18;

            builder.addOutputSlot(x, y)
                    .addItemStack(itemStacks.get(i));
        }
    }

    /**
     * @param recipe          the current recipe being drawn.
     * @param recipeSlotsView a view of the current recipe slots being drawn.
     * @param guiGraphics     the current {@link GuiGraphics} for rendering.
     * @param mouseX          the X position of the mouse, relative to the recipe.
     * @param mouseY          the Y position of the mouse, relative to the recipe.
     */
    @Override
    public void draw(AlembicRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {

        int width = getWidth();
        int height = getHeight();

        guiGraphics.blit(
                ALEMBIC_LOCATION,
                0,0,
                hTrim, vTrim,
                width, height
        );

        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
    }

    @Override
    public int getWidth() {
        return background.getWidth() - (hTrim * 2);
    }

    @Override
    public int getHeight() {
        return playerInventoryOffset - vTrim;
    }
}
