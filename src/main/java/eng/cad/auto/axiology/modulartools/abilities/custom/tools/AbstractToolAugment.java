package eng.cad.auto.axiology.modulartools.abilities.custom.tools;

import eng.cad.auto.axiology.modulartools.components.IModularTool;
import eng.cad.auto.axiology.modulartools.components.ModularToolAugment;
import eng.cad.auto.axiology.modulartools.components.tiers.IModularTierUpgrade;
import net.minecraft.world.item.*;

import java.util.List;

public abstract class AbstractToolAugment extends ModularToolAugment {
    @Override
    public int maxStackable() {
        return 1;
    }

    public Tier getMaterial(ItemStack toolInHand){
        if (!(toolInHand.getItem() instanceof IModularTool modTool)) return null;

        Tier tier = IModularTool.getAugments(toolInHand).keySet()
                .stream()
                .filter(IModularTierUpgrade.class::isInstance)
                .map(IModularTierUpgrade.class::cast)
                .map(IModularTierUpgrade::getTier)
                .findFirst().orElse(null);

        // Me using List.of() here is peak laziness
        Tier baseTier =
                List.of(toolInHand.getItem())
                .stream()
                .filter(TieredItem.class::isInstance)
                .map(TieredItem.class::cast)
                .map(item -> item.getTier())
                .findFirst().orElse(Tiers.IRON);

        return tier == null ? baseTier : tier;
    }
    @Override
    public boolean isCompatible(ModularToolAugment ability) {
        return !(ability instanceof AbstractToolAugment);
    }
}
