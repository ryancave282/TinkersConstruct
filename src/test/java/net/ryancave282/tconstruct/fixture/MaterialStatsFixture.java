package net.ryancave282.tconstruct.fixture;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.item.Tiers;
import net.ryancave282.tconstruct.library.materials.stats.ComplexTestStats;
import net.ryancave282.tconstruct.library.materials.stats.IMaterialStats;
import net.ryancave282.tconstruct.library.materials.stats.MaterialStatType;
import net.ryancave282.tconstruct.library.materials.stats.MaterialStatsId;
import net.ryancave282.tconstruct.tools.stats.HandleMaterialStats;
import net.ryancave282.tconstruct.tools.stats.HeadMaterialStats;
import net.ryancave282.tconstruct.tools.stats.StatlessMaterialStats;

import java.util.List;

public final class MaterialStatsFixture {

  public static final MaterialStatsId STATS_TYPE = new MaterialStatsId("test", "mat_stat_1");
  public static final MaterialStatsId STATS_TYPE_2 = new MaterialStatsId("test", "mat_stat_2");
  public static final MaterialStatsId STATS_TYPE_3 = new MaterialStatsId("test", "mat_stat_3");
  public static final MaterialStatsId STATS_TYPE_4 = new MaterialStatsId("test", "mat_stat_4");

  public static final MaterialStatType<ComplexTestStats> COMPLEX_TYPE = ComplexTestStats.makeType(STATS_TYPE);

  public static final ComplexTestStats MATERIAL_STATS = new ComplexTestStats(COMPLEX_TYPE, 1, 2, "3");
  public static final ComplexTestStats MATERIAL_STATS_2 = new ComplexTestStats(ComplexTestStats.makeType(STATS_TYPE_2), 4, 5, "6");

  public static final HeadMaterialStats MATERIAL_STATS_HEAD = new HeadMaterialStats(100, 1f, Tiers.GOLD, 1f);
  public static final HandleMaterialStats MATERIAL_STATS_HANDLE = new HandleMaterialStats(0.5f, 0f, 0f, 0f);
  public static final StatlessMaterialStats MATERIAL_STATS_EXTRA = StatlessMaterialStats.BINDING;

  public static final List<IMaterialStats> TIC_DEFAULT_STATS = ImmutableList.of(
      HeadMaterialStats.TYPE.getDefaultStats(),
      HandleMaterialStats.TYPE.getDefaultStats(),
      StatlessMaterialStats.BINDING
  );

  private MaterialStatsFixture() {
  }
}
