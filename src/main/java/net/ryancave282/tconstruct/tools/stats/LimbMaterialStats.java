package net.ryancave282.tconstruct.tools.stats;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.network.chat.Component;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import net.ryancave282.tconstruct.TConstruct;
import net.ryancave282.tconstruct.library.materials.stats.IMaterialStats;
import net.ryancave282.tconstruct.library.materials.stats.IRepairableMaterialStats;
import net.ryancave282.tconstruct.library.materials.stats.MaterialStatType;
import net.ryancave282.tconstruct.library.materials.stats.MaterialStatsId;
import net.ryancave282.tconstruct.library.tools.stat.IToolStat;
import net.ryancave282.tconstruct.library.tools.stat.ModifierStatsBuilder;
import net.ryancave282.tconstruct.library.tools.stat.ToolStats;

import java.util.List;

/** Primary stats for a bow */
public record LimbMaterialStats(int durability, float drawSpeed, float velocity, float accuracy) implements IRepairableMaterialStats {
  public static final MaterialStatsId ID = new MaterialStatsId(TConstruct.getResource("limb"));
  public static final MaterialStatType<LimbMaterialStats> TYPE = new MaterialStatType<>(ID, new LimbMaterialStats(1, 0f, 0f, 0f), RecordLoadable.create(
    IRepairableMaterialStats.DURABILITY_FIELD,
    FloatLoadable.ANY.defaultField("draw_speed", 0f, true, LimbMaterialStats::drawSpeed),
    FloatLoadable.ANY.defaultField("velocity", 0f, true, LimbMaterialStats::velocity),
    FloatLoadable.ANY.defaultField("accuracy", 0f, true, LimbMaterialStats::accuracy),
    LimbMaterialStats::new));

  static final String ACCURACY_PREFIX = IMaterialStats.makeTooltipKey(TConstruct.getResource("accuracy"));
  static final String DRAW_SPEED_PREFIX = IMaterialStats.makeTooltipKey(TConstruct.getResource("draw_speed"));
  static final String VELOCITY_PREFIX = IMaterialStats.makeTooltipKey(TConstruct.getResource("velocity"));
  // tooltip descriptions
  private static final List<Component> DESCRIPTION = ImmutableList.of(ToolStats.DURABILITY.getDescription(), ToolStats.DRAW_SPEED.getDescription(), ToolStats.VELOCITY.getDescription(), ToolStats.ATTACK_DAMAGE.getDescription());

  @Override
  public MaterialStatType<?> getType() {
    return TYPE;
  }

  @Override
  public List<Component> getLocalizedInfo() {
    List<Component> info = Lists.newArrayList();
    info.add(ToolStats.DURABILITY.formatValue(this.durability));
    info.add(IToolStat.formatColoredBonus(DRAW_SPEED_PREFIX, this.drawSpeed));
    info.add(IToolStat.formatColoredBonus(VELOCITY_PREFIX, this.velocity));
    info.add(IToolStat.formatColoredBonus(ACCURACY_PREFIX, this.accuracy));
    return info;
  }

  @Override
  public List<Component> getLocalizedDescriptions() {
    return DESCRIPTION;
  }

  @Override
  public void apply(ModifierStatsBuilder builder, float scale) {
    ToolStats.DURABILITY.update(builder, durability * scale);
    ToolStats.DRAW_SPEED.add(builder, drawSpeed * scale);
    ToolStats.VELOCITY.add(builder, velocity * scale);
    ToolStats.ACCURACY.add(builder, accuracy * scale);
  }
}
