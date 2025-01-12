package net.ryancave282.tconstruct.library.tools.definition.module;

import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.registry.IdAwareComponentRegistry;
import net.ryancave282.tconstruct.TConstruct;
import net.ryancave282.tconstruct.library.materials.MaterialRegistry;
import net.ryancave282.tconstruct.library.materials.definition.MaterialId;
import net.ryancave282.tconstruct.library.materials.stats.MaterialStatsId;
import net.ryancave282.tconstruct.library.module.ModuleHook;
import net.ryancave282.tconstruct.library.tools.definition.module.aoe.AreaOfEffectIterator;
import net.ryancave282.tconstruct.library.tools.definition.module.build.ToolActionToolHook;
import net.ryancave282.tconstruct.library.tools.definition.module.build.ToolStatsHook;
import net.ryancave282.tconstruct.library.tools.definition.module.build.ToolTraitHook;
import net.ryancave282.tconstruct.library.tools.definition.module.build.VolatileDataToolHook;
import net.ryancave282.tconstruct.library.tools.definition.module.interaction.InteractionToolModule;
import net.ryancave282.tconstruct.library.tools.definition.module.material.MaterialRepairToolHook;
import net.ryancave282.tconstruct.library.tools.definition.module.material.MaterialRepairToolHook.MaxMerger;
import net.ryancave282.tconstruct.library.tools.definition.module.material.MissingMaterialsToolHook;
import net.ryancave282.tconstruct.library.tools.definition.module.material.ToolMaterialHook;
import net.ryancave282.tconstruct.library.tools.definition.module.material.ToolPartsHook;
import net.ryancave282.tconstruct.library.tools.definition.module.mining.IsEffectiveToolHook;
import net.ryancave282.tconstruct.library.tools.definition.module.mining.MiningSpeedToolHook;
import net.ryancave282.tconstruct.library.tools.definition.module.mining.MiningTierToolHook;
import net.ryancave282.tconstruct.library.tools.definition.module.weapon.MeleeHitToolHook;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;
import net.ryancave282.tconstruct.library.tools.nbt.MaterialNBT;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/** Modules for tool definition data */
public class ToolHooks {
  private ToolHooks() {}

  /** Loader for tool hooks */
  public static final IdAwareComponentRegistry<ModuleHook<?>> LOADER = new IdAwareComponentRegistry<>("Unknown Tool Hook");

  public static void init() {}


  /* Build */
  /** Hook for getting the material requirements for a tool. */
  public static final ModuleHook<ToolMaterialHook> TOOL_MATERIALS = register("tool_materials", ToolMaterialHook.class, definition -> List.of());
  /** Hook for getting a list of tool parts on a tool. */
  public static final ModuleHook<ToolPartsHook> TOOL_PARTS = register("tool_parts", ToolPartsHook.class, definition -> List.of());
  /** Hook for filling materials on a tool with no materials set */
  public static final ModuleHook<MissingMaterialsToolHook> MISSING_MATERIALS = register("missing_materials", MissingMaterialsToolHook.class, ((definition, random) -> {
    MaterialNBT.Builder builder = MaterialNBT.builder();
    for (MaterialStatsId statType : ToolMaterialHook.stats(definition)) {
      builder.add(MaterialRegistry.firstWithStatType(statType));
    }
    return builder.build();
  }));

  /** Hook for repairing a tool using a material. */
  public static final ModuleHook<MaterialRepairToolHook> MATERIAL_REPAIR = register("material_repair", MaterialRepairToolHook.class, MaxMerger::new, new MaterialRepairToolHook() {
    @Override
    public boolean isRepairMaterial(IToolStackView tool, MaterialId material) {
      return false;
    }

    @Override
    public float getRepairAmount(IToolStackView tool, MaterialId material) {
      return 0;
    }
  });

