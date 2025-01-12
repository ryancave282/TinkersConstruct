package net.ryancave282.tconstruct.library.tools.definition.module.material;

import net.minecraft.util.RandomSource;
import net.ryancave282.tconstruct.library.tools.definition.ToolDefinition;
import net.ryancave282.tconstruct.library.tools.nbt.MaterialNBT;

/** Hook for filling a tool with materials */
public interface MissingMaterialsToolHook {
  /** Fills the tool with materials */
  MaterialNBT fillMaterials(ToolDefinition definition, RandomSource random);
}
