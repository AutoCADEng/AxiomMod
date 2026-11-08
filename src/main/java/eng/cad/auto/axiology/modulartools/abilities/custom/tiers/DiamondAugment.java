package eng.cad.auto.axiology.modulartools.abilities.custom.tiers;

import eng.cad.auto.axiology.modulartools.components.tiers.IModularTierDiamond;
import eng.cad.auto.axiology.modulartools.components.tiers.IModularTierGold;
import net.minecraft.network.chat.Component;

public class DiamondAugment extends AbstractMaterialAugment implements IModularTierDiamond {
    @Override
    public Component getDisplayName() {
        return Component.translatable("axiology.modulartools.plating.diamond");
    }
}
