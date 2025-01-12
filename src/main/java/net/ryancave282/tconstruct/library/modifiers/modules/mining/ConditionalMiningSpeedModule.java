package net.ryancave282.tconstruct.library.modifiers.modules.mining;

import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.block.BlockPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import net.ryancave282.tconstruct.library.json.math.ModifierFormula;
import net.ryancave282.tconstruct.library.json.variable.VariableFormula;
import net.ryancave282.tconstruct.library.json.variable.mining.MiningSpeedFormula;
import net.ryancave282.tconstruct.library.json.variable.mining.MiningSpeedVariable;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.modifiers.ModifierHooks;
import net.ryancave282.tconstruct.library.modifiers.hook.mining.BreakSpeedModifierHook;
import net.ryancave282.tconstruct.library.modifiers.modules.ModifierModule;
import net.ryancave282.tconstruct.library.modifiers.modules.util.ConditionalStatTooltip;
import net.ryancave282.tconstruct.library.modifiers.modules.util.ModifierCondition;
import net.ryancave282.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import net.ryancave282.tconstruct.library.module.HookProvider;
import net.ryancave282.tconstruct.library.module.ModuleHook;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;
import net.ryancave282.tconstruct.library.tools.stat.INumericToolStat;
import net.ryancave282.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Implementation of attack damage conditioned on the attacker or target's properties
 * @param block      Blocks to boost speed
 * @param holder     Condition on the entity holding this tool
 * @param formula    Damage formula
 * @param condition  Standard modifier conditions
 */
public record ConditionalMiningSpeedModule(
  IJsonPredicate<BlockState> block, IJsonPredicate<LivingEntity> holder, boolean requireEffective,
  MiningSpeedFormula formula, ModifierCondition<IToolStackView> condition
) implements BreakSpeedModifierHook, ConditionalStatTooltip, ModifierModule, ConditionalModule<IToolStackView> {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<ConditionalMiningSpeedModule>defaultHooks(ModifierHooks.BREAK_SPEED, ModifierHooks.TOOLTIP);
  public static final RecordLoadable<ConditionalMiningSpeedModule> LOADER = RecordLoadable.create(
    BlockPredicate.LOADER.defaultField("blocks", ConditionalMiningSpeedModule::block),
    LivingEntityPredicate.LOADER.defaultField("entity", ConditionalMiningSpeedModule::holder),
    BooleanLoadable.INSTANCE.defaultField("require_effective", true, ConditionalMiningSpeedModule::requireEffective),
    MiningSpeedFormula.LOADER.directField(ConditionalMiningSpeedModule::formula),
    ModifierCondition.TOOL_FIELD,
    ConditionalMiningSpeedModule::new);

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
  public void onBreakSpeed(IToolStackView tool, ModifierEntry modifier, BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) {
    Player player = event.getEntity();
    if ((isEffective || !requireEffective) && condition.matches(tool, modifier) && block.matches(event.getState()) && holder.matches(player)) {
      event.setNewSpeed(formula.apply(tool, modifier, event, player, sideHit, event.getOriginalSpeed(), event.getNewSpeed(), miningSpeedModifier));
    }
  }

  @Override
  public INumericToolStat<?> stat() {
    return ToolStats.MINING_SPEED;
  }

  @Override
  public float computeTooltipValue(IToolStackView tool, ModifierEntry entry, @Nullable Player player) {
    return formula.apply(tool, entry, null, player, null, 1, 1, 1);
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public RecordLoadable<ConditionalMiningSpeedModule> getLoader() {
    return LOADER;
  }


  /* Builder */

  /** Creates a builder instance */
  public static Builder builder() {
    return new Builder();
  }

  /** Builder class */
  @Accessors(fluent = true)
  public static class Builder extends VariableFormula.Builder<Builder,ConditionalMiningSpeedModule,MiningSpeedVariable> {
    @Setter
    private IJsonPredicate<BlockState> blocks = BlockPredicate.ANY;
    @Setter
    private IJsonPredicate<LivingEntity> holder = LivingEntityPredicate.ANY;
    private boolean requireEffective = true;

    private Builder() {
      super(MiningSpeedFormula.VARIABLES);
    }

    /** Sets this to a percent boost formula */
    public Builder allowIneffective() {
      this.requireEffective = false;
      return this;
    }

    @Override
    protected ConditionalMiningSpeedModule build(ModifierFormula formula) {
      return new ConditionalMiningSpeedModule(blocks, holder, requireEffective, new MiningSpeedFormula(formula, variables, percent), condition);
    }
  }
}
