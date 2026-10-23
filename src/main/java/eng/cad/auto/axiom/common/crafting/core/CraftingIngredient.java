package eng.cad.auto.axiom.common.crafting.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eng.cad.auto.axiom.AxiomMod;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

// Do not convert into a record, or it will become possible to have both an Item and a Fluid active at the same time.
public class CraftingIngredient{
    private final FluidStack fluid;
    private final ItemStack item;
    public static final CraftingIngredient EMPTY = new CraftingIngredient(ItemStack.EMPTY);

    public CraftingIngredient(FluidStack fluid) {
        this.item = ItemStack.EMPTY;
        this.fluid = fluid;
    }

    public CraftingIngredient(ItemStack item) {
        this.item = item;
        this.fluid = FluidStack.EMPTY;
    }

    public static CraftingIngredient fromJson(JsonObject ingredientJSON) {
        String type = ingredientJSON.get("type").getAsString();
        String name = ingredientJSON.get("input").getAsString();
        int count = ingredientJSON.get("count").getAsInt();

        CraftingIngredient ingredient;
        if (Objects.equals(type, "fluid")) {
            FluidStack fluid = new FluidStack(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(ResourceLocation.parse(name))), count);
            ingredient = new CraftingIngredient(fluid);
        }else if(Objects.equals(type, "item")){
            ItemStack item = new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(name))), count);
            ingredient = new CraftingIngredient(item);
        }else{
            AxiomMod.LOGGER.error("JSON ERROR: {} is not a valid CraftingIngredient type (item, fluid)", type);
            return CraftingIngredient.EMPTY;
        }
        return ingredient;
    }

    public static NonNullList<CraftingIngredient> arrayFromJson(JsonArray ingredientJSON) {
        JsonArray JsonData = ingredientJSON.getAsJsonArray();
        NonNullList<CraftingIngredient> output = NonNullList.create();
        for(JsonElement ingredientJson:JsonData){
            JsonObject objectJson = ingredientJson.getAsJsonObject();
            CraftingIngredient ingredient = fromJson(objectJson);
            output.add(ingredient);
        }
        return output;
    }

    public static boolean CheckIngredient(CraftingIngredient target, CraftingIngredient recipe) {
        if (!IsSameKind(target, recipe)) return false;
        if (target.isItem()){
            if (!target.getItem().is(recipe.getItem().getItem())) return false;
            return target.getItem().getCount() >= recipe.getItem().getCount();
        }else{
            if (!target.getFluid().getFluid().isSame(recipe.getFluid().getFluid())) return false;
            return target.getFluid().getAmount() >= recipe.getFluid().getAmount();
        }
    }

    public static boolean IsSameKind(CraftingIngredient craftingIngredient, CraftingIngredient ingredientInSlot){
        //if (ingredientInSlot.isEmpty() && craftingIngredient.isEmpty()) return true; omitting this saves some lines later, even if it is technically true.
        if (ingredientInSlot.isEmpty()) return false;
        if (craftingIngredient.isEmpty()) return false;
        return (!craftingIngredient.isFluid() || !ingredientInSlot.isItem()) && (!craftingIngredient.isItem() || !ingredientInSlot.isFluid());
    }

    public boolean isFluid() {
        return !fluid.isEmpty();
    }

    public boolean isItem() {
        return !item.isEmpty();
    }

    public boolean isEmpty() {
        return item.isEmpty() && fluid.isEmpty();
    }

    public ItemStack getItem() {
        return item;
    }

    public FluidStack getFluid() {
        return fluid;
    }

    public void toNetwork(FriendlyByteBuf byteBuffer) {
        if (isEmpty()) return;

        byteBuffer.writeBoolean(isFluid());
        if (isFluid()){
            byteBuffer.writeFluidStack(fluid);
        }else{
            byteBuffer.writeItem(item);
        }

    }

    public static CraftingIngredient fromNetwork(FriendlyByteBuf byteBuffer) {
        boolean isFluid = byteBuffer.readBoolean();
        if (isFluid) {
            return new CraftingIngredient(byteBuffer.readFluidStack());
        }else{
            return new CraftingIngredient(byteBuffer.readItem());
        }
    }
}
