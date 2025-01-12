package net.ryancave282.tconstruct.library.tools.definition.module.weapon;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import net.ryancave282.tconstruct.library.module.HookProvider;
import net.ryancave282.tconstruct.library.module.ModuleHook;
import net.ryancave282.tconstruct.library.tools.context.ToolAttackContext;
import net.ryancave282.tconstruct.library.tools.definition.module.ToolHooks;
import net.ryancave282.tconstruct.library.tools.definition.module.ToolModule;
import net.ryancave282.tconstruct.library.tools.helper.ToolAttackUtil;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

/** Weapon attack that just spawns an extra particle */
public record ParticleWeaponAttack(SimpleParticleType particle) implements MeleeHitToolHook, ToolModule {
  public static final RecordLoadable<ParticleWeaponAttack> LOADER = RecordLoadable.create(
    Loadables.PARTICLE_TYPE.comapFlatMap((type, error) -> {
      if (type instanceof SimpleParticleType simple) {
        return simple;
      }
      throw error.create("Expected particle " + BuiltInRegistries.PARTICLE_TYPE.getKey(type) + " be a simple particle, got " + type);
    }, type -> type).requiredField("particle", ParticleWeaponAttack::particle), ParticleWeaponAttack::new);
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<ParticleWeaponAttack>defaultHooks(ToolHooks.MELEE_HIT);

  @Override
  public RecordLoadable<ParticleWeaponAttack> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public void afterMeleeHit(IToolStackView tool, ToolAttackContext context, float damage) {
    if (context.isFullyCharged()) {
      ToolAttackUtil.spawnAttackParticle(particle, context.getAttacker(), 0.8d);
    }
  }
}
