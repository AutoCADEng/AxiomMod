package eng.cad.auto.axiology.magic.rebounds;

import eng.cad.auto.axiology.magic.rebounds.core.AbstractRebound;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;

public class Rebounds {

    public static final ArrayList<AbstractRebound> REBOUNDS = new ArrayList<>();
    public static Rebounds INSTANCE = new Rebounds();

    private Rebounds(){
        REBOUNDS.add(new CurseOfBlindness());
        REBOUNDS.add(new CurseOfFire());
        REBOUNDS.add(new CurseOfFoodRot());
        REBOUNDS.add(new CurseOfHunger());
        REBOUNDS.add(new CurseOfPain());
        REBOUNDS.add(new CurseOfWithering());
    }

    public static AbstractRebound randomEffect(){
        if (Minecraft.getInstance().level == null) return new CurseOfNothing();
        return REBOUNDS.get(Minecraft.getInstance().level.getRandom().nextInt(0,REBOUNDS.size()));
    }
}
