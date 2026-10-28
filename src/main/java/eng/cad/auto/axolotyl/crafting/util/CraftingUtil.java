package eng.cad.auto.axolotyl.crafting.util;

import eng.cad.auto.axolotyl.crafting.core.CraftingIngredient;
import eng.cad.auto.axolotyl.crafting.core.CustomCraftingContainer;
import eng.cad.auto.axolotyl.crafting.core.GenericRecipe;
import eng.cad.auto.axolotyl.machine.core.GenericBlockEntity;
import eng.cad.auto.axolotyl.machine.core.slot.SlotIORestriction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.HashMap;
import java.util.Optional;

public class CraftingUtil {

    public static boolean craft(GenericBlockEntity entity, GenericRecipe recipe){
        if (!validate(entity, recipe)) return false;

        for (CraftingIngredient ingredient: recipe.getCraftingIngredients()){
            consume(ingredient, entity, recipe);
        }

        for (CraftingIngredient output: recipe.getCraftingOutput()){
            produce(output, entity, recipe);
        }
        return true;
    }

    private static void produce(CraftingIngredient target, GenericBlockEntity blockEntity, GenericRecipe recipe) {
        int required = 0;
        if (target.isItem()) {
            required = target.getItem().getCount();
            for (int idx = 0; idx < blockEntity.machine.inventory.getSlots(); idx++) {
                if (blockEntity.machineConfig.itemSlotConfig().get(idx).restriction() == SlotIORestriction.INPUT) continue;
                ItemStack itemStack = blockEntity.machine.inventory.getStackInSlot(idx);
                if (itemStack.is(target.getItem().getItem()) || itemStack.isEmpty()){
                    if (required <= 0) break;
                    int originalCount = itemStack.getCount();
                    int space = Math.min(blockEntity.machine.inventory.getSlotLimit(idx), target.getItem().getMaxStackSize()) - originalCount;
                    int producedItems = Math.min(required, space);
                    if (producedItems <= 0) continue;
                    ItemStack produced = target.getItem().copy();
                    produced.setCount(originalCount + producedItems);
                    blockEntity.machine.inventory.setStackInSlot(idx, produced);
                    required -= producedItems;
                }
            }
        }

        if (target.isFluid()) {
            required = target.getFluid().getAmount();
            for (int idx = 0; idx < blockEntity.machine.tanks.getTanks(); idx++) {
                if (blockEntity.machineConfig.fluidSlotConfig().get(idx).restriction() == SlotIORestriction.INPUT) continue;
                FluidStack fluidStack = blockEntity.machine.tanks.getFluidInTank(idx);
                if (fluidStack.isFluidEqual(target.getFluid()) || fluidStack.isEmpty()){
                    if (required <= 0) break;
                    int originalCount = fluidStack.getAmount();
                    int space = blockEntity.machine.tanks.getTankCapacity(idx) - originalCount;
                    int producedFluid = Math.min(required, space);
                    if (producedFluid <= 0) continue;
                    FluidStack produced = target.getFluid().copy();
                    produced.setAmount(originalCount + producedFluid);
                    blockEntity.machine.tanks.setStackInSlot(idx, produced);
                    required -= producedFluid;
                }
            }
        }
    }

