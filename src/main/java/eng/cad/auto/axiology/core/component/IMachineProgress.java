package eng.cad.auto.axiology.core.component;

import eng.cad.auto.axiology.core.machine.BaseMachine;
import eng.cad.auto.axiology.core.ISlotData;

public interface IMachineProgress {
    int progressSlot();
    int maxProgress();

    default int getProgress(BaseMachine machineEntity){
        if (!(machineEntity instanceof ISlotData datastore)) return 0;
        return datastore.getDataSlots().get(progressSlot());
    };

    default void incrementProgress(BaseMachine machineEntity, int amount){
        if (!(machineEntity instanceof ISlotData datastore)) return;
        datastore.getDataSlots().set(progressSlot(), getProgress(machineEntity) + amount);
    };

    default void resetProgress(BaseMachine machineEntity){
        if (!(machineEntity instanceof ISlotData datastore)) return;
        datastore.getDataSlots().set(progressSlot(), 0);
    };

    default boolean progressCompleted(BaseMachine machineEntity){
        return getProgress(machineEntity)>= maxProgress();
    }
}
