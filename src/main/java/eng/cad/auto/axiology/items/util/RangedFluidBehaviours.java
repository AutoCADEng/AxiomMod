package eng.cad.auto.axiology.items.util;

import eng.cad.auto.axiology.core.fluid.FluidHolder;
import eng.cad.auto.axiology.core.fluid.IRangedFluidBehaviour;
import eng.cad.auto.axiology.registry.FluidRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;

public class RangedFluidBehaviours {
    public static HashMap<Fluid, IRangedFluidBehaviour> FLUIDS = new HashMap<>();
    public static IRangedFluidBehaviour GENERIC_BEHAVIOUR = new IRangedFluidBehaviour() {
    };

    public static void init() {
        FLUIDS.put(Fluids.LAVA, new IRangedFluidBehaviour() {
            @Override
            public void applyAsProjectile(Level level, Entity entity) {
                if (!level.isClientSide && entity instanceof LivingEntity living) {
                    living.hurt(level.damageSources().magic(), 1.0F);
                    living.setSecondsOnFire(10);
                }
            }
        });
        FLUIDS.put(ForgeMod.MILK.get(), new IRangedFluidBehaviour() {
            @Override
            public void applyAsProjectile(Level level, Entity entity) {
                if (!level.isClientSide && entity instanceof LivingEntity living) {
                    living.setSecondsOnFire(0);
                    living.removeAllEffects();
                }
            }
        });
        for (FluidHolder fluid : FluidRegistry.Fluid_Holders.values()) {
            FLUIDS.put(fluid.SOURCE.get(), fluid.behaviour);
        }

        for (Fluid fluid : ForgeRegistries.FLUIDS.getValues()) {
            if (FLUIDS.containsKey(fluid)) continue;
            FLUIDS.put(fluid, GENERIC_BEHAVIOUR);
        }
    }

}
