package eng.cad.auto.axiom.registry;

import eng.cad.auto.axiom.AxiomMod;
import eng.cad.auto.axiom.machines.Alembic;
import eng.cad.auto.axiom.machines.ChemicalReactor;
import eng.cad.auto.axiom.machines.FermenterMachine;
import eng.cad.auto.axiom.machines.SoakingDrum;
import eng.cad.auto.axiom.machines.multiblock.EtchedFluidInput;
import eng.cad.auto.axiom.machines.multiblock.EtchedFluidOutput;
import eng.cad.auto.axiom.machines.multiblock.TreatmentPlant;
import eng.cad.auto.axolotyl.machine.core.MachineCore;
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
        new FermenterMachine();
        new SoakingDrum();
        new Alembic();
        new ChemicalReactor();
        new EtchedFluidInput();
        new EtchedFluidOutput();
        new TreatmentPlant();
    }

}
