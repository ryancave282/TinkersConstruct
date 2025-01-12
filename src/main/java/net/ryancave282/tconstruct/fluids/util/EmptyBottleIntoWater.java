package net.ryancave282.tconstruct.fluids.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.function.Supplier;

public record EmptyBottleIntoWater(Supplier<Item> empty, CauldronInteraction fallback) implements CauldronInteraction {
  @Override
  public InteractionResult interact(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
    if (state.getValue(LayeredCauldronBlock.LEVEL) == 3 || PotionUtils.getPotion(stack) != Potions.WATER) {
      return fallback.interact(state, level, pos, player, hand, stack);
    }
    if (!level.isClientSide) {
      player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(empty.get())));
      player.awardStat(Stats.USE_CAULDRON);
      player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
      level.setBlockAndUpdate(pos, state.cycle(LayeredCauldronBlock.LEVEL));
      level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
      level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
    }
    return InteractionResult.sidedSuccess(level.isClientSide);
  }
}
