package net.ryancave282.tconstruct.library.modifiers.hook.display;

import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.mantle.client.TooltipKey;
import net.ryancave282.tconstruct.common.TinkerTags;
import net.ryancave282.tconstruct.library.modifiers.Modifier;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;
import net.ryancave282.tconstruct.library.tools.stat.FloatToolStat;
import net.ryancave282.tconstruct.library.tools.stat.IToolStat;
import net.ryancave282.tconstruct.library.tools.stat.ToolStats;
import net.ryancave282.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

/**
 * Hook for modifiers to add tooltip information
 */
public interface TooltipModifierHook {
  /**
   * Adds additional information from the modifier to the tooltip. Shown when holding shift on a tool, or in the stats area of the tinker station
   * @param tool         Tool instance
   * @param modifier        Tool level
   * @param player       Player holding this tool
   * @param tooltip      Tooltip
   * @param tooltipKey   Shows if the player is holding shift, control, or neither
   * @param tooltipFlag  Flag determining tooltip type
   */
  void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag);

  /** Merger that runs all hooks */
  record AllMerger(Collection<TooltipModifierHook> modules) implements TooltipModifierHook {
    @Override
    public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
      for (TooltipModifierHook module : modules) {
        module.addTooltip(tool, modifier, player, tooltip, tooltipKey, tooltipFlag);
      }
    }
  }


  /* Helpers */

  /** Gets the name of the stat to display, uses a translation key built from the tool and the stat */
  static Component statName(Modifier modifier, IToolStat<?> stat) {
    return Component.translatable(modifier.getTranslationKey() + "." + stat.getName().getPath());
  }

  /** Adds a flat bonus tooltip */
  static void addFlatBoost(Modifier modifier, Component name, double bonus, List<Component> tooltip) {
    tooltip.add(modifier.applyStyle(Component.literal(Util.BONUS_FORMAT.format(bonus) + " ").append(name)));
  }

  /** Adds a percentage boost tooltip */
  static void addPercentBoost(Modifier modifier, Component name, double bonus, List<Component> tooltip) {
    tooltip.add(modifier.applyStyle(Component.literal(Util.PERCENT_BOOST_FORMAT.format(bonus) + " ").append(name)));
  }

  /**
   * Adds a tooltip showing a bonus stat
   * @param tool       Tool instance
   * @param modifier   Modifier for style
   * @param stat       Stat added
   * @param condition  Condition to show the tooltip
   * @param amount     Amount to show, before scaling by the tool's modifier
   * @param tooltip    Tooltip list
   */
  static void addStatBoost(IToolStackView tool, Modifier modifier, FloatToolStat stat, TagKey<Item> condition, float amount, List<Component> tooltip) {
    if (tool.hasTag(condition)) {
      addFlatBoost(modifier, statName(modifier, stat), amount * tool.getMultiplier(stat), tooltip);
    }
  }

  /**
   * Adds a tooltip showing the bonus damage and the type of damage
   * @param tool     Tool instance
   * @param modifier Modifier for style
   * @param amount   Damage amount
   * @param tooltip  Tooltip
   */
  static void addDamageBoost(IToolStackView tool, Modifier modifier, float amount, List<Component> tooltip) {
    addStatBoost(tool, modifier, ToolStats.ATTACK_DAMAGE, TinkerTags.Items.MELEE, amount, tooltip);
  }

  /**
   * Adds a tooltip showing the bonus damage and the type of damage dded
   * @param tool         Tool instance
   * @param modifier     Modifier and level
   * @param levelAmount  Bonus per level
   * @param tooltip      Tooltip
   */
  static void addDamageBoost(IToolStackView tool, ModifierEntry modifier, float levelAmount, List<Component> tooltip) {
    addDamageBoost(tool, modifier.getModifier(), modifier.getEffectiveLevel() * levelAmount, tooltip);
  }
}
