package eng.cad.auto.axiom;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.HashMap;
import java.util.Map;

/**
 * I want to rework this
 */
public class Axiom {
//    String name;
//    int color;
//    AxiomType type;
//
//    private Axiom(String name, int color, AxiomType type) {
//        this.name = name;
//        this.color = color;
//        this.type = type;
//    }
//
//    public static Axiom fromJson(ResourceLocation resourceLocation, JsonObject jsonObject){
//        String name = jsonObject.get("name").getAsString();
//        int color = Integer.parseInt(jsonObject.get("color").getAsString(),16);
//        AxiomType type = AxiomType.valueOf(jsonObject.get("type").getAsString());
//        return new Axiom(name, color, type);
//    }
//
//    public static class JsonListener extends SimpleJsonResourceReloadListener {
//        private static final Axiom.JsonListener INSTANCE = new Axiom.JsonListener();
//        private static final Gson GSON = new GsonBuilder().create();
//        public static Map<ResourceLocation, JsonObject> Axioms = new HashMap<>();
//
//        private JsonListener() {
//            super(GSON, "axiom");
//        }
//
//        public Axiom.JsonListener getInstance(){
//            return INSTANCE;
//        }
//
//        @Override
//        protected void apply (Map <ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profilerFiller){
//            Axioms.clear();
//            for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
//                ResourceLocation location = entry.getKey();
//                JsonElement json = entry.getValue();
//                if (json.isJsonObject()) {
//                    Axioms.put(location, json.getAsJsonObject());
//                } else{
//                    AxiomMod.LOGGER.error("Failed to parse JSON for {}, JsonElement is not a JsonObject!", location.toString());
//                }
//            }
//        }
//    }
}