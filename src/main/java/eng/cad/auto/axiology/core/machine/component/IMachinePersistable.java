package eng.cad.auto.axiology.core.machine.component;

import eng.cad.auto.axiology.core.ISlotData;
import eng.cad.auto.axiology.core.ISlotFluid;
import eng.cad.auto.axiology.core.ISlotItem;
import eng.cad.auto.axiology.core.machine.BaseMachine;
import net.minecraft.nbt.CompoundTag;

public interface IMachinePersistable {

    default void saveInventory(BaseMachine blockEntity, CompoundTag tag) {
        if (blockEntity instanceof ISlotItem inventory) {
            tag.put("Inventory", inventory.getItemSlots().serializeNBT());
        }

        if (blockEntity instanceof ISlotFluid tanks) {
            tag.put("Tanks", tanks.getFluidSlots().serializeNBT());
        }
        if (blockEntity instanceof ISlotData datastore) {
            tag.putInt("Data_Slots", datastore.getDataSlots().getCount());
            for (int idx = 0; idx < datastore.getDataSlots().getCount(); idx++) {
                tag.putInt("Data_" + idx, datastore.getDataSlots().get(idx));
            }
        }
    }

    default void loadInventory(BaseMachine blockEntity, CompoundTag tag) {
        if (blockEntity instanceof ISlotItem inventory) {
            if (tag.contains("Inventory")) inventory.getItemSlots().deserializeNBT(tag.getCompound("Inventory"));
        }
        if (blockEntity instanceof ISlotFluid tanks) {
            if (tag.contains("Tanks")) tanks.getFluidSlots().deserializeNBT(tag.getCompound("Tanks"));
        }
        if (blockEntity instanceof ISlotData datastore) {
            int dataSlots = tag.getInt("Data_Slots");
            for (int idx = 0; idx < dataSlots; idx++) {
                datastore.getDataSlots().set(idx, tag.getInt("Data_" + idx));
            }
        }
    }
}

