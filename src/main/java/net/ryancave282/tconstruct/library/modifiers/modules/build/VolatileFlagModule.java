package net.ryancave282.tconstruct.library.modifiers.modules.build;

import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.modifiers.ModifierHooks;
import net.ryancave282.tconstruct.library.modifiers.hook.build.VolatileDataModifierHook;
import net.ryancave282.tconstruct.library.modifiers.modules.ModifierModule;
import net.ryancave282.tconstruct.library.modifiers.modules.util.ModifierCondition;
import net.ryancave282.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import net.ryancave282.tconstruct.library.module.HookProvider;
import net.ryancave282.tconstruct.library.module.ModuleHook;
import net.ryancave282.tconstruct.library.tools.nbt.IToolContext;
import net.ryancave282.tconstruct.library.tools.nbt.ToolDataNBT;

import java.util.List;

/**
 * Module that just sets a boolean flag to true on a tool.
 * @see net.ryancave282.tconstruct.library.tools.definition.module.build.VolatileFlagModule
 */
public record VolatileFlagModule(ResourceLocation flag, ModifierCondition<IToolContext> condition) implements VolatileDataModifierHook, ModifierModule, ConditionalModule<IToolContext> {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<VolatileFlagModule>defaultHooks(ModifierHooks.VOLATILE_DATA);
  public static final RecordLoadable<VolatileFlagModule> LOADER = RecordLoadable.create(Loadables.RESOURCE_LOCATION.requiredField("flag", VolatileFlagModule::flag), ModifierCondition.CONTEXT_FIELD, VolatileFlagModule::new);

  public VolatileFlagModule(ResourceLocation flag) {
    this(flag, ModifierCondition.ANY_CONTEXT);
  }

  @Override
  public void addVolatileData(IToolContext context, ModifierEntry modifier, ToolDataNBT volatileData) {
    if (condition.matches(context, modifier)) {
      volatileData.putBoolean(flag, true);
    }
  }

  @Override
  public RecordLoadable<VolatileFlagModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }
}
