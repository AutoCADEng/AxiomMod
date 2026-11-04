package eng.cad.auto.engine.crafting.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public abstract class AbstractRecipe<C extends CustomCraftingContainer, T extends AbstractRecipe<C,T>> extends BaseRecipe<C,T> implements Recipe<C> {
    protected AbstractRecipe(ResourceLocation craftingRecipe, NonNullList<CraftingIngredient> input, NonNullList<CraftingIngredient> output) {
        super(craftingRecipe, input, output);
    }

    @Override @ParametersAreNonnullByDefault
    public boolean matches(C container, Level level){
        if (level.isClientSide()) return false;
        if (input.isEmpty()) return false;
        for (CraftingIngredient ingredient:input){
            if (!container.containsIngredient(ingredient)) return false;
        }
        return true;
    };

    @Override
    public ItemStack assemble(C container, RegistryAccess registryAccess){
        return output.stream()
                .filter(ingredient -> ingredient.is(CraftingIngredient.Kind.ITEM))
                .map(CraftingIngredient::getItem)
                .findFirst()
                .map(ItemStack::copy)
                .orElse(ItemStack.EMPTY);
    }

    @Override @ParametersAreNonnullByDefault
    public boolean canCraftInDimensions(int width, int height){
        return true;
    }

    @Override @ParametersAreNonnullByDefault
    public ItemStack getResultItem(RegistryAccess p_267052_){
        return output.stream()
                .filter(ingredient -> ingredient.is(CraftingIngredient.Kind.ITEM))
                .map(CraftingIngredient::getItem)
                .findFirst()
                .map(ItemStack::copy)
                .orElse(ItemStack.EMPTY);
    }

    @Override
    public abstract ResourceLocation getId();

    @Override
    public abstract RecipeSerializer<T> getSerializer();

    @Override
    public abstract RecipeType<T> getType();

    public abstract static class Type<C extends CustomCraftingContainer, T extends AbstractRecipe<C, T>> implements RecipeType<T> { }

    public abstract static class Serializer<C extends CustomCraftingContainer, T extends AbstractRecipe<C, T>> implements  RecipeSerializer<T>{

        @Override
        public T fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            JsonArray ingredientJSON = GsonHelper.getAsJsonArray(jsonObject,"inputs");
            NonNullList<CraftingIngredient> ingredient = CraftingIngredient.arrayFromJson(ingredientJSON);
            JsonArray outputJSON = GsonHelper.getAsJsonArray(jsonObject,"outputs");
            NonNullList<CraftingIngredient> outputs = CraftingIngredient.arrayFromJson(outputJSON);
            return create(ingredient, outputs, resourceLocation, null);
        }
        protected abstract T create(NonNullList<CraftingIngredient> inputs, NonNullList<CraftingIngredient> outputs, ResourceLocation recipeId, @Nullable FriendlyByteBuf extra);

        @Override
        public void toNetwork(FriendlyByteBuf byteBuffer, T recipe){
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
        public T fromNetwork(@NotNull ResourceLocation resourceLocation, FriendlyByteBuf byteBuffer){
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
            return create(inputs, outputs, resourceLocation, byteBuffer);
        }
    }
}
