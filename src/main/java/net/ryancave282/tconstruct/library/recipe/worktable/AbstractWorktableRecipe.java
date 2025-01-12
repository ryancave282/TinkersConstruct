package net.ryancave282.tconstruct.library.recipe.worktable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import slimeknights.mantle.data.loadable.common.IngredientLoadable;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import net.ryancave282.tconstruct.common.TinkerTags;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.recipe.ITinkerableContainer;
import net.ryancave282.tconstruct.library.recipe.modifiers.ModifierRecipeLookup;
import net.ryancave282.tconstruct.library.recipe.modifiers.adding.ModifierRecipe;
import net.ryancave282.tconstruct.library.tools.item.IModifiableDisplay;
import net.ryancave282.tconstruct.library.tools.nbt.LazyToolStack;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Default implementation of modifier worktable recipes, taking a list of inputs
 */
@RequiredArgsConstructor
public abstract class AbstractWorktableRecipe implements IModifierWorktableRecipe {
  public static final Ingredient DEFAULT_TOOLS = Ingredient.of(TinkerTags.Items.MODIFIABLE);
  protected static final LoadableField<Ingredient,AbstractWorktableRecipe> TOOL_FIELD = IngredientLoadable.DISALLOW_EMPTY.defaultField("tools", DEFAULT_TOOLS, true, r -> r.toolRequirement);
  protected static final LoadableField<List<SizedIngredient>,AbstractWorktableRecipe> INPUTS_FIELD = SizedIngredient.LOADABLE.list(1).requiredField("inputs", r -> r.inputs);

  @Getter
  private final ResourceLocation id;
  protected final Ingredient toolRequirement;
  protected final List<SizedIngredient> inputs;

  /* JEI */
  @Nullable
  protected List<ItemStack> tools;

  public AbstractWorktableRecipe(ResourceLocation id, List<SizedIngredient> inputs) {
    this(id, Ingredient.of(TinkerTags.Items.MODIFIABLE), inputs);
  }

  @Override
  public boolean matches(ITinkerableContainer inv, Level world) {
    if (!toolRequirement.test(inv.getTinkerableStack())) {
      return false;
    }
    return ModifierRecipe.checkMatch(inv, inputs);
  }

  @Override
  public List<ModifierEntry> getModifierOptions(@Nullable ITinkerableContainer inv) {
    if (inv == null) {
      return ModifierRecipeLookup.getRecipeModifierList();
    }
    return inv.getTinkerable().getUpgrades().getModifiers();
  }

  @Override
  public void updateInputs(LazyToolStack result, ITinkerableContainer.Mutable inv, ModifierEntry selected, boolean isServer) {
    ModifierRecipe.updateInputs(inv, inputs);
  }


  /* JEI */

  /** Gets a list of tools to display */
  @Override
  public List<ItemStack> getInputTools() {
    if (tools == null) {
      tools = Arrays.stream(toolRequirement.getItems()).map(stack -> IModifiableDisplay.getDisplayStack(stack.getItem())).toList();
    }
    return tools;
  }

  @Override
  public List<ItemStack> getDisplayItems(int slot) {
    if (slot < 0 || slot >= inputs.size()) {
      return Collections.emptyList();
    }
    return inputs.get(slot).getMatchingStacks();
  }

  @Override
  public int getInputCount() {
    return inputs.size();
  }
}
