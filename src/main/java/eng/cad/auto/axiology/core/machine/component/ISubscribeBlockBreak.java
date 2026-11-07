package eng.cad.auto.axiology.core.machine.component;

import eng.cad.auto.axiology.core.events.BlockBreakListeners;
import eng.cad.auto.axiology.core.machine.TickingMachine;
import net.minecraftforge.event.level.BlockEvent;

public interface ISubscribeBlockBreak {
    default void bindBlockBreakEvent(TickingMachine<?> listener){
        BlockBreakListeners.bindListener(listener);
    }

    default void unbindBlockBreakEvent(TickingMachine<?> listener){
        BlockBreakListeners.unbindListener(listener);
    }

    void fireBlockBreak(BlockEvent.BreakEvent event);
}
