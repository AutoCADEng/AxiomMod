package eng.cad.auto.axiology.core.slot;

public record SlotConfig(SlotIORestriction restriction, int capacity) {
    public boolean operationAllowed(SlotIORestriction operation) {
        if (restriction.equals(SlotIORestriction.IN_OUT)) return true;
        if (operation.equals(SlotIORestriction.IN_OUT)) return false;
        return operation.equals(restriction);
    }
}
