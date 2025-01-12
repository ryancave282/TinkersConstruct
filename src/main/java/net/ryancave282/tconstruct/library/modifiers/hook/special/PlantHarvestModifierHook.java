package net.ryancave282.tconstruct.library.modifiers.hook.special;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

/** Modifier module to detect when the harvest modifier harvested a block. */
public interface PlantHarvestModifierHook {
  /**
   * Called after a block is successfully harvested
   * @param tool    Tool used in harvesting
   * @param modifier Entry calling this hook
   * @param context Item use context, corresponds to the original targeted position
   * @param world   Server world instance
   * @param state   State before it was harvested
   * @param pos     Position that was harvested, may be different from the context
   */
  void afterHarvest(IToolStackView tool, ModifierEntry modifier, UseOnContext context, ServerLevel world, BlockState state, BlockPos pos);

  /** Merger that runs all hooks */
  record AllMerger(Collection<PlantHarvestModifierHook> modules) implements PlantHarvestModifierHook {
    @Override
    public void afterHarvest(IToolStackView tool, ModifierEntry modifier, UseOnContext context, ServerLevel world, BlockState state, BlockPos pos) {
      for (PlantHarvestModifierHook module : modules) {
        module.afterHarvest(tool, modifier, context, world, state, pos);
      }
    }
  }
}
