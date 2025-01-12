package net.ryancave282.tconstruct.library.tools.definition.module.interaction;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.loadable.record.SingletonLoader;
import net.ryancave282.tconstruct.TConstruct;
import net.ryancave282.tconstruct.library.modifiers.Modifier;
import net.ryancave282.tconstruct.library.modifiers.ModifierId;
import net.ryancave282.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import net.ryancave282.tconstruct.library.module.HookProvider;
import net.ryancave282.tconstruct.library.module.ModuleHook;
import net.ryancave282.tconstruct.library.recipe.worktable.ModifierSetWorktableRecipe;
import net.ryancave282.tconstruct.library.tools.definition.module.ToolHooks;
import net.ryancave282.tconstruct.library.tools.definition.module.ToolModule;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

/** Tool that supports interaction with either hand. Uses persistent NBT to choose which hand is allowed to interact */
public class DualOptionInteraction implements InteractionToolModule, ToolModule {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<DualOptionInteraction>defaultHooks(ToolHooks.INTERACTION);
  /** Singleton instance */
  public static final DualOptionInteraction INSTANCE = new DualOptionInteraction();
  /** Loader instance */
  public static final SingletonLoader<DualOptionInteraction> LOADER = new SingletonLoader<>(INSTANCE);
  /** Key for persistent data set of modifiers */
  public static final ResourceLocation KEY = TConstruct.getResource("attack_modifiers");
  /** Key for denoting this feature in the tooltip */
  private static final String MODIFIER_FORMAT = TConstruct.makeTranslationKey("modifier", "attack_toggled");

  private DualOptionInteraction() {}

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public boolean canInteract(IToolStackView tool, ModifierId modifier, InteractionSource source) {
    return (source == InteractionSource.RIGHT_CLICK) != ModifierSetWorktableRecipe.isInSet(tool.getPersistentData(), KEY, modifier);
  }

  @Override
  public SingletonLoader<DualOptionInteraction> getLoader() {
    return LOADER;
  }

  /** Adds the format string to the modifier name */
  public static Component formatModifierName(IToolStackView tool, Modifier modifier, Component originalName) {
    if (ModifierSetWorktableRecipe.isInSet(tool.getPersistentData(), KEY, modifier.getId())) {
      return modifier.applyStyle(Component.translatable(MODIFIER_FORMAT, originalName));
    }
    return originalName;
  }
}
