package eng.cad.auto.axiology.magic.rebounds;

import eng.cad.auto.axiology.magic.rebounds.core.AbstractRebound;
import eng.cad.auto.axiology.magic.rebounds.core.InfusionContext;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class CurseOfWithering extends AbstractRebound {
    @Override
    public void apply(InfusionContext context) {
        if (context.level == null) return;
        if (context.level.isClientSide) return;
        if (context.player == null) return;

        context.player.addEffect(new MobEffectInstance(MobEffects.WITHER,300,1));
    }
}
