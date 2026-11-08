package eng.cad.auto.axiology.modulartools.components.tools;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;

public interface IModularShear {
    ShearsItem getShear(ItemStack stackInHand);
    default public InteractionResult useAsShear(UseOnContext context) {
        return getShear(context.getItemInHand()).useOn(context);
    }
    default public boolean shearEffectiveness(BlockState state, ShearsItem item){
        return item.isCorrectToolForDrops(state);
    }
}
