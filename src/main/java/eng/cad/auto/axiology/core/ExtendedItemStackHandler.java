package eng.cad.auto.axiology.core;

import eng.cad.auto.axiology.core.slot.SlotConfig;
import eng.cad.auto.axiology.core.slot.SlotIORestriction;
import net.minecraftforge.items.ItemStackHandler;

public class ExtendedItemStackHandler extends ItemStackHandler{
    SlotConfig[] slots;

    public ExtendedItemStackHandler(SlotConfig[] slotConfig){
        super(slotConfig.length);
        slots = slotConfig;
    }

    public ExtendedItemStackHandler() {
        super();
    }

    public boolean operationAllowed(int slot, SlotIORestriction operation){
        return slots[slot].operationAllowed(operation);
    }
    public boolean operationNotAllowed(int slot, SlotIORestriction operation){
        return !slots[slot].operationAllowed(operation);
    }


    public int getCapacity(int slot){
        return slots[slot].capacity();
    }
}
