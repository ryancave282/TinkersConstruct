package net.ryancave282.tconstruct.library.materials.definition;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.ryancave282.tconstruct.library.tools.part.IMaterialItem;
import net.ryancave282.tconstruct.library.utils.IdParser;

import javax.annotation.Nullable;

/**
 * This is just a copy of ResourceLocation for type safety.
 */
public final class MaterialId extends ResourceLocation implements MaterialVariantId {
  public static final IdParser<MaterialId> PARSER = new IdParser<>(MaterialId::new, "Material");

  public MaterialId(String resourceName) {
    super(resourceName);
  }

  public MaterialId(String namespaceIn, String pathIn) {
    super(namespaceIn, pathIn);
  }

  public MaterialId(ResourceLocation resourceLocation) {
    super(resourceLocation.getNamespace(), resourceLocation.getPath());
  }

  /** Checks if this ID matches the given material */
  public boolean matches(IMaterial material) {
    return this.equals(material.getIdentifier());
  }

  /** Checks if this ID matches the given stack */
  public boolean matches(ItemStack stack) {
    return !stack.isEmpty() && this.equals(IMaterialItem.getMaterialFromStack(stack));
  }

  @Override
  public MaterialId getId() {
    return this;
  }

  @Override
  public String getVariant() {
    return "";
  }

  @Override
  public boolean hasVariant() {
    return false;
  }

  @Override
  public ResourceLocation getLocation(char separator) {
    return this;
  }

  @Override
  public String getSuffix() {
    return getNamespace() + '_' + getPath();
  }

  @Override
  public boolean matchesVariant(MaterialVariantId other) {
    return this.equals(other.getId());
  }

  /* Helpers */

  /**
   * Creates a new material ID from the given string
   * @param string  String
   * @return  Material ID, or null if invalid
   */
  @Nullable
  public static MaterialId tryParse(String string) {
    return PARSER.tryParse(string);
  }
}
