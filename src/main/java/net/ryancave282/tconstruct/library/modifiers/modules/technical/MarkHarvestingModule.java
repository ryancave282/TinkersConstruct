package net.ryancave282.tconstruct.library.modifiers.modules.technical;

import net.ryancave282.tconstruct.library.modifiers.ModifierHooks;
import net.ryancave282.tconstruct.library.modifiers.hook.mining.BlockHarvestModifierHook;
import net.ryancave282.tconstruct.library.module.HookProvider;
import net.ryancave282.tconstruct.library.module.ModuleHook;

import java.util.List;

/** Simple module with hooks form of {@link BlockHarvestModifierHook.MarkHarvesting}. */
public enum MarkHarvestingModule implements BlockHarvestModifierHook.MarkHarvesting, HookProvider {
  INSTANCE;

  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<MarkHarvestingModule>defaultHooks(ModifierHooks.BLOCK_HARVEST);

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }
}
