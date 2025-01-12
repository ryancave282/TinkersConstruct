package net.ryancave282.tconstruct.plugin.jei.partbuilder;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.item.ItemStack;
import net.ryancave282.tconstruct.library.materials.definition.MaterialVariantId;
import net.ryancave282.tconstruct.library.recipe.material.MaterialRecipe;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Allows getting a list of items for display for a given material
 */
public class MaterialItemList {
  private static List<MaterialRecipe> RECIPE_LIST = Collections.emptyList();

  /** Material recipes */
  private static final Map<MaterialVariantId,List<ItemStack>> ITEM_LISTS = new HashMap<>();

  /**
   * Sets the list of recipes
   * @param recipes  Recipes
   */
  public static void setRecipes(List<MaterialRecipe> recipes) {
    RECIPE_LIST = recipes.stream().filter(r -> !r.getMaterial().isUnknown()).collect(Collectors.toList());
    ITEM_LISTS.clear();
  }

  /**
   * Gets a list of items
   * @param material  Materials
   * @return  List of items
   */
  public static List<ItemStack> getItems(MaterialVariantId material) {
    List<ItemStack> list = ITEM_LISTS.get(material);
    if (list == null) {
      ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
      for (MaterialRecipe recipe : RECIPE_LIST) {
        if (material.matchesVariant(recipe.getMaterial())) {
          builder.addAll(recipe.getDisplayItems());
        }
      }
      list = builder.build();
      ITEM_LISTS.put(material, list);
    }
    return list;
  }
}
