package net.ryancave282.tconstruct.library.json.variable.stat;

import net.minecraft.world.entity.LivingEntity;
import net.ryancave282.tconstruct.library.modifiers.modules.behavior.ConditionalStatModule;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import net.ryancave282.tconstruct.library.json.math.ModifierFormula;
import net.ryancave282.tconstruct.library.json.variable.VariableFormula;
import net.ryancave282.tconstruct.library.json.variable.VariableFormulaLoadable;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

import static net.ryancave282.tconstruct.library.json.math.ModifierFormula.LEVEL;
import static net.ryancave282.tconstruct.library.json.math.ModifierFormula.MULTIPLIER;
import static net.ryancave282.tconstruct.library.json.math.ModifierFormula.VALUE;

/** Variable context for {@link ConditionalStatModule} */
public record ConditionalStatFormula(ModifierFormula formula, List<ConditionalStatVariable> variables, String[] variableNames, boolean percent) implements VariableFormula<ConditionalStatVariable> {
  /** Variables for the modifier formula */
  public static final String[] VARIABLES = { "level", "value", "multiplier" };
  /** Loader instance */
  public static final RecordLoadable<ConditionalStatFormula> LOADER = new VariableFormulaLoadable<>(ConditionalStatVariable.LOADER, VARIABLES, (formula, variables, percent) -> new ConditionalStatFormula(formula, variables, EMPTY_STRINGS, percent));

  public ConditionalStatFormula(ModifierFormula formula, Map<String,ConditionalStatVariable> variables, boolean percent) {
    this(formula, List.copyOf(variables.values()), VariableFormula.getNames(variables), percent);
  }

  /** Builds the arguments from the context */
  private float[] getArguments(IToolStackView tool, ModifierEntry modifier, @Nullable LivingEntity entity, float value, float multiplier) {
    int size = variables.size();
    float[] arguments = new float[3 + size];
    arguments[LEVEL] = formula.processLevel(modifier);
    arguments[VALUE] = value;
    arguments[MULTIPLIER] = multiplier;
    for (int i = 0; i < size; i++) {
      arguments[3+i] = variables.get(i).getValue(tool, entity);
    }
    return arguments;
  }

  /** Runs this formula */
  public float apply(IToolStackView tool, ModifierEntry modifier, @Nullable LivingEntity entity, float value, float multiplier) {
    return formula.apply(getArguments(tool, modifier, entity, value, multiplier));
  }
}
