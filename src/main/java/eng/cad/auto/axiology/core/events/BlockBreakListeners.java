package eng.cad.auto.axiology.core.events;

import eng.cad.auto.axiology.core.machine.TickingMachine;
import eng.cad.auto.axiology.core.machine.component.ISubscribeBlockBreak;
import net.minecraft.core.BlockPos;
import net.minecraftforge.event.level.BlockEvent;

import java.util.HashMap;

public class BlockBreakListeners {
    static HashMap<BlockPos, TickingMachine<?>> listeners = new HashMap<>();

    public static void bindListener(TickingMachine<?> listener) {
        listeners.put(listener.getBlockPos(), listener);
    }

    public static void unbindListener(TickingMachine<?> listener) {
        listeners.remove(listener.getBlockPos());
    }

    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        BlockPos breakPos = event.getPos();
        if (!listeners.containsKey(breakPos)) return;
        if (!(listeners.get(breakPos) instanceof ISubscribeBlockBreak eventBlock)) return;
        eventBlock.fireBlockBreak(event);
    }
}
