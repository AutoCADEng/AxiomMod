package eng.cad.auto.axiology.modulartools.abilities.custom.tiers;

import eng.cad.auto.axiology.modulartools.components.tiers.IModularTierWood;
import net.minecraft.network.chat.Component;

public class WoodAugment extends AbstractMaterialAugment implements IModularTierWood {
    @Override
    public Component getDisplayName() {
        return Component.translatable("axiology.modulartools.plating.wood");
    }
}
