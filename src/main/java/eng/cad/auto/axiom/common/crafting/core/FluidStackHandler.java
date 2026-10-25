package eng.cad.auto.axiom.common.crafting.core;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

public class FluidStackHandler implements IFluidHandler, IFluidHandlerModifiable, INBTSerializable<CompoundTag> {
    protected NonNullList<FluidStack> stacks;
    int maxVolume = 16000;

    public FluidStackHandler()
    {
        this(1);
    }

    public FluidStackHandler(int size)
    {
        stacks = NonNullList.withSize(size, FluidStack.EMPTY);
    }

    public FluidStackHandler(NonNullList<FluidStack> stacks)
    {
        this.stacks = stacks;
    }

    public void setSize(int size)
    {
        stacks = NonNullList.withSize(size, FluidStack.EMPTY);
    }

    @Override
    public void setStackInSlot(int slot, @NotNull FluidStack stack)
    {
        validateSlotIndex(slot);
        this.stacks.set(slot, stack);
        onContentsChanged(slot);
    }

    @Override
    public CompoundTag serializeNBT() {
        ListTag nbtTagList = new ListTag();
        for (int i = 0; i < stacks.size(); i++)
        {
            if (!stacks.get(i).isEmpty())
            {
                CompoundTag fluidTag = new CompoundTag();
                fluidTag.putInt("Slot", i);
                stacks.get(i).writeToNBT(fluidTag);
                nbtTagList.add(fluidTag);
            }
        }
        CompoundTag nbt = new CompoundTag();
        nbt.put("Fluids", nbtTagList);
        nbt.putInt("Size", stacks.size());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        setSize(nbt.contains("Size", Tag.TAG_INT) ? nbt.getInt("Size") : stacks.size());
        ListTag tagList = nbt.getList("Fluids", Tag.TAG_COMPOUND);
        for (int i = 0; i < tagList.size(); i++)
        {
            CompoundTag fluidSlot = tagList.getCompound(i);
            int slot = fluidSlot.getInt("Slot");

            if (slot >= 0 && slot < stacks.size())
            {
                stacks.set(slot, FluidStack.loadFluidStackFromNBT(fluidSlot));
            }
        }
        onLoad();
    }

    protected void validateSlotIndex(int slot)
    {
        if (slot < 0 || slot >= stacks.size())
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + stacks.size() + "]");
    }

    protected void onLoad()
    {

    }

    protected void onContentsChanged(int slot)
    {

    }
    @Override
    public int getTanks() {
        return stacks.size();
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        validateSlotIndex(tank);
        return stacks.get(tank);
    }

    @Override
    public int getTankCapacity(int tank) {
        return maxVolume;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return true;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) return 0;

        for (int i = 0; i < stacks.size(); i++) {
            if (!stacks.get(i).isEmpty()
                    && stacks.get(i).isFluidEqual(resource)) {
                return fillTank(i, resource, action);
            }
        }

        for (int i = 0; i < stacks.size(); i++) {
            if (stacks.get(i).isEmpty()) {
                return fillTank(i, resource, action);
            }
        }

        return 0;
    }

    private int fillTank(int idx, FluidStack resource, FluidAction action) {
        FluidStack existing = stacks.get(idx);

        if (!existing.isEmpty() && !existing.isFluidEqual(resource)) {
            return 0;
        }

        int currentAmount = existing.isEmpty() ? 0 : existing.getAmount();
        int accepted = Math.min(resource.getAmount(), maxVolume - currentAmount);

        if (accepted <= 0) return 0;

        if (action.execute()) {
            if (existing.isEmpty()) {
                stacks.set(idx, new FluidStack(resource, accepted));
            } else {
                existing.grow(accepted);
            }

            onContentsChanged(idx);
        }

        return accepted;
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        for (FluidStack tank:stacks){
            if (tank.getFluid().isSame(resource.getFluid())){
                int oldVolume = tank.getAmount();
                int newVolume = Math.max(oldVolume - resource.getAmount(), 0);
                int deltaVolume = newVolume - oldVolume;
                if (action.execute()) {
                    tank.setAmount(newVolume);
                }
                FluidStack output = new FluidStack(tank.getFluid(), deltaVolume);
                return output.getAmount()>0?output:FluidStack.EMPTY;
            }
        }
        return FluidStack.EMPTY;
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        if (maxDrain <= 0) return FluidStack.EMPTY;

        for (int i = 0; i < stacks.size(); i++) {
            FluidStack tank = stacks.get(i);

            if (tank.isEmpty()) continue;

            int drained = Math.min(maxDrain, tank.getAmount());
            FluidStack result = new FluidStack(tank, drained);

            if (action.execute()) {
                tank.shrink(drained);

                if (tank.isEmpty()) {
                    stacks.set(i, FluidStack.EMPTY);
                }

                onContentsChanged(i);
            }

            return result;
        }

        return FluidStack.EMPTY;
    }

}
