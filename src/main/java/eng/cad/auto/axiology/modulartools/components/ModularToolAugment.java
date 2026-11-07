package eng.cad.auto.axiology.modulartools.components;

import net.minecraft.network.chat.Component;

public abstract class ModularToolAugment {
    public static final ModularToolAugment EMPTY = new ModularToolAugment() {
        @Override
        public Component getDisplayName() {
            return Component.empty();
        }
    };

    abstract public Component getDisplayName();
    public int maxStackable(){
        return 3;
    }
    public boolean isCompatible(ModularToolAugment ability){
        return true;
    }
}
