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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.Optional;

public class GenericRecipe implements Recipe<CustomContainer> {
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
    public boolean matches(CustomContainer container, Level level) {
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
    public ItemStack assemble(CustomContainer container, RegistryAccess registryAccess) {
        if (this.output.isEmpty()) return ItemStack.EMPTY;
        if (this.output.get(0).isItem()) return ItemStack.EMPTY;
        return this.output.get(0).getItem().copy();
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
        if (target.isEmpty()) {
            return false;
        }

        return level.getRecipeManager()
                .getAllRecipesFor(recipeType)
                .stream()
                .flatMap(recipe -> recipe.output.stream())
                .anyMatch(output -> {



                    boolean matchesItem = !target.getItem().isEmpty()
                            && output.getItem().is(target.getItem().getItem());



                    boolean matchesFluid = !target.getItem().isEmpty()
                            && output.getFluid().containsFluid(target.getFluid());

                    return matchesItem || matchesFluid;
                });
    }

    public boolean matchesTypeAndQuantity(CustomContainer machineInventory, Level level) {
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
        return false;
    }

    public static GenericRecipe getActiveRecipe(CustomContainer machineInventory, RecipeType<GenericRecipe> type, Level level, GenericBlockEntity blockEntity ){

        Optional<GenericRecipe> recipeOutput = level.getRecipeManager().getAllRecipesFor(type)
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
            AxiomMod.LOGGER.info("REGISTERING A NEW RECIPE~!~");
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