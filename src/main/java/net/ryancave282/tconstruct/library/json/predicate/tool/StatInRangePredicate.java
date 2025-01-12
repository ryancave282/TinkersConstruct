package net.ryancave282.tconstruct.library.json.predicate.tool;

import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;
import net.ryancave282.tconstruct.library.tools.nbt.StatsNBT;
import net.ryancave282.tconstruct.library.tools.stat.INumericToolStat;
import net.ryancave282.tconstruct.library.tools.stat.ToolStats;

import java.util.function.Predicate;

/**
 * Predicate to check if a tool has the given stat within the range.
 * @see StatInSetPredicate
 */
public record StatInRangePredicate(INumericToolStat<?> stat, float min, float max) implements Predicate<StatsNBT>, ToolStackPredicate {
  public static final RecordLoadable<StatInRangePredicate> LOADER = RecordLoadable.create(
    ToolStats.NUMERIC_LOADER.requiredField("stat", StatInRangePredicate::stat),
    FloatLoadable.ANY.defaultField("min", Float.NEGATIVE_INFINITY, StatInRangePredicate::min),
    FloatLoadable.ANY.defaultField("max", Float.POSITIVE_INFINITY, StatInRangePredicate::max),
    StatInRangePredicate::new);

  /**
   * Creates a predicate matching the exact value
   * @param stat  Stat
   * @param value Value to match
   * @return Predicate
   */
  public static StatInRangePredicate match(INumericToolStat<?> stat, float value) {
    return new StatInRangePredicate(stat, value, value);
  }

  /**
   * Creates a predicate matching the exact value
   * @param stat Stat
   * @param min  Min value
   * @return Predicate
   */
  public static StatInRangePredicate min(INumericToolStat<?> stat, float min) {
    return new StatInRangePredicate(stat, min, Float.POSITIVE_INFINITY);
  }

  /**
   * Creates a predicate matching the exact value
   * @param stat Stat
   * @param max  Max value
   * @return Predicate
   */
  public static StatInRangePredicate max(INumericToolStat<?> stat, float max) {
    return new StatInRangePredicate(stat, Float.NEGATIVE_INFINITY, max);
  }

  @Override
  public boolean test(StatsNBT statsNBT) {
    float value = statsNBT.get(stat).floatValue();
    return value >= min && value <= max;
  }

  @Override
  public boolean matches(IToolStackView tool) {
    return test(tool.getStats());
  }

  @Override
  public RecordLoadable<StatInRangePredicate> getLoader() {
    return LOADER;
  }
}
