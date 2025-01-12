package net.ryancave282.tconstruct.library.modifiers.hook.interaction;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.ryancave282.tconstruct.common.TinkerTags;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.modifiers.ModifierHooks;
import net.ryancave282.tconstruct.library.tools.helper.ToolAttackUtil;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;
import net.ryancave282.tconstruct.library.tools.nbt.ToolStack;

import java.util.Collection;
import java.util.List;

/**
 * Hooks for standard interaction logic though entities. See {@link GeneralInteractionModifierHook} for general interaction and {@link BlockInteractionModifierHook} for blocks.
 */
public interface EntityInteractionModifierHook {
  /**
	 * Called when interacting with an entity before standard entity interaction.
   * In general, its better to use {@link #afterEntityUse(IToolStackView, ModifierEntry, Player, LivingEntity, InteractionHand, InteractionSource)} for behavior more consistent with vanilla.
   * @param tool       Tool performing interaction
   * @param modifier   Modifier instance
   * @param player     Interacting player
   * @param target     Target of interaction
   * @param hand       Hand used for interaction
   * @param source     Source of the interaction
   * @return  Return PASS or FAIL to allow vanilla handling, any other to stop vanilla and later modifiers from running.
	 */
  default InteractionResult beforeEntityUse(IToolStackView tool, ModifierEntry modifier, Player player, Entity target, InteractionHand hand, InteractionSource source) {
    return InteractionResult.PASS;
  }

  /**
   * Called when interacting with an entity after standard entity interaction.
   * @param tool       Tool performing interaction
   * @param modifier   Modifier instance
   * @param player     Interacting player
   * @param target     Target of interaction
   * @param hand       Hand used for interaction
   * @param source     Source of the interaction
   * @return  Return PASS or FAIL to allow vanilla handling, any other to stop vanilla and later modifiers from running.
   */
  default InteractionResult afterEntityUse(IToolStackView tool, ModifierEntry modifier, Player player, LivingEntity target, InteractionHand hand, InteractionSource source) {
    return InteractionResult.PASS;
  }

  /** Logic to merge multiple interaction hooks into one */
  record FirstMerger(Collection<EntityInteractionModifierHook> modules) implements EntityInteractionModifierHook {
    @Override
    public InteractionResult beforeEntityUse(IToolStackView tool, ModifierEntry modifier, Player player, Entity target, InteractionHand hand, InteractionSource source) {
      InteractionResult result = InteractionResult.PASS;
      for (EntityInteractionModifierHook module : modules) {
        result = module.beforeEntityUse(tool, modifier, player, target, hand, source);
        if (result.consumesAction()) {
          return result;
        }
      }
      return result;
    }

    @Override
    public InteractionResult afterEntityUse(IToolStackView tool, ModifierEntry modifier, Player player, LivingEntity target, InteractionHand hand, InteractionSource source) {
      InteractionResult result = InteractionResult.PASS;
      for (EntityInteractionModifierHook module : modules) {
        result = module.afterEntityUse(tool, modifier, player, target, hand, source);
        if (result.consumesAction()) {
          return result;
        }
      }
      return result;
    }
  }


  /** Logic to left click an entity using interaction modifiers */
  static boolean leftClickEntity(ItemStack stack, Player player, Entity target) {
    ToolStack tool = ToolStack.from(stack);
    if (stack.is(TinkerTags.Items.INTERACTABLE_LEFT)) {
      if (!player.getCooldowns().isOnCooldown(stack.getItem())) {
        List<ModifierEntry> modifiers = tool.getModifierList();
        // TODO: should this be in the event?
        for (ModifierEntry entry : modifiers) {
          if (entry.getHook(ModifierHooks.ENTITY_INTERACT).beforeEntityUse(tool, entry, player, target, InteractionHand.MAIN_HAND, InteractionSource.LEFT_CLICK).consumesAction()) {
            return true;
          }
        }
        if (target instanceof LivingEntity living) {
          for (ModifierEntry entry : modifiers) {
            if (entry.getHook(ModifierHooks.ENTITY_INTERACT).afterEntityUse(tool, entry, player, living, InteractionHand.MAIN_HAND, InteractionSource.LEFT_CLICK).consumesAction()) {
              return true;
            }
          }
        }
      }
    }
    // no left click modifiers? fallback to standard attack
    return ToolAttackUtil.attackEntity(tool, player, target);
  }
}
