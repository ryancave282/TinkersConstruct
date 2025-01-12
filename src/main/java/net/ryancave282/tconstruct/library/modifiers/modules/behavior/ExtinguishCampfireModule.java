package net.ryancave282.tconstruct.library.modifiers.modules.behavior;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import net.ryancave282.tconstruct.library.modifiers.modules.util.ModifierCondition;
import net.ryancave282.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;

/**
 * Module which performs AOE removing of campfires
 */
public record ExtinguishCampfireModule(ModifierCondition<IToolStackView> condition) implements BlockTransformModule, ConditionalModule<IToolStackView> {
  public static final ExtinguishCampfireModule INSTANCE = new ExtinguishCampfireModule(ModifierCondition.ANY_TOOL);
  public static final RecordLoadable<ExtinguishCampfireModule> LOADER = RecordLoadable.create(ModifierCondition.TOOL_FIELD, ExtinguishCampfireModule::new);

  @Override
  public RecordLoadable<ExtinguishCampfireModule> getLoader() {
    return LOADER;
  }

  @Override
  public boolean requireGround() {
    return false;
  }

  @Override
  public InteractionResult afterBlockUse(IToolStackView tool, ModifierEntry modifier, UseOnContext context, InteractionSource source) {
    if (condition.matches(tool, modifier)) {
      return BlockTransformModule.super.afterBlockUse(tool, modifier, context, source);
    }
    return InteractionResult.PASS;
  }

  @Override
  public boolean transform(IToolStackView tool, UseOnContext context, BlockState original, boolean playSound) {
    if (original.getBlock() instanceof CampfireBlock && original.getValue(CampfireBlock.LIT)) {
      Level level = context.getLevel();
      BlockPos pos = context.getClickedPos();
      if (!level.isClientSide) {
        if (playSound) {
          level.playSound(null, pos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        CampfireBlock.dowse(context.getPlayer(), level, pos, original);
      }
      level.setBlock(pos, original.setValue(CampfireBlock.LIT, false), Block.UPDATE_ALL_IMMEDIATE);
      return true;
    }
    return false;
  }
}
