package net.ryancave282.tconstruct.library.recipe.material;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.ryancave282.tconstruct.library.materials.definition.MaterialVariant;
import net.ryancave282.tconstruct.library.materials.definition.MaterialVariantId;

/**
 * Constant material value used for tool parts
 */
@RequiredArgsConstructor
public class MaterialValue implements IMaterialValue {
  @Getter
  private final MaterialVariant material;
  @Getter
  private final int value;

  public MaterialValue(MaterialVariantId material, int value) {
    this(MaterialVariant.of(material), value);
  }
}
