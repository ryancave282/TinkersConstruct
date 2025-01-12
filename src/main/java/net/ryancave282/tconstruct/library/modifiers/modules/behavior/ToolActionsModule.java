package net.ryancave282.tconstruct.library.modifiers.modules.behavior;

import com.google.common.collect.ImmutableSet;
import net.minecraftforge.common.ToolAction;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.modifiers.ModifierHooks;
import net.ryancave282.tconstruct.library.modifiers.hook.behavior.ToolActionModifierHook;
import net.ryancave282.tconstruct.library.modifiers.modules.ModifierModule;
import net.ryancave282.tconstruct.library.modifiers.modules.util.ModifierCondition;
import net.ryancave282.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import net.ryancave282.tconstruct.library.module.HookProvider;
import net.ryancave282.tconstruct.library.module.ModuleHook;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;
import java.util.Set;

/**
 * Module that allows a modifier to perform tool actions
 */
public record ToolActionsModule(Set<ToolAction> actions, ModifierCondition<IToolStackView> condition) implements ToolActionModifierHook, ModifierModule, ConditionalModule<IToolStackView> {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<ToolActionsModule>defaultHooks(ModifierHooks.TOOL_ACTION);
  public static final RecordLoadable<ToolActionsModule> LOADER = RecordLoadable.create(
    Loadables.TOOL_ACTION.set().requiredField("tool_actions", ToolActionsModule::actions),
    ModifierCondition.TOOL_FIELD,
    ToolActionsModule::new);

  public ToolActionsModule(ToolAction... actions) {
    this(ImmutableSet.copyOf(actions), ModifierCondition.ANY_TOOL);
  }

  @Override
  public boolean canPerformAction(IToolStackView tool, ModifierEntry modifier, ToolAction toolAction) {
    return condition.matches(tool, modifier) && actions.contains(toolAction);
  }

  @Override
  public RecordLoadable<ToolActionsModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }
}
