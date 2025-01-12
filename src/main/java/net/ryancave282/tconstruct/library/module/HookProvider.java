package net.ryancave282.tconstruct.library.module;

import java.util.List;

/** Interface to simplify building of modifier hook maps */
public interface HookProvider {
  /** Gets the default list of hooks this module implements. */
  List<ModuleHook<?>> getDefaultHooks();

  /** Adds additional modules to the builder alongside this module */
  default void addModules(ModuleHookMap.Builder builder) {}

  /**
   * Helper method to validate generics on the hooks when building a default hooks list. To use, make sure you set the generics instead of leaving it automatic.
   */
  @SafeVarargs
  static <T> List<ModuleHook<?>> defaultHooks(ModuleHook<? super T>... hooks) {
    return List.of(hooks);
  }
}
