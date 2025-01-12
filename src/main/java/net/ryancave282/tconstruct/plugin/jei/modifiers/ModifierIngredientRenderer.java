package net.ryancave282.tconstruct.plugin.jei.modifiers;

import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public record ModifierIngredientRenderer(int width, int height) implements IIngredientRenderer<ModifierEntry> {
  @Override
  public int getWidth() {
    return width;
  }

  @Override
  public int getHeight() {
    return height;
  }

  @Override
  public void render(GuiGraphics graphics, @Nullable ModifierEntry entry) {
    if (entry != null) {
      Component name = entry.getDisplayName();
      Font fontRenderer = getFontRenderer(Minecraft.getInstance(), entry);
      int x = (width - fontRenderer.width(name)) / 2;
      graphics.drawString(fontRenderer, name, x, 1, -1, true);
    }
  }

  @Override
  public List<Component> getTooltip(ModifierEntry entry, TooltipFlag flag) {
    List<Component> tooltip = entry.getModifier().getDescriptionList(entry.getLevel());
    if (flag.isAdvanced()) {
      tooltip = new ArrayList<>(tooltip);
      tooltip.add((Component.literal(entry.getId().toString())).withStyle(ChatFormatting.DARK_GRAY));
    }
    return tooltip;
  }
}
