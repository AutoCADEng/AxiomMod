package eng.cad.auto.engine.crafting.core;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class BaseRecipe<C extends CustomCraftingContainer, T extends AbstractRecipe<C, T>>{
    public final ResourceLocation craftingRecipe;
    public final NonNullList<CraftingIngredient> craftingIngredients;
    public final NonNullList<CraftingIngredient> craftingOutputs;

    protected BaseRecipe(ResourceLocation craftingRecipe, NonNullList<CraftingIngredient> input, NonNullList<CraftingIngredient> output) {
        this.craftingRecipe = craftingRecipe;
        this.craftingIngredients = input;
        this.craftingOutputs = output;
    }

    public boolean matchesTypeAndQuantity(C machineInventory, Level level, T recipe){
        if (!recipe.matches(machineInventory, level)) return false;
        for (CraftingIngredient ingredient: craftingIngredients){
            if (ingredient.isEmpty() || machineInventory.isEmpty()) return false;
            switch (ingredient.getType()){
                case ITEM -> {
                    if(machineInventory.items.stream().noneMatch(itemstack -> {
                        if (!itemstack.is(ingredient.getItem().getItem())) return false;
                        return itemstack.getCount() >= ingredient.getItem().getCount();
                    })
                    ) return false;
                }
                case FLUID -> {
                    if(machineInventory.tanks.stream().noneMatch(fluidstack -> {
                        if (!fluidstack.getFluid().isSame(ingredient.getFluid().getFluid())) return false;
                        return fluidstack.getAmount() >= ingredient.getFluid().getAmount();
                    })
                    ) return false;
                }
                case ITEM_TAG -> {
                    if(machineInventory.items.stream().noneMatch(itemstack -> {
                        if (!itemstack.is(ingredient.getItemTag())) return false;
                        return itemstack.getCount() >= ingredient.getTag().count();
                    })) return false;
                }
                case FLUID_TAG -> {
                    if(machineInventory.tanks.stream().noneMatch(fluidstack -> {
                        if (!fluidstack.getFluid().defaultFluidState().is(ingredient.getFluidTag())) return false;
                        return fluidstack.getAmount() >= ingredient.getTag().count();
                    })) return false;
                }
                case EMPTY -> {
                    return false;
                }
            }
        }
        return true;
    }

    public NonNullList<CraftingIngredient> getCraftingIngredients() {
        return craftingIngredients;
    }

    public NonNullList<CraftingIngredient> getCraftingOutput() {
        return craftingOutputs;
    }
}