    private static void consume(CraftingIngredient target, GenericBlockEntity blockEntity, GenericRecipe recipe) {
        int required = 0;
        if (target.isItem()) {
            required = target.getItem().getCount();
            for (int idx = 0; idx < blockEntity.machine.inventory.getSlots(); idx++) {
                if (blockEntity.machineConfig.itemSlotConfig().get(idx).restriction() == SlotIORestriction.OUTPUT) continue;
                ItemStack itemStack = blockEntity.machine.inventory.getStackInSlot(idx);
                if (itemStack.is(target.getItem().getItem())) {
                    if (required <= 0) break;
                    int originalCount = itemStack.getCount();
                    int remainingItems = Math.max(originalCount - Math.min(itemStack.getMaxStackSize(), required), 0);
                    blockEntity.machine.inventory.setStackInSlot(idx, remainingItems == 0 ? ItemStack.EMPTY : new ItemStack(itemStack.getItem(), remainingItems));
                    required = originalCount - remainingItems;
                }
            }
        }

        if (target.isFluid()) {
            required = target.getFluid().getAmount();
            for (int idx = 0; idx < blockEntity.machine.tanks.getTanks(); idx++) {
                if (blockEntity.machineConfig.fluidSlotConfig().get(idx).restriction() == SlotIORestriction.OUTPUT) continue;
                FluidStack fluidStack = blockEntity.machine.tanks.getFluidInTank(idx);
                if (fluidStack.isFluidEqual(target.getFluid())) {
                    if (required <= 0) break;
                    int originalCount = fluidStack.getAmount();
                    int remainingVolume = Math.max(originalCount - Math.min(blockEntity.machine.tanks.getTankCapacity(idx), required), 0);
                    blockEntity.machine.tanks.setStackInSlot(idx, remainingVolume == 0 ? FluidStack.EMPTY : new FluidStack(fluidStack.getFluid(), remainingVolume));
                    required = originalCount - remainingVolume;
                }
            }
        }
    }

    public static boolean validate(GenericBlockEntity entity, GenericRecipe recipe){
        HashMap<Item, Integer> recipeItems = new HashMap<>();
        HashMap<Fluid, Integer> recipeFluids = new HashMap<>();

        for (CraftingIngredient ingredient: recipe.getCraftingIngredients()){
            if (ingredient.isItem()) {
                ItemStack itemStack = ingredient.getItem();

                if (recipeItems.containsKey(itemStack.getItem())){
                    recipeItems.put(itemStack.getItem(), recipeItems.get(itemStack.getItem()) + itemStack.getCount());
                } else {
                    recipeItems.put(itemStack.getItem(),  itemStack.getCount());
                }
            }

            if (ingredient.isFluid()) {
                FluidStack fluidStack = ingredient.getFluid();

                if (recipeFluids.containsKey(fluidStack.getFluid())){
                    recipeFluids.put(fluidStack.getFluid(), recipeFluids.get(fluidStack.getFluid()) + fluidStack.getAmount());
                } else {
                    recipeFluids.put(fluidStack.getFluid(),  fluidStack.getAmount());
                }
            }
        }
        if (entity.machine.inventory != null) {
            for (int idx = 0; idx < entity.machine.inventory.getSlots(); idx++) {
                ItemStack stack = entity.machine.inventory.getStackInSlot(idx);
                if (recipeItems.containsKey(stack.getItem())) {
                    recipeItems.put(stack.getItem(), recipeItems.get(stack.getItem()) - stack.getCount());
                }
            }
        }

        if (entity.machine.tanks != null) {
            for (int idx = 0; idx < entity.machine.tanks.getTanks(); idx++) {
                FluidStack stack = entity.machine.tanks.getFluidInTank(idx);
                if (recipeFluids.containsKey(stack.getFluid())) {
                    recipeFluids.put(stack.getFluid(), recipeFluids.get(stack.getFluid()) - stack.getAmount());
                }
            }
        }
        return recipeItems.values().stream().allMatch(count -> count <= 0) && recipeFluids.values().stream().allMatch(count -> count <= 0);
    }

    public static GenericRecipe getActiveRecipe(CustomCraftingContainer machineInventory, RecipeType<GenericRecipe> type, Level level, GenericBlockEntity blockEntity ){
        Optional<GenericRecipe> recipeOutput = level.getRecipeManager()
                .getAllRecipesFor(type)
                .stream()
                .filter(recipe -> recipe.matchesTypeAndQuantity(machineInventory, level)).findFirst();

        return recipeOutput.orElse(null);
    }

    public static boolean hasSpace(GenericBlockEntity blockEntity, GenericRecipe recipe) {
        return true;
    }
}
