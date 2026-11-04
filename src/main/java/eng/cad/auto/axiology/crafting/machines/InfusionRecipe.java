package eng.cad.auto.axiology.crafting.machines;

import eng.cad.auto.axiology.crafting.AbstractMachineRecipe;
import eng.cad.auto.engine.crafting.core.CraftingIngredient;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

public class InfusionRecipe extends AbstractMachineRecipe<InfusionRecipe> {
    public InfusionRecipe(NonNullList<CraftingIngredient> input, NonNullList<CraftingIngredient> outputs, ResourceLocation craftingRecipe) {
        super(input, outputs, craftingRecipe);
    }

    @Override
    public RecipeSerializer<InfusionRecipe> getSerializer() {
        return InfusionSerializer.INSTANCE;
    }

    @Override
    public RecipeType<InfusionRecipe> getType() {
        return InfusionType.INSTANCE;
    }

    public static class InfusionType extends AbstractMachineRecipe.MachineType<InfusionRecipe>{
        public static InfusionType INSTANCE = new InfusionType();
    }

    public static class InfusionSerializer extends AbstractMachineRecipe.MachineSerializer<InfusionRecipe>{
        public static InfusionSerializer INSTANCE = new InfusionSerializer();

        @Override
        protected InfusionRecipe create(NonNullList<CraftingIngredient> inputs, NonNullList<CraftingIngredient> outputs, ResourceLocation recipeId, @Nullable FriendlyByteBuf extra) {
            return new InfusionRecipe(inputs,outputs, recipeId);
        }
    }
}
