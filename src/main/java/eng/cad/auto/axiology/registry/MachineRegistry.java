package eng.cad.auto.axiology.registry;

import eng.cad.auto.axiology.AxiomMod;
import eng.cad.auto.axiology.core.machine.MachineRegistryObject;
import eng.cad.auto.axiology.machines.Alembic;
import eng.cad.auto.axiology.machines.ChemicalReactor;
import eng.cad.auto.axiology.machines.Fermenter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;

public class MachineRegistry
{
    public static final DeferredRegister<BlockEntityType<?>> MACHINES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, AxiomMod.MODID);
    public static final HashMap<String, MachineRegistryObject> MACHINELIST = new HashMap<>();

    public static final MachineRegistryObject ALEMBIC = registerMachine(new MachineRegistryObject(Alembic.name, Alembic::new, Alembic.MachineBlock::new));
    public static final MachineRegistryObject CHEMICAL_REACTOR = registerMachine(new MachineRegistryObject(ChemicalReactor.name, ChemicalReactor::new, ChemicalReactor.MachineBlock::new));
    public static final MachineRegistryObject FERMENTER = registerMachine(new MachineRegistryObject(Fermenter.name, Fermenter::new, Fermenter.MachineBlock::new));

    public static MachineRegistryObject registerMachine(MachineRegistryObject machine){
        MACHINELIST.put(machine.name, machine);
        return machine;
    }
}
