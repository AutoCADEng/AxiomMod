package eng.cad.auto.axiology.crafting;

import eng.cad.auto.engine.crafting.core.AbstractRecipe;
import eng.cad.auto.engine.crafting.core.CraftingIngredient;
import eng.cad.auto.engine.crafting.core.CustomCraftingContainer;
import eng.cad.auto.engine.machine.core.GenericMachineEntity;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public abstract class AbstractMachineRecipe<T extends AbstractMachineRecipe<T>> extends AbstractRecipe<CustomCraftingContainer, T>{
    public AbstractMachineRecipe(NonNullList<CraftingIngredient> input, NonNullList<CraftingIngredient> outputs, ResourceLocation craftingRecipe) {
        super(craftingRecipe, input, outputs);
    }

    @Override
    public ResourceLocation getId() {
        return craftingRecipe;
    }

    public static <T extends AbstractMachineRecipe<T>> T getActiveRecipe(CustomCraftingContainer machineInventory, RecipeType<T> type, Level level, GenericMachineEntity blockEntity) {
        return level.getRecipeManager()
                .getAllRecipesFor(type)
                .stream()
                .filter(recipe -> recipe.matchesTypeAndQuantity(machineInventory, level, recipe))
                .findFirst()
                .orElse(null);
    }

    @Override
    public abstract RecipeSerializer<T> getSerializer();

    @Override
    public abstract RecipeType<T> getType();

    public abstract static class MachineType<T extends AbstractMachineRecipe<T>> implements RecipeType<T> { }

    public abstract static class MachineSerializer<T extends AbstractMachineRecipe<T>> extends AbstractRecipe.Serializer<CustomCraftingContainer, T> { }
}