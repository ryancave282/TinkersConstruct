package net.ryancave282.tconstruct.library.tools.definition.module.material;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import net.ryancave282.tconstruct.library.json.field.OptionallyNestedLoadable;
import net.ryancave282.tconstruct.library.materials.IMaterialRegistry;
import net.ryancave282.tconstruct.library.materials.MaterialRegistry;
import net.ryancave282.tconstruct.library.materials.definition.MaterialId;
import net.ryancave282.tconstruct.library.materials.stats.IMaterialStats;
import net.ryancave282.tconstruct.library.materials.stats.MaterialStatsId;
import net.ryancave282.tconstruct.library.module.HookProvider;
import net.ryancave282.tconstruct.library.module.ModuleHook;
import net.ryancave282.tconstruct.library.module.ModuleHookMap;
import net.ryancave282.tconstruct.library.tools.definition.ToolDefinition;
import net.ryancave282.tconstruct.library.tools.definition.module.ToolHooks;
import net.ryancave282.tconstruct.library.tools.definition.module.ToolModule;
import net.ryancave282.tconstruct.library.tools.definition.module.build.ToolStatsHook;
import net.ryancave282.tconstruct.library.tools.definition.module.build.ToolTraitHook;
import net.ryancave282.tconstruct.library.tools.helper.ModifierBuilder;
import net.ryancave282.tconstruct.library.tools.nbt.IToolContext;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;
import net.ryancave282.tconstruct.library.tools.nbt.MaterialNBT;
import net.ryancave282.tconstruct.library.tools.stat.ModifierStatsBuilder;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

/** Module for building tool stats using materials */
public class MaterialStatsModule implements ToolStatsHook, ToolTraitHook, ToolMaterialHook, MaterialRepairToolHook, ToolModule {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<MaterialStatsModule>defaultHooks(ToolHooks.TOOL_STATS, ToolHooks.TOOL_TRAITS, ToolHooks.TOOL_MATERIALS, ToolHooks.MATERIAL_REPAIR);
  protected static final LoadableField<Integer,MaterialStatsModule> PRIMARY_PART_FIELD = IntLoadable.FROM_MINUS_ONE.defaultField("primary_part", 0, true, m -> m.primaryPart);
  public static final RecordLoadable<MaterialStatsModule> LOADER = RecordLoadable.create(
    new OptionallyNestedLoadable<>(MaterialStatsId.PARSER, "stat").list().requiredField("stat_types", m -> m.statTypes),
    new StatScaleField("stat", "stat_types"),
    PRIMARY_PART_FIELD,
    MaterialStatsModule::new);

  private final List<MaterialStatsId> statTypes;
  @Getter @VisibleForTesting
  final float[] scales;
  private int[] repairIndices;
  private final int primaryPart;

  protected MaterialStatsModule(List<MaterialStatsId> statTypes, float[] scales, int primaryPart) {
    this.statTypes = statTypes;
    this.scales = scales;
    this.primaryPart = primaryPart;
  }

  @Override
  public RecordLoadable<? extends MaterialStatsModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public void addModules(ModuleHookMap.Builder builder) {
    // automatically add the primary part if not disabled
    if (primaryPart >= 0 && primaryPart < statTypes.size()) {
      builder.addHook(new MaterialTraitsModule(statTypes.get(primaryPart), primaryPart), ToolHooks.REBALANCED_TRAIT);
    }
  }

  @Override
  public List<MaterialStatsId> getStatTypes(ToolDefinition definition) {
    return statTypes;
  }

  /** Gets the repair indices, calculating them if needed */
  private int[] getRepairIndices() {
    if (repairIndices == null) {
      IMaterialRegistry registry = MaterialRegistry.getInstance();
      repairIndices = IntStream.range(0, statTypes.size()).filter(i -> registry.canRepair(statTypes.get(i))).toArray();
    }
    return repairIndices;
  }

  @Override
  public boolean isRepairMaterial(IToolStackView tool, MaterialId material) {
    for (int part : getRepairIndices()) {
      if (tool.getMaterial(part).matches(material)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public float getRepairAmount(IToolStackView tool, MaterialId material) {
    ResourceLocation toolId = tool.getDefinition().getId();
    for (int i : getRepairIndices()) {
      if (tool.getMaterial(i).matches(material)) {
        return MaterialRepairModule.getDurability(toolId, material, statTypes.get(i));
      }
    }
    return 0;
  }

  @Override
  public void addToolStats(IToolContext context, ModifierStatsBuilder builder) {
    MaterialNBT materials = context.getMaterials();
    if (materials.size() > 0) {
      IMaterialRegistry registry = MaterialRegistry.getInstance();
      for (int i = 0; i < statTypes.size(); i++) {
        MaterialStatsId statType = statTypes.get(i);
        // apply the stats if they exist for the material
        Optional<IMaterialStats> stats = registry.getMaterialStats(materials.get(i).getId(), statType);
        if (stats.isPresent()) {
          stats.get().apply(builder, scales[i]);
        } else {
          // fallback to the default stats if present
          IMaterialStats defaultStats = registry.getDefaultStats(statType);
          if (defaultStats != null) {
            defaultStats.apply(builder, scales[i]);
          }
        }
      }
    }
  }

  @Override
  public void addTraits(ToolDefinition definition, MaterialNBT materials, ModifierBuilder builder) {
    int max = Math.min(materials.size(), statTypes.size());
    if (max > 0) {
      IMaterialRegistry materialRegistry = MaterialRegistry.getInstance();
      for (int i = 0; i < max; i++) {
        builder.add(materialRegistry.getTraits(materials.get(i).getId(), statTypes.get(i)));
      }
    }
  }


  /* Builder */

  /** Creates a new builder instance */
  public static Builder stats() {
    return new Builder();
  }

  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder {
    private final ImmutableList.Builder<MaterialStatsId> stats = ImmutableList.builder();
    private final ImmutableList.Builder<Float> scales = ImmutableList.builder();
    @Setter @Accessors(fluent = true)
    private int primaryPart = 0;

    /** Adds a stat type */
    public Builder stat(MaterialStatsId stat, float scale) {
      stats.add(stat);
      scales.add(scale);
      return this;
    }

    /** Adds a stat type */
    public Builder stat(MaterialStatsId stat) {
      return stat(stat, 1);
    }

    /** Builds the array of scales from the list */
    static float[] buildScales(List<Float> list) {
      float[] scales = new float[list.size()];
      for (int i = 0; i < list.size(); i++) {
        scales[i] = list.get(i);
      }
      return scales;
    }

    /** Builds the module */
    public MaterialStatsModule build() {
      List<MaterialStatsId> stats = this.stats.build();
      if (primaryPart >= stats.size() || primaryPart < -1) {
        throw new IllegalStateException("Primary part must be within parts list, maximum " + stats.size() + ", got " + primaryPart);
      }
      return new MaterialStatsModule(stats, buildScales(scales.build()), primaryPart);
    }
  }
}
