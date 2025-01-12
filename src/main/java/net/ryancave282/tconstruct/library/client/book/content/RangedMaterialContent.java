package net.ryancave282.tconstruct.library.client.book.content;

import net.minecraft.resources.ResourceLocation;
import net.ryancave282.tconstruct.TConstruct;
import net.ryancave282.tconstruct.library.materials.definition.MaterialId;
import net.ryancave282.tconstruct.library.materials.definition.MaterialVariantId;
import net.ryancave282.tconstruct.library.materials.stats.MaterialStatsId;
import net.ryancave282.tconstruct.library.utils.Util;
import net.ryancave282.tconstruct.tools.stats.GripMaterialStats;
import net.ryancave282.tconstruct.tools.stats.LimbMaterialStats;
import net.ryancave282.tconstruct.tools.stats.StatlessMaterialStats;

import javax.annotation.Nullable;

public class RangedMaterialContent extends AbstractMaterialContent {
  /** Page ID for using this index directly */
  public static final ResourceLocation ID = TConstruct.getResource("ranged_material");

  public RangedMaterialContent(MaterialVariantId materialVariant, boolean detailed) {
    super(materialVariant, detailed);
  }

  @Override
  public ResourceLocation getId() {
    return ID;
  }

  @Nullable
  @Override
  protected MaterialStatsId getStatType(int index) {
    return switch (index) {
      case 0 -> LimbMaterialStats.ID;
      case 1 -> GripMaterialStats.ID;
      case 2 -> StatlessMaterialStats.BOWSTRING.getIdentifier();
      default -> null;
    };
  }

  @Override
  protected String getTextKey(MaterialId material) {
    if (detailed) {
      String primaryKey = String.format("material.%s.%s.ranged", material.getNamespace(), material.getPath());
      if (Util.canTranslate(primaryKey)) {
        return primaryKey;
      }
      return String.format("material.%s.%s.encyclopedia", material.getNamespace(), material.getPath());
    }
    return String.format("material.%s.%s.flavor", material.getNamespace(), material.getPath());
  }

  @Override
  protected boolean supportsStatType(MaterialStatsId statsId) {
    return statsId.equals(LimbMaterialStats.ID) || statsId.equals(GripMaterialStats.ID) || statsId.equals(StatlessMaterialStats.BOWSTRING.getIdentifier());
  }
}
