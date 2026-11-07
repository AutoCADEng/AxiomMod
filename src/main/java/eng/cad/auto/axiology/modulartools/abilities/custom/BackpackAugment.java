package eng.cad.auto.axiology.modulartools.abilities.custom;

import eng.cad.auto.axiology.core.ExtendedItemStackHandler;
import eng.cad.auto.axiology.core.slot.SlotConfig;
import eng.cad.auto.axiology.core.slot.SlotIORestriction;
import eng.cad.auto.axiology.modulartools.components.IModularItemInventory;
import eng.cad.auto.axiology.modulartools.components.ModularToolAugment;
import net.minecraft.network.chat.Component;

public class BackpackAugment extends ModularToolAugment implements IModularItemInventory {
    ExtendedItemStackHandler itemInventory = new ExtendedItemStackHandler(new SlotConfig[]{
            new SlotConfig(SlotIORestriction.IN_OUT,64),
            new SlotConfig(SlotIORestriction.IN_OUT,64),
            new SlotConfig(SlotIORestriction.IN_OUT,64),
            new SlotConfig(SlotIORestriction.IN_OUT,64),
            new SlotConfig(SlotIORestriction.IN_OUT,64),
            new SlotConfig(SlotIORestriction.IN_OUT,64),
            new SlotConfig(SlotIORestriction.IN_OUT,64),
            new SlotConfig(SlotIORestriction.IN_OUT,64),
            new SlotConfig(SlotIORestriction.IN_OUT,64)
    });

    @Override
    public Component getDisplayName() {
        return Component.translatable("axiology.modulartools.backpack");
    }

    @Override
    public ExtendedItemStackHandler getInventory() {
        return itemInventory;
    }

    @Override
    public int maxStackable() {
        return 1;
    }
}
