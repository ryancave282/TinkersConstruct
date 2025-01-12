package net.ryancave282.tconstruct.tools.recipe;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import net.ryancave282.tconstruct.TConstruct;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.modifiers.ModifierId;
import net.ryancave282.tconstruct.library.recipe.ITinkerableContainer;
import net.ryancave282.tconstruct.library.recipe.ITinkerableContainer.Mutable;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;
import net.ryancave282.tconstruct.library.tools.nbt.LazyToolStack;
import net.ryancave282.tconstruct.tools.TinkerModifiers;
import net.ryancave282.tconstruct.tools.item.ModifierCrystalItem;

import javax.annotation.Nullable;
import java.util.List;

/** Recipe that removes a modifier, placing it on a crystal for reapplication */
public class ExtractModifierRecipe extends ModifierRemovalRecipe {
  public static final String BASE_KEY = TConstruct.makeTranslationKey("recipe", "extract_modifier");
  private static final Component DESCRIPTION = TConstruct.makeTranslation("recipe", "extract_modifier.description");
  private static final Component NO_MODIFIERS = TConstruct.makeTranslation("recipe", "extract_modifier.no_modifiers");

  /** Recipe loadable */
  public static final RecordLoadable<ExtractModifierRecipe> LOADER = RecordLoadable.create(ContextKey.ID.requiredField(), NAME_FIELD, TOOLS_FIELD, INPUTS_FIELD, LEFTOVERS_FIELD, MODIFIER_PREDICATE_FIELD, ExtractModifierRecipe::new);

  public ExtractModifierRecipe(ResourceLocation id, String name, SizedIngredient toolRequirements, List<SizedIngredient> inputs, List<ItemStack> leftovers, IJsonPredicate<ModifierId> modifierPredicate) {
    super(id, name, toolRequirements, inputs, leftovers, modifierPredicate);
  }

  /** Gets the base key for the title translation */
  @Override
  protected String getBaseKey() {
    return BASE_KEY;
  }

  @Override
  public Component getDescription(@Nullable ITinkerableContainer inv) {
    if (inv != null) {
      IToolStackView tool = inv.getTinkerable();
      if (filter(tool, tool.getModifierList()).isEmpty()) {
        return NO_MODIFIERS;
      }
    }
    return DESCRIPTION;
  }

  @Override
  protected List<ModifierEntry> filter(@Nullable IToolStackView tool, List<ModifierEntry> modifiers) {
    if (tool != null) {
      // filter out incremental modifiers at level 1 with only a partial level to prevent exploiting the recipe to get a lot of crystals
      return modifiers.stream().filter(entryPredicate).filter(entry -> entry.intEffectiveLevel() > 0).toList();
    }
    return super.filter(tool, modifiers);
  }

  @Override
  public void updateInputs(LazyToolStack result, Mutable inv, ModifierEntry selected, boolean isServer) {
    super.updateInputs(result, inv, selected, isServer);
    if (isServer) {
      // just 1 crystal in this version as just 1 level was removed
      inv.giveItem(ModifierCrystalItem.withModifier(selected.getId()));
    }
  }

  @Override
  public boolean isModifierOutput() {
    return true;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerModifiers.extractModifierSerializer.get();
  }
}
