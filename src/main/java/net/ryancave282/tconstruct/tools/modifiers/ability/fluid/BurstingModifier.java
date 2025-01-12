package net.ryancave282.tconstruct.tools.modifiers.ability.fluid;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.modifiers.ModifierHooks;
import net.ryancave282.tconstruct.library.modifiers.fluid.FluidEffectContext;
import net.ryancave282.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import net.ryancave282.tconstruct.library.module.ModuleHookMap.Builder;
import net.ryancave282.tconstruct.library.tools.context.EquipmentContext;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

import static net.ryancave282.tconstruct.library.tools.helper.ModifierUtil.asLiving;

/** Modifier to handle spilling recipes */
public class BurstingModifier extends UseFluidOnHitModifier implements OnAttackedModifierHook {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ModifierHooks.ON_ATTACKED);
  }

  @Override
  public FluidEffectContext.Entity createContext(LivingEntity self, @Nullable Player player, @Nullable Entity attacker) {
    assert attacker != null;
    return new FluidEffectContext.Entity(self.level(), self, player, null, attacker, asLiving(attacker));
  }

  @Override
  public void onAttacked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
    if (source.getEntity() != null && isDirectDamage) {
      useFluid(tool, modifier, context, slotType, source);
    }
  }
}
