package net.ryancave282.tconstruct.plugin.jsonthings;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

/** This plugin is referenced in the main class, so it may not directly access JSON Things classes. It may access classes that access them however */
public class JsonThingsPlugin {
  /** Called by mod constructor to register JsonThings things */
  public static void onConstruct() {
    FlexBlockTypes.init();
    FlexItemTypes.init();

    if (FMLEnvironment.dist == Dist.CLIENT) {
      PluginClient.init();
    }
  }
}
