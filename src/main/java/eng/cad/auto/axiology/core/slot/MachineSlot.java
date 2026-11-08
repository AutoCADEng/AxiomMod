package eng.cad.auto.axiology.core.slot;

public class MachineSlot {
    protected SlotConfig config;
    protected UniversalSlot contents;

    public UniversalSlot getContents() {
        return contents;
    }

    public MachineSlot setContents(UniversalSlot newContents) {
        contents = newContents;
        return this;
    }

    public SlotConfig getConfig() {
        return config;
    }

    public MachineSlot setConfig(SlotConfig newConfig) {
        config = newConfig;
        return this;
    }
}
