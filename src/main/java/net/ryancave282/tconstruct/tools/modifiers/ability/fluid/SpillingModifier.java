package net.ryancave282.tconstruct.tools.modifiers.ability.fluid;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.ryancave282.tconstruct.library.modifiers.Modifier;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.modifiers.ModifierHooks;
import net.ryancave282.tconstruct.library.modifiers.fluid.FluidEffectContext;
import net.ryancave282.tconstruct.library.modifiers.fluid.FluidEffectManager;
import net.ryancave282.tconstruct.library.modifiers.fluid.FluidEffects;
import net.ryancave282.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import net.ryancave282.tconstruct.library.modifiers.modules.build.StatBoostModule;
import net.ryancave282.tconstruct.library.module.ModuleHookMap.Builder;
import net.ryancave282.tconstruct.library.tools.capability.fluid.ToolTankHelper;
import net.ryancave282.tconstruct.library.tools.context.ToolAttackContext;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;

import static net.ryancave282.tconstruct.library.tools.capability.fluid.ToolTankHelper.TANK_HELPER;
import static net.ryancave282.tconstruct.tools.modifiers.ability.fluid.UseFluidOnHitModifier.spawnParticles;

/** Modifier applying fluid effects on melee hit */
public class SpillingModifier extends Modifier implements MeleeHitModifierHook {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(ToolTankHelper.TANK_HANDLER);
    hookBuilder.addModule(StatBoostModule.add(ToolTankHelper.CAPACITY_STAT).eachLevel(FluidType.BUCKET_VOLUME));
    hookBuilder.addHook(this, ModifierHooks.MELEE_HIT);
  }

  @Override
  public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
    if (damageDealt > 0 && context.isFullyCharged()) {
      FluidStack fluid = TANK_HELPER.getFluid(tool);
      if (!fluid.isEmpty()) {
        FluidEffects recipe = FluidEffectManager.INSTANCE.find(fluid.getFluid());
        if (recipe.hasEntityEffects()) {
          LivingEntity living = context.getAttacker();
          Player player = context.getPlayerAttacker();
          int consumed = recipe.applyToEntity(fluid, modifier.getEffectiveLevel(), new FluidEffectContext.Entity(living.level(), living, player, null, context.getTarget(), context.getLivingTarget()), FluidAction.EXECUTE);
          if (consumed > 0 && (player == null || !player.isCreative())) {
            spawnParticles(context.getTarget(), fluid);
            fluid.shrink(consumed);
            TANK_HELPER.setFluid(tool, fluid);
          }
        }
      }
    }
  }
}
