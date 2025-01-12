package net.ryancave282.tconstruct.common.data;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import net.ryancave282.tconstruct.library.modifiers.fluid.entity.DamageFluidEffect.DamageTypePair;

import static net.ryancave282.tconstruct.TConstruct.prefix;
import static net.ryancave282.tconstruct.common.TinkerDamageTypes.BLEEDING;
import static net.ryancave282.tconstruct.common.TinkerDamageTypes.FLUID_FIRE;
import static net.ryancave282.tconstruct.common.TinkerDamageTypes.FLUID_MAGIC;
import static net.ryancave282.tconstruct.common.TinkerDamageTypes.MELEE_ARROW;
import static net.ryancave282.tconstruct.common.TinkerDamageTypes.PIERCING;
import static net.ryancave282.tconstruct.common.TinkerDamageTypes.SELF_DESTRUCT;
import static net.ryancave282.tconstruct.common.TinkerDamageTypes.SMELTERY_HEAT;
import static net.ryancave282.tconstruct.common.TinkerDamageTypes.SMELTERY_MAGIC;
import static net.ryancave282.tconstruct.common.TinkerDamageTypes.WATER;

/** Datagen for damage types */
public class DamageTypeProvider implements RegistrySetBuilder.RegistryBootstrap<DamageType> {
  private DamageTypeProvider() {}

  /** Registers this provider with the registry set builder */
  public static void register(RegistrySetBuilder builder) {
    builder.add(Registries.DAMAGE_TYPE, new DamageTypeProvider());
  }

  @Override
  public void run(BootstapContext<DamageType> context) {
    context.register(SMELTERY_HEAT, new DamageType(prefix("smeltery_heat"), DamageScaling.NEVER, 0.1f, DamageEffects.BURNING));
    context.register(SMELTERY_MAGIC, new DamageType(prefix("smeltery_magic"), DamageScaling.NEVER, 0.1f, DamageEffects.BURNING));
    context.register(PIERCING, new DamageType(prefix("piercing"), 0.1f));
    context.register(BLEEDING, new DamageType(prefix("bleed"), DamageScaling.NEVER, 0.1f));
    context.register(SELF_DESTRUCT, new DamageType(prefix("self_destruct"), DamageScaling.NEVER, 0.1f));
    context.register(MELEE_ARROW, new DamageType("arrow", 0.1f));

    // fluid effects
    register(context, FLUID_FIRE, new DamageType(prefix("fluid.fire"), 0.1f, DamageEffects.BURNING));
    register(context, FLUID_MAGIC, new DamageType(prefix("fluid.magic"), 0.1f, DamageEffects.THORNS));
    register(context, WATER, new DamageType(prefix("fluid.water"), 0.1f, DamageEffects.DROWNING));
  }

  /** Registers a damage type pair for a fluid effect */
  private static void register(BootstapContext<DamageType> context, DamageTypePair pair, DamageType damageType) {
    context.register(pair.melee(), damageType);
    context.register(pair.ranged(), damageType);
  }
}
