package eng.cad.auto.axiology.modulartools.abilities.custom.tiers;

import eng.cad.auto.axiology.modulartools.components.ModularToolAugment;

public abstract class AbstractMaterialAugment extends ModularToolAugment {

    @Override
    public int maxStackable() {
        return 1;
    }

    @Override
    public boolean isCompatible(ModularToolAugment ability) {
        return !(ability instanceof AbstractMaterialAugment);
    }
}
