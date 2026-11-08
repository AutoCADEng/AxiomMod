package eng.cad.auto.axiology.crafting.machines;

import eng.cad.auto.axiology.core.slot.UniversalSlot;
import eng.cad.auto.axiology.crafting.AbstractMachineRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

public class FermenterRecipe extends AbstractMachineRecipe<FermenterRecipe> {
    public FermenterRecipe(NonNullList<UniversalSlot> input, NonNullList<UniversalSlot> outputs, ResourceLocation craftingRecipe) {
        super(input, outputs, craftingRecipe);

    }

    @Override
    public RecipeSerializer<FermenterRecipe> getSerializer() {
        return FermenterSerializer.INSTANCE;
    }

    @Override
    public RecipeType<FermenterRecipe> getType() {
        return FermenterType.INSTANCE;
    }

    public static class FermenterType extends AbstractMachineRecipe.MachineType<FermenterRecipe>{
        public static FermenterType INSTANCE = new FermenterType();
    }

    public static class FermenterSerializer extends AbstractMachineRecipe.MachineSerializer<FermenterRecipe>{
        public static FermenterSerializer INSTANCE = new FermenterSerializer();

        @Override
        protected FermenterRecipe create(NonNullList<UniversalSlot> inputs, NonNullList<UniversalSlot> outputs, ResourceLocation recipeId, @Nullable FriendlyByteBuf extra) {
            return new FermenterRecipe(inputs, outputs, recipeId);
        }
    }
}
