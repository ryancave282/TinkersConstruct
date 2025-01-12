package net.ryancave282.tconstruct.smeltery.block.entity.component;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import slimeknights.mantle.util.RetexturedHelper;
import net.ryancave282.tconstruct.TConstruct;
import net.ryancave282.tconstruct.library.client.model.ModelProperties;
import net.ryancave282.tconstruct.smeltery.TinkerSmeltery;
import net.ryancave282.tconstruct.smeltery.block.entity.component.SmelteryInputOutputBlockEntity.SmelteryFluidIO;
import net.ryancave282.tconstruct.smeltery.block.entity.inventory.DuctItemHandler;
import net.ryancave282.tconstruct.smeltery.block.entity.inventory.DuctTankWrapper;
import net.ryancave282.tconstruct.smeltery.menu.SingleItemContainerMenu;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Filtered drain tile entity
 */
public class DuctBlockEntity extends SmelteryFluidIO implements MenuProvider {
  private static final String TAG_ITEM = "item";
  private static final Component TITLE = TConstruct.makeTranslation("gui", "duct");

  @Getter
  private final DuctItemHandler itemHandler = new DuctItemHandler(this);
  private final LazyOptional<IItemHandler> itemCapability = LazyOptional.of(() -> itemHandler);

  public DuctBlockEntity(BlockPos pos, BlockState state) {
    this(TinkerSmeltery.duct.get(), pos, state);
  }

  protected DuctBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }


  /* Container */

  @Override
  public Component getDisplayName() {
    return TITLE;
  }

  @Nullable
  @Override
  public AbstractContainerMenu createMenu(int id, Inventory inventory, Player playerEntity) {
    return new SingleItemContainerMenu(id, inventory, this);
  }


  /* Capability */

  @Nonnull
  @Override
  public <C> LazyOptional<C> getCapability(Capability<C> capability, @Nullable Direction facing) {
    if (capability == ForgeCapabilities.ITEM_HANDLER) {
      return itemCapability.cast();
    }
    return super.getCapability(capability, facing);
  }

  @Override
  public void invalidateCaps() {
    super.invalidateCaps();
    itemCapability.invalidate();
  }

  @Override
  protected LazyOptional<IFluidHandler> makeWrapper(LazyOptional<IFluidHandler> capability) {
    return LazyOptional.of(() -> new DuctTankWrapper(capability.orElse(emptyInstance), itemHandler));
  }

  @Nonnull
  @Override
  public ModelData getModelData() {
    return RetexturedHelper.getModelDataBuilder(getTexture()).with(ModelProperties.FLUID_STACK, itemHandler.getFluid().copy()).build();
  }

  /** Updates the fluid in model data */
  public void updateFluid() {
    requestModelDataUpdate();
    assert level != null;
    BlockState state = getBlockState();
    level.sendBlockUpdated(worldPosition, state, state, 48);
  }


  /* NBT */

  @Override
  protected boolean shouldSyncOnUpdate() {
    return true;
  }

  @Override
  public void load(CompoundTag tags) {
    super.load(tags);
    if (tags.contains(TAG_ITEM, Tag.TAG_COMPOUND)) {
      itemHandler.readFromNBT(tags.getCompound(TAG_ITEM));
    }
  }

  @Override
  public void handleUpdateTag(CompoundTag tag) {
    super.handleUpdateTag(tag);
    if (level != null && level.isClientSide) {
      updateFluid();
    }
  }

  @Override
  public void saveSynced(CompoundTag tags) {
    super.saveSynced(tags);
    tags.put(TAG_ITEM, itemHandler.writeToNBT());
  }
}
