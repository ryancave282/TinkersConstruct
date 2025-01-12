package net.ryancave282.tconstruct.smeltery.block.entity.inventory;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import slimeknights.mantle.block.entity.MantleBlockEntity;
import slimeknights.mantle.inventory.SingleItemHandler;
import net.ryancave282.tconstruct.library.recipe.TinkerRecipeTypes;

/**
 * Item handler holding the heater inventory
 */
public class HeaterItemHandler extends SingleItemHandler<MantleBlockEntity> {
  public HeaterItemHandler(MantleBlockEntity parent) {
    super(parent, 64);
  }

  @Override
  protected boolean isItemValid(ItemStack stack) {
    // fuel module divides by 4, so anything 3 or less is treated as 0
    return ForgeHooks.getBurnTime(stack, TinkerRecipeTypes.FUEL.get()) > 3;
  }
}
