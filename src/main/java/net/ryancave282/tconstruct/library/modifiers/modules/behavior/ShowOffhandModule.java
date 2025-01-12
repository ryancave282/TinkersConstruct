package net.ryancave282.tconstruct.library.modifiers.modules.behavior;

import net.minecraft.world.entity.EquipmentSlot;
import slimeknights.mantle.data.loadable.primitive.EnumLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.modifiers.ModifierHooks;
import net.ryancave282.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import net.ryancave282.tconstruct.library.modifiers.modules.ModifierModule;
import net.ryancave282.tconstruct.library.modifiers.modules.technical.ArmorLevelModule;
import net.ryancave282.tconstruct.library.module.HookProvider;
import net.ryancave282.tconstruct.library.module.ModuleHook;
import net.ryancave282.tconstruct.library.tools.capability.TinkerDataKeys;
import net.ryancave282.tconstruct.library.tools.context.EquipmentChangeContext;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;
import java.util.function.Function;

/** Module to show the offhand for a tool that can interact using the offhand */
public enum ShowOffhandModule implements ModifierModule, EquipmentChangeModifierHook {
  /** Mode which will always show the offhand */
  ALLOW_BROKEN,
  /** Mode which will only show the offhand when the tool is not broken */
  DISALLOW_BROKEN;

  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<ShowOffhandModule>defaultHooks(ModifierHooks.EQUIPMENT_CHANGE);
  public static final RecordLoadable<ShowOffhandModule> LOADER = RecordLoadable.create(new EnumLoadable<>(ShowOffhandModule.class).requiredField("mode", Function.identity()), Function.identity());

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (context.getChangedSlot() == EquipmentSlot.CHEST && (!tool.isBroken() || this == ALLOW_BROKEN)) {
      ArmorLevelModule.addLevels(context, TinkerDataKeys.SHOW_EMPTY_OFFHAND, 1);
    }
  }

  @Override
  public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (context.getChangedSlot() == EquipmentSlot.CHEST && (!tool.isBroken() || this == ALLOW_BROKEN)) {
      ArmorLevelModule.addLevels(context, TinkerDataKeys.SHOW_EMPTY_OFFHAND, -1);
    }
  }

  @Override
  public RecordLoadable<ShowOffhandModule> getLoader() {
    return LOADER;
  }
}
