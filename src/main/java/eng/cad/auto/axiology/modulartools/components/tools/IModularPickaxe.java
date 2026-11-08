package eng.cad.auto.axiology.modulartools.components.tools;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;

public interface IModularPickaxe {
    PickaxeItem getPickaxe(ItemStack stackInHand);
    default public InteractionResult useAsPickaxe(UseOnContext context) {
        return getPickaxe(context.getItemInHand()).useOn(context);
    }
    default public boolean pickaxeEffective(BlockState state, PickaxeItem item){
        return item.isCorrectToolForDrops(state);
    }
}
