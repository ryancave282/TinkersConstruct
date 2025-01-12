package net.ryancave282.tconstruct.library.tools.definition.module.material;

import net.ryancave282.tconstruct.library.materials.definition.MaterialId;
import net.ryancave282.tconstruct.library.tools.definition.module.ToolHooks;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

/** Hook for repairing a tool via tool materials */
public interface MaterialRepairToolHook {
  /**
   * Checks if the given material can be used to repair this tool
   * @param tool      Tool to check
   * @param material  Material to check
   * @return  True if it can be used to repair this tool
   */
  boolean isRepairMaterial(IToolStackView tool, MaterialId material);

  /**
   * Gets the amount of durability restored by this materail for repair
   * @param tool      Tool instance
   * @param material  Material used for repair
   * @return  Repair amount
   */
  float getRepairAmount(IToolStackView tool, MaterialId material);


  /** Gets the repair stat for the given tool */
  static boolean canRepairWith(IToolStackView tool, MaterialId material) {
    return tool.getHook(ToolHooks.MATERIAL_REPAIR).isRepairMaterial(tool, material);
  }

  /** Gets the repair stat for the given tool */
  static float repairAmount(IToolStackView tool, MaterialId material) {
    return tool.getHook(ToolHooks.MATERIAL_REPAIR).getRepairAmount(tool, material);
  }

  /** Merger that takes the largest option from all nested modules */
  record MaxMerger(Collection<MaterialRepairToolHook> hooks) implements MaterialRepairToolHook {
    @Override
    public boolean isRepairMaterial(IToolStackView tool, MaterialId material) {
      for (MaterialRepairToolHook hook : hooks) {
        if (hook.isRepairMaterial(tool, material)) {
          return true;
        }
      }
      return false;
    }

    @Override
    public float getRepairAmount(IToolStackView tool, MaterialId material) {
      float amount = 0;
      for (MaterialRepairToolHook hook : hooks) {
        amount = Math.max(amount, hook.getRepairAmount(tool, material));
      }
      return amount;
    }
  }
}
