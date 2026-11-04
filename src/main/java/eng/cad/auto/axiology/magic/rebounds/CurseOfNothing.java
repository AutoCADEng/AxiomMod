package eng.cad.auto.axiology.magic.rebounds;

import eng.cad.auto.axiology.magic.rebounds.core.AbstractRebound;
import eng.cad.auto.axiology.magic.rebounds.core.InfusionContext;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class CurseOfNothing extends AbstractRebound {
    @Override
    public void apply(InfusionContext context) {
        context.player.sendSystemMessage(
                Component.translatable("curse.none").withStyle(ChatFormatting.DARK_PURPLE)
        );
    }
}
