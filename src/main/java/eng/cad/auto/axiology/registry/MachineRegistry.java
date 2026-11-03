package eng.cad.auto.axiology.registry;

import eng.cad.auto.axiology.AxiomMod;
import eng.cad.auto.axiology.machines.*;
import eng.cad.auto.axiology.machines.multiblock.*;
import eng.cad.auto.axiology.machines.multiblock.followers.*;
import eng.cad.auto.engine.machine.core.MachineCore;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;

public class MachineRegistry
{
    public static final DeferredRegister<BlockEntityType<?>> MACHINES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, AxiomMod.MODID);

    public static final HashMap<String, MachineCore> MACHINELIST = new HashMap<>();

    public static MachineCore registerMachine(MachineCore machine){
        MACHINELIST.put(machine.config.name(), machine);
        return machine;
    }

    public static void init(){
        new Fermenter();
        new SoakingDrum();
        new Alembic();
        new ChemicalReactor();
        new EtchedFluidInput();
        new EtchedFluidOutput();
        new EtchedItemInput();
        new EtchedItemOutput();
        new Pyrolyzer();
        new ArcanePedestal();
        new ArcaneImbuer();
    }
}
