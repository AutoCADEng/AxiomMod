package eng.cad.auto.axiology.modulartools.abilities.custom.tiers;

import eng.cad.auto.axiology.modulartools.components.ModularToolAugment;
import eng.cad.auto.axiology.modulartools.components.tiers.IModularTierGold;
import eng.cad.auto.axiology.modulartools.components.tiers.IModularTierIron;
import net.minecraft.network.chat.Component;

public class IronAugment extends AbstractMaterialAugment implements IModularTierIron {
    @Override
    public Component getDisplayName() {
        return Component.translatable("axiology.modulartools.plating.iron");
    }
}
