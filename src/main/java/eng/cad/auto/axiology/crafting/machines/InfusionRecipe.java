package eng.cad.auto.axiology.crafting.machines;

import eng.cad.auto.axiology.crafting.AbstractMachineRecipe;
import eng.cad.auto.axiology.core.slot.UniversalSlot;
import eng.cad.auto.engine.crafting.core.CustomCraftingContainer;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class InfusionRecipe extends AbstractMachineRecipe<InfusionRecipe> {
    public UniversalSlot targetItem;

    public InfusionRecipe(NonNullList<UniversalSlot> input, NonNullList<UniversalSlot> outputs, ResourceLocation craftingRecipe) {
        super(input, outputs, craftingRecipe);
        targetItem = craftingIngredients.get(0);
        craftingIngredients.remove(0);
    }

    @Override
    public RecipeSerializer<InfusionRecipe> getSerializer() {
        return InfusionSerializer.INSTANCE;
    }

    @Override
    public RecipeType<InfusionRecipe> getType() {
        return InfusionType.INSTANCE;
    }

    /**
     * Modified version of matchesTypeAndQuantity that forces the first item in the recipe to be the first item in the container.
     * This is how we enforce the altar pedestal target item being present on the altar and not a pedestal.
     * @param machineInventory
     * @param level
     * @param recipe
     * @return
     */
    @Override
    public boolean matchesTypeAndQuantity(CustomCraftingContainer machineInventory, Level level, InfusionRecipe recipe) {
        if (!(super.matchesTypeAndQuantity(machineInventory, level, recipe))) return false;
        ItemStack stack = machineInventory.getItem(0);
        if (stack.isEmpty()) return false;
        if (stack.getCount() < targetItem.getItem().getCount()) return false;
        switch(targetItem.getType()){
            case ITEM -> {
                return stack.is(targetItem.getItem().getItem());
            }
            case ITEM_TAG -> {
                return stack.is(targetItem.getItemTag());
            }
        }
        return false;
    }

    public static class InfusionType extends AbstractMachineRecipe.MachineType<InfusionRecipe>{
        public static InfusionType INSTANCE = new InfusionType();
    }

    public static class InfusionSerializer extends AbstractMachineRecipe.MachineSerializer<InfusionRecipe>{
        public static InfusionSerializer INSTANCE = new InfusionSerializer();

        @Override
        protected InfusionRecipe create(NonNullList<UniversalSlot> inputs, NonNullList<UniversalSlot> outputs, ResourceLocation recipeId, @Nullable FriendlyByteBuf extra) {
            return new InfusionRecipe(inputs,outputs, recipeId);
        }
    }
}
