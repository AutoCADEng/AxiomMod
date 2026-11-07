package eng.cad.auto.axiology.modulartools.abilities.custom;

import eng.cad.auto.axiology.core.events.BlockDropEvent;
import eng.cad.auto.axiology.modulartools.components.IModularDigAbility;
import eng.cad.auto.axiology.modulartools.components.ModularToolAugment;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class SmeltItemsAugment extends ModularToolAugment implements IModularDigAbility {
    @Override
    public Component getDisplayName() {
        return Component.translatable("axiology.modulartools.smelting");
    }

    @Override
    public void digDropEvent(BlockDropEvent event) {
        if (!(event.getEntity() instanceof ItemEntity drop)) return;
        Player miner = event.getMiner();
        ItemStack smeltedItem = smelt(event.getLevel(), drop.getItem());
        drop.setItem(smeltedItem);
    }

    private static ItemStack smelt(Level level, ItemStack stack) {
        Optional<SmeltingRecipe> recipe = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(stack), level);
        if (recipe.isEmpty()) return stack;
        ItemStack result = recipe.get().getResultItem(level.registryAccess()).copy();
        result.setCount(result.getCount() * stack.getCount());
        return result;
    }

    @Override
    public void onDig(ItemStack item, Level level, BlockState blockState, BlockPos blockPos, LivingEntity player, Integer integer) {}
}
