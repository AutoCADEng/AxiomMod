package eng.cad.auto.axiology.crafting.machines;

import eng.cad.auto.axiology.crafting.AbstractMachineRecipe;
import eng.cad.auto.axiology.core.slot.UniversalSlot;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

public class ChemicalReactorRecipe extends AbstractMachineRecipe<ChemicalReactorRecipe> {
    public ChemicalReactorRecipe(NonNullList<UniversalSlot> input, NonNullList<UniversalSlot> outputs, ResourceLocation craftingRecipe) {
        super(input, outputs, craftingRecipe);
    }

    @Override
    public RecipeSerializer<ChemicalReactorRecipe> getSerializer() {
        return ChemicalReactorSerializer.INSTANCE;
    }

    @Override
    public RecipeType<ChemicalReactorRecipe> getType() {
        return ChemicalReactorType.INSTANCE;
    }

    public static class ChemicalReactorType extends AbstractMachineRecipe.MachineType<ChemicalReactorRecipe>{
        public static ChemicalReactorType INSTANCE = new ChemicalReactorType();
    }

    public static class ChemicalReactorSerializer extends AbstractMachineRecipe.MachineSerializer<ChemicalReactorRecipe>{
        public static ChemicalReactorSerializer INSTANCE = new ChemicalReactorSerializer();

        @Override
        protected ChemicalReactorRecipe create(NonNullList<UniversalSlot> inputs, NonNullList<UniversalSlot> outputs, ResourceLocation recipeId, @Nullable FriendlyByteBuf extra) {
            return new ChemicalReactorRecipe(inputs,outputs, recipeId);
        }
    }
}
