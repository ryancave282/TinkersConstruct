package net.ryancave282.tconstruct.library.tools.definition.module.build;

import net.ryancave282.tconstruct.library.modifiers.hook.build.RawDataModifierHook;
import net.ryancave282.tconstruct.library.tools.nbt.IToolContext;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;
import net.ryancave282.tconstruct.library.tools.nbt.ToolDataNBT;

import java.util.Collection;

/**
 * Hook to add NBT to a tool that is reset every time modifiers or stats need a refresh. Ideal for storing data to communicate between modifiers or cache stat calculations.
 */
public interface VolatileDataToolHook {
  /**
   * Adds any relevant volatile data to the tool data. This data is rebuilt every time modifiers rebuild.
   * <br>
   * Alternatives:
   * <ul>
   *   <li>Persistent mod data (accessed via {@link IToolStackView}): Can be written to freely, but will not automatically remove if the modifier is removed.</li>
   *   <li>{@link RawDataModifierHook}: Allows modifying a restricted view of the tools main data, might help with other mod compat, but not modifier compat</li>
   * </ul>
   * @param context         Context about the tool being built. Will not yet contain
   * @param volatileData    Mutable mod NBT data, result of this method
   */
  void addVolatileData(IToolContext context, ToolDataNBT volatileData);

  /** Merger that runs all hooks */
  record AllMerger(Collection<VolatileDataToolHook> modules) implements VolatileDataToolHook {
    @Override
    public void addVolatileData(IToolContext context, ToolDataNBT volatileData) {
      for (VolatileDataToolHook module : modules) {
        module.addVolatileData(context, volatileData);
      }
    }
  }
}
