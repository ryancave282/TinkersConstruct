package net.ryancave282.tconstruct.library.tools.stat;

import net.minecraft.world.item.Tiers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import net.ryancave282.tconstruct.library.tools.nbt.StatsNBT;
import net.ryancave282.tconstruct.test.BaseMcTest;

import static org.assertj.core.api.Assertions.assertThat;

class ToolStatsModifierBuilderTest extends BaseMcTest {
  private final StatsNBT testStatsNBT = StatsNBT.builder()
                                                .set(ToolStats.DURABILITY, 100f)
                                                .set(ToolStats.HARVEST_TIER, Tiers.STONE)
                                                .set(ToolStats.ATTACK_DAMAGE, 2f)
                                                .set(ToolStats.MINING_SPEED, 3f)
                                                .set(ToolStats.ATTACK_SPEED, 5f)
                                                .build();

  @BeforeAll
  static void setup() {
    setupTierSorting();
  }

  @Test
  void empty() {
    ModifierStatsBuilder builder = ModifierStatsBuilder.builder();
    assertThat(builder.build()).isEqualTo(StatsNBT.EMPTY);
  }


  /* Tier tool stats */

  @Test
  void tierToolStat_defaultStat() {
    ModifierStatsBuilder builder = ModifierStatsBuilder.builder();
    ToolStats.ATTACK_DAMAGE.add(builder, 1);
    StatsNBT nbt = builder.build();
    assertThat(nbt.get(ToolStats.HARVEST_TIER)).isEqualTo(ToolStats.HARVEST_TIER.getDefaultValue());
  }

  @Test
  void tierToolStat_set() {
    ModifierStatsBuilder builder = ModifierStatsBuilder.builder();
    ToolStats.HARVEST_TIER.update(builder, Tiers.DIAMOND);
    StatsNBT nbt = builder.build();
    assertThat(nbt.get(ToolStats.HARVEST_TIER)).isEqualTo(Tiers.DIAMOND);
  }

  @Test
  void tierToolStat_largest() {
    ModifierStatsBuilder builder = ModifierStatsBuilder.builder();
    ToolStats.HARVEST_TIER.update(builder, Tiers.IRON);
    ToolStats.HARVEST_TIER.update(builder, Tiers.DIAMOND);
    StatsNBT nbt = builder.build();
    assertThat(nbt.get(ToolStats.HARVEST_TIER)).isEqualTo(Tiers.DIAMOND);

    builder = ModifierStatsBuilder.builder();
    ToolStats.HARVEST_TIER.update(builder, Tiers.NETHERITE);
    ToolStats.HARVEST_TIER.update(builder, Tiers.DIAMOND);
    nbt = builder.build();
    assertThat(nbt.get(ToolStats.HARVEST_TIER)).isEqualTo(Tiers.NETHERITE);
  }


  /* Float stat value */

  @Test
  void floatToolStat_defaultStat() {
    ModifierStatsBuilder builder = ModifierStatsBuilder.builder();
    ToolStats.ATTACK_DAMAGE.add(builder, 1);
    StatsNBT nbt = builder.build();
    assertThat(nbt.get(ToolStats.DURABILITY)).isEqualTo(ToolStats.DURABILITY.getDefaultValue());
  }

  @Test
  void floatToolStat_add() {
    ModifierStatsBuilder builder = ModifierStatsBuilder.builder();
    ToolStats.DURABILITY.add(builder, 10);
    StatsNBT nbt = builder.build();
    assertThat(nbt.getInt(ToolStats.DURABILITY)).isEqualTo(11);
  }

  @Test
  void floatToolStat_addMultiple() {
    ModifierStatsBuilder builder = ModifierStatsBuilder.builder();
    ToolStats.DURABILITY.add(builder, 10);
    ToolStats.DURABILITY.add(builder, 15);
    StatsNBT nbt = builder.build();
    assertThat(nbt.getInt(ToolStats.DURABILITY)).isEqualTo(26);
  }

  @Test
  void floatToolStat_multiply() {
    ModifierStatsBuilder builder = ModifierStatsBuilder.builder();
    ToolStats.DURABILITY.multiply(builder, 2f);
    StatsNBT nbt = builder.build();
    assertThat(nbt.getInt(ToolStats.DURABILITY)).isEqualTo(2);
  }

  @Test
  void floatToolStat_multiplyMultiple() {
    ModifierStatsBuilder builder = ModifierStatsBuilder.builder();
    ToolStats.DURABILITY.multiply(builder, 2f);
    ToolStats.DURABILITY.multiply(builder, 1.5f);
    StatsNBT nbt = builder.build();
    assertThat(nbt.getInt(ToolStats.DURABILITY)).isEqualTo(3);
  }

  @Test
  void durability_order() {
    ModifierStatsBuilder builder = ModifierStatsBuilder.builder();
    ToolStats.DURABILITY.add(builder, 10);
    ToolStats.DURABILITY.multiply(builder, 2f);
    StatsNBT nbt = builder.build();
    assertThat(nbt.getInt(ToolStats.DURABILITY)).isEqualTo(22);

    builder = ModifierStatsBuilder.builder();
    ToolStats.DURABILITY.multiply(builder, 2f);
    ToolStats.DURABILITY.add(builder, 10);
    nbt = builder.build();
    assertThat(nbt.getInt(ToolStats.DURABILITY)).isEqualTo(22);
  }

  @Test
  void floatToolStat_min() {
    ModifierStatsBuilder builder = ModifierStatsBuilder.builder();
    ToolStats.DURABILITY.add(builder, Short.MIN_VALUE);
    StatsNBT nbt = builder.build();
    assertThat(nbt.get(ToolStats.DURABILITY)).isEqualTo(ToolStats.DURABILITY.getMinValue());
  }

  @Test
  void floatToolStat_max() {
    ModifierStatsBuilder builder = ModifierStatsBuilder.builder();
    ToolStats.ATTACK_DAMAGE.add(builder, 4096);
    StatsNBT nbt = builder.build();
    assertThat(nbt.get(ToolStats.ATTACK_DAMAGE)).isEqualTo(ToolStats.ATTACK_DAMAGE.getMaxValue());
  }
}
