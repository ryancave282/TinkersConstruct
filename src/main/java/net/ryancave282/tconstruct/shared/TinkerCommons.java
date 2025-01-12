package net.ryancave282.tconstruct.shared;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StainedGlassBlock;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TintedGlassBlock;
import net.minecraft.world.level.block.WeatheringCopper.WeatherState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;
import slimeknights.mantle.data.predicate.block.BlockPredicate;
import slimeknights.mantle.data.predicate.damage.DamageSourcePredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.mantle.data.predicate.item.ItemPredicate;
import slimeknights.mantle.item.EdibleItem;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.registration.object.ItemObject;
import net.ryancave282.tconstruct.TConstruct;
import net.ryancave282.tconstruct.common.TinkerModule;
import net.ryancave282.tconstruct.common.data.model.ModelSpriteProvider;
import net.ryancave282.tconstruct.common.data.model.TinkerBlockStateProvider;
import net.ryancave282.tconstruct.common.data.model.TinkerItemModelProvider;
import net.ryancave282.tconstruct.common.data.model.TinkerSpriteSourceProvider;
import net.ryancave282.tconstruct.common.data.render.RenderFluidProvider;
import net.ryancave282.tconstruct.common.data.render.RenderItemProvider;
import net.ryancave282.tconstruct.common.json.BlockOrEntityCondition;
import net.ryancave282.tconstruct.common.json.ConfigEnabledCondition;
import net.ryancave282.tconstruct.common.recipe.RecipeCacheInvalidator;
import net.ryancave282.tconstruct.gadgets.TinkerGadgets;
import net.ryancave282.tconstruct.library.json.condition.TagDifferencePresentCondition;
import net.ryancave282.tconstruct.library.json.condition.TagIntersectionPresentCondition;
import net.ryancave282.tconstruct.library.json.condition.TagNotEmptyCondition;
import net.ryancave282.tconstruct.library.json.loot.TagPreferenceLootEntry;
import net.ryancave282.tconstruct.library.json.predicate.TinkerPredicate;
import net.ryancave282.tconstruct.library.recipe.ingredient.BlockTagIngredient;
import net.ryancave282.tconstruct.library.recipe.ingredient.NoContainerIngredient;
import net.ryancave282.tconstruct.library.utils.SlimeBounceHandler;
import net.ryancave282.tconstruct.shared.block.BetterPaneBlock;
import net.ryancave282.tconstruct.shared.block.ClearGlassPaneBlock;
import net.ryancave282.tconstruct.shared.block.ClearStainedGlassBlock;
import net.ryancave282.tconstruct.shared.block.ClearStainedGlassBlock.GlassColor;
import net.ryancave282.tconstruct.shared.block.ClearStainedGlassPaneBlock;
import net.ryancave282.tconstruct.shared.block.GlowBlock;
import net.ryancave282.tconstruct.shared.block.PlatformBlock;
import net.ryancave282.tconstruct.shared.block.SlimeType;
import net.ryancave282.tconstruct.shared.block.SoulGlassBlock;
import net.ryancave282.tconstruct.shared.block.SoulGlassPaneBlock;
import net.ryancave282.tconstruct.shared.block.WaxedPlatformBlock;
import net.ryancave282.tconstruct.shared.block.WeatheringPlatformBlock;
import net.ryancave282.tconstruct.shared.command.TConstructCommand;
import net.ryancave282.tconstruct.shared.data.CommonRecipeProvider;
import net.ryancave282.tconstruct.shared.inventory.BlockContainerOpenedTrigger;
import net.ryancave282.tconstruct.shared.item.CheeseBlockItem;
import net.ryancave282.tconstruct.shared.item.CheeseItem;
import net.ryancave282.tconstruct.shared.item.TinkerBookItem;
import net.ryancave282.tconstruct.shared.item.TinkerBookItem.BookType;
import net.ryancave282.tconstruct.shared.particle.FluidParticleData;
import net.ryancave282.tconstruct.tools.TinkerModifiers;

