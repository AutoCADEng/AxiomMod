package eng.cad.auto.axiology.mixins;

import eng.cad.auto.axiology.core.BlockDropContext;
import eng.cad.auto.axiology.core.events.BlockDropEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

@Mixin(Block.class)
public class BlockMixin {
    @Redirect(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;" +
            "Lnet/minecraft/world/level/Level;" +
            "Lnet/minecraft/core/BlockPos;" +
            "Lnet/minecraft/world/level/block/entity/BlockEntity;" +
            "Lnet/minecraft/world/entity/Entity;" +
            "Lnet/minecraft/world/item/ItemStack;Z)V",
            at = @At(value = "INVOKE",
                    target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V"))
    private static void axiology$dropResources(List<ItemStack> drops, Consumer<ItemStack> consumer, BlockState state, Level level, BlockPos pos, @Nullable BlockEntity blockEntity, @Nullable Entity entity, ItemStack tool, boolean dropXp) {
        if (entity instanceof Player player) BlockDropContext.push(player, tool);
        try {
            drops.forEach(consumer);
        } finally {
            BlockDropContext.pop();
        }
    }

    @Redirect(method = "popResource(Lnet/minecraft/world/level/Level;" +
            "Ljava/util/function/Supplier;" +
            "Lnet/minecraft/world/item/ItemStack;)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;" +
                            "addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private static boolean axiology$addFreshEntity(Level level, Entity entity) {
        if (entity instanceof ItemEntity itemEntity) {
            BlockDropContext.Context context = BlockDropContext.get();
            if (context != null) {
                BlockDropEvent event = new BlockDropEvent(level, itemEntity.blockPosition(), itemEntity, context.miner());
                MinecraftForge.EVENT_BUS.post(event);
            }
        }
        return level.addFreshEntity(entity);
    }
}
