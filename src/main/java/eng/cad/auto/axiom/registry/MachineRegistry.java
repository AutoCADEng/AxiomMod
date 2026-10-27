package eng.cad.auto.axiom.registry;

import eng.cad.auto.axiom.AxiomMod;
import eng.cad.auto.axiom.common.machine.Alembic;
import eng.cad.auto.axiom.common.machine.ChemicalReactor;
import eng.cad.auto.axiom.common.machine.FermenterMachine;
import eng.cad.auto.axiom.common.machine.SoakingDrum;
import eng.cad.auto.axiom.common.machine.core.MachineHolder;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;

public class MachineRegistry
{
    public static final DeferredRegister<BlockEntityType<?>> MACHINES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, AxiomMod.MODID);

    public static final HashMap<String, MachineHolder> MACHINELIST = new HashMap<>();

    public static MachineHolder registerMachine(MachineHolder machine){
        MACHINELIST.put(machine.config.name(), machine);
        return machine;
    }

    public static void init(){
        new FermenterMachine();
        new SoakingDrum();
        new Alembic();
        new ChemicalReactor();
    }

}
