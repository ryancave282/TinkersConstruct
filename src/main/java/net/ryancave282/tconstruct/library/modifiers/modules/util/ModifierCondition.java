package net.ryancave282.tconstruct.library.modifiers.modules.util;

import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import net.ryancave282.tconstruct.library.json.IntRange;
import net.ryancave282.tconstruct.library.json.predicate.tool.ToolContextPredicate;
import net.ryancave282.tconstruct.library.json.predicate.tool.ToolStackPredicate;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.tools.nbt.IToolContext;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;

import java.util.function.Function;

/** Represents common conditions for a modifier module using tool stacks, since this is reused across several modules */
public record ModifierCondition<T extends IToolContext>(IJsonPredicate<T> tool, IntRange modifierLevel) {
  /** Instance matching any tool context predicate and any modifier level */
  public static final ModifierCondition<IToolContext> ANY_CONTEXT = new ModifierCondition<>(ToolContextPredicate.ANY, ModifierEntry.VALID_LEVEL);
  /** Instance matching any tool context predicate and any modifier level */
  public static final ModifierCondition<IToolStackView> ANY_TOOL = new ModifierCondition<>(ToolStackPredicate.ANY, ModifierEntry.VALID_LEVEL);

  /** Validates that the tool and modifier pass the conditions */
  public boolean matches(T tool, ModifierEntry modifier) {
    return this.modifierLevel.test(modifier.getLevel()) && this.tool.matches(tool);
  }

  /** Swaps the modifier level condition for the passed condition */
  public ModifierCondition<T> with(IJsonPredicate<T> tool) {
    return new ModifierCondition<T>(tool, modifierLevel);
  }

  /** Swaps the modifier level condition for the passed condition */
  public ModifierCondition<T> with(IntRange modifierLevel) {
    return new ModifierCondition<T>(this.tool, modifierLevel);
  }


  /* Loadable */

  /** Simple interface to allow a module to use  */
  public interface ConditionalModule<T extends IToolContext> {
    ModifierCondition<T> condition();
  }

  /** Loadable for tool context conditions, typically used with {@link RecordLoadable#directField(Function)} */
  public static final RecordLoadable<ModifierCondition<IToolContext>> CONTEXT_LOADABLE = RecordLoadable.create(
    ToolContextPredicate.LOADER.defaultField("tool", ModifierCondition::tool),
    ModifierEntry.VALID_LEVEL.defaultField("modifier_level", ModifierCondition::modifierLevel),
    ModifierCondition::new);
  /** Loadable for tool stack conditions {@link RecordLoadable#directField(Function)} */
  public static final RecordLoadable<ModifierCondition<IToolStackView>> TOOL_LOADABLE = RecordLoadable.create(
    ToolStackPredicate.LOADER.defaultField("tool", ModifierCondition::tool),
    ModifierEntry.VALID_LEVEL.defaultField("modifier_level", ModifierCondition::modifierLevel),
    ModifierCondition::new);

  /** Generic field instance used for most modules with conditions */
  public static final LoadableField<ModifierCondition<IToolContext>,ConditionalModule<IToolContext>> CONTEXT_FIELD = CONTEXT_LOADABLE.directField(ConditionalModule::condition);
  /** Generic field instance used for most modules with conditions */
  public static final LoadableField<ModifierCondition<IToolStackView>,ConditionalModule<IToolStackView>> TOOL_FIELD = TOOL_LOADABLE.directField(ConditionalModule::condition);
}
