package eng.cad.auto.axiology.magic.rebounds;

import eng.cad.auto.axiology.magic.rebounds.core.AbstractRebound;
import eng.cad.auto.axiology.magic.rebounds.core.InfusionContext;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class CurseOfBlindness extends AbstractRebound {
    @Override
    public void apply(InfusionContext context) {
        if (context.level == null) return;
        if (context.level.isClientSide) return;
        if (context.player == null) return;
        context.player.sendSystemMessage(
                Component.translatable("curse.blindness").withStyle(ChatFormatting.DARK_PURPLE)
        );
        context.player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS,99999,1));
    }
}