import static net.ryancave282.tconstruct.TConstruct.getResource;

/**
 * Contains items and blocks and stuff that is shared by multiple modules, but might be required individually
 */
@SuppressWarnings("unused")
public final class TinkerCommons extends TinkerModule {
  /** Creative tab for general items, or those that lack another tab */
  public static final DeferredRegister<CreativeModeTab> tabGeneral = CREATIVE_TABS.register(
    "general", () -> CreativeModeTab.builder().title(TConstruct.makeTranslation("itemGroup", "general"))
                                    .icon(() -> new ItemStack(TinkerCommons.materialsAndYou))
                                    .displayItems(TinkerCommons::addTabItems)
                                    .build());

  /*
   * Blocks
   */
  public static final RegistryObject<GlowBlock> glow = BLOCKS.registerNoItem("glow", () -> new GlowBlock(builder(MapColor.NONE, SoundType.WOOL).noCollission().pushReaction(PushReaction.DESTROY).replaceable().strength(0.0F).lightLevel(s -> 14).noOcclusion()));
  // glass
  public static final ItemObject<StainedGlassBlock> clearGlass = BLOCKS.register("clear_glass", () -> new StainedGlassBlock(glassBuilder(MapColor.NONE)), BLOCK_ITEM);
  public static final ItemObject<TintedGlassBlock> clearTintedGlass = BLOCKS.register("clear_tinted_glass", () -> new TintedGlassBlock(glassBuilder(MapColor.COLOR_GRAY).noOcclusion().isValidSpawn(Blocks::never).isRedstoneConductor(Blocks::never).isSuffocating(Blocks::never).isViewBlocking(Blocks::never)), BLOCK_ITEM);
  public static final ItemObject<ClearGlassPaneBlock> clearGlassPane = BLOCKS.register("clear_glass_pane", () -> new ClearGlassPaneBlock(glassBuilder(MapColor.NONE)), BLOCK_ITEM);
  public static final EnumObject<GlassColor,ClearStainedGlassBlock> clearStainedGlass = BLOCKS.registerEnum(GlassColor.values(), "clear_stained_glass", (color) -> new ClearStainedGlassBlock(glassBuilder(color.getDye().getMapColor()), color), BLOCK_ITEM);
  public static final EnumObject<GlassColor,ClearStainedGlassPaneBlock> clearStainedGlassPane = BLOCKS.registerEnum(GlassColor.values(), "clear_stained_glass_pane", (color) -> new ClearStainedGlassPaneBlock(glassBuilder(color.getDye().getMapColor()), color), BLOCK_ITEM);
  public static final ItemObject<StainedGlassBlock> soulGlass = BLOCKS.register("soul_glass", () -> new SoulGlassBlock(glassBuilder(MapColor.COLOR_BROWN).speedFactor(0.2F).noCollission().isViewBlocking((state, getter, pos) -> true)), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<ClearGlassPaneBlock> soulGlassPane = BLOCKS.register("soul_glass_pane", () -> new SoulGlassPaneBlock(glassBuilder(MapColor.COLOR_BROWN).speedFactor(0.2F)), TOOLTIP_BLOCK_ITEM);
  // panes
  public static final ItemObject<IronBarsBlock> goldBars = BLOCKS.register("gold_bars", () -> new IronBarsBlock(builder(MapColor.NONE, SoundType.METAL).requiresCorrectToolForDrops().strength(3.0F, 6.0F).noOcclusion()), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<BetterPaneBlock> obsidianPane = BLOCKS.register("obsidian_pane", () -> new BetterPaneBlock(builder(MapColor.COLOR_BLACK, SoundType.STONE).requiresCorrectToolForDrops().instrument(NoteBlockInstrument.BASEDRUM).noOcclusion().strength(25.0F, 400.0F)), BLOCK_ITEM);
  // platforms
  public static final ItemObject<PlatformBlock> goldPlatform = BLOCKS.register("gold_platform", () -> new PlatformBlock(builder(MapColor.GOLD, SoundType.COPPER).requiresCorrectToolForDrops().strength(3.0F, 6.0F).noOcclusion()), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<PlatformBlock> ironPlatform = BLOCKS.register("iron_platform", () -> new PlatformBlock(builder(MapColor.METAL, SoundType.COPPER).requiresCorrectToolForDrops().strength(5.0F, 6.0F).noOcclusion()), BLOCK_ITEM);
  public static final ItemObject<PlatformBlock> cobaltPlatform = BLOCKS.register("cobalt_platform", () -> new PlatformBlock(builder(MapColor.COLOR_BLUE, SoundType.COPPER).requiresCorrectToolForDrops().strength(5.0f).noOcclusion()), BLOCK_ITEM);
  public static final EnumObject<WeatherState,PlatformBlock> copperPlatform = new EnumObject.Builder<WeatherState,PlatformBlock>(WeatherState.class)
    .put(WeatherState.UNAFFECTED, BLOCKS.register("copper_platform",           () -> new WeatheringPlatformBlock(WeatherState.UNAFFECTED, builder(MapColor.COLOR_ORANGE,          SoundType.COPPER).requiresCorrectToolForDrops().strength(3.0F, 6.0F).noOcclusion()), BLOCK_ITEM))
    .put(WeatherState.EXPOSED,    BLOCKS.register("exposed_copper_platform",   () -> new WeatheringPlatformBlock(WeatherState.EXPOSED,    builder(MapColor.TERRACOTTA_LIGHT_GRAY, SoundType.COPPER).requiresCorrectToolForDrops().strength(3.0F, 6.0F).noOcclusion()), BLOCK_ITEM))
    .put(WeatherState.WEATHERED,  BLOCKS.register("weathered_copper_platform", () -> new WeatheringPlatformBlock(WeatherState.WEATHERED,  builder(MapColor.WARPED_STEM,           SoundType.COPPER).requiresCorrectToolForDrops().strength(3.0F, 6.0F).noOcclusion()), BLOCK_ITEM))
    .put(WeatherState.OXIDIZED,   BLOCKS.register("oxidized_copper_platform",  () -> new WeatheringPlatformBlock(WeatherState.OXIDIZED,   builder(MapColor.WARPED_NYLIUM,         SoundType.COPPER).requiresCorrectToolForDrops().strength(3.0F, 6.0F).noOcclusion()), BLOCK_ITEM))
    .build();
  public static final EnumObject<WeatherState,PlatformBlock> waxedCopperPlatform = new EnumObject.Builder<WeatherState,PlatformBlock>(WeatherState.class)
    .put(WeatherState.UNAFFECTED, BLOCKS.register("waxed_copper_platform",           () -> new WaxedPlatformBlock(WeatherState.UNAFFECTED, builder(MapColor.COLOR_ORANGE,          SoundType.COPPER).requiresCorrectToolForDrops().strength(3.0F, 6.0F).noOcclusion()), BLOCK_ITEM))
    .put(WeatherState.EXPOSED,    BLOCKS.register("waxed_exposed_copper_platform",   () -> new WaxedPlatformBlock(WeatherState.EXPOSED,    builder(MapColor.TERRACOTTA_LIGHT_GRAY, SoundType.COPPER).requiresCorrectToolForDrops().strength(3.0F, 6.0F).noOcclusion()), BLOCK_ITEM))
    .put(WeatherState.WEATHERED,  BLOCKS.register("waxed_weathered_copper_platform", () -> new WaxedPlatformBlock(WeatherState.WEATHERED,  builder(MapColor.WARPED_STEM,           SoundType.COPPER).requiresCorrectToolForDrops().strength(3.0F, 6.0F).noOcclusion()), BLOCK_ITEM))
    .put(WeatherState.OXIDIZED,   BLOCKS.register("waxed_oxidized_copper_platform",  () -> new WaxedPlatformBlock(WeatherState.OXIDIZED,   builder(MapColor.WARPED_NYLIUM,         SoundType.COPPER).requiresCorrectToolForDrops().strength(3.0F, 6.0F).noOcclusion()), BLOCK_ITEM))
    .build();


  /*
   * Items
   */
  public static final ItemObject<EdibleItem> bacon = ITEMS.register("bacon", () -> new EdibleItem(TinkerFood.BACON));
  public static final ItemObject<EdibleItem> jeweledApple = ITEMS.register("jeweled_apple", () -> new EdibleItem(TinkerFood.JEWELED_APPLE));
  public static final ItemObject<Item> cheeseIngot = ITEMS.register("cheese_ingot", () -> new CheeseItem(new Properties().food(TinkerFood.CHEESE)));
  public static final ItemObject<Block> cheeseBlock = BLOCKS.register("cheese_block", () -> new HalfTransparentBlock(builder(MapColor.COLOR_YELLOW, SoundType.HONEY_BLOCK).strength(1.5F, 3.0F).speedFactor(0.4F).jumpFactor(0.5F).noOcclusion()), block -> new CheeseBlockItem(block, new Properties().food(TinkerFood.CHEESE)));

  public static final ItemObject<TinkerBookItem> materialsAndYou  = ITEMS.register("materials_and_you", () -> new TinkerBookItem(UNSTACKABLE_PROPS, BookType.MATERIALS_AND_YOU));
  public static final ItemObject<TinkerBookItem> punySmelting     = ITEMS.register("puny_smelting",     () -> new TinkerBookItem(UNSTACKABLE_PROPS, BookType.PUNY_SMELTING));
  public static final ItemObject<TinkerBookItem> mightySmelting   = ITEMS.register("mighty_smelting",   () -> new TinkerBookItem(UNSTACKABLE_PROPS, BookType.MIGHTY_SMELTING));
  public static final ItemObject<TinkerBookItem> tinkersGadgetry  = ITEMS.register("tinkers_gadgetry",  () -> new TinkerBookItem(UNSTACKABLE_PROPS, BookType.TINKERS_GADGETRY));
  public static final ItemObject<TinkerBookItem> fantasticFoundry = ITEMS.register("fantastic_foundry", () -> new TinkerBookItem(UNSTACKABLE_PROPS, BookType.FANTASTIC_FOUNDRY));
  public static final ItemObject<TinkerBookItem> encyclopedia     = ITEMS.register("encyclopedia",      () -> new TinkerBookItem(UNSTACKABLE_PROPS, BookType.ENCYCLOPEDIA));

  public static final RegistryObject<ParticleType<FluidParticleData>> fluidParticle = PARTICLE_TYPES.register("fluid", FluidParticleData.Type::new);

  /* Loot conditions */
  public static final RegistryObject<LootItemConditionType> lootConfig = LOOT_CONDITIONS.register(ConfigEnabledCondition.ID.getPath(), () -> new LootItemConditionType(ConfigEnabledCondition.SERIALIZER));
  public static final RegistryObject<LootItemConditionType> lootBlockOrEntity = LOOT_CONDITIONS.register("block_or_entity", () -> new LootItemConditionType(new BlockOrEntityCondition.ConditionSerializer()));
  public static final RegistryObject<LootItemConditionType> lootTagNotEmptyCondition = LOOT_CONDITIONS.register("tag_not_empty", () -> new LootItemConditionType(new TagNotEmptyCondition.ConditionSerializer()));
  public static final RegistryObject<LootPoolEntryType> lootTagPreference = LOOT_ENTRIES.register("tag_preference", () -> new LootPoolEntryType(new TagPreferenceLootEntry.Serializer()));

  /* Slime Balls are edible, believe it or not */
  public static final EnumObject<SlimeType, Item> slimeball = new EnumObject.Builder<SlimeType, Item>(SlimeType.class)
    .put(SlimeType.EARTH, () -> Items.SLIME_BALL)
    .putAll(ITEMS.registerEnum(SlimeType.TINKER, "slime_ball", type -> new Item(ITEM_PROPS)))
    .build();

  public static final BlockContainerOpenedTrigger CONTAINER_OPENED_TRIGGER = new BlockContainerOpenedTrigger();

  public TinkerCommons() {
    TConstructCommand.init();
    NeoForge.EVENT_BUS.addListener(RecipeCacheInvalidator::onReloadListenerReload);
  }

  @SubscribeEvent
  void commonSetupEvent(FMLCommonSetupEvent event) {
    SlimeBounceHandler.init();
  }

  @SubscribeEvent
  void registerRecipeSerializers(RegisterEvent event) {
    if (event.getRegistryKey() == Registries.RECIPE_SERIALIZER) {
      CraftingHelper.register(NoContainerIngredient.ID, NoContainerIngredient.Serializer.INSTANCE);
      CraftingHelper.register(BlockTagIngredient.Serializer.ID, BlockTagIngredient.Serializer.INSTANCE);
      CraftingHelper.register(ConfigEnabledCondition.SERIALIZER);
      CriteriaTriggers.register(CONTAINER_OPENED_TRIGGER);

      CraftingHelper.register(TagIntersectionPresentCondition.SERIALIZER);
      CraftingHelper.register(TagDifferencePresentCondition.SERIALIZER);
      CraftingHelper.register(new TagNotEmptyCondition.ConditionSerializer());
      // mantle
      DamageSourcePredicate.LOADER.register(getResource("direct"), TinkerPredicate.DIRECT_DAMAGE.getLoader());
      LivingEntityPredicate.LOADER.register(getResource("airborne"), TinkerPredicate.AIRBORNE.getLoader());
      ItemPredicate.LOADER.register(getResource("arrow"), TinkerPredicate.ARROW.getLoader());
      ItemPredicate.LOADER.register(getResource("can_melt"), TinkerPredicate.CAN_MELT_ITEM.getLoader());
      BlockPredicate.LOADER.register(getResource("can_melt"), TinkerPredicate.CAN_MELT_BLOCK.getLoader());
    }
  }

  @SubscribeEvent
  void gatherData(final GatherDataEvent event) {
    DataGenerator generator = event.getGenerator();
    PackOutput output = generator.getPackOutput();
    ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
    boolean client = event.includeClient();
    generator.addProvider(client, new ModelSpriteProvider(output, existingFileHelper));
    generator.addProvider(client, new TinkerSpriteSourceProvider(output, existingFileHelper));
    generator.addProvider(client, new TinkerItemModelProvider(output, existingFileHelper));
    generator.addProvider(client, new TinkerBlockStateProvider(output, existingFileHelper));
    generator.addProvider(client, new RenderFluidProvider(output));
    generator.addProvider(client, new RenderItemProvider(output));
    generator.addProvider(event.includeServer(), new CommonRecipeProvider(output));
  }

  /** Adds all relevant items to the creative tab */
  private static void addTabItems(ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output) {
    // books
    output.accept(materialsAndYou);
    output.accept(punySmelting);
    output.accept(mightySmelting);
    output.accept(tinkersGadgetry);
    output.accept(fantasticFoundry);
    output.accept(encyclopedia);

    // food
    output.accept(bacon);
    output.accept(jeweledApple);
    output.accept(cheeseIngot);
    output.accept(cheeseBlock);

    // glass
    output.accept(clearGlass);
    accept(output, clearStainedGlass);
    output.accept(clearTintedGlass);
    output.accept(soulGlass);
    output.accept(clearGlassPane);
    accept(output, clearStainedGlassPane);
    output.accept(soulGlassPane);
    // bars
    output.accept(goldBars);
    output.accept(obsidianPane);
    // platforms
    accept(output, copperPlatform);
    accept(output, waxedCopperPlatform);
    output.accept(ironPlatform);
    output.accept(goldPlatform);
    output.accept(cobaltPlatform);

    // slimeballs are in world

    TinkerGadgets.addTabItems(itemDisplayParameters, output);
    TinkerMaterials.addTabItems(itemDisplayParameters, output);
    TinkerModifiers.addTabItems(itemDisplayParameters, output);
  }
}
