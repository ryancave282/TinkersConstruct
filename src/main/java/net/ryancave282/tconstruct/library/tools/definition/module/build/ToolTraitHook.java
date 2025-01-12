package net.ryancave282.tconstruct.library.tools.definition.module.build;

import net.ryancave282.tconstruct.library.tools.definition.ToolDefinition;
import net.ryancave282.tconstruct.library.tools.definition.module.ToolHooks;
import net.ryancave282.tconstruct.library.tools.helper.ModifierBuilder;
import net.ryancave282.tconstruct.library.tools.nbt.MaterialNBT;
import net.ryancave282.tconstruct.library.tools.nbt.ModifierNBT;

import java.util.Collection;

/** Hook for tools exposing tool traits */
public interface ToolTraitHook {
  /** Adds all traits to the given builder */
  void addTraits(ToolDefinition definition, MaterialNBT materials, ModifierBuilder builder);

  /** Gets the traits from the given definition */
  static ModifierNBT getTraits(ToolDefinition definition, MaterialNBT materials) {
    ModifierNBT.Builder builder = ModifierNBT.builder();
    definition.getHook(ToolHooks.TOOL_TRAITS).addTraits(definition, materials, builder);
    return builder.build();
  }

  /** Gets the traits for the given tool */
  record AllMerger(Collection<ToolTraitHook> hooks) implements ToolTraitHook {
    @Override
    public void addTraits(ToolDefinition definition, MaterialNBT materials, ModifierBuilder builder) {
      for (ToolTraitHook hook : hooks) {
        hook.addTraits(definition, materials, builder);
      }
    }
  }
}
