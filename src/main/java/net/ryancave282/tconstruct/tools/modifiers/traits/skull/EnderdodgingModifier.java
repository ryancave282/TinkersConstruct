package net.ryancave282.tconstruct.tools.modifiers.traits.skull;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.ryancave282.tconstruct.library.events.teleport.EnderdodgingTeleportEvent;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.modifiers.ModifierHooks;
import net.ryancave282.tconstruct.library.modifiers.hook.armor.DamageBlockModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import net.ryancave282.tconstruct.library.modifiers.impl.NoLevelsModifier;
import net.ryancave282.tconstruct.library.module.ModuleHookMap.Builder;
import net.ryancave282.tconstruct.library.tools.context.EquipmentContext;
import net.ryancave282.tconstruct.library.tools.helper.ToolDamageUtil;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;
import net.ryancave282.tconstruct.library.utils.TeleportHelper;
import net.ryancave282.tconstruct.library.utils.TeleportHelper.ITeleportEventFactory;
import net.ryancave282.tconstruct.tools.TinkerModifiers;

public class EnderdodgingModifier extends NoLevelsModifier implements DamageBlockModifierHook, OnAttackedModifierHook {
  private static final ITeleportEventFactory FACTORY = EnderdodgingTeleportEvent::new;

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ModifierHooks.DAMAGE_BLOCK, ModifierHooks.ON_ATTACKED);
  }

  @Override
  public boolean isDamageBlocked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount) {
    // teleport always from projectiles
    LivingEntity self = context.getEntity();
    if (!self.hasEffect(TinkerModifiers.teleportCooldownEffect.get()) && source.isIndirect()) {
      if (TeleportHelper.randomNearbyTeleport(context.getEntity(), FACTORY)) {
        TinkerModifiers.teleportCooldownEffect.get().apply(self, 15 * 20, 0, true);
        ToolDamageUtil.damageAnimated(tool, (int)amount, self, slotType);
        return true;
      }
      return false;
    }
    return false;
  }

  @Override
  public void onAttacked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
    // teleport randomly from other damage
    LivingEntity self = context.getEntity();
    if (!self.hasEffect(TinkerModifiers.teleportCooldownEffect.get()) && source.getEntity() instanceof LivingEntity && RANDOM.nextInt(10) == 0) {
      if (TeleportHelper.randomNearbyTeleport(context.getEntity(), FACTORY)) {
        TinkerModifiers.teleportCooldownEffect.get().apply(self, 15 * 20, 1, true);
      }
    }
  }
}
