package net.ryancave282.tconstruct.test;

import net.minecraft.world.item.Items;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.tools.definition.ToolDefinition;
import net.ryancave282.tconstruct.library.tools.definition.ToolDefinitionData;
import net.ryancave282.tconstruct.library.tools.definition.module.ToolHooks;
import net.ryancave282.tconstruct.library.tools.nbt.DummyToolStack;
import net.ryancave282.tconstruct.library.tools.nbt.MaterialNBT;
import net.ryancave282.tconstruct.library.tools.nbt.ModDataNBT;
import net.ryancave282.tconstruct.library.tools.nbt.ModifierNBT;
import net.ryancave282.tconstruct.library.tools.nbt.MultiplierNBT;
import net.ryancave282.tconstruct.library.tools.nbt.StatsNBT;
import net.ryancave282.tconstruct.library.tools.stat.INumericToolStat;
import net.ryancave282.tconstruct.library.tools.stat.ModifierStatsBuilder;

import java.util.List;

/** Helpers for running tests */
public class TestHelper {
  private TestHelper() {}

  /** Helper to fetch traits from the trait hook */
  public static List<ModifierEntry> getTraits(ToolDefinitionData data) {
    ModifierNBT.Builder builder = ModifierNBT.builder();
    data.getHook(ToolHooks.TOOL_TRAITS).addTraits(ToolDefinition.EMPTY, MaterialNBT.EMPTY, builder);
    return builder.build().getModifiers();
  }

  public record ToolDefinitionStats(StatsNBT base, MultiplierNBT multipliers) {}

  /** Computes the stats for the given tool */
  public static ToolDefinitionStats buildStats(ToolDefinitionData data) {
    ModifierStatsBuilder builder = ModifierStatsBuilder.builder();
    data.getHook(ToolHooks.TOOL_STATS).addToolStats(new DummyToolStack(Items.AIR, ModifierNBT.EMPTY, new ModDataNBT()), builder);
    MultiplierNBT multipliers = builder.buildMultipliers();
    // cancel out multipliers on the base stats, as people expect base stats to be comparable to be usable in the modifier stats builder
    for (INumericToolStat<?> stat : multipliers.getContainedStats()) {
      stat.multiply(builder, 1 / multipliers.get(stat));
    }
    return new ToolDefinitionStats(builder.build(), multipliers);
  }
}
