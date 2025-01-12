package net.ryancave282.tconstruct.tools.modifiers.ability.tool;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import slimeknights.mantle.util.OffhandCooldownTracker;
import net.ryancave282.tconstruct.TConstruct;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.modifiers.ModifierHooks;
import net.ryancave282.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.build.VolatileDataModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.interaction.EntityInteractionModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import net.ryancave282.tconstruct.library.modifiers.impl.NoLevelsModifier;
import net.ryancave282.tconstruct.library.modifiers.modules.behavior.ShowOffhandModule;
import net.ryancave282.tconstruct.library.module.ModuleHookMap.Builder;
import net.ryancave282.tconstruct.library.tools.context.EquipmentChangeContext;
import net.ryancave282.tconstruct.library.tools.helper.ToolAttackUtil;
import net.ryancave282.tconstruct.library.tools.nbt.IToolContext;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;
import net.ryancave282.tconstruct.library.tools.nbt.ToolDataNBT;
import net.ryancave282.tconstruct.library.tools.stat.ToolStats;

public class OffhandAttackModifier extends NoLevelsModifier implements EntityInteractionModifierHook, GeneralInteractionModifierHook, EquipmentChangeModifierHook, VolatileDataModifierHook {
  public static final ResourceLocation DUEL_WIELDING = TConstruct.getResource("duel_wielding");

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ModifierHooks.GENERAL_INTERACT, ModifierHooks.ENTITY_INTERACT, ModifierHooks.EQUIPMENT_CHANGE, ModifierHooks.VOLATILE_DATA);
    hookBuilder.addModule(ShowOffhandModule.DISALLOW_BROKEN);
  }

  @Override
  public boolean shouldDisplay(boolean advanced) {
    return false;
  }

  @Override
  public void addVolatileData(IToolContext context, ModifierEntry modifier, ToolDataNBT volatileData) {
    volatileData.putBoolean(DUEL_WIELDING, true);
  }

  /** If true, we can use the attack */
  protected boolean canAttack(IToolStackView tool, Player player, InteractionHand hand) {
    return !tool.isBroken() && hand == InteractionHand.OFF_HAND && OffhandCooldownTracker.isAttackReady(player);
  }

  @Override
  public InteractionResult beforeEntityUse(IToolStackView tool, ModifierEntry modifier, Player player, Entity target, InteractionHand hand, InteractionSource source) {
    if (canAttack(tool, player, hand)) {
      if (!player.level().isClientSide()) {
        ToolAttackUtil.attackEntity(tool, player, InteractionHand.OFF_HAND, target, ToolAttackUtil.getCooldownFunction(player, InteractionHand.OFF_HAND), false, source.getSlot(hand));
      }
      // for armor, always assume attack speed is 4.0, we cannot change the attack speed of the main hand and we want them to match
      OffhandCooldownTracker.applyCooldown(player, source == InteractionSource.ARMOR ? 4 : tool.getStats().get(ToolStats.ATTACK_SPEED), 20);
      // we handle swinging the arm, return consume to prevent resetting cooldown
      OffhandCooldownTracker.swingHand(player, InteractionHand.OFF_HAND, false);
      return InteractionResult.CONSUME;
    }
    return InteractionResult.PASS;
  }

  @Override
  public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
    if (canAttack(tool, player, hand)) {
      // target done in onEntityInteract, this is just for cooldown cause you missed
      OffhandCooldownTracker.applyCooldown(player, source == InteractionSource.ARMOR ? 4 : tool.getStats().get(ToolStats.ATTACK_SPEED), 20);
      // we handle swinging the arm, return consume to prevent resetting cooldown
      OffhandCooldownTracker.swingHand(player, InteractionHand.OFF_HAND, false);
      return InteractionResult.CONSUME;
    }
    return InteractionResult.PASS;
  }

  @Override
  public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (!tool.isBroken() && context.getChangedSlot() == EquipmentSlot.OFFHAND) {
      context.getEntity().getCapability(OffhandCooldownTracker.CAPABILITY).ifPresent(cap -> cap.setEnabled(true));
    }
  }

  @Override
  public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (!tool.isBroken() && context.getChangedSlot() == EquipmentSlot.OFFHAND) {
      context.getEntity().getCapability(OffhandCooldownTracker.CAPABILITY).ifPresent(cap -> cap.setEnabled(false));
    }
  }
}
