package eng.cad.auto.axiom.common.machine.core;

import eng.cad.auto.axiom.AxiomMod;
import eng.cad.auto.axiom.network.FluidNetwork;
import eng.cad.auto.axiom.network.FluidSyncPacket;
import eng.cad.auto.axiom.registry.MachineRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GenericBlockEntity extends BlockEntity implements MenuProvider {
    public final MachineInventory machine;
    private LazyOptional<IItemHandler> itemCapability;
    private LazyOptional<IFluidHandler> fluidCapability;
    public MachineHolder.Config machineConfig;


    public GenericBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, MachineHolder.Config machineConfig){
        super(type, pos, state);
        this.machineConfig = machineConfig;
        machine = new MachineInventory(machineConfig, this);
        refreshCapabilities();
    }

    public void tick(Level level, BlockPos blockPos, BlockState blockState) {
        AxiomMod.LOGGER.warn("FAILED TO OVERWRITE TICK METHOD FOR BLOCK ENTITY {}", level.getBlockEntity(blockPos).getType().toString());
    }

    public static  void tick(Level level, BlockPos blockPos, BlockState blockState, GenericBlockEntity blockEntity){
        blockEntity.machineConfig.serverLogic().serverTick(level, blockPos, blockState);

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
            AxiomMod.LOGGER.info(
                    "Pipe item capability request: side={}, inventory={}, valid={}",
                    side,
                    machine.inventory != null,
                    itemCapability.isPresent()
            );
            AxiomMod.LOGGER.info(
                    "Slots={}, slot0={}, extractable={}",
                    machine.inventory.getSlots(),
                    machine.inventory.getStackInSlot(0),
                    machine.inventory.extractItem(0, 1, true)
            );
            return itemCapability.cast();
        }

        if ((machineConfig.fluidSlots()>0) && (capability == ForgeCapabilities.FLUID_HANDLER)) {
            AxiomMod.LOGGER.info(
                    "Pipe fluid capability request: side={}, inventory={}, valid={}",
                    side,
                    machine.tanks != null,
                    itemCapability.isPresent()
            );
            AxiomMod.LOGGER.info(
                    "Slots={}, slot0={}, extractable={}",
                    machine.tanks.getTanks(),
                    machine.tanks.getFluidInTank(0),
                    machine.tanks.fill(new FluidStack(Fluids.WATER, 1000), IFluidHandler.FluidAction.SIMULATE)
            );
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
}
