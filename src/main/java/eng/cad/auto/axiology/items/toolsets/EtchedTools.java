package eng.cad.auto.axiology.items.toolsets;

import eng.cad.auto.axiology.core.item.tools.*;
import eng.cad.auto.axiology.modulartools.components.IModularTool;
import net.minecraft.world.item.Tier;

public class EtchedTools {
    public static String name = "etched_iron";

    public static class Pickaxe extends CustomPickaxe implements IModularTool {
        public Pickaxe(Tier tier, int attackDamageModifier, float attackSpeedModifier, Properties properties) {
            super(tier, attackDamageModifier, attackSpeedModifier, properties);
        }
    }

    public static class Axe extends CustomAxe implements IModularTool {
        public Axe(Tier tier, int attackDamageModifier, float attackSpeedModifier, Properties properties) {
            super(tier, attackDamageModifier, attackSpeedModifier, properties);
        }
    }

    public static class Hoe extends CustomHoe implements IModularTool {
        public Hoe(Tier tier, int attackDamageModifier, float attackSpeedModifier, Properties properties) {
            super(tier, attackDamageModifier, attackSpeedModifier, properties);
        }
    }

    public static class Sword extends CustomSword implements IModularTool {
        public Sword(Tier tier, int attackDamageModifier, float attackSpeedModifier, Properties properties) {
            super(tier, attackDamageModifier, attackSpeedModifier, properties);
        }
    }

    public static class Shovel extends CustomShovel implements IModularTool {
        public Shovel(Tier tier, int attackDamageModifier, float attackSpeedModifier, Properties properties) {
            super(tier, attackDamageModifier, attackSpeedModifier, properties);
        }
    }

    public static class Shears extends CustomShears implements IModularTool {
        public Shears(Properties properties) {
            super(properties);
        }
    }
}
