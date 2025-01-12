package net.ryancave282.tconstruct.tools.modifiers.traits.melee;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import net.ryancave282.tconstruct.library.modifiers.Modifier;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.modifiers.ModifierHooks;
import net.ryancave282.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import net.ryancave282.tconstruct.library.module.ModuleHookMap.Builder;
import net.ryancave282.tconstruct.library.tools.context.ToolAttackContext;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;
import net.ryancave282.tconstruct.library.tools.nbt.ModifierNBT;
import net.ryancave282.tconstruct.library.tools.nbt.ModDataNBT;

import javax.annotation.Nullable;

public class DecayModifier extends Modifier implements ProjectileLaunchModifierHook, ProjectileHitModifierHook, MeleeHitModifierHook {
  /* gets the effect for the given level, including a random time */
  private static MobEffectInstance makeDecayEffect(int level) {
    // potions are 0 indexed instead of 1 indexed
    // wither skeletons apply 10 seconds of wither for comparison
    return new MobEffectInstance(MobEffects.WITHER, 20 * (5 + (RANDOM.nextInt(level * 3))), level - 1);
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ModifierHooks.PROJECTILE_LAUNCH, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MELEE_HIT);
  }

  @Override
  public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
    if (context.isFullyCharged()) {
      // note the time of each effect is calculated independently

      // 25% chance to poison yourself
      if (RANDOM.nextInt(3) == 0) {
        context.getAttacker().addEffect(makeDecayEffect(modifier.getLevel()));
      }

      // always poison the target, means it works twice as often as lacerating
      LivingEntity target = context.getLivingTarget();
      if (target != null && target.isAlive()) {
        target.addEffect(makeDecayEffect(modifier.getLevel()));
      }
    }
  }

  @Override
  public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
    if (target != null && (!(projectile instanceof AbstractArrow arrow) || arrow.isCritArrow())) {
      // always poison the target, means it works twice as often as lacerating
      target.addEffect(makeDecayEffect(modifier.getLevel()));
    }
    return false;
  }

  @Override
  public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
    if (primary && (arrow == null || arrow.isCritArrow()) && RANDOM.nextInt(3) == 0) {
      // 25% chance to poison yourself
      shooter.addEffect(makeDecayEffect(modifier.getLevel()));
    }
  }
}
