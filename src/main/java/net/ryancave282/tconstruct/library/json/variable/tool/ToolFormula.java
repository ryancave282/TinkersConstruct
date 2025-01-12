package net.ryancave282.tconstruct.library.json.variable.tool;

import net.ryancave282.tconstruct.library.modifiers.modules.behavior.AttributeModule;
import net.ryancave282.tconstruct.library.json.math.ModifierFormula;
import net.ryancave282.tconstruct.library.json.variable.VariableFormula;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;
import java.util.Map;

import static net.ryancave282.tconstruct.library.json.math.ModifierFormula.LEVEL;

/** Variable context for {@link AttributeModule}, potentially other modules with just tool context in the future */
public record ToolFormula(ModifierFormula formula, List<ToolVariable> variables, String[] variableNames) implements VariableFormula<ToolVariable> {
  public ToolFormula(ModifierFormula formula, Map<String,ToolVariable> variables) {
    this(formula, List.copyOf(variables.values()), VariableFormula.getNames(variables));
  }

  @Override
  public boolean percent() {
    return false;
  }

  /** Builds the arguments from the context */
  private float[] getArguments(IToolStackView tool, ModifierEntry modifier) {
    int size = variables.size();
    float[] arguments = new float[1 + size];
    arguments[LEVEL] = formula.processLevel(modifier);
    for (int i = 0; i < size; i++) {
      arguments[1+i] = variables.get(i).getValue(tool);
    }
    return arguments;
  }

  /** Runs this formula */
  public float apply(IToolStackView tool, ModifierEntry modifier) {
    return formula.apply(getArguments(tool, modifier));
  }
}
