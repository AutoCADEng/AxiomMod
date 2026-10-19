package eng.cad.auto.axiom.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import eng.cad.auto.axiom.AxiomMod;
import eng.cad.auto.axiom.exceptions.InvalidAlchemyRecipeException;
import eng.cad.auto.axiom.recipes.api.CustomAlchemyRecipe;
import eng.cad.auto.axiom.recipes.api.CustomResult;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

public class FermenterRecipe implements CustomAlchemyRecipe<Container> {
    private final ResourceLocation id;
    private final Ingredient input;
    private final NonNullList<ItemStack> output;
    private final int maxOutputs = 4;
    private final int maxInputs = 1;

    public FermenterRecipe(Ingredient input, NonNullList<ItemStack> outputs, ResourceLocation resourceLocation) {
        if (outputs.size()>maxOutputs) throw(new InvalidAlchemyRecipeException(String.format("Attempt to register fermenter recipe %s with too many outputs %d", input.toString(), outputs.size())));
        this.input = input;
        this.output = outputs;
        this.id = resourceLocation;
    }

    @Override @ParametersAreNonnullByDefault
    public boolean matches(Container container, Level level) {
        if (level.isClientSide()) return false;
        return this.input.test(container.getItem(0));
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override @ParametersAreNonnullByDefault
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return true;
    }

    @Override @ParametersAreNonnullByDefault
    public ItemStack getResultItem(RegistryAccess p_267052_) {
        return output.stream()
                .filter(stack -> !stack.isEmpty())
                .findFirst()
                .orElse(ItemStack.EMPTY);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    /**
     * @return
     */
    @Override
    public CustomResult getOutput() {
        return new CustomResult(output);
    }

    public static boolean contains(ItemStack targetItem, Level level) {
        if (targetItem.isEmpty()) {
            return false;
        }

        return level.getRecipeManager()
                .getAllRecipesFor(FermenterRecipe.Type.INSTANCE)
                .stream()
                .flatMap(recipe -> recipe.output.stream())
                .anyMatch(outputStack ->
                        !outputStack.isEmpty()
                                && ItemStack.isSameItem(outputStack, targetItem)
                );
    }
    /**
     * @return
     */
    @Override
    public Ingredient getInput() {
        return input;
    }

    public static class Type implements RecipeType<FermenterRecipe> {
        public static final Type INSTANCE =  new Type();
        public static final String ID = "fermenter_recipe";
    }

    public static class Serializer implements RecipeSerializer<FermenterRecipe>{
        public static final Serializer INSTANCE =  new Serializer();
        public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(AxiomMod.MODID, Type.ID);
        /**
         * @param resourceLocation
         * @param jsonObject
         * @return
         */
        @Override
        public FermenterRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {

            JsonObject ingredientJSON = GsonHelper.getAsJsonObject(jsonObject,"input");
            Ingredient ingredient = Ingredient.fromJson(ingredientJSON);
            JsonArray outputList = GsonHelper.getAsJsonArray(jsonObject,"outputs");

            NonNullList<ItemStack> outputJSON = NonNullList.create();
            for(int idx = 0; idx < outputList.size(); idx++){
                outputJSON.add(ShapedRecipe.itemStackFromJson(outputList.get(idx).getAsJsonObject()));
            }

            return new FermenterRecipe(ingredient, outputJSON, resourceLocation);
        }

        @Override
        public @Nullable FermenterRecipe fromNetwork(@NotNull ResourceLocation resourceLocation, FriendlyByteBuf byteBuffer) {

            Ingredient ingredient = Ingredient.fromNetwork(byteBuffer);

            NonNullList<ItemStack> outputs = NonNullList.withSize(byteBuffer.readInt(), ItemStack.EMPTY);

            for (int idx = 0; idx < outputs.size(); idx++) {
                outputs.set(idx, byteBuffer.readItem());
            }
            return new FermenterRecipe(ingredient, outputs, resourceLocation);
        }

        @Override
        public void toNetwork(FriendlyByteBuf byteBuffer, FermenterRecipe recipe) {
            recipe.input.toNetwork(byteBuffer);

            byteBuffer.writeInt(recipe.getOutput().outputItems.size());
            for (ItemStack output:recipe.getOutput().outputItems){
                byteBuffer.writeItemStack(output, false);
            }
        }
    }

}