package eng.cad.auto.axiom.common.machine.core;

import eng.cad.auto.axiom.api.FluidStackHandler;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraftforge.items.ItemStackHandler;

public class MachineData {
    public final GenericMachine.Config config;
    public final ItemStackHandler inventory;
    public final FluidStackHandler tanks;
    public final SimpleContainerData metadata;

    public MachineData(GenericMachine.Config config){
        this.config = config;

        inventory = config.itemSlots() > 0 ? new ItemStackHandler(config.itemSlots()) : null;
        tanks = config.itemSlots() > 0 ? new FluidStackHandler(config.fluidSlots()) : null;
        metadata = config.dataSlots() > 0 ? new SimpleContainerData(config.dataSlots()) : null;
    }
}
