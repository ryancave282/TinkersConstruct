package net.ryancave282.tconstruct.smeltery.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.ryancave282.tconstruct.TConstruct;

import javax.annotation.Nullable;
import java.util.List;

/** Item for creating casts that looks like a tool part */
public class DummyMaterialItem extends Item {
  private static final Component DUMMY_TOOL_PART = TConstruct.makeTranslation("item", "dummy_tool_part.tooltip").withStyle(ChatFormatting.GRAY);
  public DummyMaterialItem(Properties pProperties) {
    super(pProperties);
  }

  @Override
  public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> tooltip, TooltipFlag pIsAdvanced) {
    tooltip.add(DUMMY_TOOL_PART);
  }
}
