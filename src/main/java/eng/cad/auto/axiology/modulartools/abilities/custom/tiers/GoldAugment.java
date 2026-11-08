package eng.cad.auto.axiology.modulartools.abilities.custom.tiers;

import eng.cad.auto.axiology.modulartools.components.tiers.IModularTierGold;
import net.minecraft.network.chat.Component;

public class GoldAugment extends AbstractMaterialAugment implements IModularTierGold {
    @Override
    public Component getDisplayName() {
        return Component.translatable("axiology.modulartools.plating.gold");
    }
}
