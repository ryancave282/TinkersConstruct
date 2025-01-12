package net.ryancave282.tconstruct.library.tools.definition.module.build;

import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import net.ryancave282.tconstruct.library.module.HookProvider;
import net.ryancave282.tconstruct.library.module.ModuleHook;
import net.ryancave282.tconstruct.library.tools.definition.module.ToolHooks;
import net.ryancave282.tconstruct.library.tools.definition.module.ToolModule;
import net.ryancave282.tconstruct.library.tools.nbt.IToolContext;
import net.ryancave282.tconstruct.library.tools.nbt.ToolDataNBT;

import java.util.List;

/**
 * Module that just sets a boolean flag to true on a tool.
 * @see net.ryancave282.tconstruct.library.modifiers.modules.build.VolatileFlagModule
 */
public record VolatileFlagModule(ResourceLocation flag) implements ToolModule, VolatileDataToolHook {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<VolatileFlagModule>defaultHooks(ToolHooks.VOLATILE_DATA);
  public static final RecordLoadable<VolatileFlagModule> LOADER = RecordLoadable.create(Loadables.RESOURCE_LOCATION.requiredField("flag", VolatileFlagModule::flag), VolatileFlagModule::new);

  @Override
  public RecordLoadable<VolatileFlagModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public void addVolatileData(IToolContext context, ToolDataNBT volatileData) {
    volatileData.putBoolean(flag, true);
  }
}
