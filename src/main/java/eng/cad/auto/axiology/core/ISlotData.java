package eng.cad.auto.axiology.core;

import eng.cad.auto.axiology.core.machine.BaseMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface ISlotData {
    default int getSyncInterval(){
        return 20;
    }

    DataSlotHandler getDataSlots();
    void setChanged();
    int[] getLastSyncedData();
    void setLastSyncedData(int[] data);
    long getLastSyncTime();
    void setLastSyncTime(long time);

    default void syncWithClients(Level level, BlockPos pos) {
        if (level == null || level.isClientSide) return;
        if (!(level.getBlockEntity(pos) instanceof BaseMachine baseMachine)) return;
        setChanged();
        BlockState state = baseMachine.getBlockState();
        level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
    }

    default void updateDataIfChanged(Level level, BlockPos pos) {
        if (level == null || level.isClientSide) return;

        DataSlotHandler data = getDataSlots();
        int count = data.getCount();

        int[] last = getLastSyncedData();
        boolean firstSync = last == null || last.length != count;
        if (firstSync) {
            last = new int[count];
            setLastSyncedData(last);
        }

        int[] current = new int[count];
        boolean changed = firstSync;
        boolean skipTimer = firstSync;
        for (int i = 0; i < count; i++) {
            current[i] = data.get(i);
            if (current[i] != last[i]) {
                changed = true;
                if ((current[i] == 0) != (last[i] == 0)) skipTimer = true;
            }
        }

        if (!changed) return;
        long time = level.getGameTime();
        if (!skipTimer && time - getLastSyncTime() < getSyncInterval()) return;
        System.arraycopy(current, 0, last, 0, count);
        setLastSyncTime(time);
        syncWithClients(level, pos);
    }
}