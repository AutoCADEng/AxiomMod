package eng.cad.auto.axiology.modulartools.components;

import net.minecraft.network.chat.Component;

public abstract class ModularToolAbility {
    abstract public Component getDisplayName();
    public int maxStackable(){
        return 3;
    }
    public boolean isCompatible(ModularToolAbility ability){
        return true;
    }
}
