package eng.cad.auto.axiom.registry;

import eng.cad.auto.axiom.AxiomMod;

import eng.cad.auto.axiom.common.util.dataholder.FluidHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.function.Supplier;

public class FluidRegistry {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, AxiomMod.MODID);
    public static final DeferredRegister<FluidType> FLUIDTYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, AxiomMod.MODID);
    public static final ArrayList<RegistryObject<? extends Fluid>> FLUIDLIST = new ArrayList<>();

    public static final FluidHelper DEATHWATER = new FluidHelper(
            "deathwater", 0xFF330033, MapColor.COLOR_BLACK, (level, pos, entity) -> {
                if (!level.isClientSide && entity instanceof LivingEntity living) {
                    living.invulnerableTime = 0;
                    living.hurt(level.damageSources().magic(), 2.0F);
                }
            });

    public static final FluidHelper Ethanol = new FluidHelper(
            "ethanol", 0xCCF5DEB3, MapColor.COLOR_BLACK, (level, pos, entity) -> {
        if (!level.isClientSide && entity instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.CONFUSION,200,1));
        }
    });

    private void registerAxiomFluid(String name, int color){
        new FluidHelper(name, color, MapColor.COLOR_BLACK);
    }

    public static <T extends Fluid> RegistryObject<T> registerFluid(String name, Supplier<T> fluid){
        RegistryObject<T> newFluid = FLUIDS.register(name, fluid);
        FLUIDLIST.add(newFluid);
        return newFluid;
    }
    public static <T extends Block> RegistryObject<T> registerFluidBlock(String name, Supplier<T> block){
        RegistryObject<T> blockObject = BlockRegistry.BLOCKS.register(name, block);
        return blockObject;
    }

}
