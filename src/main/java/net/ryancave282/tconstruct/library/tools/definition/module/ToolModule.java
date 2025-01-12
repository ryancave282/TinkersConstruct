package net.ryancave282.tconstruct.library.tools.definition.module;

import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.GenericLoaderRegistry;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IHaveLoader;
import net.ryancave282.tconstruct.library.module.HookProvider;
import net.ryancave282.tconstruct.library.module.WithHooks;

/**
 * Base interface for modules within the tool definition data
 */
public interface ToolModule extends IHaveLoader, HookProvider {
  /** Loader instance for any modules loadable in tools */
  GenericLoaderRegistry<ToolModule> LOADER = new GenericLoaderRegistry<>("Tool Module", false);
  /** Loadable for modules including hooks */
  RecordLoadable<WithHooks<ToolModule>> WITH_HOOKS = WithHooks.makeLoadable(LOADER, ToolHooks.LOADER);

}
