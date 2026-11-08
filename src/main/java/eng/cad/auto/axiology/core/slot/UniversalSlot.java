package eng.cad.auto.axiology.core.slot;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eng.cad.auto.axiology.AxiomMod;
import eng.cad.auto.axiology.modulartools.components.ModularToolAugment;
import eng.cad.auto.axiology.registry.AugmentsRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;


/**
 * This class is intended to reduce friction in the code by handling FluidStacks and ItemStacks as a single object.
 * It takes an ItemStack or FluidStack as input from the constructor and locks the other stack type to EMPTY.
 * It is intended that an ItemStack and FluidStack never are defined in the same UniversalSlot.
 * If a way to do so is found, it should be patched out
 */
// Warning: Do not convert into a record, or it will become possible to have both an Item and a Fluid active at the same time.
public class UniversalSlot {
    public static final UniversalSlot EMPTY = new UniversalSlot();
    private final FluidStack fluid;

    private final ItemStack item;
    private final ModularToolAugment modularToolAbility;
    Kind type;
    private SmartTag tag = new SmartTag(null, 0);
    private UniversalSlot() {
        type = Kind.EMPTY;
        fluid = FluidStack.EMPTY;
        item = ItemStack.EMPTY;
        modularToolAbility = null;
    }
    public UniversalSlot(FluidStack fluid) {
        this.item = ItemStack.EMPTY;
        this.fluid = fluid;
        type = Kind.FLUID;
        modularToolAbility = null;
    }

    public UniversalSlot(ItemStack item) {
        this.item = item;
        this.fluid = FluidStack.EMPTY;
        this.type = Kind.ITEM;
        modularToolAbility = null;
    }

    public UniversalSlot(SmartTag tag, Kind kind) {
        this.item = ItemStack.EMPTY;
        this.fluid = FluidStack.EMPTY;
        this.type = kind;
        this.tag = tag;
        modularToolAbility = null;
    }

    public UniversalSlot(ModularToolAugment module) {
        this.item = ItemStack.EMPTY;
        this.fluid = FluidStack.EMPTY;
        this.type = Kind.MODULE;
        this.modularToolAbility = module;
    }

