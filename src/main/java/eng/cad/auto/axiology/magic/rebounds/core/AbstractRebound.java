package eng.cad.auto.axiology.magic.rebounds.core;

public abstract class AbstractRebound {
    public boolean canApply(InfusionContext context) {
        return true;
    }
    public abstract void apply(InfusionContext context);
}
