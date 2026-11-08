package eng.cad.auto.axiology.modulartools.components.tiers;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;

public interface IModularTierWood extends IModularTierUpgrade{
    default Tier getTier(){
        return Tiers.WOOD;
    }
}
