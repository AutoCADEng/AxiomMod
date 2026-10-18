package eng.cad.auto.axiom.recipes.api;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

public class CustomResult {
    public NonNullList<ItemStack> outputItems;
    public CustomResult(NonNullList<ItemStack> resultItems){
        this.outputItems = resultItems;
    }
}
