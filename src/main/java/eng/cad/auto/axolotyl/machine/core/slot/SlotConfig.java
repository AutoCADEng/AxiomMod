package eng.cad.auto.axolotyl.machine.core.slot;

public record SlotConfig(SlotType slotType, int slot, SlotIORestriction restriction, int capacity) {
}
