package net.ryancave282.tconstruct.plugin.jei.modifiers;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.ryancave282.tconstruct.common.TinkerTags;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.modifiers.ModifierManager;
import net.ryancave282.tconstruct.plugin.jei.TConstructJEIConstants;
import net.ryancave282.tconstruct.tools.item.ModifierCrystalItem;

import javax.annotation.Nullable;

public class ModifierIngredientHelper implements IIngredientHelper<ModifierEntry> {
  @Override
  public IIngredientType<ModifierEntry> getIngredientType() {
    return TConstructJEIConstants.MODIFIER_TYPE;
  }

  @Override
  public String getDisplayName(ModifierEntry entry) {
    return entry.getDisplayName().getString();
  }

  @Override
  public String getUniqueId(ModifierEntry entry, UidContext context) {
    return entry.getId().toString();
  }

  @Override
  public ResourceLocation getResourceLocation(ModifierEntry entry) {
    return entry.getId();
  }
  @Override
  public ModifierEntry copyIngredient(ModifierEntry entry) {
    return entry;
  }

  @Override
  public String getErrorInfo(@Nullable ModifierEntry entry) {
    if (entry == null) {
      return "null";
    }
    return entry.getId().toString();
  }

  @Override
  public ItemStack getCheatItemStack(ModifierEntry ingredient) {
    if (!ModifierManager.isInTag(ingredient.getId(), TinkerTags.Modifiers.EXTRACT_MODIFIER_BLACKLIST)) {
      return ModifierCrystalItem.withModifier(ingredient.getId());
    }
    return ItemStack.EMPTY;
  }
}
