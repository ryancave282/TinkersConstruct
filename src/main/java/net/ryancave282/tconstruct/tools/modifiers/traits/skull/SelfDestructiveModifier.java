package net.ryancave282.tconstruct.tools.modifiers.traits.skull;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import slimeknights.mantle.client.TooltipKey;
import net.ryancave282.tconstruct.common.TinkerDamageTypes;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.modifiers.ModifierHooks;
import net.ryancave282.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.interaction.KeybindInteractModifierHook;
import net.ryancave282.tconstruct.library.modifiers.impl.NoLevelsModifier;
import net.ryancave282.tconstruct.library.module.ModuleHookMap.Builder;
import net.ryancave282.tconstruct.library.tools.context.EquipmentChangeContext;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;
import net.ryancave282.tconstruct.tools.TinkerModifiers;
import net.ryancave282.tconstruct.tools.modifiers.effect.NoMilkEffect;

public class SelfDestructiveModifier extends NoLevelsModifier implements KeybindInteractModifierHook, EquipmentChangeModifierHook {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ModifierHooks.ARMOR_INTERACT, ModifierHooks.EQUIPMENT_CHANGE);
  }

  @Override
  public boolean startInteract(IToolStackView tool, ModifierEntry modifier, Player player, EquipmentSlot slot, TooltipKey keyModifier) {
    if (player.isShiftKeyDown()) {
      TinkerModifiers.selfDestructiveEffect.get().apply(player, 30, 2, true);
      player.playSound(SoundEvents.CREEPER_PRIMED, 1.0F, 0.5F);
      return true;
    }
    return false;
  }

  @Override
  public void stopInteract(IToolStackView tool, ModifierEntry modifier, Player player, EquipmentSlot slot) {
    player.removeEffect(TinkerModifiers.selfDestructiveEffect.get());
  }

  @Override
  public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    context.getEntity().removeEffect(TinkerModifiers.selfDestructiveEffect.get());
  }

  /** Internal potion effect handling the explosion */
  public static class SelfDestructiveEffect extends NoMilkEffect {
    public SelfDestructiveEffect() {
      super(MobEffectCategory.HARMFUL, 0x59D24A, true);
      // make the player slow
      addAttributeModifier(Attributes.MOVEMENT_SPEED, "68ee3026-1d50-4eb4-914e-a8b05fbfdb71", -0.9f, Operation.MULTIPLY_TOTAL);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
      return duration == 1;
    }

    @Override
    public void applyEffectTick(LivingEntity living, int amplifier) {
      // effect level is the explosion radius
      Level level = living.level();
      if (!level.isClientSide) {
        // TODO: is firing mob grief event with a player okay?
        level.explode(living, living.getX(), living.getY(), living.getZ(), amplifier + 1, ExplosionInteraction.MOB);
        living.hurt(TinkerDamageTypes.source(level.registryAccess(), TinkerDamageTypes.SELF_DESTRUCT), 99999);
      }
    }
  }
}
