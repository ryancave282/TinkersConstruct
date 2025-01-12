package net.ryancave282.tconstruct.smeltery.network;

import lombok.AllArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent.Context;
import slimeknights.mantle.inventory.BaseContainerMenu;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import net.ryancave282.tconstruct.smeltery.block.entity.tank.ISmelteryTankHandler;

/**
 * Packet sent when a fluid is clicked in the smeltery UI
 */
@AllArgsConstructor
public class SmelteryFluidClickedPacket implements IThreadsafePacket {
  private final int index;

  public SmelteryFluidClickedPacket(FriendlyByteBuf buffer) {
    index = buffer.readVarInt();
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeVarInt(index);
  }

  @Override
  public void handleThreadsafe(Context context) {
    ServerPlayer sender = context.getSender();
    if (sender != null && !sender.isSpectator()) {
      AbstractContainerMenu container = sender.containerMenu;
      if (container instanceof BaseContainerMenu<?> base && base.getTile() instanceof ISmelteryTankHandler tank) {
        tank.getTank().moveFluidToBottom(index);
      }
    }
  }
}
