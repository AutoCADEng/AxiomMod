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

public class AxiomUtil {


//     public static Axiom addHybridAxiom(String name, Axiom reactant, Axiom reactant_2){
//         int color1 = reactant.color;
//         int color2 = reactant_2.color;
//         int alpha = hybridizeColor(color1, color2, 0xFF000000,24);
//         int red = hybridizeColor(color1, color2, 0x00FF0000,16);
//         int green = hybridizeColor(color1, color2, 0x0000FF00,8);
//         int blue = hybridizeColor(color1, color2, 0x000000FF, 0);
//         return addAxiom(name, alpha|red|green|blue, AxiomType.COMPOUND);
//     }

     public static int hybridizeColor(int value_a, int value_b, int bitMask, int bitShift){
         int channel_a = (value_a&bitMask)>>>bitShift;
         int channel_b = (value_b&bitMask)>>>bitShift;
         int average = (channel_a+channel_b)/2;
         return average<<bitShift;
     }

}
