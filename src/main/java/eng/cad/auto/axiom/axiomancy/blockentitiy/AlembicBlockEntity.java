package eng.cad.auto.axiom.axiomancy.blockentitiy;

import eng.cad.auto.axiom.recipes.AlembicRecipe;
import eng.cad.auto.axiom.registry.BlockEntityRegistry;
import eng.cad.auto.axiom.registry.recipes.AlembicRecipeRegistry;
import eng.cad.auto.axiom.tags.CustomTags;
import eng.cad.auto.axiom.ui.AlembicMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNullableByDefault;
import java.util.Optional;

public class AlembicBlockEntity extends BlockEntity implements Container, MenuProvider {

    public final ItemStackHandler blockInventory = new ItemStackHandler(10)
    {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    public static final int INPUT_SLOT = 0;
    public static final int MAX_PROGRESS = 200;
    private LazyOptional<IItemHandler> itemHandlerCapability = LazyOptional.empty();

    public SimpleContainerData data = new SimpleContainerData(2);
    public AlembicBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityRegistry.ALEMBIC.get(), blockPos, blockState);
    }

    @Override
    public int getContainerSize() {
        return blockInventory.getSlots();
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < blockInventory.getSlots(); i++) {
            if (!blockInventory.getStackInSlot(i).isEmpty()){
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return blockInventory.getStackInSlot(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return blockInventory.extractItem(slot, amount, false);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return blockInventory.extractItem(slot, blockInventory.getStackInSlot(slot).getCount(), false);
    }

    @Override
    public void setItem(int slot, ItemStack itemStack) {
        blockInventory.setStackInSlot(slot, itemStack);
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < blockInventory.getSlots(); i++) {
            blockInventory.setStackInSlot(i, ItemStack.EMPTY);
        }
        setChanged();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("axiology.machines.alembic");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int menuID, Inventory inventory, Player player) {
        return new AlembicMenu(
        menuID, inventory, this, this.data
        );
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) return;
        data.set(1,level.getBlockState(pos.below()).is(CustomTags.Blocks.ELDERITCH_BLOCK)?1:0);
        if (data.get(1) == 0){
            data.set(0,0);
            return;
        }
        ItemStack input = getItem(0);
        if (input.isEmpty()) {
            data.set(0, 0);
            return;
        }
        Optional<AlembicRecipe> recipeObject =
                level.getRecipeManager().getRecipeFor(
                        AlembicRecipe.Type.INSTANCE,
                        this,
                        level
                );
        if (recipeObject.isEmpty()) {
            data.set(0, 0);
            return;
        }
        AlembicRecipe recipe = recipeObject.get();
        if (!recipe.matches(this, level)) {
            data.set(0, 0);
            return;
        }

        if (data.get(0) >= MAX_PROGRESS) {
            NonNullList<ItemStack> outputs = recipe.getOutput().outputItems;
            blockInventory.extractItem(0, 1, false);
                        for (ItemStack output : outputs) {
                if (output.isEmpty()) continue;
                ItemStack remaining = output.copy();
                for (int slot = 1; slot <= 9 && !remaining.isEmpty(); slot++) {
                    remaining = blockInventory.insertItem(slot, remaining, false);
                }
            }
            data.set(0, 0);
            return;
        }

        data.set(0, Math.min(data.get(0) + 2, MAX_PROGRESS));
    }

    @Override @ParametersAreNullableByDefault
    public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction side) {
        if (capability == ForgeCapabilities.ITEM_HANDLER) {
            return itemHandlerCapability.cast();
        }
        return super.getCapability(capability, side);
    }


    @Override
    protected void saveAdditional(CompoundTag tags) {
        tags.put("alembic.inventory", blockInventory.serializeNBT());
        tags.putInt("alembic.progress", data.get(0));
        super.saveAdditional(tags);
    }

    /**
     * @param tags
     */
    @Override
    public void load(CompoundTag tags) {
        super.load(tags);
        blockInventory.deserializeNBT(tags.getCompound("alembic.inventory"));
        data.set(0, tags.getInt("alembic.progress"));
    }
    @Override
    public void onLoad() {
        super.onLoad();
        itemHandlerCapability = LazyOptional.of(() -> blockInventory);
    }
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemHandlerCapability.invalidate();
    }
}
