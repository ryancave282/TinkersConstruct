package net.ryancave282.tconstruct.library.tools.capability.inventory;

import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.tools.capability.CompoundIndexHookIterator;
import net.ryancave282.tconstruct.library.tools.capability.fluid.ToolFluidCapability;
import net.ryancave282.tconstruct.library.tools.capability.inventory.ToolInventoryCapability.InventoryModifierHook;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;

/**
 * Shared logic to iterate fluid capabilities for {@link ToolFluidCapability}
 */
abstract class InventoryModifierHookIterator<I> extends CompoundIndexHookIterator<InventoryModifierHook,I> {
  /** Entry from {@link #findHook(IToolStackView, int)}, will be set during or before iteration */
  protected ModifierEntry indexEntry = null;

  @Override
  protected int getSize(IToolStackView tool, InventoryModifierHook hook) {
    return hook.getSlots(tool, indexEntry);
  }
}
