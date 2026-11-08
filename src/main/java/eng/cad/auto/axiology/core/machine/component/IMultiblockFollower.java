package eng.cad.auto.axiology.core.machine.component;

import eng.cad.auto.axiology.crafting.core.CustomCraftingContainer;

public interface IMultiblockFollower {
    IMultiblockMaster getMaster();

    void setMaster(IMultiblockMaster master);

    CustomCraftingContainer getCraftingContainer();

    default void clearMaster() {
        setMaster(null);
    }
}
