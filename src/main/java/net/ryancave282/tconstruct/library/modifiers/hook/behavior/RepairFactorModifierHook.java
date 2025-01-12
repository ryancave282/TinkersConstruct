package net.ryancave282.tconstruct.library.modifiers.hook.behavior;

import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

/** Hook called when repairing a tool to allow modifiers to adjust repair amount */
public interface RepairFactorModifierHook {
  /**
   * Called when the tool is repair. Can be used to decrease, increase, or cancel the repair.
   * TODO: cleanup to always pass in durability instead of multipliers
   * @param tool       Tool stack
   * @param entry      Modifier and level
   * @param factor     Original factor
   * @return  Replacement factor. Returning 0 prevents repair
   */
  float getRepairFactor(IToolStackView tool, ModifierEntry entry, float factor);

  /** Merger that composes all submodules */
  record ComposeMerger(Collection<RepairFactorModifierHook> modules) implements RepairFactorModifierHook {
    @Override
    public float getRepairFactor(IToolStackView tool, ModifierEntry entry, float factor) {
      for (RepairFactorModifierHook module : modules) {
        factor = module.getRepairFactor(tool, entry, factor);
      }
      return factor;
    }
  }
}
