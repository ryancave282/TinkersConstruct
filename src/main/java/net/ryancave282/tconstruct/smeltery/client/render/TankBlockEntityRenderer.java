package net.ryancave282.tconstruct.smeltery.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.world.level.block.entity.BlockEntity;
import slimeknights.mantle.client.render.FluidCuboid;
import net.ryancave282.tconstruct.common.config.Config;
import net.ryancave282.tconstruct.library.client.RenderUtils;
import net.ryancave282.tconstruct.library.fluid.FluidTankAnimated;
import net.ryancave282.tconstruct.smeltery.block.entity.ITankBlockEntity;

import java.util.List;

public class TankBlockEntityRenderer<T extends BlockEntity & ITankBlockEntity> implements BlockEntityRenderer<T> {
  public TankBlockEntityRenderer(Context context) {}

  @Override
  public void render(T tile, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
    if (Config.CLIENT.tankFluidModel.get()) {
      return;
    }
    // render the fluid
    List<FluidCuboid> fluids = FluidCuboid.REGISTRY.get(tile.getBlockState(), List.of());
    if (!fluids.isEmpty()) {
      FluidTankAnimated tank = tile.getTank();
      for (FluidCuboid fluid : fluids) {
        RenderUtils.renderFluidTank(matrixStack, buffer, fluid, tank, combinedLightIn, partialTicks, true);
      }
    }
  }
}
