package eng.cad.auto.axiology.core.machine;

import eng.cad.auto.axiology.core.*;
import eng.cad.auto.axiology.core.machine.component.IMachinePersistable;
import eng.cad.auto.axiology.core.machine.component.ISidedMachine;
import eng.cad.auto.axiology.core.fluid.FluidStackHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseMachine<T extends BaseMachine<T>> extends BlockEntity {
    protected ExtendedItemStackHandler items;
    protected FluidStackHandler fluids;
    protected DataSlotHandler data;

    private LazyOptional<IItemHandler> itemCapability;
    private LazyOptional<IFluidHandler> fluidCapability;

    MachineFactory factory;
    public BaseMachine(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        items = new ExtendedItemStackHandler();
        fluids = new FluidStackHandler();
        data = new DataSlotHandler();
        refreshCapabilities();
    }

    public abstract String getName();



    public abstract T getInstance();

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag){
        super.saveAdditional(tag);
        if(!(getInstance() instanceof IMachinePersistable persistable)) return;
        persistable.saveInventory(getInstance(), tag);
    }

    @Override
    public void load(@NotNull CompoundTag tag){
        super.load(tag);
        if(!(getInstance() instanceof IMachinePersistable persistable)) return;
        persistable.loadInventory(getInstance(), tag);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
        if (getInstance() instanceof ISlotItem item && capability == ForgeCapabilities.ITEM_HANDLER) {
            if (getInstance() instanceof ISidedMachine sided) {
                if (sided.getSide(side)) return itemCapability.cast();
            } else {
                return itemCapability.cast();
            }
        }

        if (getInstance() instanceof ISlotFluid fluid && (capability == ForgeCapabilities.FLUID_HANDLER)) {
            if (getInstance() instanceof ISidedMachine sided) {
                if (sided.getSide(side)) return fluidCapability.cast();
            } else {
                return fluidCapability.cast();
            }
        }
        return super.getCapability(capability, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        if (itemCapability != null) itemCapability.invalidate();
        if (fluidCapability != null) fluidCapability.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        refreshCapabilities();
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = saveWithoutMetadata();
        if (getInstance() instanceof ISlotData datastore && getInstance() instanceof IMachinePersistable persistable){
            if (datastore.getDataSlots() != null) {
                int[] data = new int[datastore.getDataSlots().getCount()];
                for (int i = 0; i < data.length; i++) data[i] = datastore.getDataSlots().get(i);
                tag.putIntArray("AxiomData", data);
            }
        }
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);
        if (getInstance() instanceof ISlotData datastore) {
            if (datastore.getDataSlots() != null && tag.contains("AxiomData", Tag.TAG_INT_ARRAY)) {
                int[] data = tag.getIntArray("AxiomData");
                for (int i = 0; i < Math.min(data.length, datastore.getDataSlots().getCount()); i++) {
                    datastore.getDataSlots().set(i, data[i]);
                }
            }
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
    public void refreshCapabilities() {
        if (getInstance() instanceof ISlotItem item) {
            itemCapability = item.getItemSlots().getSlots() > 0
                    ? LazyOptional.of(item::getItemSlots)
                    : LazyOptional.empty();
        }
        if (getInstance() instanceof ISlotFluid fluid) {
            fluidCapability = fluid.getFluidSlots().getTanks() > 0
                    ? LazyOptional.of(fluid::getFluidSlots)
                    : LazyOptional.empty();
        }
    }
    @FunctionalInterface
    public interface MachineFactory {
        BaseMachine<?> create(BlockEntityType<?> type, BlockPos pos, BlockState state);
    }
}
