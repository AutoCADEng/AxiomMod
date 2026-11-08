package eng.cad.auto.axiology.modulartools.components.tools;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;

public interface IModularSword {
    SwordItem getSword(ItemStack stackInHand);

    default public InteractionResult useAsSword(UseOnContext context) {
        return getSword(context.getItemInHand()).useOn(context);
    }

    default public boolean swordEffective(BlockState state, SwordItem item){
        return item.isCorrectToolForDrops(state);
    }
}
