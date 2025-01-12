package net.ryancave282.tconstruct.tools.modifiers.traits.melee;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.ryancave282.tconstruct.common.TinkerDamageTypes;
import net.ryancave282.tconstruct.library.modifiers.Modifier;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.modifiers.ModifierHooks;
import net.ryancave282.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import net.ryancave282.tconstruct.library.module.ModuleHookMap.Builder;
import net.ryancave282.tconstruct.library.tools.context.EquipmentContext;
import net.ryancave282.tconstruct.library.tools.context.ToolAttackContext;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;
import net.ryancave282.tconstruct.library.tools.nbt.ModifierNBT;
import net.ryancave282.tconstruct.library.tools.nbt.ModDataNBT;
import net.ryancave282.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.List;

public class EnderferenceModifier extends Modifier implements ProjectileHitModifierHook, MeleeHitModifierHook, OnAttackedModifierHook {
  public EnderferenceModifier() {
    MinecraftForge.EVENT_BUS.addListener(EnderferenceModifier::onTeleport);
  }

  private static void onTeleport(EntityTeleportEvent event) {
    if (event.getEntity() instanceof LivingEntity living && living.hasEffect(TinkerModifiers.enderferenceEffect.get())) {
      event.setCanceled(true);
    }
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    hookBuilder.addHook(this, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MELEE_HIT, ModifierHooks.ON_ATTACKED);
  }

  @Override
  public int getPriority() {
    return 50; // run later so other hooks can run before we cancel it all
  }

  @Override
  public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
    LivingEntity entity = context.getLivingTarget();
    if (entity != null) {
      // hack: do not want them teleporting from this hit
      TinkerModifiers.enderferenceEffect.get().apply(entity, 1, 0, true);
    }
    return knockback;
  }

  @Override
  public void failedMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageAttempted) {
    LivingEntity entity = context.getLivingTarget();
    if (entity != null) {
      entity.removeEffect(TinkerModifiers.enderferenceEffect.get());
    }
  }

  @Override
  public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
    LivingEntity entity = context.getLivingTarget();
    if (entity != null) {
      // 5 seconds of interference per level, affect all entities as players may teleport too
      entity.addEffect(new MobEffectInstance(TinkerModifiers.enderferenceEffect.get(), modifier.getLevel() * 100, 0, false, true, true));
    }
  }

  @Override
  public void onAttacked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
    // this works like vanilla, damage is capped due to the hurt immunity mechanics, so if multiple pieces apply thorns between us and vanilla, damage is capped at 4
    if (isDirectDamage && source.getEntity() instanceof LivingEntity attacker) {
      // 15% chance of working per level, doubled bonus on shields
      int level = modifier.getLevel();
      if (slotType.getType() == Type.HAND) {
        level *= 2;
      }
      if (RANDOM.nextFloat() < (level * 0.25f)) {
        attacker.addEffect(new MobEffectInstance(TinkerModifiers.enderferenceEffect.get(), modifier.getLevel() * 100, 0, false, true, true));
      }
    }
  }

  @Override
  public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
    if (target != null) {
      target.addEffect(new MobEffectInstance(TinkerModifiers.enderferenceEffect.get(), modifier.getLevel() * 100, 0, false, true, true));

      // endermen are hardcoded to not take arrow damage, so disagree by reimplementing arrow damage right here
      if (target.getType() == EntityType.ENDERMAN && projectile instanceof AbstractArrow arrow) {
        // first, give up if we reached pierce capacity, and ensure list are created
        if (arrow.getPierceLevel() > 0) {
          if (arrow.piercingIgnoreEntityIds == null) {
            arrow.piercingIgnoreEntityIds = new IntOpenHashSet(5);
          }
          if (arrow.piercedAndKilledEntities == null) {
            arrow.piercedAndKilledEntities = Lists.newArrayListWithCapacity(5);
          }
          if (arrow.piercingIgnoreEntityIds.size() >= arrow.getPierceLevel() + 1) {
            arrow.discard();
            return true;
          }
          arrow.piercingIgnoreEntityIds.add(target.getId());
        }

        // calculate damage, bonus on crit
        int damage = Mth.ceil(Mth.clamp(arrow.getDeltaMovement().length() * arrow.getBaseDamage(), 0.0D, Integer.MAX_VALUE));
        if (arrow.isCritArrow()) {
          damage = (int)Math.min(RANDOM.nextInt(damage / 2 + 2) + (long)damage, Integer.MAX_VALUE);
        }

        // create damage source, don't use projectile sources as that makes endermen ignore it
        Entity owner = arrow.getOwner();
        DamageSource damageSource = TinkerDamageTypes.source(projectile.level().registryAccess(), TinkerDamageTypes.MELEE_ARROW, projectile, attacker);
        if (attacker != null) {
          attacker.setLastHurtMob(target);
        }

        // handle fire
        int remainingFire = target.getRemainingFireTicks();
        if (arrow.isOnFire()) {
          target.setSecondsOnFire(5);
        }

        Level level = arrow.level();
        if (target.hurt(damageSource, (float)damage)) {
          if (!level.isClientSide && arrow.getPierceLevel() <= 0) {
            target.setArrowCount(target.getArrowCount() + 1);
          }

          // knockback from punch
          int knockback = arrow.getKnockback();
          if (knockback > 0) {
            Vec3 knockbackVec = arrow.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D).normalize().scale(knockback * 0.6D);
            if (knockbackVec.lengthSqr() > 0.0D) {
              target.push(knockbackVec.x, 0.1D, knockbackVec.z);
            }
          }

          if (!level.isClientSide && attacker != null) {
            EnchantmentHelper.doPostHurtEffects(target, attacker);
            EnchantmentHelper.doPostDamageEffects(attacker, target);
          }

          arrow.doPostHurtEffects(target);

          if (!target.isAlive() && arrow.piercedAndKilledEntities != null) {
            arrow.piercedAndKilledEntities.add(target);
          }

          if (!level.isClientSide && arrow.shotFromCrossbow() && owner instanceof ServerPlayer player) {
            if (arrow.piercedAndKilledEntities != null) {
              CriteriaTriggers.KILLED_BY_CROSSBOW.trigger(player, arrow.piercedAndKilledEntities);
            } else if (!target.isAlive()) {
              CriteriaTriggers.KILLED_BY_CROSSBOW.trigger(player, List.of(target));
            }
          }

          arrow.playSound(arrow.soundEvent, 1.0F, 1.2F / (RANDOM.nextFloat() * 0.2F + 0.9F));
          if (arrow.getPierceLevel() <= 0) {
            arrow.discard();
          }
        } else {
          // reset fire and drop the arrow
          target.setRemainingFireTicks(remainingFire);
          arrow.setDeltaMovement(arrow.getDeltaMovement().scale(-0.1D));
          arrow.setYRot(arrow.getYRot() + 180.0F);
          arrow.yRotO += 180.0F;
          if (!level.isClientSide && arrow.getDeltaMovement().lengthSqr() < 1.0E-7D) {
            if (arrow.pickup == AbstractArrow.Pickup.ALLOWED) {
              arrow.spawnAtLocation(arrow.getPickupItem(), 0.1F);
            }

            arrow.discard();
          }
        }

        return true;
      }
    }
    return false;
  }
}
