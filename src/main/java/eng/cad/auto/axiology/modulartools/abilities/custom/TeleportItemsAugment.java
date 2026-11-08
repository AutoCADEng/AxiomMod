package eng.cad.auto.axiology.modulartools.abilities.custom;

import eng.cad.auto.axiology.core.events.BlockDropEvent;
import eng.cad.auto.axiology.modulartools.components.IModularDigAbility;
import eng.cad.auto.axiology.modulartools.components.IModularHitAbility;
import eng.cad.auto.axiology.modulartools.components.ModularToolAugment;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.Objects;

public class TeleportItemsAugment extends ModularToolAugment implements IModularDigAbility, IModularHitAbility {
    @Override
    public Component getDisplayName() {
        return Component.translatable("axiology.modulartools.teleport_items");
    }

    @Override
    public void onHit(ItemStack item, LivingEntity target, LivingEntity player, Integer integer) {
    }

    @Override
    public void killDropsEvent(LivingDropsEvent event) {
        for (ItemEntity drop : event.getDrops()) {
            if (!(event.getSource().getEntity() instanceof Player player)) {
                drop.setPos(Objects.requireNonNull(event.getSource().getSourcePosition()));
                drop.setDeltaMovement(Vec3.ZERO);
                continue;
            }
            ItemHandlerHelper.giveItemToPlayer(player, drop.getItem());
        }
    }

    @Override
    public void digDropEvent(BlockDropEvent event) {
        if (!(event.getEntity() instanceof ItemEntity drop)) return;
        Player miner = event.getMiner();
        ItemHandlerHelper.giveItemToPlayer(miner, drop.getItem());
    }

    @Override
    public void onDig(ItemStack item, Level level, BlockState blockState, BlockPos blockPos, LivingEntity player, Integer integer) {
    }
}
