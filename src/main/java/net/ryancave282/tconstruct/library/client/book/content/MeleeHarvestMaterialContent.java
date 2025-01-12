package net.ryancave282.tconstruct.library.client.book.content;

import net.minecraft.resources.ResourceLocation;
import net.ryancave282.tconstruct.TConstruct;
import net.ryancave282.tconstruct.library.materials.definition.MaterialId;
import net.ryancave282.tconstruct.library.materials.definition.MaterialVariantId;
import net.ryancave282.tconstruct.library.materials.stats.MaterialStatsId;
import net.ryancave282.tconstruct.tools.stats.HandleMaterialStats;
import net.ryancave282.tconstruct.tools.stats.HeadMaterialStats;
import net.ryancave282.tconstruct.tools.stats.StatlessMaterialStats;

/**
 * Content page for melee/harvest materials
 */
public class MeleeHarvestMaterialContent extends AbstractMaterialContent {
  /** Page ID for using this index directly */
  public static final ResourceLocation ID = TConstruct.getResource("melee_harvest_material");

  public MeleeHarvestMaterialContent(MaterialVariantId materialVariant, boolean detailed) {
    super(materialVariant, detailed);
  }

  @Override
  public ResourceLocation getId() {
    return ID;
  }

  @Override
  protected MaterialStatsId getStatType(int index) {
    return switch (index) {
      case 0 -> HeadMaterialStats.ID;
      case 1 -> HandleMaterialStats.ID;
      case 2 -> StatlessMaterialStats.BINDING.getIdentifier();
      default -> null;
    };
  }

  @Override
  protected String getTextKey(MaterialId material) {
    return String.format(detailed ? "material.%s.%s.encyclopedia" : "material.%s.%s.flavor", material.getNamespace(), material.getPath());
  }

  @Override
  protected boolean supportsStatType(MaterialStatsId statsId) {
    return statsId.equals(HeadMaterialStats.ID) || statsId.equals(HandleMaterialStats.ID) || statsId.equals(StatlessMaterialStats.BINDING.getIdentifier());
  }
}
