package eng.cad.auto.axiology.magic.rebounds;

import eng.cad.auto.axiology.magic.rebounds.core.AbstractRebound;
import eng.cad.auto.axiology.magic.rebounds.core.InfusionContext;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class CurseOfFoodRot extends AbstractRebound {
    @Override
    public void apply(InfusionContext context) {
        if (context.level == null) return;
        if (context.level.isClientSide) return;
        if (context.player == null) return;
        context.player.sendSystemMessage(
                Component.translatable("curse.rot.food").withStyle(ChatFormatting.DARK_PURPLE)
        );
        List<ItemStack> mainInventory = context.player.getInventory().items;
        for (int idx = 0; idx < mainInventory.size(); idx++) {
            ItemStack itemStack = mainInventory.get(idx);
            if (itemStack.getItem().isEdible()) {
                mainInventory.set(idx, new ItemStack(Items.ROTTEN_FLESH, itemStack.getCount()));
            }
        }
    }
}
