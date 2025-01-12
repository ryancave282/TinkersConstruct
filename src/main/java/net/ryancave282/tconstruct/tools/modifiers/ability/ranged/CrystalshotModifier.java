package net.ryancave282.tconstruct.tools.modifiers.ability.ranged;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.client.ResourceColorManager;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.modifiers.ModifierHooks;
import net.ryancave282.tconstruct.library.modifiers.hook.ranged.BowAmmoModifierHook;
import net.ryancave282.tconstruct.library.modifiers.impl.NoLevelsModifier;
import net.ryancave282.tconstruct.library.module.ModuleHookMap.Builder;
import net.ryancave282.tconstruct.library.tools.helper.ToolDamageUtil;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;
import net.ryancave282.tconstruct.tools.item.CrystalshotItem;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class CrystalshotModifier extends NoLevelsModifier implements BowAmmoModifierHook {

  @Override
  protected void registerHooks(Builder hookBuilder) {
    hookBuilder.addHook(this, ModifierHooks.BOW_AMMO);
  }

  @Override
  public int getPriority() {
    // TODO: rethink ordering of ammo modifiers
    return 60; // after trick quiver, before bulk quiver, can't go after bulk due to desire to use inventory
  }

  @Override
  public Component getDisplayName(IToolStackView tool, ModifierEntry entry, @Nullable RegistryAccess access) {
    // color the display name for the variant
    String variant = tool.getPersistentData().getString(getId());
    if (!variant.isEmpty()) {
      String key = getTranslationKey();
      return Component.translatable(getTranslationKey())
        .withStyle(style -> style.withColor(ResourceColorManager.getTextColor(key + "." + variant)));
    }
    return super.getDisplayName();
  }

  @Override
  public ItemStack findAmmo(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, ItemStack standardAmmo, Predicate<ItemStack> ammoPredicate) {
    return CrystalshotItem.withVariant(tool.getPersistentData().getString(getId()), 64);
  }

  @Override
  public void shrinkAmmo(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, ItemStack ammo, int needed) {
    ToolDamageUtil.damageAnimated(tool, 4 * needed, shooter, shooter.getUsedItemHand());
  }
}
