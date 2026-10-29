package eng.cad.auto.axolotyl.machine.multiblock;

import com.google.gson.*;
import eng.cad.auto.axiom.AxiomMod;
import eng.cad.auto.axiom.registry.MultiblockRegistry;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

//TODO make a JSON listener for axioms! Future me here, this was a bad idea, we can't register items if the game doesn't know about it yet
public class MultiblockStructure {
    public final HashMap<Character, ArrayList<Block>> blockMap;
    public final ArrayList<String[]> shapeList;
    public Vec3i rootPos;

    public MultiblockStructure(HashMap<Character, ArrayList<Block>> blockMap, ArrayList<String[]> shapeList, Vec3i rootPos){
        this.blockMap = blockMap;
        this.shapeList = shapeList;
        this.rootPos = rootPos;
    }

    public static MultiblockStructure fromJson(JsonObject jsonObject){
        HashMap<Character, ArrayList<Block>> newBlockMap = new HashMap<>();
        JsonObject keys = jsonObject.getAsJsonObject("key");
        for (Map.Entry<String, JsonElement> resourceKey : keys.entrySet()) {
            char key = resourceKey.getKey().charAt(0);
            JsonArray values = resourceKey.getValue().getAsJsonArray();
            ArrayList<Block> validBlocks = new ArrayList<>();

            for (JsonElement element: values.asList()){
                String resourceKeyLookup = element.getAsString();
                ResourceLocation resource = ResourceLocation.parse(resourceKeyLookup);
                if (ForgeRegistries.BLOCKS.containsKey(resource)) {
                    Block block = ForgeRegistries.BLOCKS.getValue(resource);
                    validBlocks.add(block);
                } else {
                    AxiomMod.LOGGER.error("Failed to parse JSON, Block {} is not registered!", resourceKeyLookup);
                }
            }
            newBlockMap.put(key, validBlocks);
        }
        JsonArray rootArray = jsonObject.get("root").getAsJsonArray();
        JsonArray multiblockShape = jsonObject.get("shape").getAsJsonArray();
        ArrayList<String[]> shape = new ArrayList<>();
        for (JsonElement element: multiblockShape.asList()){
            JsonArray layers = element.getAsJsonArray();

            //"You disgust me" - Spy TF2
            shape.add(layers.asList().stream().map(JsonElement::getAsString).collect(Collectors.toCollection(ArrayList::new)).toArray(String[]::new));
        }
        return new MultiblockStructure(newBlockMap, shape, new Vec3i(rootArray.get(0).getAsInt(),rootArray.get(1).getAsInt(),rootArray.get(2).getAsInt()));
    }

    public static class JsonListener extends SimpleJsonResourceReloadListener{
        private static final Gson GSON = new GsonBuilder().create();
        private static final JsonListener INSTANCE = new JsonListener();

        private JsonListener() {
            super(GSON, "multiblock");
        }

        public static JsonListener getInstance(){
            return INSTANCE;
        }

        @Override
        protected void apply (Map <ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profilerFiller){
            for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
                ResourceLocation location = entry.getKey();
                JsonElement json = entry.getValue();
                if (json.isJsonObject()) {
                    MultiblockRegistry.Structures.put(location, ()-> MultiblockStructure.fromJson(json.getAsJsonObject()));
                } else{
                    AxiomMod.LOGGER.error("Failed to parse JSON for {}, JsonElement is not a JsonObject!", location.toString());
                }
            }
        }
    }
}
