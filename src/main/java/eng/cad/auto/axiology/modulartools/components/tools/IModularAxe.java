package eng.cad.auto.axiology.modulartools.components.tools;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;

//TODO candidate for merging
public interface IModularAxe {
    AxeItem getAxe(ItemStack stackInHand);
    default public InteractionResult useAsAxe(UseOnContext context) {
        return getAxe(context.getItemInHand()).useOn(context);
    }

    default public boolean axeEffective(BlockState state, AxeItem item){
        return item.isCorrectToolForDrops(state);
    }
}
