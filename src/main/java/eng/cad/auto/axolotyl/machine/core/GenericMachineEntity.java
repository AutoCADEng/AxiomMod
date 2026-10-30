package eng.cad.auto.axolotyl.machine.core;

import eng.cad.auto.axiom.AxiomMod;
import eng.cad.auto.axolotyl.network.FluidNetwork;
import eng.cad.auto.axolotyl.network.FluidSyncPacket;
import eng.cad.auto.axiom.registry.MachineRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
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

public class GenericMachineEntity extends BlockEntity implements MenuProvider {
    private final MachineInventory machine;
    private LazyOptional<IItemHandler> itemCapability;
    private LazyOptional<IFluidHandler> fluidCapability;
    public MachineCore.Config machineConfig;
    private int[] lastSyncedData;
    private long lastDataSyncTime;

    public GenericMachineEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, MachineCore.Config machineConfig){
        super(type, pos, state);
        this.machineConfig = machineConfig;
        machine = new MachineInventory(machineConfig, this);
        refreshCapabilities();
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, GenericMachineEntity blockEntity){
        blockEntity.machineConfig.serverLogic().serverTick(level, blockPos, blockState);
        blockEntity.syncData();
    }

    public MachineInventory get(){
        return machine;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(AxiomMod.MODID + ".machines." + machineConfig.name());
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return MachineRegistry.MACHINELIST.get(machineConfig.name()).ui.new Menu(containerId, inventory, this);
    }

    @Override
    protected void saveAdditional(CompoundTag p_187471_) {
        super.saveAdditional(p_187471_);
        machineConfig.serverLogic().saveInventory(this, p_187471_);
    }

    @Override
    public void load(CompoundTag p_155245_) {
        super.load(p_155245_);
        machineConfig.serverLogic().loadInventory(this, p_155245_);
    }


    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
        if ((machineConfig.itemSlots()>0) && (capability == ForgeCapabilities.ITEM_HANDLER)) {
            return itemCapability.cast();
        }
        if ((machineConfig.fluidSlots()>0) && (capability == ForgeCapabilities.FLUID_HANDLER)) {
            return fluidCapability.cast();
        }
        return super.getCapability(capability, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemCapability.invalidate();
        fluidCapability.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        refreshCapabilities();
    }
    private void refreshCapabilities() {
        itemCapability = machineConfig.itemSlots()>0
                ? LazyOptional.of(() -> machine.inventory)
                : LazyOptional.empty();

        fluidCapability = machineConfig.fluidSlots()>0
                ? LazyOptional.of(() -> machine.tanks)
                : LazyOptional.empty();
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = saveWithoutMetadata();
        if (machine.metadata != null) {
            int[] data = new int[machine.metadata.getCount()];
            for (int i = 0; i < data.length; i++) data[i] = machine.metadata.get(i);
            tag.putIntArray("AxiomData", data);
        }
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);
        if (machine.metadata != null && tag.contains("AxiomData", Tag.TAG_INT_ARRAY)) {
            int[] data = tag.getIntArray("AxiomData");
            for (int i = 0; i < Math.min(data.length, machine.metadata.getCount()); i++) {
                machine.metadata.set(i, data[i]);
            }
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void syncFluidTank(int tank) {
        if (level == null) return;
        if (level.isClientSide) return;
        if (machine.tanks == null) return;

        FluidNetwork.sendToClientWithChunk(
                level,
                worldPosition,
                new FluidSyncPacket(
                        worldPosition,
                        tank,
                        machine.tanks.getFluidInTank(tank)
                )
        );
    }

    public void syncToClients() {
        if (level == null || level.isClientSide) return;
        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }

    public void syncData(){
        if (level == null || level.isClientSide || machine.metadata == null) return;
        if (lastSyncedData == null) lastSyncedData = new int[machineConfig.dataSlots()];
        boolean changed = false;
        boolean skipTimer = false;
        for (int idx = 0; idx<machineConfig.dataSlots(); idx++){
            int newDataValue = machine.metadata.get(idx);
            if (newDataValue != lastSyncedData[idx]){
                changed = true;
                if ((newDataValue == 0 && lastSyncedData[idx] != 0) || (newDataValue != 0 && lastSyncedData[idx] == 0)) skipTimer = true;
            }
        }

        if (!changed) return;
        long time = level.getGameTime();
        if (!skipTimer && time - lastDataSyncTime < 20) return;
        for (int idx = 0; idx < machineConfig.dataSlots(); idx++) lastSyncedData[idx] = machine.metadata.get(idx);
        lastDataSyncTime = time;
        syncToClients();
    }

    public static void clientTick(Level level, BlockPos blockPos, BlockState blockState, GenericMachineEntity blockEntity) {
        blockEntity.machineConfig.clientLogic().clientTick(level, blockPos, blockState, blockEntity);
    }
}
