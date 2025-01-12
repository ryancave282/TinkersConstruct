package net.ryancave282.tconstruct.library.recipe.modifiers.adding;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import net.ryancave282.tconstruct.library.json.IntRange;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.tools.SlotType.SlotCount;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/** Recipe instance to return in JEI from recipes that contain multiple display recipes */
@RequiredArgsConstructor
public class DisplayModifierRecipe implements IDisplayModifierRecipe {
  private final List<SizedIngredient> inputs;
  @Getter
  private final List<ItemStack> toolWithoutModifier;
  @Getter
  private final List<ItemStack> toolWithModifier;
  /** Error message to display if the requirements do not match */
  @Getter
  private final ModifierEntry displayResult;
  @Getter
  private final IntRange level;
  @Nullable
  @Getter
  private final SlotCount slots;

  @Override
  public int getInputCount() {
    return inputs.size();
  }

  @Override
  public List<ItemStack> getDisplayItems(int slot) {
    if (slot >= 0 && slot < inputs.size()) {
      return inputs.get(slot).getMatchingStacks();
    }
    return Collections.emptyList();
  }
}
