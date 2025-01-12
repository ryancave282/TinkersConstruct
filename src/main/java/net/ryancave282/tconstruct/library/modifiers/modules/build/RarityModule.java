package net.ryancave282.tconstruct.library.modifiers.modules.build;

import net.minecraft.world.item.Rarity;
import slimeknights.mantle.data.loadable.primitive.EnumLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.modifiers.ModifierHooks;
import net.ryancave282.tconstruct.library.modifiers.hook.build.VolatileDataModifierHook;
import net.ryancave282.tconstruct.library.modifiers.modules.ModifierModule;
import net.ryancave282.tconstruct.library.module.HookProvider;
import net.ryancave282.tconstruct.library.module.ModuleHook;
import net.ryancave282.tconstruct.library.tools.item.IModifiable;
import net.ryancave282.tconstruct.library.tools.nbt.IToolContext;
import net.ryancave282.tconstruct.library.tools.nbt.ToolDataNBT;

import java.util.List;

/**
 * Module for setting tool's display name rarity
 * TODO: consider modifier level/tool conditions
 */
public record RarityModule(Rarity rarity) implements VolatileDataModifierHook, ModifierModule {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<RarityModule>defaultHooks(ModifierHooks.VOLATILE_DATA);
  public static final RecordLoadable<RarityModule> LOADER = RecordLoadable.create(new EnumLoadable<>(Rarity.class).requiredField("rarity", RarityModule::rarity), RarityModule::new);

  @Override
  public void addVolatileData(IToolContext context, ModifierEntry modifier, ToolDataNBT volatileData) {
    IModifiable.setRarity(volatileData, rarity);
  }

  @Override
  public RecordLoadable<RarityModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }
}
