package net.ryancave282.tconstruct.tools.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.ryancave282.tconstruct.common.config.Config;
import net.ryancave282.tconstruct.library.materials.MaterialRegistry;
import net.ryancave282.tconstruct.library.materials.definition.IMaterial;
import net.ryancave282.tconstruct.library.materials.definition.MaterialId;
import net.ryancave282.tconstruct.library.materials.definition.MaterialVariantId;
import net.ryancave282.tconstruct.library.tools.helper.TooltipUtil;
import net.ryancave282.tconstruct.library.tools.part.IRepairKitItem;
import net.ryancave282.tconstruct.library.tools.part.MaterialItem;
import net.ryancave282.tconstruct.library.tools.part.ToolPartItem;
import net.ryancave282.tconstruct.tools.stats.StatlessMaterialStats;

import javax.annotation.Nullable;
import java.util.List;

public class RepairKitItem extends MaterialItem implements IRepairKitItem {
  public RepairKitItem(Properties properties) {
    super(properties);
  }

  @Override
  public boolean canUseMaterial(MaterialId material) {
    return MaterialRegistry.getInstance()
                           .getAllStats(material)
                           .stream()
                           .anyMatch(stats -> stats == StatlessMaterialStats.REPAIR_KIT || stats.getType().canRepair());
  }

  @Override
  public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flag) {
    if (flag.isAdvanced() && !TooltipUtil.isDisplay(stack)) {
      MaterialVariantId materialVariant = this.getMaterial(stack);
      if (!materialVariant.equals(IMaterial.UNKNOWN_ID)) {
        tooltip.add((Component.translatable(ToolPartItem.MATERIAL_KEY, materialVariant.toString())).withStyle(ChatFormatting.DARK_GRAY));
      }
    }
  }

  @Override
  public float getRepairAmount() {
    return Config.COMMON.repairKitAmount.get().floatValue();
  }
}
