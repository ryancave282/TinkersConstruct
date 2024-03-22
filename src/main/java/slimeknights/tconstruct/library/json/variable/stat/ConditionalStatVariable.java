package slimeknights.tconstruct.library.json.variable.stat;

import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.data.registry.GenericLoaderRegistry;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IHaveLoader;
import slimeknights.tconstruct.library.json.variable.VariableLoaderRegistry;
import slimeknights.tconstruct.library.json.variable.VariableLoaderRegistry.ConstantLoader;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

/** Variable for use in {@link slimeknights.tconstruct.library.modifiers.modules.behavior.ConditionalStatModule} */
public interface ConditionalStatVariable extends IHaveLoader {
  GenericLoaderRegistry<ConditionalStatVariable> LOADER = new VariableLoaderRegistry<>(Constant.LOADER.constructor());

  /**
   * Gets the value for the given content
   * @param tool    Tool context
   * @param entity  Entity context, will be null in tooltips
   * @return  Value for the given context
   */
  float getValue(IToolStackView tool, @Nullable LivingEntity entity);

  /** Constant value instance for this object */
  record Constant(float value) implements VariableLoaderRegistry.ConstantFloat, ConditionalStatVariable {
    public static final ConstantLoader<Constant> LOADER = new ConstantLoader<>(Constant::new);

    @Override
    public float getValue(IToolStackView tool, @Nullable LivingEntity entity) {
      return value;
    }

    @Override
    public IGenericLoader<? extends ConditionalStatVariable> getLoader() {
      return LOADER;
    }
  }
}
