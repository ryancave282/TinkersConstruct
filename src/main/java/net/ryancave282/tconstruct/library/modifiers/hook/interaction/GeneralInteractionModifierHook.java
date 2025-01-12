package net.ryancave282.tconstruct.library.modifiers.hook.interaction;

import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.ryancave282.tconstruct.library.client.model.TinkerItemProperties;
import net.ryancave282.tconstruct.TConstruct;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.modifiers.ModifierId;
import net.ryancave282.tconstruct.library.modifiers.hook.build.ConditionalStatModifierHook;
import net.ryancave282.tconstruct.library.tools.nbt.IModDataView;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;
import net.ryancave282.tconstruct.library.tools.nbt.ModDataNBT;
import net.ryancave282.tconstruct.library.tools.stat.ToolStats;

import java.util.Collection;

/**
 * Hooks for standard interaction logic post block/entity interaction. See {@link BlockInteractionModifierHook} for block interaction and {@link EntityInteractionModifierHook} for entities.
 */
public interface GeneralInteractionModifierHook {
  /**
   * Hook called after block/entity interaction passes, or when interacting with empty air.
   * @param tool       Tool performing interaction
   * @param modifier   Modifier instance
   * @param player     Interacting player
   * @param hand       Hand used for interaction
   * @param source     Source of the interaction
   * @return  Return PASS or FAIL to allow vanilla handling, any other to stop vanilla and later modifiers from running.
   */
  InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source);


  /* Charged usage */

  /**
   * Called every tick when the player is using an item.
   * Only supported for {@link InteractionSource#RIGHT_CLICK}.
   * To setup, use {@link #startUsing(IToolStackView, ModifierId, LivingEntity, InteractionHand)} in {@link #onToolUse(IToolStackView, ModifierEntry, Player, InteractionHand, InteractionSource)}.
   * @param tool       Tool performing interaction
   * @param modifier   Modifier instance
   * @param entity     Interacting entity
   * @param timeLeft   How many ticks of use duration was left
   */
  default void onUsingTick(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int timeLeft) {}

  /**
   * Called when the player stops using the tool without finishing. See {@link #onFinishUsing(IToolStackView, ModifierEntry, LivingEntity)} for finishing interaction.
   * Only supported for {@link InteractionSource#RIGHT_CLICK}.
   * To setup, use {@link #startUsing(IToolStackView, ModifierId, LivingEntity, InteractionHand)} in {@link #onToolUse(IToolStackView, ModifierEntry, Player, InteractionHand, InteractionSource)}.
   * @param tool       Tool performing interaction
   * @param modifier   Modifier instance
   * @param entity     Interacting entity
   * @param timeLeft   How many ticks of use duration was left
   */
  default void onStoppedUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int timeLeft) {}

  /**
   * Called when the use duration on this tool reaches the end. See {@link #onStoppedUsing(IToolStackView, ModifierEntry, LivingEntity, int)} for unfinished interaction.
   * To setup, use {@link LivingEntity#startUsingItem(InteractionHand)} in {@link #onToolUse(IToolStackView, ModifierEntry, Player, InteractionHand, InteractionSource)} and set the duration in {@link #getUseDuration(IToolStackView, ModifierEntry)}
   * @param tool       Tool performing interaction
   * @param modifier   Modifier instance
   * @param entity     Interacting entity
   */
  default void onFinishUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity) {}

  /**
   * Determines how long usage lasts on a tool.
   * <p>
   * Since these hooks are called from several locations, it is recommended to set a boolean in persistent data
   * {@link #onToolUse(IToolStackView, ModifierEntry, Player, InteractionHand, InteractionSource)} and only respond to these hooks if that boolean is set.
   * The boolean should be cleared in both {@link #onFinishUsing(IToolStackView, ModifierEntry, LivingEntity)} and {@link #onStoppedUsing(IToolStackView, ModifierEntry, LivingEntity, int)}.
   * @param tool       Tool performing interaction
   * @param modifier   Modifier instance
   * @return  For how many ticks the modifier should run its use action
   */
  default int getUseDuration(IToolStackView tool, ModifierEntry modifier) {
    return 0;
  }

  /**
   * Determines how long usage lasts on a tool.
   * <p>
   * Since these hooks are called from several locations, it is recommended to set a boolean in persistent data
   * {@link #onToolUse(IToolStackView, ModifierEntry, Player, InteractionHand, InteractionSource)} and only respond to these hooks if that boolean is set.
   * The boolean should be cleared in both {@link #onFinishUsing(IToolStackView, ModifierEntry, LivingEntity)} and {@link #onStoppedUsing(IToolStackView, ModifierEntry, LivingEntity, int)}.
   * @param tool       Tool performing interaction
   * @param modifier   Modifier instance
   * @return  Use action to be performed
   */
  default UseAnim getUseAction(IToolStackView tool, ModifierEntry modifier) {
    return UseAnim.NONE;
  }


  /* Helpers */

  /** Persistent key storing the actively running modifier for use in several hooks */
  ResourceLocation KEY_ACTIVE_MODIFIER = TConstruct.getResource("active_modifier");
  /** Persistent data key storing the drawtime needed for using the tool. Generally is set when tool usage starts */
  ResourceLocation KEY_DRAWTIME = TConstruct.getResource("drawtime");

  /**
   * Use in {@link #onToolUse(IToolStackView, ModifierEntry, Player, InteractionHand, InteractionSource)} to start using an item, ensuring later hooks are properly called.
   * @param tool      Tool being used
   * @param modifier  Modifier to call for later hooks, must be on the tool
   * @param living    Entity using the tool, used for the vanilla hook
   * @param hand      Hand using the tool
   */
  static void startUsing(IToolStackView tool, ModifierId modifier, LivingEntity living, InteractionHand hand) {
    tool.getPersistentData().putString(KEY_ACTIVE_MODIFIER, modifier.toString());
    living.startUsingItem(hand);
  }

  /**
   * Use in {@link net.minecraft.world.item.Item#use(Level, Player, InteractionHand)} or {@link #onToolUse(IToolStackView, ModifierEntry, Player, InteractionHand, InteractionSource)} to setup draw time for {@link TinkerItemProperties}.
   * @param tool      Tool being used
   * @param living    Entity using the tool, used for the vanilla hook
   * @param speedFactor  Additional factor to multiply drawtime by, after considering {@link ToolStats#DRAW_SPEED}
   */
  static void startDrawtime(IToolStackView tool, LivingEntity living, float speedFactor) {
    tool.getPersistentData().putInt(KEY_DRAWTIME, (int)Math.ceil(20f * speedFactor / ConditionalStatModifierHook.getModifiedStat(tool, living, ToolStats.DRAW_SPEED)));
  }

  /**
   * Combination of {@link #startUsing(IToolStackView, ModifierId, LivingEntity, InteractionHand)} and {@link #startDrawtime(IToolStackView, LivingEntity, float)} ensuring they are added in the proper order.
   * @param tool         Tool being used
   * @param modifier     Modifier to call for later hooks, must be on the tool
   * @param living       Entity using the tool, used for the vanilla hook
   * @param hand         Hand using the tool
   * @param speedFactor  Additional factor to multiply drawtime by, after considering {@link ToolStats#DRAW_SPEED}
   */
  static void startUsingWithDrawtime(IToolStackView tool, ModifierId modifier, LivingEntity living, InteractionHand hand, float speedFactor) {
    startDrawtime(tool, living, speedFactor);
    startUsing(tool, modifier, living, hand);
  }

  /**
   * Gets the current charge percentage based on the given tool
   * @param tool        Tool being used
   * @param chargeTime  Ticks the item has been used so far, typically from {@link #onStoppedUsing(IToolStackView, ModifierEntry, LivingEntity, int)}.
   */
  static float getToolCharge(IToolStackView tool, float chargeTime) {
    float charge = chargeTime / tool.getPersistentData().getInt(KEY_DRAWTIME);
    charge = (charge * charge + charge * 2) / 3;
    if (charge > 1) {
      charge = 1;
    }
    return charge;
  }

  /** Gets the currently active modifier, or {@link ModifierEntry#EMPTY} if none is active. Generally does not need to be called in modifiers as we call it in internal logic. */
  static ModifierEntry getActiveModifier(IToolStackView tool) {
    IModDataView persistentData = tool.getPersistentData();
    if (persistentData.contains(KEY_ACTIVE_MODIFIER, Tag.TAG_STRING)) {
      ModifierId modifier = ModifierId.tryParse(persistentData.getString(KEY_ACTIVE_MODIFIER));
      if (modifier != null) {
        return tool.getModifiers().getEntry(modifier);
      }
    }
    return ModifierEntry.EMPTY;
  }

  /** Called to clear any data modifiers set when usage starts. Generally does not need to be called in modifiers as we call it in internal logic. */
  static void finishUsing(IToolStackView tool) {
    ModDataNBT persistentData = tool.getPersistentData();
    persistentData.remove(KEY_ACTIVE_MODIFIER);
    persistentData.remove(KEY_DRAWTIME);
  }


  /** Logic to merge multiple interaction hooks into one */
  record FirstMerger(Collection<GeneralInteractionModifierHook> modules) implements GeneralInteractionModifierHook {
    @Override
    public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
      InteractionResult result = InteractionResult.PASS;
      for (GeneralInteractionModifierHook module : modules) {
        result = module.onToolUse(tool, modifier, player, hand, source);
        if (result.consumesAction()) {
          return result;
        }
      }
      return result;
    }

    @Override
    public void onStoppedUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int timeLeft) {
      for (GeneralInteractionModifierHook module : modules) {
        module.onStoppedUsing(tool, modifier, entity, timeLeft);
      }
    }

    @Override
    public void onFinishUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity) {
      for (GeneralInteractionModifierHook module : modules) {
        module.onFinishUsing(tool, modifier, entity);
      }
    }

    @Override
    public int getUseDuration(IToolStackView tool, ModifierEntry modifier) {
      for (GeneralInteractionModifierHook module : modules) {
        int duration = module.getUseDuration(tool, modifier);
        if (duration > 0) {
          return duration;
        }
      }
      return 0;
    }

    @Override
    public UseAnim getUseAction(IToolStackView tool, ModifierEntry modifier) {
      for (GeneralInteractionModifierHook module : modules) {
        UseAnim anim = module.getUseAction(tool, modifier);
        if (anim != UseAnim.NONE) {
          return anim;
        }
      }
      return UseAnim.NONE;
    }
  }
}
