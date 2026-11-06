package eng.cad.auto.axiology.core.component;

import eng.cad.auto.axiology.core.machine.TickingMachine;
import eng.cad.auto.axiology.core.ISlotData;

public interface IMachineProgress {
    int progressSlot();
    int maxProgress();

    default int getProgress(TickingMachine machineEntity){
        if (!(machineEntity instanceof ISlotData datastore)) return 0;
        return datastore.getDataSlots().get(progressSlot());
    };

    default void incrementProgress(TickingMachine machineEntity, int amount){
        if (!(machineEntity instanceof ISlotData datastore)) return;
        datastore.getDataSlots().set(progressSlot(), getProgress(machineEntity) + amount);
    };

    default void resetProgress(TickingMachine machineEntity){
        if (!(machineEntity instanceof ISlotData datastore)) return;
        datastore.getDataSlots().set(progressSlot(), 0);
    };

    default boolean progressCompleted(TickingMachine machineEntity){
        return getProgress(machineEntity)>= maxProgress();
    }
}
