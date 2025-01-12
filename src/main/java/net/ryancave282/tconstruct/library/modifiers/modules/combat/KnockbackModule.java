package net.ryancave282.tconstruct.library.modifiers.modules.combat;

import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import net.ryancave282.tconstruct.library.json.math.FormulaLoadable;
import net.ryancave282.tconstruct.library.json.math.ModifierFormula;
import net.ryancave282.tconstruct.library.json.math.ModifierFormula.FallbackFormula;
import net.ryancave282.tconstruct.library.json.predicate.TinkerPredicate;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.modifiers.ModifierHooks;
import net.ryancave282.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import net.ryancave282.tconstruct.library.modifiers.modules.ModifierModule;
import net.ryancave282.tconstruct.library.modifiers.modules.util.ModifierCondition;
import net.ryancave282.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import net.ryancave282.tconstruct.library.module.HookProvider;
import net.ryancave282.tconstruct.library.module.ModuleHook;
import net.ryancave282.tconstruct.library.tools.context.ToolAttackContext;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

/**
 * Module to add knockback to a melee attack
 * @param entity     Filter on entities to receive knockback
 * @param formula    Formula to compute the knockback amount
 */
public record KnockbackModule(IJsonPredicate<LivingEntity> entity, ModifierFormula formula, ModifierCondition<IToolStackView> condition) implements MeleeHitModifierHook, ModifierModule, ConditionalModule<IToolStackView> {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<KnockbackModule>defaultHooks(ModifierHooks.MELEE_HIT);
  /** Setup for the formula */
  private static final FormulaLoadable FORMULA = new FormulaLoadable(FallbackFormula.ADD, "level", "knockback");
  /** Loader instance */
  public static final RecordLoadable<KnockbackModule> LOADER = RecordLoadable.create(
    LivingEntityPredicate.LOADER.defaultField("entity", KnockbackModule::entity),
    FORMULA.directField(KnockbackModule::formula),
    ModifierCondition.TOOL_FIELD,
    KnockbackModule::new);

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
    if (this.condition.matches(tool, modifier)) {
      // might want to consider an entity predicate here, this special casing is a bit odd
      if (TinkerPredicate.matches(entity, context.getLivingTarget())) {
        return formula.apply(formula.processLevel(modifier), knockback);
      }
    }
    return knockback;
  }


  @Override
  public RecordLoadable<KnockbackModule> getLoader() {
    return LOADER;
  }


  /* Builder */

  /** Creates a builder instance */
  public static Builder builder() {
    return new Builder();
  }

  /** Builder class */
  public static class Builder extends ModifierFormula.Builder<Builder,KnockbackModule> {
    @Setter
    @Accessors(fluent = true)
    private IJsonPredicate<LivingEntity> entity = LivingEntityPredicate.ANY;

    private Builder() {
      super(FORMULA.variables());
    }

    @Override
    protected KnockbackModule build(ModifierFormula formula) {
      return new KnockbackModule(entity, formula, condition);
    }
  }
}
