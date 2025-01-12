package net.ryancave282.tconstruct.tools.modifiers.traits.skull;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.modifiers.ModifierHooks;
import net.ryancave282.tconstruct.library.modifiers.hook.combat.DamageDealtModifierHook;
import net.ryancave282.tconstruct.library.modifiers.impl.NoLevelsModifier;
import net.ryancave282.tconstruct.library.module.ModuleHookMap.Builder;
import net.ryancave282.tconstruct.library.tools.context.EquipmentContext;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;
import net.ryancave282.tconstruct.tools.TinkerModifiers;

public class WitheredModifier extends NoLevelsModifier implements DamageDealtModifierHook {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(StrongBonesModifier.CALCIFIABLE_MODULE);
    hookBuilder.addHook(this, ModifierHooks.DAMAGE_DEALT);
  }

  @Override
  public void onDamageDealt(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, LivingEntity target, DamageSource source, float amount, boolean isDirectDamage) {
    // drink milk for more power, but less duration
    if (isDirectDamage && !source.is(DamageTypeTags.IS_PROJECTILE)) {
      boolean isCalcified = context.getEntity().hasEffect(TinkerModifiers.calcifiedEffect.get());
      target.addEffect(new MobEffectInstance(MobEffects.WITHER, isCalcified ? 100 : 200, isCalcified ? 1 : 0));
    }
  }
}
