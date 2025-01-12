package net.ryancave282.tconstruct.plugin.jei.modifiers;

import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import net.ryancave282.tconstruct.library.client.modifiers.ModifierIconManager;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/** Special modifier ingredient renderer used for ingredients in the bookmark menu */
public enum ModifierBookmarkIngredientRenderer implements IIngredientRenderer<ModifierEntry> {
  INSTANCE;

  private static final String WRAPPER_KEY = "jei.tconstruct.modifier_ingredient";

  @Override
  public void render(GuiGraphics graphics, @Nullable ModifierEntry entry) {
    if (entry != null) {
      ModifierIconManager.renderIcon(graphics, entry.getModifier(), 0, 0, 100, 16);
    }
  }

  @Override
  public List<Component> getTooltip(ModifierEntry entry, TooltipFlag flag) {
    List<Component> list = new ArrayList<>();
    // not using the main method as that applies color
    list.add(Component.translatable(WRAPPER_KEY, Component.translatable(entry.getModifier().getTranslationKey())));
    list.addAll(entry.getModifier().getDescriptionList());
    if (flag.isAdvanced()) {
      list.add((Component.literal(entry.getId().toString())).withStyle(ChatFormatting.DARK_GRAY));
    }
    return list;
  }
}
