package eng.cad.auto.axiom.common.crafting.core;

import eng.cad.auto.axiom.common.machine.core.GenericBlockEntity;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class CustomCraftingContainer implements Container {
    NonNullList<FluidStack> tanks = NonNullList.create();
    NonNullList<ItemStack> items= NonNullList.create();
    GenericBlockEntity blockEntity;
    public CustomCraftingContainer(GenericBlockEntity blockEntity){
        this.blockEntity = blockEntity;
    }
    @Override
    public int getContainerSize() {
        return tanks.size() + items.size();
    }

    @Override
    public boolean isEmpty() {
        return tanks.isEmpty() && items.isEmpty();
    }

    @Override
    public ItemStack getItem(int p_18941_) {
        return items.get(p_18941_);
    }
    public FluidStack getFluid(int p_18941_) {

        return tanks.get(p_18941_);
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

    @Override
    public void clearContent() {
        items.clear();
        tanks.clear();
        setChanged();
    }

    public boolean containsIngredient(CraftingIngredient ingredient) {
        if (ingredient.isFluid()){
            for (FluidStack fluid:tanks){
                if (!fluid.getFluid().isSame(ingredient.getFluid().getFluid())) return false;
                return fluid.getAmount() >= ingredient.getFluid().getAmount();
            }
        }
        if (ingredient.isItem()){
            for (ItemStack item:items){
                if (item.is(ingredient.getItem().getItem())) return true;
                return item.getCount() >= ingredient.getItem().getCount();
            }
        }
        return false;
    }

    public CustomCraftingContainer addTank(FluidStack tank) {
        this.tanks.add(tank);
        return this;
    }
    public CustomCraftingContainer addSlot(ItemStack stack) {
        this.items.add(stack);
        return this;
    }
}
