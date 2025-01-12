package net.ryancave282.tconstruct;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.registration.RegistrationHelper;
import net.ryancave282.tconstruct.common.TinkerModule;
import net.ryancave282.tconstruct.common.TinkerTags;
import net.ryancave282.tconstruct.common.config.Config;
import net.ryancave282.tconstruct.common.data.AdvancementsProvider;
import net.ryancave282.tconstruct.common.data.DamageTypeProvider;
import net.ryancave282.tconstruct.common.data.loot.GlobalLootModifiersProvider;
import net.ryancave282.tconstruct.common.data.loot.LootTableInjectionProvider;
import net.ryancave282.tconstruct.common.data.loot.TConstructLootTableProvider;
import net.ryancave282.tconstruct.common.data.tags.BiomeTagProvider;
import net.ryancave282.tconstruct.common.data.tags.BlockEntityTypeTagProvider;
import net.ryancave282.tconstruct.common.data.tags.BlockTagProvider;
import net.ryancave282.tconstruct.common.data.tags.DamageTypeTagProvider;
import net.ryancave282.tconstruct.common.data.tags.EnchantmentTagProvider;
import net.ryancave282.tconstruct.common.data.tags.EntityTypeTagProvider;
import net.ryancave282.tconstruct.common.data.tags.FluidTagProvider;
import net.ryancave282.tconstruct.common.data.tags.ItemTagProvider;
import net.ryancave282.tconstruct.common.network.TinkerNetwork;
import net.ryancave282.tconstruct.fluids.TinkerFluids;
import net.ryancave282.tconstruct.gadgets.TinkerGadgets;
import net.ryancave282.tconstruct.library.TinkerItemDisplays;
import net.ryancave282.tconstruct.library.materials.MaterialRegistry;
import net.ryancave282.tconstruct.library.tools.capability.TinkerDataCapability.ComputableDataKey;
import net.ryancave282.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import net.ryancave282.tconstruct.library.tools.definition.ToolDefinitionLoader;
import net.ryancave282.tconstruct.library.tools.layout.StationSlotLayoutLoader;
import net.ryancave282.tconstruct.library.utils.Util;
import net.ryancave282.tconstruct.plugin.DietPlugin;
import net.ryancave282.tconstruct.plugin.ImmersiveEngineeringPlugin;
import net.ryancave282.tconstruct.plugin.craftingtweaks.CraftingTweaksPlugin;
import net.ryancave282.tconstruct.plugin.jsonthings.JsonThingsPlugin;
import net.ryancave282.tconstruct.shared.TinkerClient;
import net.ryancave282.tconstruct.shared.TinkerCommons;
import net.ryancave282.tconstruct.shared.TinkerMaterials;
import net.ryancave282.tconstruct.smeltery.TinkerSmeltery;
import net.ryancave282.tconstruct.tables.TinkerTables;
import net.ryancave282.tconstruct.tools.TinkerModifiers;
import net.ryancave282.tconstruct.tools.TinkerToolParts;
import net.ryancave282.tconstruct.tools.TinkerTools;
import net.ryancave282.tconstruct.world.TinkerStructures;
import net.ryancave282.tconstruct.world.TinkerWorld;
import net.ryancave282.tconstruct.world.data.WorldgenProvider;

import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * TConstruct, the tool mod. Craft your tools with style, then modify until the original is gone!
 *
 * @author mDiyo
 */

