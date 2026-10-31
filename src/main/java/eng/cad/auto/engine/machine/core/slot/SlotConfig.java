package eng.cad.auto.engine.machine.core.slot;

public record SlotConfig(SlotType slotType, int slot, SlotIORestriction restriction, int capacity) {
}
