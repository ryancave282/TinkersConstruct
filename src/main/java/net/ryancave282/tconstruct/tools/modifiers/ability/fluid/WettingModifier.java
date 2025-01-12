package net.ryancave282.tconstruct.tools.modifiers.ability.fluid;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.modifiers.ModifierHooks;
import net.ryancave282.tconstruct.library.modifiers.fluid.FluidEffectContext;
import net.ryancave282.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import net.ryancave282.tconstruct.library.module.ModuleHookMap.Builder;
import net.ryancave282.tconstruct.library.tools.context.EquipmentContext;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

/** Modifier to handle spilling recipes onto self when attacked */
public class WettingModifier extends UseFluidOnHitModifier implements ModifyDamageModifierHook {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ModifierHooks.MODIFY_DAMAGE);
  }

  @Override
  public FluidEffectContext.Entity createContext(LivingEntity self, @Nullable Player player, @Nullable Entity attacker) {
    return new FluidEffectContext.Entity(self.level(), self, player, null, self, self);
  }

  @Override
  public float modifyDamageTaken(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
    if (!source.is(DamageTypeTags.BYPASSES_EFFECTS) && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
      useFluid(tool, modifier, context, slotType, source);
    }
    return amount;
  }
}
