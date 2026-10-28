package eng.cad.auto.axolotyl.crafting.core;

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



/**
 * This class is intended to reduce friction in the code by handling FluidStacks and ItemStacks as a single object.
 * It takes an ItemStack or FluidStack as input from the constructor and locks the other stack type to EMPTY.
 * It is intended that an ItemStack and FluidStack never are defined in the same CraftingIngredient.
 * If a way to do so is found, it should be patched out
 */
// Warning: Do not convert into a record, or it will become possible to have both an Item and a Fluid active at the same time.
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

    /**
     * The fromJson method is used to parse individual ingredients from axiom GenericRecipes.
     * @param ingredientJSON the JsonObject being parsed from.
     * @return A crafting ingredient of FluidStack or ItemStack
     */
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

    /**
     * The arrayFromJson method is intended to parse entire lists of ingredients from an axiom GenericRecipe,
     * using the fromJson method to parse the individual items.
     * @param ingredientJSON the JsonArray being parsed from.
     * @return a NonNullList of crafting ingredients
     */
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

    /**
     * The checkIngredient method compares two CraftingIngredients by count and identity.
     * @param target The ingredient from the machine inventory
     * @param recipe The ingredient from the recipe
     * @return True if target and recipe are the same item/fluid and target's amount is >= recipe's amount.
     */
    public static boolean checkIngredient(CraftingIngredient target, CraftingIngredient recipe) {
        if (!isSameKind(target, recipe)) return false;
        if (target.isItem()){
            if (!target.getItem().is(recipe.getItem().getItem())) return false;
            return target.getItem().getCount() >= recipe.getItem().getCount();
        }else{
            if (!target.getFluid().getFluid().isSame(recipe.getFluid().getFluid())) return false;
            return target.getFluid().getAmount() >= recipe.getFluid().getAmount();
        }
    }

    /**
     * The isSameKind method compares two CraftingIngredients by identity.
     * @param target The ingredient from the machine inventory
     * @param recipe The ingredient from the recipe
     * @return True if target and recipe are the same item/fluid
     */
    public static boolean isSameKind(CraftingIngredient target, CraftingIngredient recipe){
        //if (recipe.isEmpty() && target.isEmpty()) return true; omitting this saves some lines later, even if it is technically true.
        if (recipe.isEmpty()) return false;
        if (target.isEmpty()) return false;
        return (!target.isFluid() || !recipe.isItem()) && (!target.isItem() || !recipe.isFluid());
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

    /**
     * Writes a FluidStack or ItemStack to the network
     * @param byteBuffer The network byte buffer being written to
     */
    public void toNetwork(FriendlyByteBuf byteBuffer) {
        if (isEmpty()) return;

        byteBuffer.writeBoolean(isFluid());
        if (isFluid()){
            byteBuffer.writeFluidStack(fluid);
        }else{
            byteBuffer.writeItem(item);
        }

    }

    /**
     * Reads a FluidStack or ItemStack to the network and converts it into a CraftingIngredient
     * @param byteBuffer The network byte buffer being read from
     * @return A CraftingIngredient of ItemStack or FluidStack from the network
     */
    public static CraftingIngredient fromNetwork(FriendlyByteBuf byteBuffer) {
        boolean isFluid = byteBuffer.readBoolean();
        if (isFluid) {
            return new CraftingIngredient(byteBuffer.readFluidStack());
        }else{
            return new CraftingIngredient(byteBuffer.readItem());
        }
    }
}
