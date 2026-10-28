package eng.cad.auto.axiom.registry;

import eng.cad.auto.axiom.AxiomMod;
import eng.cad.auto.axiom.fluids.Blazewater;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class CreativeTabRegistry {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AxiomMod.MODID);

    public static final RegistryObject<CreativeModeTab> AXIOLOGY_TAB =
            registerTab("axiology", Component.translatable("axiology.tabs.axiology"),
                    ()-> ItemRegistry.FLUXED_INGOT.get().getDefaultInstance());

    public static final RegistryObject<CreativeModeTab> AXIOLOGY_BUCKETS =
            registerTab("axiology_bucket", Component.translatable("axiology.tabs.axiology_bucket"),
                    ()-> FluidRegistry.Fluid_Holders.get(Blazewater.name).BUCKET.get().getDefaultInstance());

//    public static final RegistryObject<CreativeModeTab> AXIOLOGY_SHARD =
//            registerTab("axiology_shard", Component.translatable("axiology.tabs.axiology_shard"),
//                    ()-> AxiomRegistry.AXIOM_CRYSTALS.get(0).get().getDefaultInstance());

    public static RegistryObject<CreativeModeTab> registerTab(String name, Component ingame_name, Supplier<ItemStack> icon){
        return CREATIVE_TABS.register(name, () -> CreativeModeTab.builder()
            .title(ingame_name)
            .icon(icon).build());
    }
}
