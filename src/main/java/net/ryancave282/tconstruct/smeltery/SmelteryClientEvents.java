package net.ryancave282.tconstruct.smeltery;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent.RegisterGeometryLoaders;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import slimeknights.mantle.client.render.ChannelFluids;
import slimeknights.mantle.client.render.FaucetFluid;
import net.ryancave282.tconstruct.TConstruct;
import net.ryancave282.tconstruct.common.ClientEventBase;
import net.ryancave282.tconstruct.library.TinkerItemDisplays;
import net.ryancave282.tconstruct.library.client.model.block.FluidTextureModel;
import net.ryancave282.tconstruct.library.client.model.block.TankModel;
import net.ryancave282.tconstruct.library.client.model.tools.ToolModel;
import net.ryancave282.tconstruct.smeltery.client.render.CastingBlockEntityRenderer;
import net.ryancave282.tconstruct.smeltery.client.render.ChannelBlockEntityRenderer;
import net.ryancave282.tconstruct.smeltery.client.render.FaucetBlockEntityRenderer;
import net.ryancave282.tconstruct.smeltery.client.render.HeatingStructureBlockEntityRenderer;
import net.ryancave282.tconstruct.smeltery.client.render.MelterBlockEntityRenderer;
import net.ryancave282.tconstruct.smeltery.client.render.TankBlockEntityRenderer;
import net.ryancave282.tconstruct.smeltery.client.screen.AlloyerScreen;
import net.ryancave282.tconstruct.smeltery.client.screen.HeatingStructureScreen;
import net.ryancave282.tconstruct.smeltery.client.screen.MelterScreen;
import net.ryancave282.tconstruct.smeltery.client.screen.SingleItemScreenFactory;

@SuppressWarnings("unused")
@EventBusSubscriber(modid= TConstruct.MOD_ID, value= Dist.CLIENT, bus= Bus.MOD)
public class SmelteryClientEvents extends ClientEventBase {
  @SubscribeEvent
  static void addResourceListener(RegisterClientReloadListenersEvent event) {
    FaucetFluid.initialize(event);
    ChannelFluids.initialize(event);
  }

  @SubscribeEvent
  static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
    event.registerBlockEntityRenderer(TinkerSmeltery.tank.get(), TankBlockEntityRenderer::new);
    event.registerBlockEntityRenderer(TinkerSmeltery.faucet.get(), FaucetBlockEntityRenderer::new);
    event.registerBlockEntityRenderer(TinkerSmeltery.channel.get(), ChannelBlockEntityRenderer::new);
    event.registerBlockEntityRenderer(TinkerSmeltery.table.get(), CastingBlockEntityRenderer::new);
    event.registerBlockEntityRenderer(TinkerSmeltery.basin.get(), CastingBlockEntityRenderer::new);
    event.registerBlockEntityRenderer(TinkerSmeltery.melter.get(), MelterBlockEntityRenderer::new);
    event.registerBlockEntityRenderer(TinkerSmeltery.alloyer.get(), TankBlockEntityRenderer::new);
    event.registerBlockEntityRenderer(TinkerSmeltery.smeltery.get(), HeatingStructureBlockEntityRenderer::new);
    event.registerBlockEntityRenderer(TinkerSmeltery.foundry.get(), HeatingStructureBlockEntityRenderer::new);
  }

  @SubscribeEvent
  static void clientSetup(final FMLClientSetupEvent event) {
    MenuScreens.register(TinkerSmeltery.melterContainer.get(), MelterScreen::new);
    MenuScreens.register(TinkerSmeltery.smelteryContainer.get(), HeatingStructureScreen::new);
    MenuScreens.register(TinkerSmeltery.singleItemContainer.get(), new SingleItemScreenFactory());
    MenuScreens.register(TinkerSmeltery.alloyerContainer.get(), AlloyerScreen::new);
    ToolModel.registerSmallTool(TinkerItemDisplays.MELTER);
    ToolModel.registerSmallTool(TinkerItemDisplays.CASTING_BASIN);
    ToolModel.registerSmallTool(TinkerItemDisplays.CASTING_TABLE);
  }

  @SubscribeEvent
  static void registerModelLoaders(RegisterGeometryLoaders event) {
    event.register("tank", TankModel.LOADER);
    event.register("fluid_texture", FluidTextureModel.LOADER);
  }
}
