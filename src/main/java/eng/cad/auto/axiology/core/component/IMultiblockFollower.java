package eng.cad.auto.axiology.core.component;

import eng.cad.auto.axiology.crafting.core.CustomCraftingContainer;

public interface IMultiblockFollower {
    void setMaster(IMultiblockMaster master);
    IMultiblockMaster getMaster();
    CustomCraftingContainer getCraftingContainer();
    default void clearMaster(){
        setMaster(null);
    }
}
