package eng.cad.auto.axiom.common.machine.core.slot;

public record SlotConfig(SlotType slotType, int slot, SlotIORestriction restriction, int capacity) {
}