  /** Hook for adding raw unconditional stats to a tool */
  public static final ModuleHook<ToolStatsHook> TOOL_STATS = register("tool_stats", ToolStatsHook.class, ToolStatsHook.AllMerger::new, (context, builder) -> {});
  /** Hook for checking if a tool can perform a given action. */
  public static final ModuleHook<VolatileDataToolHook> VOLATILE_DATA = register("volatile_data", VolatileDataToolHook.class, VolatileDataToolHook.AllMerger::new, (context, data) -> {});
  /** Hook for fetching tool traits */
  public static final ModuleHook<ToolTraitHook> TOOL_TRAITS;
  /** Hook for fetching traits for the rebalanced modifier */
  public static final ModuleHook<ToolTraitHook> REBALANCED_TRAIT;
  static {
    Function<Collection<ToolTraitHook>,ToolTraitHook> merger = ToolTraitHook.AllMerger::new;
    ToolTraitHook defaultInstance = (definition, materials, builder) -> {};
    TOOL_TRAITS = register("tool_traits", ToolTraitHook.class, merger, defaultInstance);
    REBALANCED_TRAIT = register("rebalanced_trait", ToolTraitHook.class, merger, defaultInstance);
  }
  /** Hook for checking if a tool can perform a given action. */
  public static final ModuleHook<ToolActionToolHook> TOOL_ACTION = register("tool_actions", ToolActionToolHook.class, ToolActionToolHook.AnyMerger::new, (tool, action) -> false);


  /* Mining */
  /** Hook for checking if a tool is effective against the given block */
  public static final ModuleHook<IsEffectiveToolHook> IS_EFFECTIVE = register("is_effective", IsEffectiveToolHook.class, (tool, state) -> false);
  /** Hook for modifying the tier from the stat */
  public static final ModuleHook<MiningTierToolHook> MINING_TIER = register("mining_tier", MiningTierToolHook.class, MiningTierToolHook.ComposeMerger::new, (tool, tier) -> tier);
  /** Hook for modifying the mining speed from the stat/effectiveness */
  public static final ModuleHook<MiningSpeedToolHook> MINING_SPEED = register("mining_speed_modifier", MiningSpeedToolHook.class, MiningSpeedToolHook.ComposeMerger::new, (tool, state, speed) -> speed);
  /** Logic for finding AOE blocks */
  public static final ModuleHook<AreaOfEffectIterator> AOE_ITERATOR = register("aoe_iterator", AreaOfEffectIterator.class, (tool, stack, player, state, world, origin, sideHit, match) -> Collections.emptyList());


  /* Weapon */
  /** Hook that runs after a melee hit to apply extra effects. */
  public static final ModuleHook<MeleeHitToolHook> MELEE_HIT = register("after_melee_hit", MeleeHitToolHook.class, MeleeHitToolHook.AllMerger::new, (tool, context, damage) -> {});


  /** Hook for configuring interaction behaviors on the tool */
  public static final ModuleHook<InteractionToolModule> INTERACTION = register("tool_interaction", InteractionToolModule.class, (t, m, s) -> true);


  /* Registration */

  /** Registers a new tool hook that merges */
  public static <T> ModuleHook<T> register(ResourceLocation name, Class<T> filter, @Nullable Function<Collection<T>,T> merger, T defaultInstance) {
    return LOADER.register(new ModuleHook<>(name, filter, merger, defaultInstance));
  }

  /** Registers a new tool hook that does not merge */
  public static <T> ModuleHook<T> register(ResourceLocation name, Class<T> filter, T defaultInstance) {
    return register(name, filter, null, defaultInstance);
  }

  /** Registers a new tool hook under {@code tconstruct} that merges */
  private static <T> ModuleHook<T> register(String name, Class<T> filter, @Nullable Function<Collection<T>,T> merger, T defaultInstance) {
    return register(TConstruct.getResource(name), filter, merger, defaultInstance);
  }

  /** Registers a new tool hook under {@code tconstruct} that cannot merge */
  private static <T> ModuleHook<T> register(String name, Class<T> filter, T defaultInstance) {
    return register(name, filter, null, defaultInstance);
  }
}
