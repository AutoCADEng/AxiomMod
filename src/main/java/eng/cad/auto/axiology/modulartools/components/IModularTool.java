package eng.cad.auto.axiology.modulartools.components;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IModularTool {
    static int getMaxAugments(ItemStack tool){
        return 9;
    }

    boolean canBeDepleted(Player player);
}
