package eng.cad.auto.axiom.common.machine.core;

import eng.cad.auto.axiom.AxiomMod;
import eng.cad.auto.axiom.common.fluid.FluidStackHandler;
import eng.cad.auto.axiom.common.machine.core.slot.SlotConfig;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class MachineInventory{
    public final MachineHolder.Config config;
    public final ItemStackHandler inventory;
    public final FluidStackHandler tanks;
    public final SimpleContainerData metadata;
    public final BlockEntity blockEntity;

    public MachineInventory(MachineHolder.Config config, GenericBlockEntity blockEntity){
        this.config = config;
        this.blockEntity = blockEntity;
        inventory = config.itemSlots() > 0 ? new ItemStackHandler(config.itemSlots()){
            @Override
            public int getSlotLimit(int slot) {
                SlotConfig cfg = config.itemSlotConfig().get(slot);
                return cfg == null ? 64 : cfg.capacity();
            }

            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                ItemStack result = super.insertItem(slot, stack, simulate);

                AxiomMod.LOGGER.info(
                        "INSERT slot={}, requested={}, limit={}, simulate={}, inserted={}, remainder={}",
                        slot,
                        stack.getCount(),
                        getSlotLimit(slot),
                        simulate,
                        stack.getCount() - result.getCount(),
                        result.getCount()
                );

                return result;
            }

            @Override
            public void setStackInSlot(int slot, @NotNull ItemStack stack) {
                int limit = Math.min(getSlotLimit(slot), stack.getMaxStackSize());
                if (stack.getCount() > limit) {
                    stack = stack.copy();
                    stack.setCount(limit);
                }
                super.setStackInSlot(slot, stack);
            }



            @Override
            protected void onContentsChanged(int slot) {
                blockEntity.setChanged();
            }
        } : null;
        tanks = config.fluidSlots() > 0 ? new FluidStackHandler(config.fluidSlots(), config.fluidSlotConfig().values().toArray(SlotConfig[]::new)){
            @Override
            protected void onContentsChanged(int slot) {
                blockEntity.setChanged();
                blockEntity.syncFluidTank(slot);
            }
        } : null;
        metadata = config.dataSlots() > 0 ? new SimpleContainerData(config.dataSlots()) : null;

    }

}
