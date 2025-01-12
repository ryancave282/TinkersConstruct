package net.ryancave282.tconstruct.library.json.variable.mining;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import net.ryancave282.tconstruct.library.json.variable.block.BlockVariable;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

/**
 * Uses a {@link BlockVariable} to fetch a value from the break speed event
 * @param block     Block variable logic
 * @param fallback  Fallback value if the event is null
 */
public record BlockMiningSpeedVariable(BlockVariable block, float fallback) implements MiningSpeedVariable {
  public static final RecordLoadable<BlockMiningSpeedVariable> LOADER = RecordLoadable.create(
    BlockVariable.LOADER.directField("block_type", BlockMiningSpeedVariable::block),
    FloatLoadable.ANY.requiredField("fallback", BlockMiningSpeedVariable::fallback),
    BlockMiningSpeedVariable::new);

  @Override
  public float getValue(IToolStackView tool, @Nullable BreakSpeed event, @Nullable Player player, @Nullable Direction sideHit) {
    if (event != null) {
      return block.getValue(event.getState());
    }
    return fallback;
  }

  @Override
  public RecordLoadable<BlockMiningSpeedVariable> getLoader() {
    return LOADER;
  }
}
