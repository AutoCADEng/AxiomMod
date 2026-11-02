package eng.cad.auto.axiology.items;

import eng.cad.auto.axiology.items.util.RangedFluidBehaviours;
import eng.cad.auto.engine.fluid.IRangedFluidBehaviour;
import eng.cad.auto.engine.network.AxiologyNetwork;
import eng.cad.auto.engine.network.FluidBeamSyncPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.ItemFluidContainer;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;

public class SuperSoaker extends ItemFluidContainer {
    public static int COST_PER_SHOT = 50;
    public static int RANGE = 64;
    public SuperSoaker(Properties properties, int capacity) {
        super(properties, capacity);
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (player == null) return InteractionResultHolder.fail(ItemStack.EMPTY);
        ItemStack superSoaker = player.getItemInHand(hand);
        return activateAbility(level, player, superSoaker)? InteractionResultHolder.pass(superSoaker) : InteractionResultHolder.fail(superSoaker);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();

        //Fill from tank
        BlockEntity entity = level.getBlockEntity(pos);
        ItemStack superSoaker = context.getItemInHand();
        if (entity != null) {
            IFluidHandler source = entity.getCapability(ForgeCapabilities.FLUID_HANDLER).orElse(null);
            IFluidHandlerItem gun = superSoaker.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);
            if (level.isClientSide) return InteractionResult.SUCCESS;
            int space = gun.getTankCapacity(0) - gun.getFluidInTank(0).getAmount();
            FluidStack moved = FluidUtil.tryFluidTransfer(gun, source, space, true);
            return moved.isEmpty() ? InteractionResult.PASS : InteractionResult.sidedSuccess(false);
        }
        return activateAbility(level, context.getPlayer(), superSoaker)? InteractionResult.SUCCESS: InteractionResult.FAIL;
    }

    private boolean activateAbility(Level level, Player player, ItemStack itemStack){
        if (level.isClientSide || player == null) return false;
        IFluidHandlerItem superSoakerTank = itemStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve().orElse(null);
        if (superSoakerTank == null) return false;
        if (superSoakerTank.getFluidInTank(0).getAmount()<COST_PER_SHOT) return false;
        Fluid ammo = superSoakerTank.getFluidInTank(0).getFluid();
        superSoakerTank.drain(COST_PER_SHOT, IFluidHandler.FluidAction.EXECUTE);
        Vec3 castFrom = player.getEyePosition();
        Vec3 ray = player.getLookAngle().scale(RANGE);
        Vec3 castTo = castFrom.add(ray);
        AABB searchBox = player.getBoundingBox().expandTowards(castTo.subtract(castFrom)).inflate(1.0);
        EntityHitResult hitResult = ProjectileUtil.getEntityHitResult(level,player,castFrom,castTo,searchBox, entity -> entity.isPickable() && entity != player);
        AxiologyNetwork.INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(castFrom.x, castFrom.y, castFrom.z, 128, level.dimension())), new FluidBeamSyncPacket(UUID.randomUUID(),ForgeRegistries.FLUIDS.getKey(ammo), 0xFFFFFFFF, castFrom.toVector3f(), castTo.toVector3f(), level));

        level.playSound(null, castFrom.x, castFrom.y, castFrom.z,
                SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath("axiology", "super_soaker_fire")),
                    SoundSource.PLAYERS,
                0.7f + level.random.nextFloat() * 0.6f,
                0.7f + level.random.nextFloat() * 0.6f);

        if (hitResult == null) return true;
        Entity target = hitResult.getEntity();
        if (!(target instanceof LivingEntity livingEntity)) return true;
        IRangedFluidBehaviour behaviour = RangedFluidBehaviours.FLUIDS.containsKey(ammo)? RangedFluidBehaviours.FLUIDS.get(ammo) : RangedFluidBehaviours.GENERIC_BEHAVIOUR;
        behaviour.applyAsProjectile(level, livingEntity);

        return true;
    }


}