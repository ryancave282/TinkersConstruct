package net.ryancave282.tconstruct.smeltery.block.entity.component;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.util.RetexturedHelper;
import net.ryancave282.tconstruct.library.client.model.ModelProperties;
import net.ryancave282.tconstruct.smeltery.TinkerSmeltery;
import net.ryancave282.tconstruct.smeltery.block.entity.component.SmelteryInputOutputBlockEntity.SmelteryFluidIO;
import net.ryancave282.tconstruct.smeltery.block.entity.tank.IDisplayFluidListener;

import javax.annotation.Nonnull;

/**
 * Fluid IO extension to display controller fluid
 */
public class DrainBlockEntity extends SmelteryFluidIO implements IDisplayFluidListener {
  @Getter
  private FluidStack displayFluid = FluidStack.EMPTY;

  public DrainBlockEntity(BlockPos pos, BlockState state) {
    this(TinkerSmeltery.drain.get(), pos, state);
  }

  protected DrainBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  @Nonnull
  @Override
  public ModelData getModelData() {
    return RetexturedHelper.getModelDataBuilder(getTexture()).with(ModelProperties.FLUID_STACK, displayFluid).build();
  }

  @Override
  public void notifyDisplayFluidUpdated(FluidStack fluid) {
    if (!fluid.isFluidEqual(displayFluid)) {
      // no need to copy as the fluid was copied by the caller
      displayFluid = fluid;
      requestModelDataUpdate();
      assert level != null;
      BlockState state = getBlockState();
      level.sendBlockUpdated(worldPosition, state, state, 48);
    }
  }


  /* Updating */

  // override instead of writeSynced to avoid writing master to the main tag twice
  @Override
  public CompoundTag getUpdateTag() {
    CompoundTag nbt = super.getUpdateTag();
    writeMaster(nbt);
    return nbt;
  }

  @Override
  protected boolean shouldSyncOnUpdate() {
    return true;
  }
}
