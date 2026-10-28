package eng.cad.auto.axiom.items;

import net.minecraft.world.item.Item;

public class AxiomShard extends Item {
    int color;
    public AxiomShard(Properties p_41383_, int color) {
        super(p_41383_);
        this.color = color;
    }
    public int getColor(){
        return color;
    }
}

