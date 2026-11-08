package eng.cad.auto.axiology.modulartools.components.tools;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;

public interface IModularShovel {
    ShovelItem getShovel(ItemStack stackInHand);
    default public InteractionResult useAsShovel(UseOnContext context) {
        return getShovel(context.getItemInHand()).useOn(context);
    }

    default public boolean shovelEffective(BlockState state, ShovelItem item){
        return item.isCorrectToolForDrops(state);
    }
}
