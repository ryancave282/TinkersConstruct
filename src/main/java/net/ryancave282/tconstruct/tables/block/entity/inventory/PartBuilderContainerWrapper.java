package net.ryancave282.tconstruct.tables.block.entity.inventory;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.ryancave282.tconstruct.common.TinkerTags;
import net.ryancave282.tconstruct.library.materials.definition.IMaterial;
import net.ryancave282.tconstruct.library.materials.definition.MaterialVariantId;
import net.ryancave282.tconstruct.library.recipe.TinkerRecipeTypes;
import net.ryancave282.tconstruct.library.recipe.casting.material.MaterialCastingLookup;
import net.ryancave282.tconstruct.library.recipe.material.IMaterialValue;
import net.ryancave282.tconstruct.library.recipe.material.MaterialValue;
import net.ryancave282.tconstruct.library.recipe.partbuilder.IPartBuilderContainer;
import net.ryancave282.tconstruct.library.tools.part.IMaterialItem;
import net.ryancave282.tconstruct.tables.block.entity.table.PartBuilderBlockEntity;

import javax.annotation.Nullable;
import java.util.Objects;

public class PartBuilderContainerWrapper implements IPartBuilderContainer {
  private final PartBuilderBlockEntity builder;
  /** If true, the material recipe is out of date*/
  private boolean materialNeedsUpdate = true;
  /** Cached material recipe, may be null if not a material item */
  @Nullable
  private IMaterialValue material = null;

  public PartBuilderContainerWrapper(PartBuilderBlockEntity builder) {
    this.builder = builder;
  }

  @Override
  public ItemStack getStack() {
    return builder.getItem(PartBuilderBlockEntity.MATERIAL_SLOT);
  }

  @Override
  public ItemStack getPatternStack() {
    return builder.getItem(PartBuilderBlockEntity.PATTERN_SLOT);
  }

  /** Gets the tiles world */
  protected Level getWorld() {
    return Objects.requireNonNull(builder.getLevel(), "Tile entity world must be nonnull");
  }

  /** Refreshes the stored material */
  public void refreshMaterial() {
    this.materialNeedsUpdate = true;
    this.material = null;
  }

  @Override
  @Nullable
  public IMaterialValue getMaterial() {
    if (this.materialNeedsUpdate) {
      this.materialNeedsUpdate = false;
      ItemStack stack = getStack();
      if (stack.isEmpty()) {
        this.material = null;
      } else if (stack.is(TinkerTags.Items.TOOL_PARTS)) {
        MaterialVariantId material = IMaterialItem.getMaterialFromStack(stack);
        if (IMaterial.UNKNOWN_ID.matchesVariant(material)) {
          this.material = null;
        } else {
          this.material = new MaterialValue(material, MaterialCastingLookup.getItemCost(stack.getItem()));
        }
      } else {
        Level world = getWorld();
        this.material = world.getRecipeManager().getRecipeFor(TinkerRecipeTypes.MATERIAL.get(), this, world).orElse(null);
      }
    }
    return this.material;
  }
}
