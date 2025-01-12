package net.ryancave282.tconstruct.library.json.variable.tool;

import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import net.ryancave282.tconstruct.library.json.predicate.tool.ToolContextPredicate;
import net.ryancave282.tconstruct.library.json.variable.ConditionalVariable;
import net.ryancave282.tconstruct.library.tools.nbt.IToolContext;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;

/**
 * Gets one of two entity properties based on the condition
 */
public record ConditionalToolVariable(IJsonPredicate<IToolContext> condition, ToolVariable ifTrue, ToolVariable ifFalse) implements ToolVariable, ConditionalVariable<IJsonPredicate<IToolContext>,ToolVariable> {
  public static final RecordLoadable<ConditionalToolVariable> LOADER = ConditionalVariable.loadable(ToolContextPredicate.LOADER, ToolVariable.LOADER, ConditionalToolVariable::new);

  @Override
  public float getValue(IToolStackView tool) {
    return condition.matches(tool) ? ifTrue.getValue(tool) : ifFalse.getValue(tool);
  }

  @Override
  public RecordLoadable<ConditionalToolVariable> getLoader() {
    return LOADER;
  }
}
