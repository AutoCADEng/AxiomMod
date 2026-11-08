package eng.cad.auto.axiology.modulartools.abilities.custom.tiers;

import eng.cad.auto.axiology.modulartools.components.ModularToolAugment;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Tier;

public class MaterialAugment extends ModularToolAugment {
    Component name;
    Tier tier;

    public MaterialAugment(Component name, Tier tier){
     this.name = name;
     this.tier = tier;
    }

    @Override
    public Component getDisplayName() {
        return null;
    }

    @Override
    public int maxStackable() {
        return 1;
    }

    @Override
    public boolean isCompatible(ModularToolAugment ability) {
        return !(ability instanceof MaterialAugment);
    }
}
