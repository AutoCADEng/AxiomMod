package eng.cad.auto.axiology.modulartools.abilities.custom.tiers;

import eng.cad.auto.axiology.modulartools.components.tiers.IModularTierNetherite;
import net.minecraft.network.chat.Component;

public class NetheriteAugment extends AbstractMaterialAugment implements IModularTierNetherite {
    @Override
    public Component getDisplayName() {
        return Component.translatable("axiology.modulartools.plating.netherite");
    }
}
