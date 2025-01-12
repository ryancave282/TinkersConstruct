package net.ryancave282.tconstruct.tools.modifiers.upgrades.ranged;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.ryancave282.tconstruct.library.modifiers.Modifier;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.modifiers.ModifierHooks;
import net.ryancave282.tconstruct.library.modifiers.hook.interaction.EntityInteractionModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import net.ryancave282.tconstruct.library.module.ModuleHookMap.Builder;
import net.ryancave282.tconstruct.library.tools.item.ranged.ModifiableCrossbowItem;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;

public class SinistralModifier extends Modifier implements GeneralInteractionModifierHook, EntityInteractionModifierHook {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ModifierHooks.GENERAL_INTERACT, ModifierHooks.ENTITY_INTERACT);
  }

  @Override
  public InteractionResult afterEntityUse(IToolStackView tool, ModifierEntry modifier, Player player, LivingEntity target, InteractionHand hand, InteractionSource source) {
    return onToolUse(tool, modifier, player, hand, source);
  }

  @Override
  public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
    if (source == InteractionSource.LEFT_CLICK && hand == InteractionHand.MAIN_HAND && !tool.isBroken()) {
      CompoundTag heldAmmo = tool.getPersistentData().getCompound(ModifiableCrossbowItem.KEY_CROSSBOW_AMMO);
      if (!heldAmmo.isEmpty()) {
        ModifiableCrossbowItem.fireCrossbow(tool, player, hand, heldAmmo);
        return InteractionResult.CONSUME;
      }
    }
    return InteractionResult.PASS;
  }
}
