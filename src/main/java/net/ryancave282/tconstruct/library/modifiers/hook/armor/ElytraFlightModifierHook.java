package net.ryancave282.tconstruct.library.modifiers.hook.armor;

import net.minecraft.world.entity.LivingEntity;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

/** Hook for chestplate modifiers to control eltyra flight behavior. */
public interface ElytraFlightModifierHook {
  /**
   * Call on elytra flight tick to run any update effects
   * @param tool         Elytra instance
   * @param modifier     Entry calling this hook
   * @param entity       Entity flying
   * @param flightTicks  Number of ticks the elytra has been in the air
   * @return  True if the elytra should stop flying
   */
  boolean elytraFlightTick(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int flightTicks);


  /** Flight merger: stops once the first hook says to stop flying */
  record FirstMerger(Collection<ElytraFlightModifierHook> modules) implements ElytraFlightModifierHook {
    @Override
    public boolean elytraFlightTick(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int flightTicks) {
      for (ElytraFlightModifierHook module : modules) {
        if (module.elytraFlightTick(tool, modifier, entity, flightTicks)) {
          return true;
        }
      }
      return false;
    }
  }
}
