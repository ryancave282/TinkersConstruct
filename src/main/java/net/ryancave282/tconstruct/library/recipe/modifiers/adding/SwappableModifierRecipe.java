package net.ryancave282.tconstruct.library.recipe.modifiers.adding;

import lombok.Getter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.primitive.StringLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.NamedComponentRegistry;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import net.ryancave282.tconstruct.TConstruct;
import net.ryancave282.tconstruct.library.client.materials.MaterialTooltipCache;
import net.ryancave282.tconstruct.library.json.IntRange;
import net.ryancave282.tconstruct.library.json.field.MergingField;
import net.ryancave282.tconstruct.library.json.field.MergingField.MissingMode;
import net.ryancave282.tconstruct.library.materials.definition.IMaterial;
import net.ryancave282.tconstruct.library.materials.definition.MaterialVariantId;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.modifiers.ModifierId;
import net.ryancave282.tconstruct.library.recipe.RecipeResult;
import net.ryancave282.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import net.ryancave282.tconstruct.library.tools.SlotType.SlotCount;
import net.ryancave282.tconstruct.library.tools.nbt.LazyToolStack;
import net.ryancave282.tconstruct.library.tools.nbt.ToolDataNBT;
import net.ryancave282.tconstruct.library.tools.nbt.ToolStack;
import net.ryancave282.tconstruct.library.utils.Util;
import net.ryancave282.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static net.ryancave282.tconstruct.TConstruct.getResource;
import static net.ryancave282.tconstruct.library.recipe.modifiers.adding.IDisplayModifierRecipe.modifiersForResult;
import static net.ryancave282.tconstruct.library.recipe.modifiers.adding.IDisplayModifierRecipe.withModifiers;

/**
 * Standard recipe to add a modifier
 */
public class SwappableModifierRecipe extends ModifierRecipe {

  private static final String ALREADY_PRESENT = TConstruct.makeTranslationKey("recipe", "swappable.already_present");
  public static final RecordLoadable<SwappableModifierRecipe> LOADER = RecordLoadable.create(
    ContextKey.ID.requiredField(),
    INPUTS_FIELD, TOOLS_FIELD, MAX_TOOL_SIZE_FIELD,
    new MergingField<>(ModifierId.PARSER.requiredField("name", r -> r.result.getId()), "result", MissingMode.DISALLOWED),
    new MergingField<>(StringLoadable.DEFAULT.requiredField("value", r -> r.value), "result", MissingMode.DISALLOWED),
    VariantFormatter.LOADER.defaultField("variant_formatter", VariantFormatter.DEFAULT, r -> r.variantFormatter),
    SLOTS_FIELD, ALLOW_CRYSTAL_FIELD,
    SwappableModifierRecipe::new);

  /** Value of the modifier being swapped, distinguishing this recipe from others for the same modifier */
  private final String value;
  /** Logic to format the variant, needed for syncing */
  private final VariantFormatter variantFormatter;
  /** Display component for the variant string */
  @Getter
  private final Component variant;

  public SwappableModifierRecipe(ResourceLocation id, List<SizedIngredient> inputs, Ingredient toolRequirement, int maxToolSize, ModifierId result, String value, VariantFormatter variantFormatter, @Nullable SlotCount slots, boolean allowCrystal) {
    super(id, inputs, toolRequirement, maxToolSize, result, new IntRange(1, 1), slots, allowCrystal, false);
    this.value = value;
    this.variantFormatter = variantFormatter;
    this.variant = variantFormatter.format(result, value);
  }

  @Override
  public RecipeResult<LazyToolStack> getValidatedResult(ITinkerStationContainer inv, RegistryAccess access) {
    ToolStack tool = inv.getTinkerable();

    // if the tool has the modifier already, can skip most requirements
    ModifierId modifier = result.getId();

    boolean needsModifier;
    if (tool.getUpgrades().getLevel(modifier) == 0) {
      needsModifier = true;
      Component commonError = validatePrerequisites(tool);
      if (commonError != null) {
        return RecipeResult.failure(commonError);
      }
    } else {
      needsModifier = false;
    }

    // do not allow adding the modifier if this variant is already present
    if (tool.getPersistentData().getString(modifier).equals(value)) {
      return RecipeResult.failure(ALREADY_PRESENT, result.get().getDisplayName(), variant);
    }

    // consume slots
    tool = tool.copy();
    ToolDataNBT persistentData = tool.getPersistentData();
    if (needsModifier) {
      SlotCount slots = getSlots();
      if (slots != null) {
        persistentData.addSlots(slots.type(), -slots.count());
      }
    }

    // set the new value to the modifier
    persistentData.putString(modifier, value);

    // add modifier if needed
    if (needsModifier) {
      tool.addModifier(result.getId(), 1);
    } else {
      tool.rebuildStats();
    }

    // ensure no modifier problems
    Component toolValidation = tool.tryValidate();
    if (toolValidation != null) {
      return RecipeResult.failure(toolValidation);
    }
    return success(tool, inv);
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerModifiers.swappableModifierSerializer.get();
  }


  /* Display */

  @Override
  public List<ItemStack> getToolWithModifier() {
    if (toolWithModifier == null) {
      ResourceLocation id = result.getId();
      ModifierEntry result = getDisplayResult();
      toolWithModifier = getToolInputs().stream().map(stack -> withModifiers(stack, modifiersForResult(result, result), data -> data.putString(id, value))).collect(Collectors.toList());
    }
    return toolWithModifier;
  }


  /** Methods of formatting the variant string */
  @FunctionalInterface
  public interface VariantFormatter {
    NamedComponentRegistry<VariantFormatter> LOADER = new NamedComponentRegistry<>("Unknown variant formatter");

    /** Formats the variant given the modifier */
    Component format(ModifierId modifier, String variant);


    /* Formatters */
    /** Formats using the modifier ID as a base translation key */
    VariantFormatter DEFAULT = LOADER.register(getResource("default"), (modifier, variant) -> Component.translatable(Util.makeTranslationKey("modifier", modifier) + "." + variant));
    /** Formats using the material translation key */
    VariantFormatter MATERIAL = LOADER.register(getResource("material"), (modifier, variant) -> MaterialTooltipCache.getDisplayName(Objects.requireNonNullElse(MaterialVariantId.tryParse(variant), IMaterial.UNKNOWN_ID)));
  }
}
