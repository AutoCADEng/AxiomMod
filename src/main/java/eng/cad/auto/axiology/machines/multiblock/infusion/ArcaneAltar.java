package eng.cad.auto.axiology.machines.multiblock.infusion;

import eng.cad.auto.axiology.core.DataSlotHandler;
import eng.cad.auto.axiology.core.ExtendedItemStackHandler;
import eng.cad.auto.axiology.core.ISlotData;
import eng.cad.auto.axiology.core.ISlotItem;
import eng.cad.auto.axiology.core.blocks.BaseMachineBlock;
import eng.cad.auto.axiology.core.component.IMachineProgress;
import eng.cad.auto.axiology.core.component.IRightClickable;
import eng.cad.auto.axiology.core.component.ITickable;
import eng.cad.auto.axiology.core.machine.BaseMachine;
import eng.cad.auto.axiology.core.machine.BaseMaster;
import eng.cad.auto.axiology.core.slot.SlotConfig;
import eng.cad.auto.axiology.core.slot.SlotIORestriction;
import eng.cad.auto.axiology.machines.ChemicalReactor;
import eng.cad.auto.axiology.machines.multiblock.Pyrolyzer;
import eng.cad.auto.axiology.machines.util.CommonRightClickBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.registries.RegistryObject;

public class ArcaneAltar extends BaseMaster<ArcaneAltar> implements ISlotItem, ISlotData, IMachineProgress, ITickable, IRightClickable {
    public static String name = "arcane_altar";
    int[] lastSyncData;
    long lastSyncTime;
    boolean firstInit = false;
    public ArcaneAltar(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        this.data = new DataSlotHandler("progress", "recipeCompleted", "validShape");
        this.items = new ExtendedItemStackHandler(new SlotConfig[]{
                new SlotConfig(SlotIORestriction.IN_OUT, 1)
        }
        );
        refreshCapabilities();
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if(level.isClientSide()) return;
        if (!(level.getBlockEntity(pos) instanceof ArcaneAltar altar)) return;

        if (!firstInit){
            validateStructure(getBlockPos(), level);
            firstInit = true;
        }

        if (!isStructureValid()){
            resetProgress(altar);
            return;
        }
    }

    @Override
    public void updateStructureValidity(boolean validity) {
        getDataSlots().set("validShape", validity?1:0);
    }

    @Override
    public boolean isStructureValid() {
        return getDataSlots().get("validShape") == 1;
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
    public String getName() {
        return name;
    }

    @Override
    public ArcaneAltar getInstance() {
        return this;
    }

    @Override
    public int progressSlot() {
        return getDataSlots().indexOf("progress");
    }

    @Override
    public int maxProgress() {
        return (24000/60)/4;
    }

    public static class MachineBlock extends BaseMachineBlock {
        public MachineBlock(Properties properties, RegistryObject<BlockEntityType<? extends BaseMachine<?>>> blockEntity) {
            super(properties, blockEntity);
        }
    }
}
