package net.ryancave282.tconstruct.plugin.jei;

import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.ryancave282.tconstruct.TConstruct;
import net.ryancave282.tconstruct.library.modifiers.Modifier;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.recipe.alloying.AlloyRecipe;
import net.ryancave282.tconstruct.library.recipe.casting.IDisplayableCastingRecipe;
import net.ryancave282.tconstruct.library.recipe.entitymelting.EntityMeltingRecipe;
import net.ryancave282.tconstruct.library.recipe.melting.MeltingRecipe;
import net.ryancave282.tconstruct.library.recipe.modifiers.adding.IDisplayModifierRecipe;
import net.ryancave282.tconstruct.library.recipe.modifiers.severing.SeveringRecipe;
import net.ryancave282.tconstruct.library.recipe.molding.MoldingRecipe;
import net.ryancave282.tconstruct.library.recipe.partbuilder.IDisplayPartBuilderRecipe;
import net.ryancave282.tconstruct.library.recipe.partbuilder.Pattern;
import net.ryancave282.tconstruct.library.recipe.tinkerstation.building.ToolBuildingRecipe;
import net.ryancave282.tconstruct.library.recipe.worktable.IModifierWorktableRecipe;

public class TConstructJEIConstants {
  public static final ResourceLocation PLUGIN = TConstruct.getResource("jei_plugin");

  // ingredient types
  public static final IIngredientTypeWithSubtypes<Modifier,ModifierEntry> MODIFIER_TYPE = new IIngredientTypeWithSubtypes<>() {
    @Override
    public Class<? extends ModifierEntry> getIngredientClass() {
      return ModifierEntry.class;
    }

    @Override
    public Class<? extends Modifier> getIngredientBaseClass() {
      return Modifier.class;
    }

    @Override
    public Modifier getBase(ModifierEntry ingredient) {
      return ingredient.getModifier();
    }
  };
  public static final IIngredientType<Pattern> PATTERN_TYPE = () -> Pattern.class;

  // casting
  public static final RecipeType<IDisplayableCastingRecipe> CASTING_BASIN = type("casting_basin", IDisplayableCastingRecipe.class);
  public static final RecipeType<IDisplayableCastingRecipe> CASTING_TABLE = type("casting_table", IDisplayableCastingRecipe.class);
  public static final RecipeType<MoldingRecipe> MOLDING = type("molding", MoldingRecipe.class);

  // melting
  public static final RecipeType<MeltingRecipe> MELTING = type("melting", MeltingRecipe.class);
  public static final RecipeType<EntityMeltingRecipe> ENTITY_MELTING = type("entity_melting", EntityMeltingRecipe.class);
  public static final RecipeType<AlloyRecipe> ALLOY = type("alloy", AlloyRecipe.class);
  public static final RecipeType<MeltingRecipe> FOUNDRY = type("foundry", MeltingRecipe.class);

  // tinker station
  public static final RecipeType<IDisplayModifierRecipe> MODIFIERS = type("modifiers", IDisplayModifierRecipe.class);
  public static final RecipeType<SeveringRecipe> SEVERING = type("severing", SeveringRecipe.class);
  public static final RecipeType<ToolBuildingRecipe> TOOL_BUILDING = type("tool_recipes", ToolBuildingRecipe.class);

  // part builder
  public static final RecipeType<IDisplayPartBuilderRecipe> PART_BUILDER = type("part_builder", IDisplayPartBuilderRecipe.class);

  // modifier workstation
  public static final RecipeType<IModifierWorktableRecipe> MODIFIER_WORKTABLE = type("worktable", IModifierWorktableRecipe.class);

  private static <T> RecipeType<T> type(String name, Class<T> clazz) {
    return RecipeType.create(TConstruct.MOD_ID, name, clazz);
  }
}
