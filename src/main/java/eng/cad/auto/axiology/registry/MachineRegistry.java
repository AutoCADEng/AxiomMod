package eng.cad.auto.axiology.registry;

import eng.cad.auto.axiology.AxiomMod;
import eng.cad.auto.axiology.core.machine.MachineRegistryObject;
import eng.cad.auto.axiology.machines.Alembic;
import eng.cad.auto.axiology.machines.ChemicalReactor;
import eng.cad.auto.axiology.machines.Fermenter;
import eng.cad.auto.axiology.machines.SoakingDrum;
import eng.cad.auto.axiology.machines.multiblock.Pyrolyzer;
import eng.cad.auto.axiology.machines.multiblock.followers.FluidInputHatch;
import eng.cad.auto.axiology.machines.multiblock.followers.FluidOutputHatch;
import eng.cad.auto.axiology.machines.multiblock.followers.ItemInputHatch;
import eng.cad.auto.axiology.machines.multiblock.followers.ItemOutputHatch;
import eng.cad.auto.axiology.machines.multiblock.infusion.ArcaneAltar;
import eng.cad.auto.axiology.machines.multiblock.infusion.ArcanePedestal;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;

public class MachineRegistry {
    public static final DeferredRegister<BlockEntityType<?>> MACHINES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, AxiomMod.MODID);
    public static final HashMap<String, MachineRegistryObject> MACHINELIST = new HashMap<>();

    public static final MachineRegistryObject ALEMBIC = registerMachine(new MachineRegistryObject(Alembic.name, Alembic::new, Alembic.MachineBlock::new));
    public static final MachineRegistryObject CHEMICAL_REACTOR = registerMachine(new MachineRegistryObject(ChemicalReactor.name, ChemicalReactor::new, ChemicalReactor.MachineBlock::new));
    public static final MachineRegistryObject FERMENTER = registerMachine(new MachineRegistryObject(Fermenter.name, Fermenter::new, Fermenter.MachineBlock::new));
    public static final MachineRegistryObject SOAKING_DRUM = registerMachine(new MachineRegistryObject(SoakingDrum.name, SoakingDrum::new, SoakingDrum.MachineBlock::new));
    public static final MachineRegistryObject ARCANE_ALTAR = registerMachine(new MachineRegistryObject(ArcaneAltar.name, ArcaneAltar::new, ArcaneAltar.MachineBlock::new));
    public static final MachineRegistryObject ARCANE_PEDESTAL = registerMachine(new MachineRegistryObject(ArcanePedestal.name, ArcanePedestal::new, ArcanePedestal.MachineBlock::new));
    public static final MachineRegistryObject ITEM_IN_HATCH = registerMachine(new MachineRegistryObject(ItemInputHatch.name, ItemInputHatch::new, ItemInputHatch.MachineBlock::new));
    public static final MachineRegistryObject ITEM_OUT_HATCH = registerMachine(new MachineRegistryObject(ItemOutputHatch.name, ItemOutputHatch::new, ItemOutputHatch.MachineBlock::new));
    public static final MachineRegistryObject FLUID_IN_HATCH = registerMachine(new MachineRegistryObject(FluidInputHatch.name, FluidInputHatch::new, FluidInputHatch.MachineBlock::new));
    public static final MachineRegistryObject FLUID_OUT_HATCH = registerMachine(new MachineRegistryObject(FluidOutputHatch.name, FluidOutputHatch::new, FluidOutputHatch.MachineBlock::new));
    public static final MachineRegistryObject PYROLYZER = registerMachine(new MachineRegistryObject(Pyrolyzer.name, Pyrolyzer::new, Pyrolyzer.MachineBlock::new));


    public static MachineRegistryObject registerMachine(MachineRegistryObject machine) {
        MACHINELIST.put(machine.name, machine);
        return machine;
    }
}
