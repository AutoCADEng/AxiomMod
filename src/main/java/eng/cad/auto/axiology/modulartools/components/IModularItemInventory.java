package eng.cad.auto.axiology.modulartools.components;

import eng.cad.auto.axiology.core.ExtendedItemStackHandler;
import net.minecraft.nbt.CompoundTag;

public interface IModularItemInventory {
    ExtendedItemStackHandler getInventory();

    default void saveInventory(CompoundTag tag){
            tag.put("Inventory", getInventory().serializeNBT());
    }

    default void loadInventory(CompoundTag tag) {
        if (tag.contains("Inventory")) getInventory().deserializeNBT(tag.getCompound("Inventory"));
    }
}
