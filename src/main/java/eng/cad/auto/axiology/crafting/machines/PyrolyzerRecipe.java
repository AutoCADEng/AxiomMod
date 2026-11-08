package eng.cad.auto.axiology.crafting.machines;

import eng.cad.auto.axiology.core.slot.UniversalSlot;
import eng.cad.auto.axiology.crafting.AbstractMachineRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

public class PyrolyzerRecipe extends AbstractMachineRecipe<PyrolyzerRecipe> {
    public PyrolyzerRecipe(NonNullList<UniversalSlot> input, NonNullList<UniversalSlot> outputs, ResourceLocation craftingRecipe) {
        super(input, outputs, craftingRecipe);
    }

    @Override
    public RecipeSerializer<PyrolyzerRecipe> getSerializer() {
        return PyrolyzerSerializer.INSTANCE;
    }

    @Override
    public RecipeType<PyrolyzerRecipe> getType() {
        return PyrolyzerType.INSTANCE;
    }

    public static class PyrolyzerType extends AbstractMachineRecipe.MachineType<PyrolyzerRecipe> {
        public static PyrolyzerType INSTANCE = new PyrolyzerType();
    }

    public static class PyrolyzerSerializer extends AbstractMachineRecipe.MachineSerializer<PyrolyzerRecipe> {
        public static PyrolyzerSerializer INSTANCE = new PyrolyzerSerializer();

        @Override
        protected PyrolyzerRecipe create(NonNullList<UniversalSlot> inputs, NonNullList<UniversalSlot> outputs, ResourceLocation recipeId, @Nullable FriendlyByteBuf extra) {
            return new PyrolyzerRecipe(inputs, outputs, recipeId);
        }
    }
}
