package eng.cad.auto.axiom.common.machine.core;

import eng.cad.auto.axiom.AxiomMod;
import eng.cad.auto.axiom.registry.MachineRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class GenericBlockEntity extends BlockEntity implements MenuProvider {

    public MachineHolder.Config machineConfig;
    public final MachineInventory machine;

    public GenericBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, MachineHolder.Config machineConfig){
        super(type, pos, state);
        this.machineConfig = machineConfig;
        machine = new MachineInventory(machineConfig, this);
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
}
