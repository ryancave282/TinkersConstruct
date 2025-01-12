package net.ryancave282.tconstruct.shared;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent.RegisterGeometryLoaders;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.ryancave282.tconstruct.TConstruct;
import net.ryancave282.tconstruct.common.ClientEventBase;
import net.ryancave282.tconstruct.library.client.book.TinkerBook;
import net.ryancave282.tconstruct.library.client.model.UniqueGuiModel;
import net.ryancave282.tconstruct.library.utils.DomainDisplayName;
import net.ryancave282.tconstruct.shared.client.FluidParticle;

@EventBusSubscriber(modid = TConstruct.MOD_ID, value = Dist.CLIENT, bus = Bus.MOD)
public class CommonsClientEvents extends ClientEventBase {
  @SubscribeEvent
  static void addResourceListeners(RegisterClientReloadListenersEvent event) {
    DomainDisplayName.addResourceListener(event);
  }

  @SubscribeEvent
  static void registerModelLoaders(RegisterGeometryLoaders event) {
    event.register("gui", UniqueGuiModel.LOADER);
  }

  @SubscribeEvent
  static void clientSetup(final FMLClientSetupEvent event) {
    Font unicode = unicodeFontRender();
    TinkerBook.MATERIALS_AND_YOU.fontRenderer = unicode;
    TinkerBook.TINKERS_GADGETRY.fontRenderer = unicode;
    TinkerBook.PUNY_SMELTING.fontRenderer = unicode;
    TinkerBook.MIGHTY_SMELTING.fontRenderer = unicode;
    TinkerBook.FANTASTIC_FOUNDRY.fontRenderer = unicode;
    TinkerBook.ENCYCLOPEDIA.fontRenderer = unicode;
  }

  @SubscribeEvent
  static void registerParticleFactories(RegisterParticleProvidersEvent event) {
    event.registerSpecial(TinkerCommons.fluidParticle.get(), new FluidParticle.Factory());
  }

  private static Font unicodeRenderer;

  /** Gets the unicode font renderer */
  public static Font unicodeFontRender() {
    if (unicodeRenderer == null)
      unicodeRenderer = new Font(rl -> {
        FontManager resourceManager = Minecraft.getInstance().fontManager;
        return resourceManager.fontSets.get(Minecraft.UNIFORM_FONT);
      }, false);

    return unicodeRenderer;
  }
}
