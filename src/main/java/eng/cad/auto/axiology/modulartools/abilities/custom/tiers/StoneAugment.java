package eng.cad.auto.axiology.modulartools.abilities.custom.tiers;

import eng.cad.auto.axiology.modulartools.components.ModularToolAugment;
import eng.cad.auto.axiology.modulartools.components.tiers.IModularTierIron;
import eng.cad.auto.axiology.modulartools.components.tiers.IModularTierStone;
import net.minecraft.network.chat.Component;

public class StoneAugment extends AbstractMaterialAugment implements IModularTierStone {
    @Override
    public Component getDisplayName() {
        return Component.translatable("axiology.modulartools.plating.stone");
    }

}
