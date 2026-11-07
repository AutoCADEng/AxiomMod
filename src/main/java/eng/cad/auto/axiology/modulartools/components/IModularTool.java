package eng.cad.auto.axiology.modulartools.components;

import java.util.HashMap;

public interface IModularTool {
    HashMap<ModularToolAbility, Integer> getModules();
    default int getMaxAugments(){
        return 9;
    }
}
