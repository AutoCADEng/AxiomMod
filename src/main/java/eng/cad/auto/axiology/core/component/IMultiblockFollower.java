package eng.cad.auto.axiology.core.component;

import eng.cad.auto.engine.crafting.core.CustomCraftingContainer;

public interface IMultiblockFollower {
    void setMaster(IMultiblockMaster master);
    IMultiblockMaster getMaster();
    CustomCraftingContainer getCraftingContainer();
    default void clearMaster(){
        setMaster(null);
    }
}
