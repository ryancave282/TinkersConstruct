package net.ryancave282.tconstruct.plugin.jei.partbuilder;

import lombok.Getter;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.client.SafeClientAccess;
import net.ryancave282.tconstruct.TConstruct;
import net.ryancave282.tconstruct.library.client.GuiUtil;
import net.ryancave282.tconstruct.library.client.materials.MaterialTooltipCache;
import net.ryancave282.tconstruct.library.materials.definition.MaterialVariant;
import net.ryancave282.tconstruct.library.recipe.partbuilder.IDisplayPartBuilderRecipe;
import net.ryancave282.tconstruct.library.tools.layout.Patterns;
import net.ryancave282.tconstruct.plugin.jei.TConstructJEIConstants;
import net.ryancave282.tconstruct.tables.TinkerTables;

import java.awt.Color;

public class PartBuilderCategory implements IRecipeCategory<IDisplayPartBuilderRecipe> {
  private static final ResourceLocation BACKGROUND_LOC = TConstruct.getResource("textures/gui/jei/tinker_station.png");
  private static final Component TITLE = TConstruct.makeTranslation("jei", "part_builder.title");
  private static final String KEY_COST = TConstruct.makeTranslationKey("jei", "part_builder.cost");

  @Getter
  private final IDrawable background;
  @Getter
  private final IDrawable icon;
  public PartBuilderCategory(IGuiHelper helper) {
    this.background = helper.createDrawable(BACKGROUND_LOC, 0, 117, 121, 46);
    this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(TinkerTables.partBuilder));
  }

  @Override
  public RecipeType<IDisplayPartBuilderRecipe> getRecipeType() {
    return TConstructJEIConstants.PART_BUILDER;
  }

  @Override
  public Component getTitle() {
    return TITLE;
  }

  @Override
  public void draw(IDisplayPartBuilderRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
    MaterialVariant variant = recipe.getMaterial();
    if (!variant.isEmpty()) {
      Font fontRenderer = Minecraft.getInstance().font;
      Component name = MaterialTooltipCache.getColoredDisplayName(variant.getVariant());
      graphics.drawString(fontRenderer, name, 3, 2, -1, true);
      String coolingString = I18n.get(KEY_COST, recipe.getCost());
      graphics.drawString(fontRenderer, coolingString, 3, 35, Color.GRAY.getRGB(), false);
    } else {
      GuiUtil.renderPattern(graphics, Patterns.INGOT, 25, 16);
    }
  }

  @Override
  public void setRecipe(IRecipeLayoutBuilder builder, IDisplayPartBuilderRecipe recipe, IFocusGroup focuses) {
    // items
    MaterialVariant material = recipe.getMaterial();
    if (!material.isEmpty()) {
      builder.addSlot(RecipeIngredientRole.INPUT, 25, 16).addItemStacks(MaterialItemList.getItems(material.getVariant()));
    }
    builder.addSlot(RecipeIngredientRole.INPUT,  4, 16).addItemStacks(recipe.getPatternItems());
    // patterns
    builder.addSlot(RecipeIngredientRole.INPUT, 46, 16).addIngredient(TConstructJEIConstants.PATTERN_TYPE, recipe.getPattern());
    // TODO: material input?

    // output
    RegistryAccess access = SafeClientAccess.getRegistryAccess();
    if (access != null) {
      builder.addSlot(RecipeIngredientRole.OUTPUT, 96, 15).addItemStack(recipe.getResultItem(access));
    }
  }
}
