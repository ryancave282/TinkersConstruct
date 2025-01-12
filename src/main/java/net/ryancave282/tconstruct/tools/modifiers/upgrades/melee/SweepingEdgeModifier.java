package net.ryancave282.tconstruct.tools.modifiers.upgrades.melee;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.mantle.client.TooltipKey;
import net.ryancave282.tconstruct.TConstruct;
import net.ryancave282.tconstruct.library.modifiers.Modifier;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.modifiers.ModifierHooks;
import net.ryancave282.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import net.ryancave282.tconstruct.library.module.ModuleHookMap.Builder;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;
import net.ryancave282.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.util.List;

public class SweepingEdgeModifier extends Modifier implements TooltipModifierHook {
  private static final Component SWEEPING_BONUS = TConstruct.makeTranslation("modifier", "sweeping_edge.attack_damage");

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ModifierHooks.TOOLTIP);
  }

  /** Gets the damage dealt by this tool, boosted properly by sweeping */
  public float getSweepingDamage(IToolStackView toolStack, float baseDamage) {
    float level = toolStack.getModifier(this).getEffectiveLevel();
    float sweepingDamage = 1;
    if (level > 4) {
      sweepingDamage = baseDamage;
    } else if (level > 0) {
      // gives 25% per level, cap at base damage
      sweepingDamage = Math.min(baseDamage, level * 0.25f * baseDamage + 1);
    }
    return sweepingDamage;
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    float amount = modifier.getEffectiveLevel() * 0.25f;
    tooltip.add(applyStyle(Component.literal(Util.PERCENT_FORMAT.format(amount)).append(" ").append(SWEEPING_BONUS)));
  }
}
