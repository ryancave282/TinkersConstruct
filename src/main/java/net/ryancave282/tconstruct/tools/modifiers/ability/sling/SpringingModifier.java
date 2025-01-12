package net.ryancave282.tconstruct.tools.modifiers.ability.sling;

import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.ryancave282.tconstruct.common.Sounds;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import net.ryancave282.tconstruct.library.tools.helper.ModifierUtil;
import net.ryancave282.tconstruct.library.tools.helper.ToolDamageUtil;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;
import net.ryancave282.tconstruct.library.utils.SlimeBounceHandler;

/** Add velocity in the direction you face */
public class SpringingModifier extends SlingModifier {

  @Override
  public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
    if (!tool.isBroken() && source == InteractionSource.RIGHT_CLICK) {
      GeneralInteractionModifierHook.startUsingWithDrawtime(tool, modifier.getId(), player, hand, 1f);
    }
    return InteractionResult.SUCCESS;
  }

  @Override
  public void onStoppedUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int timeLeft) {
    super.onStoppedUsing(tool, modifier, entity, timeLeft);
    Level level = entity.level();
    if (entity instanceof Player player && !player.isFallFlying()) {
      player.causeFoodExhaustion(0.2F);

      float f = getForce(tool, modifier, player, timeLeft, true) * 1.05f;
      if (f > 0) {
        Vec3 look = player.getLookAngle().add(0, 1, 0).normalize();
        float inaccuracy = ModifierUtil.getInaccuracy(tool, player) * 0.0075f;
        RandomSource random = player.getRandom();
        player.push(
          (look.x + random.nextGaussian() * inaccuracy) * f,
          (look.y + random.nextGaussian() * inaccuracy) * f / 2f,
          (look.z + random.nextGaussian() * inaccuracy) * f);

        SlimeBounceHandler.addBounceHandler(player);
        if (!level.isClientSide) {
          level.playSound(null, player.getX(), player.getY(), player.getZ(), Sounds.SLIME_SLING.getSound(), player.getSoundSource(), 1, 1);
          player.causeFoodExhaustion(0.2F);
          player.getCooldowns().addCooldown(tool.getItem(), 3);
          ToolDamageUtil.damageAnimated(tool, 1, entity);
        }
        return;
      }
    }
    level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), Sounds.SLIME_SLING.getSound(), entity.getSoundSource(), 1, 0.5f);
  }
}
