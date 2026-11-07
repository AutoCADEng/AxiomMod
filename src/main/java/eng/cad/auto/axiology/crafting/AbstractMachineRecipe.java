package eng.cad.auto.axiology.crafting;

import eng.cad.auto.axiology.core.ISlotFluid;
import eng.cad.auto.axiology.core.ISlotItem;
import eng.cad.auto.axiology.core.machine.component.IMultiblockFollower;
import eng.cad.auto.axiology.core.machine.BaseMachine;
import eng.cad.auto.axiology.crafting.core.AbstractRecipe;
import eng.cad.auto.axiology.crafting.core.BaseRecipe;
import eng.cad.auto.axiology.core.slot.UniversalSlot;
import eng.cad.auto.axiology.crafting.core.CustomCraftingContainer;
import eng.cad.auto.axiology.core.slot.SlotIORestriction;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class AbstractMachineRecipe<T extends AbstractMachineRecipe<T>> extends AbstractRecipe<CustomCraftingContainer, T>{
    public AbstractMachineRecipe(NonNullList<UniversalSlot> input, NonNullList<UniversalSlot> outputs, ResourceLocation craftingRecipe) {
        super(craftingRecipe, input, outputs);
    }

    /**
     * See documentation for three-variable variant of craft
     * @param entity
     * @param recipe
     * @return
     * @param <G> an instance of BaseMachine
     */
    public <G extends BaseMachine<?>> boolean craft(G entity, BaseRecipe<?, ?> recipe){
        return craft(entity, null, recipe);
    }

    /**
     * Performs the action of crafting within a machine(base/follower/master) network. This method will alter inventories!
     * @param entity
     * @param followers
     * @param recipe
     * @param <F>
     * @param <G> an instance of BaseMachine
     * @return True if the recipe is valid, false if not
     */
    public <F extends BaseMachine, G extends BaseMachine<? extends IMultiblockFollower>> boolean craft(F entity, ArrayList<G> followers, BaseRecipe<?, ?> recipe){
        if (!validate(entity, followers, recipe)) return false; //Candidate for deletion, we always check at the machine level anyway.

        for (UniversalSlot ingredient: recipe.getCraftingIngredients()){
            consume(ingredient, followers, entity, recipe);
        }

        for (UniversalSlot output: recipe.getCraftingOutput()){
            produce(output, followers, entity, recipe);
        }
        return true;
    }

    private <G extends BaseMachine<? extends IMultiblockFollower>> void produce(UniversalSlot recipeOutput, ArrayList<G> followers, BaseMachine<?> blockEntity, BaseRecipe<?, ?>  recipe) {
        int required = 0;

        ArrayList<BaseMachine<?>> machines = new ArrayList<>();
        machines.add(blockEntity);
        if (followers != null){
            machines.addAll(followers);
        }

        if (recipeOutput.is(UniversalSlot.Kind.ITEM)) {
            required = recipeOutput.getItem().getCount();
            for (BaseMachine<?> machine : machines) {
                if (!(machine instanceof ISlotItem itemMachine)) continue;
                if (itemMachine.getItemSlots() == null) continue;
                for (int idx = 0; idx < itemMachine.getItemSlots().getSlots(); idx++) {
                    if (itemMachine.getItemSlots().operationNotAllowed(idx, SlotIORestriction.OUTPUT)) continue;
                    ItemStack itemStack = itemMachine.getItemSlots().getStackInSlot(idx);
                    if (itemStack.is(recipeOutput.getItem().getItem()) || itemStack.isEmpty()) {
                        if (required <= 0) break;
                        int originalCount = itemStack.getCount();
                        int space = Math.min(itemMachine.getItemSlots().getSlotLimit(idx), recipeOutput.getItem().getMaxStackSize()) - originalCount;
                        int producedItems = Math.min(required, space);
                        if (producedItems <= 0) continue;
                        ItemStack produced = recipeOutput.getItem().copy();
                        produced.setCount(originalCount + producedItems);
                        itemMachine.getItemSlots().setStackInSlot(idx, produced);
                        required -= producedItems;
                    }
                }
            }
        }

        if (recipeOutput.is(UniversalSlot.Kind.FLUID)) {
            required = recipeOutput.getFluid().getAmount();
            for (BaseMachine<?> machine: machines) {
                if (!(machine instanceof ISlotFluid fluidMachine)) continue;
                if (fluidMachine.getFluidSlots() == null) continue;
                for (int idx = 0; idx < fluidMachine.getFluidSlots().getTanks(); idx++) {
                    if (fluidMachine.getFluidSlots().operationNotAllowed(idx, SlotIORestriction.OUTPUT)) continue;
                    FluidStack fluidStack = fluidMachine.getFluidSlots().getFluidInTank(idx);
                    if (fluidStack.isFluidEqual(recipeOutput.getFluid()) || fluidStack.isEmpty()) {
                        if (required <= 0) break;
                        int originalCount = fluidStack.getAmount();
                        int space = fluidMachine.getFluidSlots().getTankCapacity(idx) - originalCount;
                        int producedFluid = Math.min(required, space);
                        if (producedFluid <= 0) continue;
                        FluidStack produced = recipeOutput.getFluid().copy();
                        produced.setAmount(originalCount + producedFluid);
                        fluidMachine.getFluidSlots().setStackInSlot(idx, produced);
                        required -= producedFluid;
                    }
                }
            }
        }
    }

    private <G extends BaseMachine<?>> void consume(UniversalSlot target, ArrayList<G> followers, BaseMachine<?> blockEntity, BaseRecipe<?, ?>  recipe) {
        int required = 0;
        ArrayList<BaseMachine<?>> machines = new ArrayList<>();
        machines.add(blockEntity);

        if (followers != null){
            machines.addAll(followers);
        }
        switch (target.getType()){
            case ITEM -> {
                required = target.getItem().getCount();
                for (BaseMachine machine : machines) {
                    if (!(machine instanceof ISlotItem itemMachine)) continue;
                    if (itemMachine.getItemSlots() == null) continue;
                    for (int idx = 0; idx < itemMachine.getItemSlots().getSlots(); idx++) {
                        if (itemMachine.getItemSlots().operationNotAllowed(idx, SlotIORestriction.INPUT)) continue;
                        ItemStack itemStack = itemMachine.getItemSlots().getStackInSlot(idx);
                        if (itemStack.is(target.getItem().getItem())) {
                            if (required <= 0) break;
                            int originalCount = itemStack.getCount();
                            int remainingItems = Math.max(originalCount - Math.min(itemStack.getMaxStackSize(), required), 0);
                            itemMachine.getItemSlots().setStackInSlot(idx, remainingItems == 0 ? ItemStack.EMPTY : new ItemStack(itemStack.getItem(), remainingItems));
                            int taken = originalCount - remainingItems;
                            required -= taken;
                        }
                    }
                }
            }
            case ITEM_TAG -> {
                required = target.getTag().count();
                for (BaseMachine machine : machines) {
                    if (!(machine instanceof ISlotItem itemMachine)) continue;
                    if (itemMachine.getItemSlots() == null) continue;
                    for (int idx = 0; idx < itemMachine.getItemSlots().getSlots(); idx++) {
                        if (itemMachine.getItemSlots().operationNotAllowed(idx, SlotIORestriction.INPUT)) continue;
                        ItemStack itemStack = itemMachine.getItemSlots().getStackInSlot(idx);
                        if (itemStack.is(target.getItemTag())){
                            if (required <= 0) break;
                            int originalCount = itemStack.getCount();
                            int remainingItems = Math.max(originalCount - Math.min(itemStack.getMaxStackSize(), required), 0);
                            itemMachine.getItemSlots().setStackInSlot(idx, remainingItems == 0 ? ItemStack.EMPTY : new ItemStack(itemStack.getItem(), remainingItems));
                            int taken = originalCount - remainingItems;
                            required -= taken;
                        }
                    }
                }
            }
            case FLUID -> {
                required = target.getFluid().getAmount();
                for (BaseMachine<?> machine: machines) {
                    if (!(machine instanceof ISlotFluid fluidMachine)) continue;
                    if (fluidMachine.getFluidSlots() == null) continue;
                    for (int idx = 0; idx < fluidMachine.getFluidSlots().getTanks(); idx++) {
                        if (fluidMachine.getFluidSlots().operationNotAllowed(idx, SlotIORestriction.INPUT)) continue;
                        FluidStack fluidStack = fluidMachine.getFluidSlots().getFluidInTank(idx);
                        if (fluidStack.isFluidEqual(target.getFluid())) {
                            if (required <= 0) break;
                            int originalCount = fluidStack.getAmount();
                            int remainingVolume = Math.max(originalCount - Math.min(fluidMachine.getFluidSlots().getTankCapacity(idx), required), 0);
                            fluidMachine.getFluidSlots().setStackInSlot(idx, remainingVolume == 0 ? FluidStack.EMPTY : new FluidStack(fluidStack.getFluid(), remainingVolume));
                            int taken = originalCount - remainingVolume;
                            required -= taken;
                        }
                    }
                }
            }
            case FLUID_TAG -> {
                required = target.getFluid().getAmount();
                for (BaseMachine<?> machine: machines) {
                    if (!(machine instanceof ISlotFluid fluidMachine)) continue;
                    if (fluidMachine.getFluidSlots() == null) continue;
                    for (int idx = 0; idx < fluidMachine.getFluidSlots().getTanks(); idx++) {
                        if (fluidMachine.getFluidSlots().operationNotAllowed(idx, SlotIORestriction.INPUT)) continue;
                        FluidStack fluidStack = fluidMachine.getFluidSlots().getFluidInTank(idx);
                        if (fluidStack.getFluid().defaultFluidState().is(target.getFluidTag())) {
                            if (required <= 0) break;
                            int originalCount = fluidStack.getAmount();
                            int remainingVolume = Math.max(originalCount - Math.min(fluidMachine.getFluidSlots().getTankCapacity(idx), required), 0);
                            fluidMachine.getFluidSlots().setStackInSlot(idx, remainingVolume == 0 ? FluidStack.EMPTY : new FluidStack(fluidStack.getFluid(), remainingVolume));
                            int taken = originalCount - remainingVolume;
                            required -= taken;
                        }
                    }
                }
            }
        }
    }

    public <G extends BaseMachine<?>> boolean hasSpace(G blockEntity, BaseRecipe<?, ?>  recipe) {
        return hasSpace(blockEntity, null, recipe);
    }

    public <G extends BaseMachine<?>> boolean hasSpace(G blockEntity, ArrayList<BaseMachine<? extends IMultiblockFollower>> followers, BaseRecipe<?, ?>  recipe) {
        HashMap<BaseMachine<?>, ItemStack[]> recipeItems = new HashMap<>();
        HashMap<BaseMachine<?>, FluidStack[]> recipeFluids = new HashMap<>();

        ArrayList<BaseMachine<?>> machines = new ArrayList<>();
        machines.add(blockEntity);

        if (followers != null){
            machines.addAll(followers);
        }

        for (BaseMachine<?> machine: machines) {
            if (machine instanceof ISlotItem itemMachine) {
                ItemStack[] items = new ItemStack[itemMachine.getItemSlots() == null ? 0 : itemMachine.getItemSlots().getSlots()];
                for (int idx = 0; idx < items.length; idx++) {
                    items[idx] = itemMachine.getItemSlots().getStackInSlot(idx).copy();
                }
                recipeItems.put(machine, items);
            }

            if (machine instanceof ISlotFluid fluidMachine) {
                FluidStack[] fluids = new FluidStack[fluidMachine.getFluidSlots() == null ? 0 : fluidMachine.getFluidSlots().getTanks()];
                for (int idx = 0; idx < fluids.length; idx++) {
                    fluids[idx] = fluidMachine.getFluidSlots().getFluidInTank(idx).copy();
                }
                recipeFluids.put(machine, fluids);
            }
        }

        for (UniversalSlot target: recipe.getCraftingOutput()) {
            int required = 0;

            for (BaseMachine<?> machine: machines) {
                switch (target.getType()){
                    case ITEM -> {
                        if (machine == machines.get(0)) required = target.getItem().getCount();
                        ItemStack[] items = recipeItems.get(machine);
                        if (!(machine instanceof ISlotItem itemMachine)) continue;
                        for (int idx = 0; idx < items.length; idx++) {
                            if (itemMachine.getItemSlots().operationAllowed(idx, SlotIORestriction.INPUT)) continue;
                            ItemStack itemStack = items[idx];
                            if (itemStack.is(target.getItem().getItem()) || itemStack.isEmpty()) {
                                if (required <= 0) break;
                                int originalCount = itemStack.getCount();
                                int space = Math.min(itemMachine.getItemSlots().getSlotLimit(idx), target.getItem().getMaxStackSize()) - originalCount;
                                int producedItems = Math.min(required, space);
                                if (producedItems <= 0) continue;
                                ItemStack produced = target.getItem().copy();
                                produced.setCount(originalCount + producedItems);
                                items[idx] = produced;
                                required -= producedItems;
                            }
                        }
                    }

                    case FLUID -> {
                        if (machine == machines.get(0)) required = target.getFluid().getAmount();
                        FluidStack[] fluids = recipeFluids.get(machine);
                        if (!(machine instanceof ISlotFluid fluidMachine)) continue;
                        for (int idx = 0; idx < fluids.length; idx++) {
                            if (fluidMachine.getFluidSlots().operationAllowed(idx, SlotIORestriction.INPUT)) continue;
                            FluidStack fluidStack = fluids[idx];
                            if (fluidStack.isFluidEqual(target.getFluid()) || fluidStack.isEmpty()) {
                                if (required <= 0) break;
                                int originalCount = fluidStack.getAmount();
                                int space = fluidMachine.getFluidSlots().getTankCapacity(idx) - originalCount;
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
            }

            if (required > 0) return false;
        }
        return true;
    }

    @Override
    public ResourceLocation getId() {
        return craftingRecipe;
    }

    /**
     * See documentation for the three-arg variation of validate
     * @param entity
     * @param recipe
     * @return
     * @param <G>
     */
    public <G extends BaseMachine> boolean validate(G entity, BaseRecipe<?, ?> recipe){
        return validate(entity, null, recipe);
    }


    /**
     * INTERNAL TO CLASS
     * Sorts an ingredient from a recipe into its respective hashmap
     * @param ingredient
     * @param recipeItems
     * @param recipeFluids
     * @param recipeItemTags
     * @param recipeFluidTags
     */
    private void populateValidationHashmaps(UniversalSlot ingredient, HashMap<Item, Integer> recipeItems, HashMap<Fluid, Integer> recipeFluids, HashMap<TagKey<Item>, Integer> recipeItemTags, HashMap<TagKey<Fluid>, Integer> recipeFluidTags) {
        switch (ingredient.getType()){
            case ITEM -> {
                ItemStack itemStack = ingredient.getItem();
                if (recipeItems.containsKey(itemStack.getItem())){
                    recipeItems.put(itemStack.getItem(), recipeItems.get(itemStack.getItem()) + itemStack.getCount());
                } else {
                    recipeItems.put(itemStack.getItem(), itemStack.getCount());
                }
            }

            case FLUID -> {
                FluidStack fluidStack = ingredient.getFluid();
                if (recipeFluids.containsKey(fluidStack.getFluid())){
                    recipeFluids.put(fluidStack.getFluid(), recipeFluids.get(fluidStack.getFluid()) + fluidStack.getAmount());
                } else {
                    recipeFluids.put(fluidStack.getFluid(),  fluidStack.getAmount());
                }
            }

            case ITEM_TAG -> {
                var tag = ingredient.getItemTag();
                if (recipeItemTags.containsKey(tag)){
                    recipeItemTags.put(tag, recipeItemTags.get(tag) + ingredient.getTag().count());
                } else {
                    recipeItemTags.put(tag, ingredient.getTag().count());
                }
            }

            case FLUID_TAG -> {
                var tag = ingredient.getFluidTag();
                if (recipeFluidTags.containsKey(tag)){
                    recipeFluidTags.put(tag, recipeFluidTags.get(tag) + ingredient.getTag().count());
                } else {
                    recipeFluidTags.put(tag, ingredient.getTag().count());
                }
            }
        }
    }

    /**
     * INTERNAL TO CLASS
     * Subtracts the machine inventory from the four hashmaps representing the recipe requirements, ignoring OUTPUT slots.
     * @param machine
     * @param recipeItems
     * @param recipeFluids
     * @param recipeItemTags
     * @param recipeFluidTags
     */
    private void subtractItemCountFromValidationHashmaps(BaseMachine machine, HashMap<Item, Integer> recipeItems, HashMap<Fluid, Integer> recipeFluids, HashMap<TagKey<Item>, Integer> recipeItemTags, HashMap<TagKey<Fluid>, Integer> recipeFluidTags){
        // Working with an Item/ItemTag
        if (machine instanceof ISlotItem itemMachine) {
            if (itemMachine.getItemSlots() != null) {
                for (int idx = 0; idx < itemMachine.getItemSlots().getSlots(); idx++) {
                    if (itemMachine.getItemSlots().operationNotAllowed(idx, SlotIORestriction.INPUT)) continue;
                    ItemStack stack = itemMachine.getItemSlots().getStackInSlot(idx);
                    boolean skipTag = false;
                    if (recipeItems.containsKey(stack.getItem())) {
                        recipeItems.put(stack.getItem(), recipeItems.get(stack.getItem()) - stack.getCount());
                        skipTag = true;
                    }
                    if (!skipTag) {
                        var firstTag = stack.getTags().filter(tag -> recipeItemTags.containsKey(tag)).findFirst();
                        if (firstTag.isEmpty()) continue;
                        var tag = firstTag.get();
                        recipeItemTags.put(tag, recipeItemTags.get(tag) - stack.getCount());
                    }
                }
            }
        }

        // Working with a Fluid/FluidTag
        if (machine instanceof ISlotFluid fluidMachine){
            if (fluidMachine.getFluidSlots() != null) {
                for (int idx = 0; idx < fluidMachine.getFluidSlots().getTanks(); idx++) {
                    if (fluidMachine.getFluidSlots().operationNotAllowed(idx, SlotIORestriction.INPUT)) continue;
                    FluidStack stack = fluidMachine.getFluidSlots().getFluidInTank(idx);
                    boolean skipTag = false;
                    if (recipeFluids.containsKey(stack.getFluid())) {
                        recipeFluids.put(stack.getFluid(), recipeFluids.get(stack.getFluid()) - stack.getAmount());
                        skipTag = true;
                    }
                    if (!skipTag){
                        var firstTag = stack.getFluid().defaultFluidState().getTags().filter(tag -> recipeFluidTags.containsKey(tag)).findFirst();
                        if (firstTag.isEmpty()) continue;
                        var tag = firstTag.get();
                        recipeFluidTags.put(tag, recipeFluidTags.get(tag) - stack.getAmount());
                    }
                }
            }
        }
    }

    /**
     * INTERNAL TO CLASS
     * Checks the four hashmaps in validate() to see if all recipe ingredients are satiated (remaining items in recipe <= 0)
     * @param recipeItems
     * @param recipeFluids
     * @param recipeItemTags
     * @param recipeFluidTags
     * @return True if all hashmap entries are <= 0, false if not.
     */
    private boolean checkHashmapIngredientsFulfilled(HashMap<Item, Integer> recipeItems, HashMap<Fluid, Integer> recipeFluids, HashMap<TagKey<Item>, Integer> recipeItemTags, HashMap<TagKey<Fluid>, Integer> recipeFluidTags){
        return recipeItems.values().stream().allMatch(count -> count <= 0)
                && recipeFluids.values().stream().allMatch(count -> count <= 0)
                && recipeItemTags.values().stream().allMatch(count -> count <= 0)
                && recipeFluidTags.values().stream().allMatch(count -> count <= 0);
    }

    /**
     * Tests whether a system of machines(Master/Follower/Base) can satiate a crafting recipe
     * @param blockEntity
     * @param followers
     * @param recipe
     * @param <G> an instance of BaseMachine
     * @param <F> a list of BaseMachine instances which extends IMultiblockFollower, in practice these will often be BaseFollowers
     * @return True if the machine inventories contains enough ingredients to satiate the recipes
     */
    public <G extends BaseMachine<?>, F extends BaseMachine<? extends IMultiblockFollower>> boolean validate(G blockEntity, List<F> followers, BaseRecipe<?, ?>  recipe){
        HashMap<Item, Integer> recipeItems = new HashMap<>();
        HashMap<Fluid, Integer> recipeFluids = new HashMap<>();
        HashMap<TagKey<Item>, Integer> recipeItemTags = new HashMap<>();
        HashMap<TagKey<Fluid>, Integer> recipeFluidTags = new HashMap<>();

        ArrayList<BaseMachine> machines = new ArrayList<>();
        machines.add(blockEntity);

        if (followers != null){
            machines.addAll(followers);
        }

        for (UniversalSlot ingredient: recipe.getCraftingIngredients()){
            populateValidationHashmaps(ingredient, recipeItems, recipeFluids, recipeItemTags, recipeFluidTags);
        }

        for (BaseMachine<?> machine: machines) {
            subtractItemCountFromValidationHashmaps(machine, recipeItems, recipeFluids, recipeItemTags, recipeFluidTags);
        }

        return checkHashmapIngredientsFulfilled(recipeItems, recipeFluids, recipeItemTags, recipeFluidTags);
    }

    /**
     *  Fetches a recipe for a machine RecipeType from the minecraft RecipeManager
     * @param machineInventory
     * @param type
     * @param level
     * @param blockEntity
     * @param <T> an instance of AbstractMachineRecipe, in practice this will be a machine specific recipe class (e.g. AlembicRecipe for an Alembic)
     * @return Returns the first matching recipe, or null if none match.
     */
    public static <T extends AbstractMachineRecipe<T>> T getActiveRecipe(CustomCraftingContainer machineInventory, RecipeType<T> type, Level level, BaseMachine<?> blockEntity) {
        var rec = level.getRecipeManager()
                .getAllRecipesFor(type)
                .stream()
                .filter(recipe -> recipe.matchesTypeAndQuantity(machineInventory, level, recipe))
                .findFirst()
                .orElse(null);
        return rec;
    }

    @Override
    public abstract RecipeSerializer<T> getSerializer();

    @Override
    public abstract RecipeType<T> getType();

    public abstract static class MachineType<T extends AbstractMachineRecipe<T>> implements RecipeType<T> { }

    public abstract static class MachineSerializer<T extends AbstractMachineRecipe<T>> extends AbstractRecipe.Serializer<CustomCraftingContainer, T> { }
}