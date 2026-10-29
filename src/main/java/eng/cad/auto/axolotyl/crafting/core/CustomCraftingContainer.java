package eng.cad.auto.axolotyl.crafting.core;

import eng.cad.auto.axolotyl.machine.core.GenericMachineEntity;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

/**
 * This class is a custom implementation of Container for holding fluids and items, mainly used in crafting recipes
 * to carry the fluid components to GenericRecipe. It is currently incomplete, missing fluid handling capabilities
 * as such depreciated functions may not work as expected. Do not use a CustomCraftingContainer in its current state
 * as a Container for a machine, only for crafting.
 */
public class CustomCraftingContainer implements Container {
    NonNullList<FluidStack> tanks = NonNullList.create();
    NonNullList<ItemStack> items = NonNullList.create();
    GenericMachineEntity blockEntity;

    public CustomCraftingContainer(GenericMachineEntity blockEntity){
        this.blockEntity = blockEntity;
    }

    /**
     * Gets the container size.
     * @return The number of tanks + number of item slots
     */
    @Override
    public int getContainerSize() {
        return tanks.size() + items.size();
    }

    /**
     * Checks isEmpty() for item slots and tanks
     * @return True, if both item slots and tanks are empty
     */
    @Override
    public boolean isEmpty() {
        return tanks.isEmpty() && items.isEmpty();
    }

    @Override
    public ItemStack getItem(int p_18941_) {
        return items.get(p_18941_);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack copyOf = getItem(slot).split(amount);
        setChanged();
        return copyOf;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = getItem(slot);
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        this.items.set(slot, ItemStack.EMPTY);
        return stack;

    }

    @Override
    public void setItem(int slot, ItemStack item) {
        items.set(slot,item);
        setChanged();
    }

    @Override
    public void setChanged() {
        blockEntity.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(blockEntity, player);
    }

    /**
     * Wipes item slots and tank slots from the container
     */
    @Override
    public void clearContent() {
        items.clear();
        tanks.clear();
        setChanged();
    }

    /**
     * Scans the inventory to find out if there is a crafting ingredient present
     * @param ingredient
     * @return true if the ingredient is present and the amount stored in the tank is >= the amount the ingredient holds
     */
    public boolean containsIngredient(CraftingIngredient ingredient) {
        if (ingredient.isFluid()){
            for (FluidStack fluid:tanks){
                if (!fluid.getFluid().isSame(ingredient.getFluid().getFluid())) continue;
                return fluid.getAmount() >= ingredient.getFluid().getAmount();
            }
        }
        if (ingredient.isItem()){
            for (ItemStack item:items){
                if (!item.is(ingredient.getItem().getItem())) continue;
                return item.getCount() >= ingredient.getItem().getCount();
            }
        }
        return false;
    }

    /**
     * Builder method for making a new CustomCraftingContainer, should be implemented at the machine level.
     * @param tank The tank to be added to the container
     * @return A CustomCraftingContainer with the tank added to it.
     */
    public CustomCraftingContainer addTank(FluidStack tank) {
        this.tanks.add(tank);
        return this;
    }
    /**
     * Builder method for making a new CustomCraftingContainer, should be implemented at the machine level.
     * @param stack The stack to be added to the container
     * @return A CustomCraftingContainer with the stack added to it.
     */
    public CustomCraftingContainer addSlot(ItemStack stack) {
        this.items.add(stack);
        return this;
    }

    public CustomCraftingContainer merge(CustomCraftingContainer craftingContainer) {
        for(ItemStack stack:craftingContainer.items){
            addSlot(stack);
        }
        for(FluidStack tank:craftingContainer.tanks){
            addTank(tank);
        }
        return this;
    }
}
