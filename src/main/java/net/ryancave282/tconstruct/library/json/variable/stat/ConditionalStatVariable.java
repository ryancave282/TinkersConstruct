package net.ryancave282.tconstruct.library.json.variable.stat;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.ryancave282.tconstruct.library.modifiers.modules.behavior.ConditionalStatModule;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.GenericLoaderRegistry;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IHaveLoader;
import net.ryancave282.tconstruct.library.json.variable.VariableLoaderRegistry;
import net.ryancave282.tconstruct.library.json.variable.mining.MiningSpeedVariable;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

/**
 * Variable for use in {@link ConditionalStatModule}.
 * All conditional stat variables automatically work as mining speed variables due to the superset parameter space.
 */
public interface ConditionalStatVariable extends IHaveLoader, MiningSpeedVariable {
  GenericLoaderRegistry<ConditionalStatVariable> LOADER = new VariableLoaderRegistry<>("Conditional Stat Variable", ConditionalStatVariable.Constant::new);

  /**
   * Gets the value for the given content
   * @param tool    Tool context
   * @param entity  Entity context, will be null in tooltips
   * @return  Value for the given context
   */
  float getValue(IToolStackView tool, @Nullable LivingEntity entity);

  @Override
  default float getValue(IToolStackView tool, @Nullable BreakSpeed event, @Nullable Player player, @Nullable Direction sideHit) {
    return getValue(tool, player);
  }


  /** Registers a variable with conditional stat and mining speed */
  static void register(ResourceLocation name, RecordLoadable<? extends ConditionalStatVariable> loader) {
    LOADER.register(name, loader);
    MiningSpeedVariable.LOADER.register(name, loader);
  }


  /** Constant value instance for this object */
  record Constant(float value) implements VariableLoaderRegistry.ConstantFloat, ConditionalStatVariable {
    public static final RecordLoadable<ConditionalStatVariable.Constant> LOADER = VariableLoaderRegistry.constantLoader(ConditionalStatVariable.Constant::new);

    @Override
    public float getValue(IToolStackView tool, @Nullable LivingEntity entity) {
      return value;
    }

    @Override
    public RecordLoadable<ConditionalStatVariable.Constant> getLoader() {
      return LOADER;
    }
  }
}
