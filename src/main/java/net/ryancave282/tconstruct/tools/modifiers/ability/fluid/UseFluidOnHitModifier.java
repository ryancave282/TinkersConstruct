package net.ryancave282.tconstruct.tools.modifiers.ability.fluid;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.ryancave282.tconstruct.library.modifiers.Modifier;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.modifiers.fluid.FluidEffectContext;
import net.ryancave282.tconstruct.library.modifiers.fluid.FluidEffectManager;
import net.ryancave282.tconstruct.library.modifiers.fluid.FluidEffects;
import net.ryancave282.tconstruct.library.modifiers.modules.build.StatBoostModule;
import net.ryancave282.tconstruct.library.module.ModuleHookMap.Builder;
import net.ryancave282.tconstruct.library.tools.capability.fluid.ToolTankHelper;
import net.ryancave282.tconstruct.library.tools.context.EquipmentContext;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;
import net.ryancave282.tconstruct.shared.TinkerCommons;
import net.ryancave282.tconstruct.shared.particle.FluidParticleData;

import javax.annotation.Nullable;

import static net.ryancave282.tconstruct.library.tools.capability.fluid.ToolTankHelper.TANK_HELPER;

/** Modifier to handle spilling recipes onto self when attacked */
public abstract class UseFluidOnHitModifier extends Modifier {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(ToolTankHelper.TANK_HANDLER);
    hookBuilder.addModule(StatBoostModule.add(ToolTankHelper.CAPACITY_STAT).eachLevel(FluidType.BUCKET_VOLUME));
  }

  /** Spawns particles at the given entity */
  public static void spawnParticles(Entity target, FluidStack fluid) {
    if (target.level() instanceof ServerLevel serverLevel) {
      serverLevel.sendParticles(new FluidParticleData(TinkerCommons.fluidParticle.get(), fluid), target.getX(), target.getY(0.5), target.getZ(), 10, 0.1, 0.2, 0.1, 0.2);
    }
  }

  /** Overridable method to create the attack context and spawn particles */
  public abstract FluidEffectContext.Entity createContext(LivingEntity self, @Nullable Player player, @Nullable Entity attacker);

  /** Logic for using the fluid */
  protected void useFluid(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source) {
    // 25% chance of working per level, 50% per level on shields
    float level = modifier.getEffectiveLevel();
    if (RANDOM.nextInt(slotType.getType() == Type.HAND ? 2 : 4) < level) {
      FluidStack fluid = TANK_HELPER.getFluid(tool);
      if (!fluid.isEmpty()) {
        LivingEntity self = context.getEntity();
        Player player = self instanceof Player p ? p : null;
        FluidEffects recipe = FluidEffectManager.INSTANCE.find(fluid.getFluid());
        if (recipe.hasEffects()) {
          FluidEffectContext.Entity fluidContext = createContext(self, player, source.getEntity());
          int consumed = recipe.applyToEntity(fluid, level, fluidContext, FluidAction.EXECUTE);
          if (consumed > 0 && (player == null || !player.isCreative())) {
            spawnParticles(fluidContext.getTarget(), fluid);
            fluid.shrink(consumed);
            TANK_HELPER.setFluid(tool, fluid);
          }
        }
      }
    }
  }
}
