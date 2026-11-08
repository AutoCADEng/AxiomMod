package eng.cad.auto.axiology.core.machine.component;

import eng.cad.auto.axiology.core.ISlotFluid;
import eng.cad.auto.axiology.core.ISlotItem;
import eng.cad.auto.axiology.core.fluid.FluidStackHandler;
import eng.cad.auto.axiology.core.machine.TickingMachine;
import eng.cad.auto.axiology.crafting.AbstractMachineRecipe;
import eng.cad.auto.axiology.crafting.core.CustomCraftingContainer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public interface IRecipeMachine<T extends AbstractMachineRecipe<T>> {
    RecipeType<T> recipeType();

    default CustomCraftingContainer buildContainer(TickingMachine machine){
        CustomCraftingContainer contents = new CustomCraftingContainer(machine);
        if (machine instanceof ISlotFluid fluidMachine) {
            FluidStackHandler fluidStackHandler = fluidMachine.getFluidSlots();
            for(int idx = 0; idx<fluidStackHandler.getTanks(); idx++){
                contents.addTank(fluidStackHandler.getFluidInTank(idx));
            }
        }

        if (machine instanceof ISlotItem itemMachine) {
            ItemStackHandler itemStackHandler = itemMachine.getItemSlots();
            for(int idx = 0; idx<itemStackHandler.getSlots(); idx++){
                contents.addSlot(itemStackHandler.getStackInSlot(idx));
            }
        }
        return contents;
    }

    default T findRecipe(TickingMachine<?> machine, Level level, @Nullable CustomCraftingContainer overwriteDefaultContainer) {
        return AbstractMachineRecipe.getActiveRecipe(overwriteDefaultContainer == null ? buildContainer(machine) : overwriteDefaultContainer, recipeType(), level, machine);
    }
}