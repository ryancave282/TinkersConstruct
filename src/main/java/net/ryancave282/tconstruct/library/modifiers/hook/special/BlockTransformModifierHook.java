package net.ryancave282.tconstruct.library.modifiers.hook.special;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.modifiers.ModifierHooks;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

/** Interface that allows another modifier to hook into the block transform modifier. */
public interface BlockTransformModifierHook {
  /**
   * Called after a block is successfully transformed
   * @param tool     Tool used in transforming
   * @param modifier Entry calling this hook
   * @param context  Item use context, corresponds to the original targeted position
   * @param state    State before it was transformed
   * @param pos      Position of block that was transformed, may be different from the context
   * @param action   Action that was performed
   */
  void afterTransformBlock(IToolStackView tool, ModifierEntry modifier, UseOnContext context, BlockState state, BlockPos pos, ToolAction action);

  /**
   * Runs the hook after transforming a block
   * @param tool    Tool instance, for running modifier hooks
   * @param context Item use context, corresponds to the original targeted position
   * @param state   State before it was transformed
   * @param pos     Position of block that was transformed, may be different from the context
   * @param action  Action that was performed
   */
  static void afterTransformBlock(IToolStackView tool, UseOnContext context, BlockState state, BlockPos pos, ToolAction action) {
    for (ModifierEntry entry : tool.getModifierList()) {
      entry.getHook(ModifierHooks.BLOCK_TRANSFORM).afterTransformBlock(tool, entry, context, state, pos, action);
    }
  }

  /** Merger that runs all hooks */
  record AllMerger(Collection<BlockTransformModifierHook> modules) implements BlockTransformModifierHook {
    @Override
    public void afterTransformBlock(IToolStackView tool, ModifierEntry modifier, UseOnContext context, BlockState state, BlockPos pos, ToolAction action) {
      for (BlockTransformModifierHook module : modules) {
        module.afterTransformBlock(tool, modifier, context, state, pos, action);
      }
    }
  }
}
