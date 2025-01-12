package net.ryancave282.tconstruct.library.tools.definition.module.build;

import slimeknights.mantle.data.loadable.record.RecordLoadable;
import net.ryancave282.tconstruct.library.module.ModuleHook;
import net.ryancave282.tconstruct.library.module.HookProvider;
import net.ryancave282.tconstruct.library.tools.definition.module.ToolHooks;
import net.ryancave282.tconstruct.library.tools.definition.module.ToolModule;
import net.ryancave282.tconstruct.library.tools.nbt.IToolContext;
import net.ryancave282.tconstruct.library.tools.nbt.MultiplierNBT;
import net.ryancave282.tconstruct.library.tools.stat.INumericToolStat;
import net.ryancave282.tconstruct.library.tools.stat.ModifierStatsBuilder;

import java.util.List;

/** Module to set global multipliers on the tool */
public record MultiplyStatsModule(MultiplierNBT multipliers) implements ToolStatsHook, ToolModule {
  public static final RecordLoadable<MultiplyStatsModule> LOADER = RecordLoadable.create(MultiplierNBT.LOADABLE.requiredField("multipliers", MultiplyStatsModule::multipliers), MultiplyStatsModule::new);
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<MultiplyStatsModule>defaultHooks(ToolHooks.TOOL_STATS);

  @Override
  public RecordLoadable<MultiplyStatsModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public void addToolStats(IToolContext context, ModifierStatsBuilder builder) {
    for (INumericToolStat<?> stat : multipliers.getContainedStats()) {
      stat.multiplyAll(builder, multipliers.get(stat));
    }
  }
}
