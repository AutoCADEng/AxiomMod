package eng.cad.auto.axiom.registry;

import eng.cad.auto.axiom.AxiomMod;
import eng.cad.auto.axiom.common.machine.core.GenericBlockEntity;
import eng.cad.auto.axiom.common.machine.core.GenericMachine;
import eng.cad.auto.axiom.common.machine.FermenterLogic;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;

public class MachineRegistry
{
    public static final DeferredRegister<BlockEntityType<?>> MACHINES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, AxiomMod.MODID);

    public static final HashMap<String, GenericMachine> MACHINELIST = new HashMap<>();

    public static GenericMachine registerMachine(GenericMachine machine){
        MACHINELIST.put(machine.config.name(), machine);
        return machine;
    }
    public static void init(){
        registerMachine(new GenericMachine
                .Builder("alembic")
                .addItemSlots(10)
                .addDataSlots(2)
                .addServerLogic(new FermenterLogic())
                .addClientLogic(new FermenterLogic.Client())
                .build());
    }

}
