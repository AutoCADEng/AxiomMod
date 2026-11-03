package eng.cad.auto.engine.crafting.core;

import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import javax.annotation.ParametersAreNonnullByDefault;

public abstract class AbstractRecipe<C extends CustomCraftingContainer, T extends AbstractRecipe<C,T>> extends BaseRecipe<C,T> implements Recipe<C> {
    @Override @ParametersAreNonnullByDefault
    public abstract boolean matches(C container, Level level);

    @Override
    public abstract ItemStack assemble(C container, RegistryAccess registryAccess);

    @Override @ParametersAreNonnullByDefault
    public abstract boolean canCraftInDimensions(int width, int height);

    @Override @ParametersAreNonnullByDefault
    public abstract ItemStack getResultItem(RegistryAccess p_267052_);

    @Override
    public abstract ResourceLocation getId();

    @Override
    public abstract RecipeSerializer<T> getSerializer();

    @Override
    public abstract RecipeType<T> getType();

    public abstract static class AxiomRecipeType<C extends CustomCraftingContainer, T extends AbstractRecipe<C, T>> implements RecipeType<T> { }

    public abstract static class Serializer<C extends CustomCraftingContainer, T extends AbstractRecipe<C, T>> implements  RecipeSerializer<AbstractRecipe<C,T>>{

        @Override
        public abstract AbstractRecipe<C,T> fromJson(ResourceLocation resourceLocation, JsonObject jsonObject);

        @Override
        public abstract void toNetwork(FriendlyByteBuf byteBuf, AbstractRecipe<C, T> recipe);

        @Override
        public abstract T fromNetwork(@NotNull ResourceLocation resourceLocation, FriendlyByteBuf byteBuffer);
    }
}
