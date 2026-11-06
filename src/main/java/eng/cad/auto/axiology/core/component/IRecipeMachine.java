package eng.cad.auto.axiology.core.component;

import eng.cad.auto.axiology.core.machine.TickingMachine;
import eng.cad.auto.axiology.core.ISlotFluid;
import eng.cad.auto.axiology.core.ISlotItem;
import eng.cad.auto.axiology.crafting.AbstractMachineRecipe;
import eng.cad.auto.engine.crafting.core.CustomCraftingContainer;
import eng.cad.auto.engine.fluid.FluidStackHandler;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemStackHandler;

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

    default T findRecipe(TickingMachine<?> machine, Level level) {
        return AbstractMachineRecipe.getActiveRecipe(
                buildContainer(machine), recipeType(), level, machine);
    }
}