package eng.cad.auto.axiology.core.slot;

public record SlotConfig(SlotIORestriction restriction, int capacity) {
    public boolean operationAllowed(SlotIORestriction operation){
        if (operation.equals(SlotIORestriction.IN_OUT)) return false;
        if (restriction().equals(SlotIORestriction.IN_OUT)) return true;
        return operation.equals(restriction());
    }
}
