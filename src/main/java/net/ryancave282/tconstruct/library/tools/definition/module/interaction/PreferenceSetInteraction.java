package net.ryancave282.tconstruct.library.tools.definition.module.interaction;

import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import net.ryancave282.tconstruct.library.json.TinkerLoadables;
import net.ryancave282.tconstruct.library.json.predicate.modifier.ModifierPredicate;
import net.ryancave282.tconstruct.library.module.ModuleHook;
import net.ryancave282.tconstruct.library.modifiers.ModifierId;
import net.ryancave282.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import net.ryancave282.tconstruct.library.module.HookProvider;
import net.ryancave282.tconstruct.library.tools.definition.module.ToolHooks;
import net.ryancave282.tconstruct.library.tools.definition.module.ToolModule;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

/**
 * Interaction that makes only a limited set work in the preferred hand, the rest working in the other hand
 */
public record PreferenceSetInteraction(InteractionSource preferredSource, IJsonPredicate<ModifierId> preferenceModifiers) implements InteractionToolModule, ToolModule {
  public static final RecordLoadable<PreferenceSetInteraction> LOADER = RecordLoadable.create(
    TinkerLoadables.INTERACTION_SOURCE.requiredField("preferred_source", PreferenceSetInteraction::preferredSource),
    ModifierPredicate.LOADER.requiredField("preferred_modifiers", PreferenceSetInteraction::preferenceModifiers),
    PreferenceSetInteraction::new);
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<PreferenceSetInteraction>defaultHooks(ToolHooks.INTERACTION);

  @Override
  public boolean canInteract(IToolStackView tool, ModifierId modifier, InteractionSource source) {
    return (source == preferredSource) == preferenceModifiers.matches(modifier);
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public RecordLoadable<PreferenceSetInteraction> getLoader() {
    return LOADER;
  }
}
