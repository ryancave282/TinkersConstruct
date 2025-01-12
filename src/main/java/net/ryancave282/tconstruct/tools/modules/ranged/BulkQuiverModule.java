package net.ryancave282.tconstruct.tools.modules.ranged;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.loadable.record.SingletonLoader;
import net.ryancave282.tconstruct.TConstruct;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.modifiers.ModifierHooks;
import net.ryancave282.tconstruct.library.modifiers.hook.ranged.BowAmmoModifierHook;
import net.ryancave282.tconstruct.library.modifiers.modules.ModifierModule;
import net.ryancave282.tconstruct.library.module.HookProvider;
import net.ryancave282.tconstruct.library.module.ModuleHook;
import net.ryancave282.tconstruct.library.tools.capability.inventory.ToolInventoryCapability;
import net.ryancave282.tconstruct.library.tools.capability.inventory.ToolInventoryCapability.StackMatch;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;
import java.util.function.Predicate;

/** Module implementing bulk quiver, which pulls arrows from the inventory to fire */
public enum BulkQuiverModule implements ModifierModule, BowAmmoModifierHook {
  INSTANCE;

  public static final SingletonLoader<BulkQuiverModule> LOADER = new SingletonLoader<>(INSTANCE);
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<BulkQuiverModule>defaultHooks(ModifierHooks.BOW_AMMO);
  private static final ResourceLocation LAST_SLOT = TConstruct.getResource("quiver_last_selected");

  @Override
  public RecordLoadable<BulkQuiverModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public ItemStack findAmmo(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, ItemStack standardAmmo, Predicate<ItemStack> ammoPredicate) {
    // skip if we have standard ammo, this quiver holds backup arrows
    if (!standardAmmo.isEmpty()) {
      return ItemStack.EMPTY;
    }
    StackMatch match = modifier.getHook(ToolInventoryCapability.HOOK).findStack(tool, modifier, ammoPredicate);
    if (!match.isEmpty()) {
      tool.getPersistentData().putInt(LAST_SLOT, match.slot());
      return match.stack();
    }
    return ItemStack.EMPTY;
  }

  @Override
  public void shrinkAmmo(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, ItemStack ammo, int needed) {
    // we assume no one else touched the quiver inventory, this is a good assumption, do not make it a bad assumption by modifying the quiver in other modifiers
    ammo.shrink(needed);
    modifier.getHook(ToolInventoryCapability.HOOK).setStack(tool, modifier, tool.getPersistentData().getInt((LAST_SLOT)), ammo);
  }
}
