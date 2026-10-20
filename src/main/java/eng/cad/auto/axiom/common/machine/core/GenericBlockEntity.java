package eng.cad.auto.axiom.common.machine.core;

import eng.cad.auto.axiom.AxiomMod;
import eng.cad.auto.axiom.registry.MachineRegistry;
import net.minecraft.core.BlockPos;
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

    GenericMachine.Config machineConfig;
    public final MachineData machine;

    public GenericBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, GenericMachine.Config machineConfig){
        super(type, pos, state);
        this.machineConfig = machineConfig;
        machine = new MachineData(machineConfig);
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

}
