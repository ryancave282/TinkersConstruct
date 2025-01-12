package net.ryancave282.tconstruct.tools.modifiers.upgrades.armor;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.LivingEntity;
import net.ryancave282.tconstruct.library.modifiers.Modifier;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.modifiers.ModifierHooks;
import net.ryancave282.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import net.ryancave282.tconstruct.library.module.ModuleHookMap.Builder;
import net.ryancave282.tconstruct.library.tools.context.EquipmentContext;
import net.ryancave282.tconstruct.library.tools.helper.ToolDamageUtil;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;

public class ThornsModifier extends Modifier implements OnAttackedModifierHook {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ModifierHooks.ON_ATTACKED);
  }

  @Override
  public void onAttacked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
    // this works like vanilla, damage is capped due to the hurt immunity mechanics, so if multiple pieces apply thorns between us and vanilla, damage is capped at 4
    Entity attacker = source.getEntity();
    if (attacker != null && isDirectDamage) {
      // 15% chance of working per level, doubled bonus on shields
      float scaledLevel = modifier.getEffectiveLevel();
      if (slotType.getType() == Type.HAND) {
        scaledLevel *= 2;
      }
      if (RANDOM.nextFloat() < (scaledLevel * 0.15f)) {
        float damage = scaledLevel > 10 ? scaledLevel - 10 : 1 + RANDOM.nextInt(4);
        LivingEntity user = context.getEntity();
        attacker.hurt(user.damageSources().thorns(user), damage);
        ToolDamageUtil.damageAnimated(tool, 1, user, slotType);
      }
    }
  }
}