    /**
     * The fromJson method is used to parse individual ingredients from axiom GenericRecipes.
     *
     * @param ingredientJSON the JsonObject being parsed from.
     * @return A crafting ingredient of FluidStack or ItemStack
     */
    public static UniversalSlot fromJson(JsonObject ingredientJSON) {
        String type = ingredientJSON.get("type").getAsString();
        String name = ingredientJSON.get("input").getAsString();
        int count = ingredientJSON.get("count").getAsInt();

        UniversalSlot ingredient;
        switch (type) {
            case ("fluid") -> {
                if (name.startsWith("#")) {
                    TagKey<Fluid> fluidTag = TagKey.create(ForgeRegistries.FLUIDS.getRegistryKey(), ResourceLocation.tryParse(name.substring(1)));
                    ingredient = new UniversalSlot(new SmartTag(fluidTag, count), Kind.FLUID_TAG);
                } else {
                    FluidStack fluid = new FluidStack(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(ResourceLocation.parse(name))), count);
                    ingredient = new UniversalSlot(fluid);
                }
            }
            case ("item") -> {
                if (name.startsWith("#")) {
                    TagKey<Item> itemTag = TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), ResourceLocation.tryParse(name.substring(1)));
                    ingredient = new UniversalSlot(new SmartTag(itemTag, count), Kind.ITEM_TAG);
                } else {
                    ItemStack item = new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(name))), count);
                    ingredient = new UniversalSlot(item);
                }
            }
            case ("module") -> {
                ResourceLocation id = name.contains(":") ? ResourceLocation.parse(name) : ResourceLocation.fromNamespaceAndPath(AxiomMod.MODID, name);
                ModularToolAugment ability = AugmentsRegistry.MODULES.getEntries().stream()
                        .filter(entry -> entry.getId().equals(id))
                        .map(RegistryObject::get)
                        .findFirst()
                        .orElse(null);
                ingredient = new UniversalSlot(ability);
            }
            default -> {
                AxiomMod.LOGGER.error("JSON ERROR: {} is not a valid UniversalSlot type (item, fluid, module)", type);
                return UniversalSlot.EMPTY;
            }
        }
        return ingredient;
    }

    /**
     * The arrayFromJson method is intended to parse entire lists of ingredients from an axiom GenericRecipe,
     * using the fromJson method to parse the individual items.
     *
     * @param ingredientJSON the JsonArray being parsed from.
     * @return a NonNullList of crafting ingredients
     */
    public static NonNullList<UniversalSlot> arrayFromJson(JsonArray ingredientJSON) {
        JsonArray JsonData = ingredientJSON.getAsJsonArray();
        NonNullList<UniversalSlot> output = NonNullList.create();
        for (JsonElement ingredientJson : JsonData) {
            JsonObject objectJson = ingredientJson.getAsJsonObject();
            UniversalSlot ingredient = fromJson(objectJson);
            output.add(ingredient);
        }
        return output;
    }

    /**
     * The checkIngredient method compares two UniversalSlots by count and identity.
     *
     * @param target The ingredient from the machine inventory
     * @param recipe The ingredient from the recipe
     * @return True if target and recipe are the same item/fluid and target's amount is >= recipe's amount.
     */
    public static boolean checkIngredient(UniversalSlot target, UniversalSlot recipe) {
        if (!isSameKind(target, recipe)) return false;
        switch (recipe.type) {
            case ITEM -> {
                return target.getItem().getCount() >= recipe.getItem().getCount();
            }
            case FLUID -> {
                return target.getFluid().getAmount() >= recipe.getFluid().getAmount();
            }
            case ITEM_TAG -> {
                if (!target.is(Kind.ITEM)) return false;
                return target.getItem().getCount() >= recipe.tag.count;
            }
            case FLUID_TAG -> {
                if (!target.is(Kind.FLUID)) return false;
                return target.getFluid().getAmount() >= recipe.tag.count;
            }
            default -> {
                return false;
            }
        }
    }

    /**
     * The isSameKind method compares two UniversalSlots by identity.
     *
     * @param target The ingredient from the machine inventory
     * @param recipe The ingredient from the recipe
     * @return True if target and recipe are compatible types
     */
    public static boolean isSameKind(UniversalSlot target, UniversalSlot recipe) {
        if (recipe.isEmpty() || target.isEmpty()) return false;
        switch (recipe.type) {
            case ITEM, ITEM_TAG -> {
                return target.is(Kind.ITEM);
            }
            case FLUID, FLUID_TAG -> {
                return target.is(Kind.FLUID);
            }
            default -> {
                return false;
            }
        }
    }

    /**
     * Reads a FluidStack or ItemStack to the network and converts it into a UniversalSlot
     *
     * @param byteBuffer The network byte buffer being read from
     * @return A UniversalSlot of ItemStack or FluidStack from the network
     */
    public static UniversalSlot fromNetwork(FriendlyByteBuf byteBuffer) {
        Kind kind = byteBuffer.readEnum(Kind.class);
        switch (kind) {
            case ITEM -> {
                return new UniversalSlot(byteBuffer.readItem());
            }
            case FLUID -> {
                return new UniversalSlot(byteBuffer.readFluidStack());
            }
            case ITEM_TAG -> {
                ResourceLocation loc = byteBuffer.readResourceLocation();
                int count = byteBuffer.readInt();
                TagKey<Item> itemTag = TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), loc);
                return new UniversalSlot(new SmartTag(itemTag, count), Kind.ITEM_TAG);
            }
            case FLUID_TAG -> {
                ResourceLocation loc = byteBuffer.readResourceLocation();
                int count = byteBuffer.readInt();
                TagKey<Fluid> fluidTag = TagKey.create(ForgeRegistries.FLUIDS.getRegistryKey(), loc);
                return new UniversalSlot(new SmartTag(fluidTag, count), Kind.FLUID_TAG);
            }
            default -> {
                return UniversalSlot.EMPTY;
            }
        }
    }

    public boolean is(Kind kind) {
        return type.equals(kind);
    }

    public boolean isEmpty() {
        return is(Kind.EMPTY);
    }

    public SmartTag getTag() {
        return tag;
    }

    @SuppressWarnings("unchecked")
    public TagKey<Item> getItemTag() {
        return (TagKey<Item>) tag.tag();
    }

    @SuppressWarnings("unchecked")
    public TagKey<Fluid> getFluidTag() {
        return (TagKey<Fluid>) tag.tag();
    }

    public Kind getType() {
        return type;
    }

    public ItemStack getItem() {
        return item == null ? ItemStack.EMPTY : item;
    }

    public FluidStack getFluid() {
        return fluid == null ? FluidStack.EMPTY : fluid;
    }

    public ModularToolAugment getModule() {
        return modularToolAbility == null ? ModularToolAugment.EMPTY : modularToolAbility;
    }

    /**
     * Writes a FluidStack or ItemStack to the network
     *
     * @param byteBuffer The network byte buffer being written to
     */
    public void toNetwork(FriendlyByteBuf byteBuffer) {
        if (isEmpty()) return;
        byteBuffer.writeEnum(type);
        switch (type) {
            case ITEM -> {
                byteBuffer.writeItem(item);
            }
            case FLUID -> {
                byteBuffer.writeFluidStack(fluid);
            }
            case ITEM_TAG, FLUID_TAG -> {
                byteBuffer.writeResourceLocation(tag.tag.location());
                byteBuffer.writeInt(tag.count);
            }
        }
    }

    public enum Kind {EMPTY, ITEM, FLUID, FLUID_TAG, ITEM_TAG, MODULE}

    public record SmartTag(TagKey<?> tag, int count) {
    }
}
