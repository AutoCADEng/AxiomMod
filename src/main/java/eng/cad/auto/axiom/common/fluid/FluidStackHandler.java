package eng.cad.auto.axiom.common.fluid;

import eng.cad.auto.axiom.AxiomMod;
import eng.cad.auto.axiom.common.crafting.core.IFluidHandlerModifiable;
import eng.cad.auto.axiom.common.machine.core.slot.SlotConfig;
import eng.cad.auto.axiom.common.machine.core.slot.SlotIORestriction;
import eng.cad.auto.axiom.common.machine.core.slot.SlotType;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public class FluidStackHandler implements IFluidHandler, IFluidHandlerModifiable, INBTSerializable<CompoundTag> {
    protected NonNullList<FluidStackLimiter> stacks = NonNullList.create();

    public FluidStackHandler(SlotConfig config)
    {
        this(1, new SlotConfig[] {config});
    }

    public FluidStackHandler(int size, SlotConfig[] configs)
    {
        if (size != configs.length){
            AxiomMod.LOGGER.error("Invalid FluidStack Config Provided to FLUIDSTACKHANDLER !");
            return;
        };
        for (int idx = 0; idx<configs.length;idx++){
            stacks.add(idx, new FluidStackLimiter(FluidStack.EMPTY, configs[idx]));
        }

    }

    @Override
    public void setStackInSlot(int slot, @NotNull FluidStack stack)
    {
        validateSlotIndex(slot);
        SlotConfig config = stacks.get(slot).config;
        this.stacks.set(slot, new FluidStackLimiter(stack, config));
        onContentsChanged(slot);
    }

    @Override
    public CompoundTag serializeNBT() {
        ListTag nbtTagList = new ListTag();
        for (int i = 0; i < stacks.size(); i++)
        {
            if (!stacks.get(i).stack.isEmpty())
            {
                CompoundTag fluidTag = new CompoundTag();
                fluidTag.putInt("Slot", i);
                stacks.get(i).stack.writeToNBT(fluidTag);
                nbtTagList.add(fluidTag);
            }
        }
        CompoundTag nbt = new CompoundTag();
        nbt.put("Fluids", nbtTagList);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        for (int i = 0; i < stacks.size(); i++) {
            stacks.set(i, new FluidStackLimiter(FluidStack.EMPTY, stacks.get(i).config));
        }

        ListTag tagList = nbt.getList("Fluids", Tag.TAG_COMPOUND);
        for (int i = 0; i < tagList.size(); i++) {
            CompoundTag fluidSlot = tagList.getCompound(i);
            int slot = fluidSlot.getInt("Slot");
            if (slot >= 0 && slot < stacks.size()) {
                stacks.set(slot, new FluidStackLimiter(
                        FluidStack.loadFluidStackFromNBT(fluidSlot), stacks.get(slot).config));
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
        return stacks.get(tank).stack;
    }

    @Override
    public int getTankCapacity(int tank) {
        return stacks.get(tank).config.capacity();
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return true;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) return 0;

        for (int i = 0; i < stacks.size(); i++) {
            if (!stacks.get(i).stack.isEmpty()
                    && stacks.get(i).stack.isFluidEqual(resource)) {
                return fillTank(i, resource, action);
            }
        }

        for (int i = 0; i < stacks.size(); i++) {
            if (stacks.get(i).stack.isEmpty()) {
                return fillTank(i, resource, action);
            }
        }

        return 0;
    }

    private int fillTank(int idx, FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) return 0;

        FluidStack existing = stacks.get(idx).stack;
        if (!existing.isFluidEqual(resource) && !existing.isEmpty()) return 0;

        int oldVolume = existing.isEmpty() ? 0 : existing.getAmount();
        int maxFill = Math.max(getTankCapacity(idx) - oldVolume, 0);
        int deltaFluid = Math.min(resource.getAmount(), maxFill);
        if (deltaFluid <= 0) return 0;

        if (action.execute()) {
            if (existing.isEmpty()) {
                SlotConfig config = stacks.get(idx).config;
                stacks.set(idx, new FluidStackLimiter(new FluidStack(resource, deltaFluid), config));
            } else {
                existing.grow(deltaFluid);
            }
            onContentsChanged(idx);
        }
        return deltaFluid;
    }


    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) return FluidStack.EMPTY;
        for (int idx = stacks.size() - 1; idx >= 0; idx--) {
            FluidStack tank = stacks.get(idx).stack;
            if (tank.isEmpty() || !tank.isFluidEqual(resource)) continue;
            int drained = Math.min(resource.getAmount(), tank.getAmount());
            FluidStack result = new FluidStack(tank, drained);

            if (action.execute()) {
                tank.shrink(drained);
                SlotConfig config = stacks.get(idx).config;
                if (tank.isEmpty()) stacks.set(idx, new FluidStackLimiter(FluidStack.EMPTY, config));
                onContentsChanged(idx);
            }
            return result;
        }
        return FluidStack.EMPTY;
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        if (maxDrain <= 0) return FluidStack.EMPTY;

        for (int idx = stacks.size() - 1; idx >= 0; idx--) {
            FluidStack tank = stacks.get(idx).stack;
            if (tank.isEmpty()) continue;
            int drained = Math.min(maxDrain, tank.getAmount());
            FluidStack result = new FluidStack(tank, drained);

            if (action.execute()) {
                tank.shrink(drained);
                if (tank.isEmpty()) {
                    SlotConfig config = stacks.get(idx).config;
                    stacks.set(idx, new FluidStackLimiter(FluidStack.EMPTY, config));
                }
                onContentsChanged(idx);
            }
            return result;
        }

        return FluidStack.EMPTY;
    }

    public record FluidStackLimiter(FluidStack stack, SlotConfig config){

    }
}
