package net.ryancave282.tconstruct.library.modifiers.modules.combat;

import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import net.ryancave282.tconstruct.library.json.math.ModifierFormula;
import net.ryancave282.tconstruct.library.json.variable.VariableFormula;
import net.ryancave282.tconstruct.library.json.variable.melee.MeleeFormula;
import net.ryancave282.tconstruct.library.json.variable.melee.MeleeVariable;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.modifiers.ModifierHooks;
import net.ryancave282.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import net.ryancave282.tconstruct.library.modifiers.modules.ModifierModule;
import net.ryancave282.tconstruct.library.modifiers.modules.util.ConditionalStatTooltip;
import net.ryancave282.tconstruct.library.modifiers.modules.util.ModifierCondition;
import net.ryancave282.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import net.ryancave282.tconstruct.library.module.HookProvider;
import net.ryancave282.tconstruct.library.module.ModuleHook;
import net.ryancave282.tconstruct.library.tools.context.ToolAttackContext;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;
import net.ryancave282.tconstruct.library.tools.stat.INumericToolStat;
import net.ryancave282.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Implementation of attack damage conditioned on the attacker or target's properties
 * @param target     Target condition
 * @param attacker   Attacker condition
 * @param formula    Damage formula
 * @param condition  Standard modifier conditions
 */
public record ConditionalMeleeDamageModule(IJsonPredicate<LivingEntity> target, IJsonPredicate<LivingEntity> attacker, MeleeFormula formula, ModifierCondition<IToolStackView> condition) implements MeleeDamageModifierHook, ConditionalStatTooltip, ModifierModule, ConditionalModule<IToolStackView> {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<ConditionalMeleeDamageModule>defaultHooks(ModifierHooks.MELEE_DAMAGE, ModifierHooks.TOOLTIP);
  public static final RecordLoadable<ConditionalMeleeDamageModule> LOADER = RecordLoadable.create(
    LivingEntityPredicate.LOADER.defaultField("target", ConditionalMeleeDamageModule::target),
    LivingEntityPredicate.LOADER.defaultField("attacker", ConditionalMeleeDamageModule::attacker),
    MeleeFormula.LOADER.directField(ConditionalMeleeDamageModule::formula),
    ModifierCondition.TOOL_FIELD,
    ConditionalMeleeDamageModule::new);

  @Override
  public boolean percent() {
    return formula.percent();
  }

  @Nullable
  @Override
  public Integer getPriority() {
    // run multipliers a bit later
    return percent() ? 75 : null;
  }

  @Override
  public float getMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
    if (condition.matches(tool, modifier) && attacker.matches(context.getAttacker())) {
      LivingEntity target = context.getLivingTarget();
      if (target != null && this.target.matches(target)) {
        damage = formula.apply(tool, modifier, context, context.getAttacker(), baseDamage, damage);
      }
    }
    return damage;
  }

  @Override
  public IJsonPredicate<LivingEntity> holder() {
    return attacker;
  }

  @Override
  public INumericToolStat<?> stat() {
    return ToolStats.ATTACK_DAMAGE;
  }

  @Override
  public float computeTooltipValue(IToolStackView tool, ModifierEntry entry, @Nullable Player player) {
    return formula.apply(tool, entry, null, player, 1, 1);
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public RecordLoadable<ConditionalMeleeDamageModule> getLoader() {
    return LOADER;
  }


  /* Builder */

  /** Creates a builder instance */
  public static Builder builder() {
    return new Builder();
  }

  /** Builder class */
  @Accessors(fluent = true)
  public static class Builder extends VariableFormula.Builder<Builder,ConditionalMeleeDamageModule,MeleeVariable> {
    @Setter
    private IJsonPredicate<LivingEntity> target = LivingEntityPredicate.ANY;
    @Setter
    private IJsonPredicate<LivingEntity> attacker = LivingEntityPredicate.ANY;

    private Builder() {
      super(MeleeFormula.VARIABLES);
    }

    @Override
    protected ConditionalMeleeDamageModule build(ModifierFormula formula) {
      return new ConditionalMeleeDamageModule(target, attacker, new MeleeFormula(formula, variables, percent), condition);
    }
  }
}
