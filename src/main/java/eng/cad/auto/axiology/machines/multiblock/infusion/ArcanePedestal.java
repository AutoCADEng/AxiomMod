package eng.cad.auto.axiology.machines.multiblock.infusion;

import eng.cad.auto.axiology.core.DataSlotHandler;
import eng.cad.auto.axiology.core.ExtendedItemStackHandler;
import eng.cad.auto.axiology.core.ISlotData;
import eng.cad.auto.axiology.core.ISlotItem;
import eng.cad.auto.axiology.core.blocks.BaseMachineBlock;
import eng.cad.auto.axiology.core.machine.BaseMachine;
import eng.cad.auto.axiology.core.machine.component.IMachinePersistable;
import eng.cad.auto.axiology.core.machine.component.IRightClickable;
import eng.cad.auto.axiology.core.slot.SlotConfig;
import eng.cad.auto.axiology.core.slot.SlotIORestriction;
import eng.cad.auto.axiology.crafting.core.CustomCraftingContainer;
import eng.cad.auto.axiology.machines.multiblock.followers.Hatch;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.registries.RegistryObject;

public class ArcanePedestal extends Hatch<ArcanePedestal> implements IMachinePersistable, ISlotItem, ISlotData, IRightClickable {
    public static String name = "arcane_pedestal";
    int[] lastSyncData;
    long lastSyncTime;

    public ArcanePedestal(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        this.data = new DataSlotHandler("isItemHidden");
        this.items = new ExtendedItemStackHandler(new SlotConfig[]{
                new SlotConfig(SlotIORestriction.INPUT, 1)
        }
        );
        refreshCapabilities();
    }

    @Override
    public InteractionResult rightClick(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult result) {
        getDataSlots().set("isItemHidden", 0);
        return IRightClickable.super.rightClick(blockState, level, blockPos, player, hand, result);
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
        return this.items;
    }

    @Override
    public CustomCraftingContainer getCraftingContainer() {
        return new CustomCraftingContainer(this).addSlot(getItemSlots().getStackInSlot(0));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Hatch<ArcanePedestal> getInstance() {
        return this;
    }

    public static class MachineBlock extends BaseMachineBlock {
        public MachineBlock(Properties properties, RegistryObject<BlockEntityType<? extends BaseMachine<?>>> blockEntity) {
            super(properties, blockEntity);
        }
    }
}
