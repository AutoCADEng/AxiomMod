package eng.cad.auto.axiology.magic.rebounds;

import eng.cad.auto.axiology.magic.rebounds.core.AbstractRebound;
import eng.cad.auto.axiology.magic.rebounds.core.InfusionContext;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class CurseOfPain extends AbstractRebound {
    @Override
    public void apply(InfusionContext context) {
        if (context.level == null) return;
        if (context.level.isClientSide) return;
        if (context.player == null) return;
        context.player.sendSystemMessage(
                Component.translatable("curse.pain").withStyle(ChatFormatting.DARK_PURPLE)
        );
        context.player.setHealth(1f);
    }
}
