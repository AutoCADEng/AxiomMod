package eng.cad.auto.axiology.crafting.machines;

import eng.cad.auto.axiology.crafting.AbstractMachineRecipe;
import eng.cad.auto.engine.crafting.core.CraftingIngredient;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

public class SoakerRecipe extends AbstractMachineRecipe<SoakerRecipe> {
    public SoakerRecipe(NonNullList<CraftingIngredient> input, NonNullList<CraftingIngredient> outputs, ResourceLocation craftingRecipe) {
        super(input, outputs, craftingRecipe);
    }

    @Override
    public RecipeSerializer<SoakerRecipe> getSerializer() {
        return SoakerSerializer.INSTANCE;
    }

    @Override
    public RecipeType<SoakerRecipe> getType() {
        return SoakerType.INSTANCE;
    }

    public static class SoakerType extends AbstractMachineRecipe.MachineType<SoakerRecipe>{
        public static SoakerType INSTANCE = new SoakerType();
    }

    public static class SoakerSerializer extends AbstractMachineRecipe.MachineSerializer<SoakerRecipe>{
        public static SoakerSerializer INSTANCE = new SoakerSerializer();

        @Override
        protected SoakerRecipe create(NonNullList<CraftingIngredient> inputs, NonNullList<CraftingIngredient> outputs, ResourceLocation recipeId, @Nullable FriendlyByteBuf extra) {
            return new SoakerRecipe(inputs,outputs, recipeId);
        }
    }
}
