package eng.cad.auto.axiology.machines.multiblock.infusion;

import eng.cad.auto.axiology.core.DataSlotHandler;
import eng.cad.auto.axiology.core.ExtendedItemStackHandler;
import eng.cad.auto.axiology.core.ISlotData;
import eng.cad.auto.axiology.core.ISlotItem;
import eng.cad.auto.axiology.core.slot.SlotConfig;
import eng.cad.auto.axiology.core.slot.SlotIORestriction;
import eng.cad.auto.axiology.machines.multiblock.followers.Hatch;
import eng.cad.auto.engine.crafting.core.CustomCraftingContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ArcanePedestal extends Hatch<ArcanePedestal> implements ISlotItem, ISlotData {
    public static String name = "arcane_pedestal";
    int[] lastSyncData;
    long lastSyncTime;

    public ArcanePedestal(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        this.data = new DataSlotHandler("itemVisible");
        this.items = new ExtendedItemStackHandler(new SlotConfig[]{
                new SlotConfig(SlotIORestriction.INPUT, 1)
            }
        );

        refreshCapabilities();
    }

    @Override
    public DataSlotHandler getDataSlots() {
        return this.data;
    }

    @Override
    public int[] getLastSyncedData() {
        return lastSyncData;
    }

    @Override
    public void setLastSyncedData(int[] data) {
        this.lastSyncData = data;
    }

    @Override
    public long getLastSyncTime() {
        return lastSyncTime;
    }

    @Override
    public void setLastSyncTime(long time) {
        lastSyncTime = time;
    }

    @Override
    public ExtendedItemStackHandler getItemSlots() {
        return null;
    }

    @Override
    public CustomCraftingContainer getCraftingContainer() {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Hatch<ArcanePedestal> getInstance() {
        return this;
    }
}
