package eng.cad.auto.axiology.modulartools.components.tools;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;

public interface IModularHoe {
    HoeItem getHoe(ItemStack stackInHand);
    default public InteractionResult useAsHoe(UseOnContext context) {
        return getHoe(context.getItemInHand()).useOn(context);
    }
    default public boolean hoeEffective(BlockState state, HoeItem item){
        return item.isCorrectToolForDrops(state);
    }
}
