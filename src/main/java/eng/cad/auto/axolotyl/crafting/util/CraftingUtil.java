package eng.cad.auto.axolotyl.crafting.util;

import eng.cad.auto.axolotyl.crafting.core.CraftingIngredient;
import eng.cad.auto.axolotyl.crafting.core.CustomCraftingContainer;
import eng.cad.auto.axolotyl.crafting.core.GenericRecipe;
import eng.cad.auto.axolotyl.machine.core.GenericMachineEntity;
import eng.cad.auto.axolotyl.machine.core.slot.SlotIORestriction;
import eng.cad.auto.axolotyl.machine.multiblock.GenericFollowerEntity;
import eng.cad.auto.axolotyl.machine.multiblock.GenericMasterEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class CraftingUtil {

    public static boolean craft(GenericMachineEntity entity, GenericRecipe recipe){
        if (!validate(entity, null, recipe)) return false;

        for (CraftingIngredient ingredient: recipe.getCraftingIngredients()){
            consume(ingredient, null, entity, recipe);
        }

        for (CraftingIngredient output: recipe.getCraftingOutput()){
            produce(output, null, entity, recipe);
        }
        return true;
    }

    public static boolean craft(GenericMasterEntity entity, ArrayList<GenericFollowerEntity> followers, GenericRecipe recipe){
        if (!validate(entity, followers, recipe)) return false;

        for (CraftingIngredient ingredient: recipe.getCraftingIngredients()){
            consume(ingredient, followers, entity, recipe);
        }

        for (CraftingIngredient output: recipe.getCraftingOutput()){
            produce(output, followers, entity, recipe);
        }
        return true;
    }

    private static void produce(CraftingIngredient target, ArrayList<GenericFollowerEntity> followers, GenericMachineEntity blockEntity, GenericRecipe recipe) {
        int required = 0;

        ArrayList<GenericMachineEntity> machines = new ArrayList<>();
        machines.add(blockEntity);
        if (followers != null){
            machines.addAll(followers);
        }


        if (target.isItem()) {
            required = target.getItem().getCount();
            for (GenericMachineEntity machine : machines) {
                if (machine.get().inventory == null) continue;
                for (int idx = 0; idx < machine.get().inventory.getSlots(); idx++) {
                    if (machine.machineConfig.itemSlotConfig().get(idx).restriction() == SlotIORestriction.INPUT)
                        continue;
                    ItemStack itemStack = machine.get().inventory.getStackInSlot(idx);
                    if (itemStack.is(target.getItem().getItem()) || itemStack.isEmpty()) {
                        if (required <= 0) break;
                        int originalCount = itemStack.getCount();
                        int space = Math.min(machine.get().inventory.getSlotLimit(idx), target.getItem().getMaxStackSize()) - originalCount;
                        int producedItems = Math.min(required, space);
                        if (producedItems <= 0) continue;
                        ItemStack produced = target.getItem().copy();
                        produced.setCount(originalCount + producedItems);
                        machine.get().inventory.setStackInSlot(idx, produced);
                        required -= producedItems;
                    }
                }
            }
        }

        if (target.isFluid()) {
            required = target.getFluid().getAmount();
            for (GenericMachineEntity machine: machines) {
                if (machine.get().tanks == null) continue;
                for (int idx = 0; idx < machine.get().tanks.getTanks(); idx++) {
                    if (machine.machineConfig.fluidSlotConfig().get(idx).restriction() == SlotIORestriction.INPUT)
                        continue;
                    FluidStack fluidStack = machine.get().tanks.getFluidInTank(idx);
                    if (fluidStack.isFluidEqual(target.getFluid()) || fluidStack.isEmpty()) {
                        if (required <= 0) break;
                        int originalCount = fluidStack.getAmount();
                        int space = machine.get().tanks.getTankCapacity(idx) - originalCount;
                        int producedFluid = Math.min(required, space);
                        if (producedFluid <= 0) continue;
                        FluidStack produced = target.getFluid().copy();
                        produced.setAmount(originalCount + producedFluid);
                        machine.get().tanks.setStackInSlot(idx, produced);
                        required -= producedFluid;
                    }
                }
            }
        }
    }

    private static void consume(CraftingIngredient target, ArrayList<GenericFollowerEntity> followers, GenericMachineEntity blockEntity, GenericRecipe recipe) {
        int required = 0;
        ArrayList<GenericMachineEntity> machines = new ArrayList<>();
        machines.add(blockEntity);
        if (followers != null){
            machines.addAll(followers);
        }
        if (target.isItem()) {
            required = target.getItem().getCount();
            for (GenericMachineEntity machine : machines) {
                if (machine.get().inventory == null) continue;
                for (int idx = 0; idx < machine.get().inventory.getSlots(); idx++) {
                    if (machine.machineConfig.itemSlotConfig().get(idx).restriction() == SlotIORestriction.OUTPUT)
                        continue;
                    ItemStack itemStack = machine.get().inventory.getStackInSlot(idx);
                    if (itemStack.is(target.getItem().getItem())) {
                        if (required <= 0) break;
                        int originalCount = itemStack.getCount();
                        int remainingItems = Math.max(originalCount - Math.min(itemStack.getMaxStackSize(), required), 0);
                        machine.get().inventory.setStackInSlot(idx, remainingItems == 0 ? ItemStack.EMPTY : new ItemStack(itemStack.getItem(), remainingItems));
                        int taken = originalCount - remainingItems;
                        required -= taken;
                    }
                }
            }
        }

        if (target.isFluid()) {
            required = target.getFluid().getAmount();
            for (GenericMachineEntity machine: machines) {
                if (machine.get().tanks == null) continue;
                for (int idx = 0; idx < machine.get().tanks.getTanks(); idx++) {
                    if (machine.machineConfig.fluidSlotConfig().get(idx).restriction() == SlotIORestriction.OUTPUT)
                        continue;
                    FluidStack fluidStack = machine.get().tanks.getFluidInTank(idx);
                    if (fluidStack.isFluidEqual(target.getFluid())) {
                        if (required <= 0) break;
                        int originalCount = fluidStack.getAmount();
                        int remainingVolume = Math.max(originalCount - Math.min(machine.get().tanks.getTankCapacity(idx), required), 0);
                        machine.get().tanks.setStackInSlot(idx, remainingVolume == 0 ? FluidStack.EMPTY : new FluidStack(fluidStack.getFluid(), remainingVolume));
                        int taken = originalCount - remainingVolume;
                        required -= taken;
                    }
                }
            }
        }
    }

    public static boolean validate(GenericMachineEntity entity, GenericRecipe recipe){
        return validate(entity, null, recipe);
    }
    public static boolean validate(GenericMachineEntity blockEntity, ArrayList<GenericFollowerEntity> followers, GenericRecipe recipe){
        HashMap<Item, Integer> recipeItems = new HashMap<>();
        HashMap<Fluid, Integer> recipeFluids = new HashMap<>();

        ArrayList<GenericMachineEntity> machines = new ArrayList<>();
        machines.add(blockEntity);
        if (followers != null){
            machines.addAll(followers);
        }

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
        for (GenericMachineEntity machine: machines) {
            if (machine.get().inventory != null) {
                for (int idx = 0; idx < machine.get().inventory.getSlots(); idx++) {
                    if (machine.get().config.itemSlotConfig().get(idx).restriction() == SlotIORestriction.OUTPUT) continue;
                    ItemStack stack = machine.get().inventory.getStackInSlot(idx);
                    if (recipeItems.containsKey(stack.getItem())) {
                        recipeItems.put(stack.getItem(), recipeItems.get(stack.getItem()) - stack.getCount());
                    }
                }
            }

            if (machine.get().tanks != null) {
                for (int idx = 0; idx < machine.get().tanks.getTanks(); idx++) {
                    if (machine.get().config.fluidSlotConfig().get(idx).restriction() == SlotIORestriction.OUTPUT) continue;
                    FluidStack stack = machine.get().tanks.getFluidInTank(idx);
                    if (recipeFluids.containsKey(stack.getFluid())) {
                        recipeFluids.put(stack.getFluid(), recipeFluids.get(stack.getFluid()) - stack.getAmount());
                    }
                }
            }
        }
        return recipeItems.values().stream().allMatch(count -> count <= 0) && recipeFluids.values().stream().allMatch(count -> count <= 0);
    }

    public static GenericRecipe getActiveRecipe(CustomCraftingContainer machineInventory, RecipeType<GenericRecipe> type, Level level, GenericMachineEntity blockEntity ){
                Optional<GenericRecipe> recipeOutput = level.getRecipeManager()
                .getAllRecipesFor(type)
                .stream()
                .filter(recipe -> recipe.matchesTypeAndQuantity(machineInventory, level)).findFirst();

        return recipeOutput.orElse(null);
    }

    public static boolean hasSpace(GenericMachineEntity blockEntity, GenericRecipe recipe) {
        return hasSpace(blockEntity, null, recipe);
    }

    public static boolean hasSpace(GenericMachineEntity blockEntity, ArrayList<GenericFollowerEntity> followers, GenericRecipe recipe) {
        HashMap<GenericMachineEntity, ItemStack[]> recipeItems = new HashMap<>();
        HashMap<GenericMachineEntity, FluidStack[]> recipeFluids = new HashMap<>();

        ArrayList<GenericMachineEntity> machines = new ArrayList<>();
        machines.add(blockEntity);

        if (followers != null){
            machines.addAll(followers);
        }

        for (GenericMachineEntity machine: machines) {
            ItemStack[] items = new ItemStack[machine.get().inventory == null ? 0 : machine.get().inventory.getSlots()];
            for (int idx = 0; idx < items.length; idx++) {
                items[idx] = machine.get().inventory.getStackInSlot(idx).copy();
            }
            recipeItems.put(machine, items);

            FluidStack[] fluids = new FluidStack[machine.get().tanks == null ? 0 : machine.get().tanks.getTanks()];
            for (int idx = 0; idx < fluids.length; idx++) {
                fluids[idx] = machine.get().tanks.getFluidInTank(idx).copy();
            }
            recipeFluids.put(machine, fluids);
        }

        for (CraftingIngredient target: recipe.getCraftingOutput()) {
            int required = 0;

            for (GenericMachineEntity machine: machines) {
                if (target.isItem()) {
                    if (machine == machines.get(0)) required = target.getItem().getCount();
                    ItemStack[] items = recipeItems.get(machine);

                    for (int idx = 0; idx < items.length; idx++) {
                        if (machine.machineConfig.itemSlotConfig().get(idx).restriction() == SlotIORestriction.INPUT) continue;
                        ItemStack itemStack = items[idx];
                        if (itemStack.is(target.getItem().getItem()) || itemStack.isEmpty()) {
                            if (required <= 0) break;
                            int originalCount = itemStack.getCount();
                            int space = Math.min(machine.get().inventory.getSlotLimit(idx), target.getItem().getMaxStackSize()) - originalCount;
                            int producedItems = Math.min(required, space);
                            if (producedItems <= 0) continue;
                            ItemStack produced = target.getItem().copy();
                            produced.setCount(originalCount + producedItems);
                            items[idx] = produced;
                            required -= producedItems;
                        }
                    }
                }

                if (target.isFluid()) {
                    if (machine == machines.get(0)) required = target.getFluid().getAmount();
                    FluidStack[] fluids = recipeFluids.get(machine);

                    for (int idx = 0; idx < fluids.length; idx++) {
                        if (machine.machineConfig.fluidSlotConfig().get(idx).restriction() == SlotIORestriction.INPUT) continue;
                        FluidStack fluidStack = fluids[idx];
                        if (fluidStack.isFluidEqual(target.getFluid()) || fluidStack.isEmpty()) {
                            if (required <= 0) break;
                            int originalCount = fluidStack.getAmount();
                            int space = machine.get().tanks.getTankCapacity(idx) - originalCount;
                            int producedFluid = Math.min(required, space);
                            if (producedFluid <= 0) continue;
                            FluidStack produced = target.getFluid().copy();
                            produced.setAmount(originalCount + producedFluid);
                            fluids[idx] = produced;
                            required -= producedFluid;
                        }
                    }
                }
            }

            if (required > 0) return false;
        }
        return true;
    }
}
