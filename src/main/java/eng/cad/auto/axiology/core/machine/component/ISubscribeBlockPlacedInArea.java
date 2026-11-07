package eng.cad.auto.axiology.core.machine.component;

import eng.cad.auto.axiology.core.events.BlockPlaceAreaListeners;
import eng.cad.auto.axiology.core.machine.TickingMachine;
import net.minecraftforge.event.level.BlockEvent;

public interface ISubscribeBlockPlacedInArea {
    default void bindBlockPlacedInArea(TickingMachine<?> listener){
        BlockPlaceAreaListeners.bindListener(listener);
    }
    default void unbindBlockPlacedInArea(TickingMachine<?> listener){
        BlockPlaceAreaListeners.unbindListener(listener);
    }
    void fireBlockPlaceEvent(BlockEvent.EntityPlaceEvent event);
}
