package eng.cad.auto.axiology.registry;

import eng.cad.auto.axiology.AxiomMod;
import eng.cad.auto.axiology.core.MachineUI;
import eng.cad.auto.axiology.machines.ChemicalReactor;
import eng.cad.auto.axiology.machines.Fermenter;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class MenuRegistry {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, AxiomMod.MODID);

    public static final MachineUI CHEMICAL_REACTOR = new MachineUI(ChemicalReactor.name, MachineRegistry.CHEMICAL_REACTOR.blockEntity);
    public static final MachineUI FERMENTER = new MachineUI(Fermenter.name, MachineRegistry.FERMENTER.blockEntity);

    public static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenu(String name, IContainerFactory<T> menuFactory) {
        return MENUS.register(name, () -> IForgeMenuType.create(menuFactory));
    }
}
