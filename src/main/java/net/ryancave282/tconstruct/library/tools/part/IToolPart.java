package net.ryancave282.tconstruct.library.tools.part;

import net.ryancave282.tconstruct.library.materials.definition.MaterialId;
import net.ryancave282.tconstruct.library.materials.stats.MaterialStatsId;

/**
 * Any Class that's used as a tool part needs to implement this.
 */
public interface IToolPart extends IMaterialItem {
  /**
   * Gets the stat type for the given item, limits which materials are supported
   * @return  Stat type for the given item
   */
  MaterialStatsId getStatType();

  @Override
  default boolean canUseMaterial(MaterialId material) {
    return getStatType().canUseMaterial(material);
  }
}
