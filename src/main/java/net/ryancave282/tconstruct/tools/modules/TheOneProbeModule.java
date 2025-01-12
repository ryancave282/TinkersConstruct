package net.ryancave282.tconstruct.tools.modules;

import slimeknights.mantle.data.loadable.record.SingletonLoader;
import net.ryancave282.tconstruct.common.TinkerTags;
import net.ryancave282.tconstruct.library.modifiers.Modifier;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.modifiers.ModifierHooks;
import net.ryancave282.tconstruct.library.modifiers.hook.build.RawDataModifierHook;
import net.ryancave282.tconstruct.library.modifiers.modules.ModifierModule;
import net.ryancave282.tconstruct.library.module.HookProvider;
import net.ryancave282.tconstruct.library.module.ModuleHook;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;
import net.ryancave282.tconstruct.library.utils.RestrictedCompoundTag;

import java.util.List;

/** Module implementing the one probe on held tools and helmets */
public enum TheOneProbeModule implements ModifierModule, RawDataModifierHook {
  INSTANCE;

  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<TheOneProbeModule>defaultHooks(ModifierHooks.RAW_DATA);
  public static final SingletonLoader<TheOneProbeModule> LOADER = new SingletonLoader<>(INSTANCE);
  public static final String TOP_NBT_HELMET = "theoneprobe";
  public static final String TOP_NBT_HAND = "theoneprobe_hand";

  @Override
  public void addRawData(IToolStackView tool, ModifierEntry modifier, RestrictedCompoundTag tag) {
    if (tool.hasTag(TinkerTags.Items.HELD)) {
      tag.putBoolean(TOP_NBT_HAND, true);
    }
    if (tool.hasTag(TinkerTags.Items.HELMETS)) {
      tag.putBoolean(TOP_NBT_HELMET, true);
    }
  }

  @Override
  public void removeRawData(IToolStackView tool, Modifier modifier, RestrictedCompoundTag tag) {
    tag.remove(TOP_NBT_HAND);
    tag.remove(TOP_NBT_HELMET);
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public SingletonLoader<TheOneProbeModule> getLoader() {
    return LOADER;
  }
}
