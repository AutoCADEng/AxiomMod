package eng.cad.auto.axiology.crafting.machines;

import eng.cad.auto.axiology.core.slot.UniversalSlot;
import eng.cad.auto.axiology.crafting.AbstractMachineRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

public class AlembicRecipe extends AbstractMachineRecipe<AlembicRecipe> {
    public AlembicRecipe(NonNullList<UniversalSlot> input, NonNullList<UniversalSlot> outputs, ResourceLocation craftingRecipe) {
        super(input, outputs, craftingRecipe);
    }

    @Override
    public RecipeSerializer<AlembicRecipe> getSerializer() {
        return AlembicSerializer.INSTANCE;
    }

    @Override
    public RecipeType<AlembicRecipe> getType() {
        return AlembicType.INSTANCE;
    }

    public static class AlembicType extends AbstractMachineRecipe.MachineType<AlembicRecipe>{
        public static AlembicType INSTANCE = new AlembicType();
    }

    public static class AlembicSerializer extends AbstractMachineRecipe.MachineSerializer<AlembicRecipe>{
        public static AlembicSerializer INSTANCE = new AlembicSerializer();

        @Override
        protected AlembicRecipe create(NonNullList<UniversalSlot> inputs, NonNullList<UniversalSlot> outputs, ResourceLocation recipeId, @Nullable FriendlyByteBuf extra) {
            return new AlembicRecipe(inputs,outputs, recipeId);
        }
    }
}