@Mod(TConstruct.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class TConstruct {

  public static final String MOD_ID = "tconstruct";
  public static final Logger LOG = LogManager.getLogger(MOD_ID);
  public static final Random RANDOM = new Random();

  /* Instance of this mod, used for grabbing prototype fields */
  public static TConstruct instance;

  public TConstruct(IEventBus bus, Dist dist) {
    instance = this;

    Config.init();
    TinkerItemDisplays.init();

    // initialize modules, done this way rather than with annotations to give us control over the order
    bus.addListener(TConstruct::missingMappings);
    // base
    bus.register(new TinkerCommons());
    bus.register(new TinkerMaterials());
    bus.register(new TinkerFluids());
    bus.register(new TinkerGadgets());
    // world
    bus.register(new TinkerWorld());
    bus.register(new TinkerStructures());
    // tools
    bus.register(new TinkerTables());
    bus.register(new TinkerModifiers());
    bus.register(new TinkerToolParts());
    bus.register(new TinkerTools());
    // smeltery
    bus.register(new TinkerSmeltery());

    // init deferred registers
    TinkerModule.initRegisters();
    TinkerNetwork.setup();
    TinkerTags.init();
    // init client logic
    if (dist == Dist.CLIENT) {
      TinkerClient.onConstruct();
    }
    // compat
    ModList modList = ModList.get();
    if (modList.isLoaded("immersiveengineering")) {
      bus.register(new ImmersiveEngineeringPlugin());
    }
    if (modList.isLoaded("jsonthings")) {
      JsonThingsPlugin.onConstruct();
    }
    if (modList.isLoaded("diet")) {
      DietPlugin.onConstruct();
    }
    if (modList.isLoaded("craftingtweaks")) {
      CraftingTweaksPlugin.onConstruct();
    }
  }

  @SubscribeEvent
  static void commonSetup(final FMLCommonSetupEvent event) {
    MaterialRegistry.init();
    ToolDefinitionLoader.init();
    StationSlotLayoutLoader.init();
  }

  @SubscribeEvent
  static void gatherData(final GatherDataEvent event) {
    DataGenerator generator = event.getGenerator();
    PackOutput packOutput = generator.getPackOutput();
    ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
    CompletableFuture<Provider> lookupProvider = event.getLookupProvider();
    boolean server = event.includeServer();

    // its sometimes cleaner to splitup different registry sets to their own classes, combine them here into a single provider
    RegistrySetBuilder registrySetBuilder = new RegistrySetBuilder();
    DamageTypeProvider.register(registrySetBuilder);
    WorldgenProvider.register(registrySetBuilder);
    DatapackBuiltinEntriesProvider datapackRegistryProvider = new DatapackBuiltinEntriesProvider(packOutput, lookupProvider, registrySetBuilder, Set.of(MOD_ID));
    generator.addProvider(server, datapackRegistryProvider);

    // tags
    BlockTagProvider blockTags = new BlockTagProvider(packOutput, lookupProvider, existingFileHelper);
    generator.addProvider(server, blockTags);
    generator.addProvider(server, new ItemTagProvider(packOutput, lookupProvider, blockTags.contentsGetter(), existingFileHelper));
    generator.addProvider(server, new FluidTagProvider(packOutput, lookupProvider, existingFileHelper));
    generator.addProvider(server, new EntityTypeTagProvider(packOutput, lookupProvider, existingFileHelper));
    generator.addProvider(server, new BlockEntityTypeTagProvider(packOutput, lookupProvider, existingFileHelper));
    generator.addProvider(server, new BiomeTagProvider(packOutput, lookupProvider, existingFileHelper));
    generator.addProvider(server, new EnchantmentTagProvider(packOutput, lookupProvider, existingFileHelper));
    generator.addProvider(server, new DamageTypeTagProvider(packOutput, datapackRegistryProvider.getRegistryProvider(), existingFileHelper));

    // other datagen
    generator.addProvider(server, new TConstructLootTableProvider(packOutput));
    generator.addProvider(server, new AdvancementsProvider(packOutput));
    generator.addProvider(server, new GlobalLootModifiersProvider(packOutput));
    generator.addProvider(server, new LootTableInjectionProvider(packOutput));
  }

  /** Handles missing mappings of all types */
  private static void missingMappings(MissingMappingsEvent event) {
    RegistrationHelper.handleMissingMappings(event, MOD_ID, Registries.BLOCK, name -> switch (name) {
      case "piglin_head" -> Blocks.PIGLIN_HEAD;
      case "piglin_wall_head" -> Blocks.PIGLIN_WALL_HEAD;
      default -> null;
    });
    RegistrationHelper.handleMissingMappings(event, MOD_ID, Registries.ITEM, name -> {
      return switch (name) {
        case "piglin_head" -> Items.PIGLIN_HEAD;
        case "round_plate" -> TinkerToolParts.adzeHead.get();
        case "round_plate_cast" -> TinkerSmeltery.adzeHeadCast.get();
        case "round_plate_sand_cast" -> TinkerSmeltery.adzeHeadCast.getSand();
        case "round_plate_red_sand_cast" -> TinkerSmeltery.adzeHeadCast.getRedSand();
        default -> null;
      };
    });
  }

  /* Utils */

  /**
   * Gets a resource location for Tinkers
   * @param name  Resource path
   * @return  Location for tinkers
   */
  public static ResourceLocation getResource(String name) {
    return new ResourceLocation(MOD_ID, name);
  }

  /**
   * Gets a data key for the capability, mainly used for modifier markers
   * @param name  Resource path
   * @return  Location for tinkers
   */
  public static <T> TinkerDataKey<T> createKey(String name) {
    return TinkerDataKey.of(getResource(name));
  }

  /**
   * Gets a data key for the capability, mainly used for modifier markers
   * @param name         Resource path
   * @param constructor  Constructor for compute if absent
   * @return  Location for tinkers
   */
  public static <T> ComputableDataKey<T> createKey(String name, Supplier<T> constructor) {
    return ComputableDataKey.of(getResource(name), constructor);
  }

  /**
   * Returns the given Resource prefixed with tinkers resource location. Use this function instead of hardcoding
   * resource locations.
   */
  public static String resourceString(String res) {
    return String.format("%s:%s", MOD_ID, res);
  }

  /**
   * Prefixes the given unlocalized name with tinkers prefix. Use this when passing unlocalized names for a uniform
   * namespace.
   */
  public static String prefix(String name) {
    return MOD_ID + "." + name.toLowerCase(Locale.US);
  }

  /** Makes a Tinker's description ID */
  public static String makeDescriptionId(String type, String name) {
    return type + "." + MOD_ID + "." + name;
  }

  /**
   * Makes a translation key for the given name
   * @param base  Base name, such as "block" or "gui"
   * @param name  Object name
   * @return  Translation key
   */
  public static String makeTranslationKey(String base, String name) {
    return Util.makeTranslationKey(base, getResource(name));
  }

  /**
   * Makes a translation text component for the given name
   * @param base  Base name, such as "block" or "gui"
   * @param name  Object name
   * @return  Translation key
   */
  public static MutableComponent makeTranslation(String base, String name) {
    return Component.translatable(makeTranslationKey(base, name));
  }

  /**
   * Makes a translation text component for the given name
   * @param base       Base name, such as "block" or "gui"
   * @param name       Object name
   * @param arguments  Additional arguments to the translation
   * @return  Translation key
   */
  public static MutableComponent makeTranslation(String base, String name, Object... arguments) {
    return Component.translatable(makeTranslationKey(base, name), arguments);
  }

  /**
   * This function is called in the constructor in some internal classes that are a common target for addons to wrongly extend.
   * These classes will cause issues if blindly used by the addon, and are typically trivial for the addon to implement
   * the parts they need if they just put in some effort understanding the code they are copying.

   * As a reminder for addon devs, anything that is not in the library package can and will change arbitrarily. If you need to use a feature outside library, request it on our github.
   * @param self  Class to validate
   */
  public static void sealTinkersClass(Object self, String base, String solution) {
    // note for future maintainers: this does not use Java 9's sealed classes as unless you use modules those are restricted to the same package.
    // Dumb restriction but not like we can change it.
    String name = self.getClass().getName();
    if (!name.startsWith("slimeknights.tconstruct.")) {
      throw new IllegalStateException(base + " being extended from invalid package " + name + ". " + solution);
    }
  }
}
