package eng.cad.auto.axiom.common.crafting.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import eng.cad.auto.axiom.AxiomMod;
import eng.cad.auto.axiom.common.machine.core.GenericBlockEntity;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

public class GenericRecipe implements Recipe<CustomCraftingContainer> {
    private final ResourceLocation craftingRecipe;
    private final NonNullList<CraftingIngredient> input;
    final NonNullList<CraftingIngredient> output;
    private final RecipeType<? extends GenericRecipe> recipeType;

    public GenericRecipe(NonNullList<CraftingIngredient> input, NonNullList<CraftingIngredient> outputs, ResourceLocation craftingRecipe, RecipeType<? extends GenericRecipe> recipeType) {
        this.input = input;
        this.output = outputs;
        this.craftingRecipe = craftingRecipe;
        this.recipeType = recipeType;
    }

    @Override @ParametersAreNonnullByDefault
    public boolean matches(CustomCraftingContainer container, Level level) {
        if (level.isClientSide()) return false;
        if (input.isEmpty()) return false;
        for (CraftingIngredient ingredient:input){
            if (!container.containsIngredient(ingredient)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(CustomCraftingContainer container, RegistryAccess registryAccess) {
        return output.stream()
                .filter(CraftingIngredient::isItem)
                .map(CraftingIngredient::getItem)
                .findFirst()
                .map(ItemStack::copy)
                .orElse(ItemStack.EMPTY);
    }

    @Override @ParametersAreNonnullByDefault
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return true;
    }

    @Override @ParametersAreNonnullByDefault
    public ItemStack getResultItem(RegistryAccess p_267052_) {
        return output.stream()
            .filter(CraftingIngredient::isItem)
            .map(CraftingIngredient::getItem)
            .findFirst()
            .map(ItemStack::copy)
            .orElse(ItemStack.EMPTY);
    }

    @Override
    public ResourceLocation getId() {
        return craftingRecipe;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return recipeType;
    }

    public boolean contains(CraftingIngredient target, Level level) {
        if (target.isEmpty()) return false;

        return level.getRecipeManager()
                .getAllRecipesFor(recipeType)
                .stream()
                .flatMap(recipe -> recipe.output.stream())
                .anyMatch(output -> {
                    if (output.isEmpty()) return false;

                    if (target.isItem() && output.isItem()) {
                        return output.getItem().getItem()
                                .equals(target.getItem().getItem());
                    }

                    if (target.isFluid() && output.isFluid()) {
                        return output.getFluid()
                                .containsFluid(target.getFluid());
                    }

                    return false;
                });
    }

    public boolean matchesTypeAndQuantity(CustomCraftingContainer machineInventory, Level level) {
        if (!matches(machineInventory, level)) return false;
        for (CraftingIngredient ingredient:input){
            if (ingredient.isEmpty() || machineInventory.isEmpty()) return false;
            if (ingredient.isItem()) {
                if(!(machineInventory.items.stream().anyMatch(itemstack -> {
                    if (!itemstack.is(ingredient.getItem().getItem())) return false;
                    return itemstack.getCount() >= ingredient.getItem().getCount();
                }))) return false;
            }
            if (ingredient.isFluid()) {
                if(!(machineInventory.tanks.stream().anyMatch(fluidstack -> {
                    if (!fluidstack.getFluid().isSame(ingredient.getFluid().getFluid())) return false;
                    return fluidstack.getAmount() >= ingredient.getFluid().getAmount();
                }))) return false;
            }
        }
        return true;
    }

    public static GenericRecipe getActiveRecipe(CustomCraftingContainer machineInventory, RecipeType<GenericRecipe> type, Level level, GenericBlockEntity blockEntity ){
        Optional<GenericRecipe> recipeOutput = level.getRecipeManager()
                .getAllRecipesFor(type)
                .stream()
                .filter(recipe -> recipe.matchesTypeAndQuantity(machineInventory, level)).findFirst();

        return recipeOutput.orElse(null);
    }

    public NonNullList<CraftingIngredient> getCraftingIngredients() {
        return input;
    }

    public NonNullList<CraftingIngredient> getCraftingOutput() {
        return output;
    }

    public boolean validateCraft(GenericBlockEntity entity){
            HashMap<Item, Integer> recipeItems = new HashMap<>();
            HashMap<Fluid, Integer> recipeFluids = new HashMap<>();

            for (CraftingIngredient ingredient: getCraftingIngredients()){
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

            for (int idx = 0; idx<entity.machine.inventory.getSlots(); idx++){
                ItemStack stack = entity.machine.inventory.getStackInSlot(idx);
                if(recipeItems.containsKey(stack.getItem())){
                    recipeItems.put(stack.getItem(), recipeItems.get(stack.getItem()) - stack.getCount());
                }
            }

            for (int idx = 0; idx<entity.machine.tanks.getTanks(); idx++){
                FluidStack stack = entity.machine.tanks.getFluidInTank(idx);
                if(recipeFluids.containsKey(stack.getFluid())){
                    recipeFluids.put(stack.getFluid(), recipeFluids.get(stack.getFluid()) - stack.getAmount());
                }
            }

        return recipeItems.values().stream().allMatch(count -> count <= 0) && recipeFluids.values().stream().allMatch(count -> count <= 0);
    }

    public void performCraft(CraftingIngredient target, GenericBlockEntity blockEntity, boolean simulate) {
        int required = 0;
        if (target.isItem()) {
            required = target.getItem().getCount();
            for (int idx = 0; idx < blockEntity.machine.inventory.getSlots(); idx++) {
                ItemStack itemStack = blockEntity.machine.inventory.getStackInSlot(idx);
                if (itemStack.is(target.getItem().getItem())) {
                    if (required <= 0) continue;

                    int originalCount = itemStack.getCount();

                    int remainingItems = Math.max(originalCount - Math.min(itemStack.getMaxStackSize(), required), 0);
                    if (!simulate) {
                        blockEntity.machine.inventory.setStackInSlot(idx, remainingItems == 0 ? ItemStack.EMPTY : new ItemStack(itemStack.getItem(), remainingItems));
                    }
                    required = originalCount - remainingItems;
                }
            }
            if (required > 0)
                AxiomMod.LOGGER.warn("Unbalanced crafting equation for recipe {} {} [item] {} left", this.getId(), required, target.getItem().toString());
        }

        if (target.isFluid()) {
            required = target.getFluid().getAmount();
            for (int idx = 0; idx < blockEntity.machine.tanks.getTanks(); idx++) {
                FluidStack fluidStack = blockEntity.machine.tanks.getFluidInTank(idx);
                if (fluidStack.isFluidEqual(target.getFluid())) {
                    if (required <= 0) continue;

                    int originalCount = fluidStack.getAmount();

                    int remainingVolume = Math.max(originalCount - Math.min(blockEntity.machine.tanks.getTankCapacity(idx), required), 0);
                    if (!simulate) {
                        blockEntity.machine.tanks.setStackInSlot(idx, remainingVolume == 0 ? FluidStack.EMPTY : new FluidStack(fluidStack.getFluid(), remainingVolume));
                    }
                    required = originalCount - remainingVolume;
                }
            }
            if (required > 0)
                AxiomMod.LOGGER.warn("Unbalanced crafting equation for recipe {} {} [fluid] {} left", this.getId(), required, target.getFluid().toString());
        }
    }
    public static class Type implements RecipeType<GenericRecipe> {
    }

    // Look into making this generic
    public static class Serializer implements RecipeSerializer<GenericRecipe>{

        public static final Serializer INSTANCE =  new Serializer();

        @Override
        public GenericRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            String recipeTypeString = jsonObject.get("recipe_type").getAsString();
            RecipeType<GenericRecipe> recipeType = (RecipeType<GenericRecipe>) ForgeRegistries.RECIPE_TYPES.getValue(ResourceLocation.parse(recipeTypeString));
            JsonArray ingredientJSON = GsonHelper.getAsJsonArray(jsonObject,"inputs");
            NonNullList<CraftingIngredient> ingredient = CraftingIngredient.arrayFromJson(ingredientJSON);
            JsonArray outputJSON = GsonHelper.getAsJsonArray(jsonObject,"outputs");
            NonNullList<CraftingIngredient> outputs = CraftingIngredient.arrayFromJson(outputJSON);
            return new GenericRecipe(ingredient, outputs, resourceLocation, recipeType);
        }

        @Override
        public void toNetwork(FriendlyByteBuf byteBuffer, GenericRecipe recipe) {
            byteBuffer.writeResourceLocation(Objects.requireNonNull(ForgeRegistries.RECIPE_TYPES.getKey(recipe.recipeType)));
            byteBuffer.writeInt(recipe.input.size());
            for (CraftingIngredient ingredient:recipe.input){
                ingredient.toNetwork(byteBuffer);
            }

            byteBuffer.writeInt(recipe.output.size());
            for (CraftingIngredient result:recipe.output){
                result.toNetwork(byteBuffer);
            }
        }

        @Override
        public GenericRecipe fromNetwork(@NotNull ResourceLocation resourceLocation, FriendlyByteBuf byteBuffer) {
            ResourceLocation recipeType = byteBuffer.readResourceLocation();
            int numberOfIngredients = byteBuffer.readInt();

            NonNullList<CraftingIngredient> inputs = NonNullList.create();
            NonNullList<CraftingIngredient> outputs = NonNullList.create();
            for (int i = 0; i<numberOfIngredients; i++){
                inputs.add(CraftingIngredient.fromNetwork(byteBuffer));
            }
            int numberOfOutputs = byteBuffer.readInt();
            for (int i = 0; i<numberOfOutputs; i++){
                outputs.add(CraftingIngredient.fromNetwork(byteBuffer));
            }
            return new GenericRecipe(inputs, outputs, resourceLocation, (RecipeType<GenericRecipe>) ForgeRegistries.RECIPE_TYPES.getValue(recipeType));
        }
    }
}