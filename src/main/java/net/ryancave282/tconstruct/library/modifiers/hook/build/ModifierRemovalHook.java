package net.ryancave282.tconstruct.library.modifiers.hook.build;

import net.minecraft.network.chat.Component;
import net.ryancave282.tconstruct.library.modifiers.Modifier;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.modifiers.ModifierHooks;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;
import net.ryancave282.tconstruct.library.utils.RestrictedCompoundTag;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Hook called when a modifier is removed to clean up unused data and error if the new state is not valid.
 */
public interface ModifierRemovalHook {
  /**
   * Called after this modifier is removed (and after stats are rebuilt) to clean up persistent data and validate removal.
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link ValidateModifierHook}: Called when the modifier still has levels on the tool</li>
   *   <li>{@link RawDataModifierHook#removeRawData(IToolStackView, Modifier, RestrictedCompoundTag)}: Grants access to the tools raw NBT, but called before tool stats are rebuilt</li>
   *   <li>{@link VolatileDataModifierHook}: Adds NBT that is automatically removed</li>
   * </ul>
   * @param tool      Tool instance
   * @param modifier  Modifier being removed
   * @return  null if the modifier can be removed, text component with error message if there was an error
   */
  @Nullable
  Component onRemoved(IToolStackView tool, Modifier modifier);

  /** Merger that runs all hooks */
  record FirstMerger(Collection<ModifierRemovalHook> modules) implements ModifierRemovalHook {
    @Override
    public Component onRemoved(IToolStackView tool, Modifier modifier) {
      for (ModifierRemovalHook module : modules) {
        Component error = module.onRemoved(tool, modifier);
        if (error != null) {
          return error;
        }
      }
      return null;
    }
  }

  /**
   * Calls the hook for all modifiers that were on the original but not on the updated tool.
   * Handles any modifier update including standard removal, part swapping, and even modifier traits.
   * @param original  Tool before changes were made
   * @param updated   Tool after changes were made
   * @return  Error message if a removed modifier errored, or null if no error
   */
  @Nullable
  static Component onRemoved(IToolStackView original, IToolStackView updated) {
    for (ModifierEntry entry : original.getModifierList()) {
      ModifierRemovalHook hook = entry.getModifier().getHooks().getOrNull(ModifierHooks.REMOVE);
      if (hook != null && updated.getModifierLevel(entry.getId()) == 0) {
        Component error = hook.onRemoved(updated, entry.getModifier());
        if (error != null) {
          return error;
        }
      }
    }
    return null;
  }
}
