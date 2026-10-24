package eng.cad.auto.axiom.common.machine.core;

import eng.cad.auto.axiom.common.crafting.core.FluidStackHandler;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;

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
            protected void onContentsChanged(int slot) {

                blockEntity.setChanged();
                blockEntity.syncFluidTank(slot);
            }
        } : null;
        tanks = config.fluidSlots() > 0 ? new FluidStackHandler(config.fluidSlots()){
            @Override
            protected void onContentsChanged(int slot) {
                blockEntity.setChanged();
                blockEntity.syncFluidTank(slot);
            }
        } : null;
        metadata = config.dataSlots() > 0 ? new SimpleContainerData(config.dataSlots()) : null;

    }

}
