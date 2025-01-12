package net.ryancave282.tconstruct.smeltery.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.ryancave282.tconstruct.smeltery.TinkerSmeltery;
import net.ryancave282.tconstruct.smeltery.block.entity.component.TankBlockEntity;

/** Extension of {@link TankBlockEntity} that uses no TESR, forcing the model fluid render, its more efficient for decoration */
public class LanternBlockEntity extends TankBlockEntity {
  public LanternBlockEntity(BlockPos pos, BlockState state) {
    this(pos, state, TinkerSmeltery.searedLantern.get());
  }

  /** Main constructor */
  public LanternBlockEntity(BlockPos pos, BlockState state, ITankBlock block) {
    super(TinkerSmeltery.lantern.get(), pos, state, block);
  }

  @Override
  public boolean isFluidInModel() {
    return true;
  }
}
