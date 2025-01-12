package net.ryancave282.tconstruct.common.data.loot;

import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MangrovePropaguleBlock;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer.Builder;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.loot.CanToolPerformAction;
import slimeknights.mantle.loot.function.RetexturedLootFunction;
import slimeknights.mantle.registration.object.BuildingBlockObject;
import slimeknights.mantle.registration.object.FenceBuildingBlockObject;
import slimeknights.mantle.registration.object.WallBuildingBlockObject;
import slimeknights.mantle.registration.object.WoodBlockObject;
import net.ryancave282.tconstruct.TConstruct;
import net.ryancave282.tconstruct.common.registration.GeodeItemObject;
import net.ryancave282.tconstruct.common.registration.GeodeItemObject.BudSize;
import net.ryancave282.tconstruct.gadgets.TinkerGadgets;
import net.ryancave282.tconstruct.library.utils.NBTTags;
import net.ryancave282.tconstruct.shared.TinkerCommons;
import net.ryancave282.tconstruct.shared.TinkerMaterials;
import net.ryancave282.tconstruct.shared.block.ClearStainedGlassBlock;
import net.ryancave282.tconstruct.shared.block.SlimeType;
import net.ryancave282.tconstruct.smeltery.TinkerSmeltery;
import net.ryancave282.tconstruct.tables.TinkerTables;
import net.ryancave282.tconstruct.tables.block.entity.chest.TinkersChestBlockEntity;
import net.ryancave282.tconstruct.tools.TinkerModifiers;
import net.ryancave282.tconstruct.world.TinkerWorld;
import net.ryancave282.tconstruct.world.block.DirtType;
import net.ryancave282.tconstruct.world.block.FoliageType;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BlockLootTableProvider extends BlockLootSubProvider {
  protected BlockLootTableProvider() {
    super(Set.of(), FeatureFlags.REGISTRY.allFlags());
  }

  @SuppressWarnings("deprecation")  // the vanilla registry is perfectly fine for our uses, will make migration away from forge registries easier
  @Override
  protected Iterable<Block> getKnownBlocks() {
    return BuiltInRegistries.BLOCK.stream()
                                  .filter(block -> TConstruct.MOD_ID.equals(BuiltInRegistries.BLOCK.getKey(block).getNamespace()))
                                  .collect(Collectors.toList());
  }

  @Override
  protected void generate() {
    this.addCommon();
    this.addDecorative();
    this.addGadgets();
    this.addWorld();
    this.addTools();
    this.addSmeltery();
    this.addFoundry();
  }

  private void addCommon() {
    this.registerFenceBuildingLootTables(TinkerMaterials.blazewood);
    this.registerFenceBuildingLootTables(TinkerMaterials.nahuatl);
    this.dropSelf(TinkerCommons.cheeseBlock.get());

    this.dropSelf(TinkerModifiers.silkyJewelBlock.get());
    this.dropSelf(TinkerCommons.goldBars.get());
    this.dropSelf(TinkerCommons.goldPlatform.get());
    this.dropSelf(TinkerCommons.ironPlatform.get());
    this.dropSelf(TinkerCommons.cobaltPlatform.get());
    TinkerCommons.copperPlatform.forEach(this::dropSelf);
    TinkerCommons.waxedCopperPlatform.forEach(this::dropSelf);

    // ores
    this.dropSelf(TinkerMaterials.cobalt.get());
    // tier 3
    this.dropSelf(TinkerMaterials.slimesteel.get());
    this.dropSelf(TinkerMaterials.amethystBronze.get());
    this.dropSelf(TinkerMaterials.roseGold.get());
    this.dropSelf(TinkerMaterials.pigIron.get());
    // tier 4
    this.dropSelf(TinkerMaterials.manyullyn.get());
    this.dropSelf(TinkerMaterials.hepatizon.get());
    this.dropSelf(TinkerMaterials.queensSlime.get());
    this.dropSelf(TinkerMaterials.soulsteel.get());
    // tier 5
    this.dropSelf(TinkerMaterials.knightslime.get());
  }

  private void addDecorative() {
    this.dropSelf(TinkerCommons.obsidianPane.get());
    this.dropSelf(TinkerCommons.clearGlass.get());
    this.dropSelf(TinkerCommons.clearTintedGlass.get());
    this.dropSelf(TinkerCommons.clearGlassPane.get());
    for (ClearStainedGlassBlock.GlassColor color : ClearStainedGlassBlock.GlassColor.values()) {
      this.dropSelf(TinkerCommons.clearStainedGlass.get(color));
      this.dropSelf(TinkerCommons.clearStainedGlassPane.get(color));
    }
    this.dropSelf(TinkerCommons.soulGlass.get());
    this.dropSelf(TinkerCommons.soulGlassPane.get());
  }

  private void addTools() {
    // chests
    // tinker chest - name and color
    this.add(TinkerTables.tinkersChest.get(), block -> droppingWithFunctions(block, builder ->
      builder.apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
             .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy(TinkersChestBlockEntity.TAG_CHEST_COLOR, "display.color"))));
    // part chest - just name
    this.add(TinkerTables.partChest.get(), block ->
      droppingWithFunctions(block, builder ->
        builder.apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))));
    // cast chest - name and inventory
    this.add(TinkerTables.castChest.get(), block -> droppingWithFunctions(block, builder ->
      builder.apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
             .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("Items", "TinkerData.Items"))));

    // tables with legs
    this.dropTable(TinkerTables.craftingStation.get());
    this.dropTable(TinkerTables.partBuilder.get());
    this.dropTable(TinkerTables.tinkerStation.get());
    this.dropTable(TinkerTables.tinkersAnvil.get());
    this.dropTable(TinkerTables.modifierWorktable.get());
    this.dropTable(TinkerTables.scorchedAnvil.get());
  }

  private void addWorld() {
    this.add(TinkerWorld.cobaltOre.get(), block -> createOreDrop(block, TinkerWorld.rawCobalt.asItem()));
    this.dropSelf(TinkerWorld.rawCobaltBlock.get());
    TinkerWorld.heads.forEach(this::dropSelf);

    // slime blocks
    TinkerWorld.slime.forEach((type, block) -> {
      if (type != SlimeType.EARTH) {
        this.dropSelf(block);
      }
    });
    TinkerWorld.congealedSlime.forEach((slime, block) -> this.add(block, createSingleItemTableWithSilkTouch(block, TinkerCommons.slimeball.get(slime), ConstantValue.exactly(4))));

    // slime dirt and grass
    TinkerWorld.slimeDirt.forEach(this::dropSelf);
    TinkerWorld.vanillaSlimeGrass.forEach(block -> this.add(block, createSingleItemTableWithSilkTouch(block, Blocks.DIRT)));
    TinkerWorld.earthSlimeGrass.forEach(block -> this.add(block, createSingleItemTableWithSilkTouch(block, TinkerWorld.slimeDirt.get(DirtType.EARTH))));
    TinkerWorld.skySlimeGrass.forEach(block -> this.add(block, createSingleItemTableWithSilkTouch(block, TinkerWorld.slimeDirt.get(DirtType.SKY))));
    TinkerWorld.enderSlimeGrass.forEach(block -> this.add(block, createSingleItemTableWithSilkTouch(block, TinkerWorld.slimeDirt.get(DirtType.ENDER))));
    TinkerWorld.ichorSlimeGrass.forEach(block -> this.add(block, createSingleItemTableWithSilkTouch(block, TinkerWorld.slimeDirt.get(DirtType.ICHOR))));

    // saplings
    TinkerWorld.slimeSapling.forEach((type, block) -> {
      if (type != FoliageType.ENDER) {
        this.dropSelf(block);
      }
    });
    this.add(TinkerWorld.slimeSapling.get(FoliageType.ENDER), sapling -> applyExplosionDecay(
      sapling, LootTable.lootTable().withPool(LootPool.lootPool()
                                                           .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(sapling).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(MangrovePropaguleBlock.AGE, 4)))
                                                           .add(LootItem.lootTableItem(sapling)))));
    TinkerWorld.pottedSlimeSapling.forEach(this::dropPottedContents);
    TinkerWorld.pottedSlimeFern.forEach(this::dropPottedContents);

    // foliage
    TinkerWorld.slimeTallGrass.forEach(block -> this.add(block, BlockLootTableProvider::onlyShears));
    for (FoliageType type : FoliageType.OVERWORLD) {
      // overworld leaves, drops with leaves and slimeballs
      this.add(TinkerWorld.slimeLeaves.get(type), block -> randomDropSlimeBallOrSapling(type, block, TinkerWorld.slimeSapling.get(type), NORMAL_LEAVES_SAPLING_CHANCES));
      this.add(TinkerWorld.slimeFern.get(type), BlockLootTableProvider::onlyShears);
    }
    for (FoliageType type : FoliageType.NETHER) {
      // nether leaves drop self
      this.dropSelf(TinkerWorld.slimeLeaves.get(type));
      this.dropSelf(TinkerWorld.slimeFern.get(type));
    }
    // mangrove leaves do not drop saplings, they just drop sticks. We do slimeballs instead
    this.add(TinkerWorld.slimeLeaves.get(FoliageType.ENDER), leaves -> droppingSilkOrShears(leaves,
      applyExplosionDecay(leaves, LootItem.lootTableItem(TinkerCommons.slimeball.get(SlimeType.ENDER)).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F))))
        .when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, NORMAL_LEAVES_STICK_CHANCES))));
    this.add(TinkerWorld.slimeFern.get(FoliageType.ENDER), BlockLootTableProvider::onlyShears);


    // vines
    this.add(TinkerWorld.skySlimeVine.get(), BlockLootTableProvider::onlyShears);
    this.add(TinkerWorld.enderSlimeVine.get(), BlockLootTableProvider::onlyShears);

    // wood
    this.registerWoodLootTables(TinkerWorld.greenheart);
    this.registerWoodLootTables(TinkerWorld.skyroot);
    this.registerWoodLootTables(TinkerWorld.bloodshroom);
    this.registerWoodLootTables(TinkerWorld.enderbark);
    this.dropSelf(TinkerWorld.enderbarkRoots.get());
    TinkerWorld.slimyEnderbarkRoots.forEach(this::dropSelf);

    // geode
    this.registerGeode(TinkerWorld.earthGeode);
    this.registerGeode(TinkerWorld.skyGeode);
    this.registerGeode(TinkerWorld.ichorGeode);
    this.registerGeode(TinkerWorld.enderGeode);
  }

  private void addGadgets() {
    this.dropSelf(TinkerGadgets.punji.get());
    TinkerGadgets.cake.forEach(block -> this.add(block, noDrop()));
    this.add(TinkerGadgets.magmaCake.get(), noDrop());
  }

  private void addSmeltery() {
    this.dropSelf(TinkerSmeltery.grout.get());
    // controller
    this.dropSelf(TinkerSmeltery.searedMelter.get());
    this.dropSelf(TinkerSmeltery.searedHeater.get());
    this.dropTable(TinkerSmeltery.smelteryController.get());

    // smeltery component
    this.registerBuildingLootTables(TinkerSmeltery.searedStone);
    this.registerWallBuildingLootTables(TinkerSmeltery.searedCobble);
    this.registerBuildingLootTables(TinkerSmeltery.searedPaver);
    this.registerWallBuildingLootTables(TinkerSmeltery.searedBricks);
    this.dropSelf(TinkerSmeltery.searedCrackedBricks.get());
    this.dropSelf(TinkerSmeltery.searedFancyBricks.get());
    this.dropSelf(TinkerSmeltery.searedTriangleBricks.get());
    this.dropSelf(TinkerSmeltery.searedLadder.get());
    this.dropSelf(TinkerSmeltery.searedGlass.get());
    this.dropSelf(TinkerSmeltery.searedSoulGlass.get());
    this.dropSelf(TinkerSmeltery.searedTintedGlass.get());
    this.dropSelf(TinkerSmeltery.searedGlassPane.get());
    this.dropSelf(TinkerSmeltery.searedSoulGlassPane.get());
    this.dropTable(TinkerSmeltery.searedDrain.get());
    this.dropTable(TinkerSmeltery.searedChute.get());
    this.dropTable(TinkerSmeltery.searedDuct.get());

    Function<Block, LootTable.Builder> dropTank = block -> droppingWithFunctions(block, builder ->
      builder.apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
             .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy(NBTTags.TANK, NBTTags.TANK)));
    TinkerSmeltery.searedTank.forEach(block -> this.add(block, dropTank));
    this.add(TinkerSmeltery.searedLantern.get(), dropTank);

    // fluid
    this.dropSelf(TinkerSmeltery.searedFaucet.get());
    this.dropSelf(TinkerSmeltery.searedChannel.get());

    // casting
    this.dropSelf(TinkerSmeltery.searedBasin.get());
    this.dropSelf(TinkerSmeltery.searedTable.get());
  }

  private void addFoundry() {
    this.dropSelf(TinkerSmeltery.netherGrout.get());
    // controller
    this.dropSelf(TinkerSmeltery.scorchedAlloyer.get());
    this.dropTable(TinkerSmeltery.foundryController.get());

    // smeltery component
    this.dropSelf(TinkerSmeltery.scorchedStone.get());
    this.dropSelf(TinkerSmeltery.polishedScorchedStone.get());
    this.registerFenceBuildingLootTables(TinkerSmeltery.scorchedBricks);
    this.dropSelf(TinkerSmeltery.chiseledScorchedBricks.get());
    this.registerBuildingLootTables(TinkerSmeltery.scorchedRoad);
    this.dropSelf(TinkerSmeltery.scorchedLadder.get());
    this.dropSelf(TinkerSmeltery.scorchedGlass.get());
    this.dropSelf(TinkerSmeltery.scorchedSoulGlass.get());
    this.dropSelf(TinkerSmeltery.scorchedTintedGlass.get());
    this.dropSelf(TinkerSmeltery.scorchedGlassPane.get());
    this.dropSelf(TinkerSmeltery.scorchedSoulGlassPane.get());
    this.dropTable(TinkerSmeltery.scorchedDrain.get());
    this.dropTable(TinkerSmeltery.scorchedChute.get());
    this.dropTable(TinkerSmeltery.scorchedDuct.get());

    Function<Block, LootTable.Builder> dropTank = block -> droppingWithFunctions(block, builder ->
      builder.apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
             .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy(NBTTags.TANK, NBTTags.TANK)));
    TinkerSmeltery.scorchedTank.forEach(block -> this.add(block, dropTank));
    this.add(TinkerSmeltery.scorchedLantern.get(), dropTank);

    // fluid
    this.dropSelf(TinkerSmeltery.scorchedFaucet.get());
    this.dropSelf(TinkerSmeltery.scorchedChannel.get());

    // casting
    this.dropSelf(TinkerSmeltery.scorchedBasin.get());
    this.dropSelf(TinkerSmeltery.scorchedTable.get());
  }


  /*
   * Utils
   */

  private static final LootItemCondition.Builder SILK_TOUCH = MatchTool.toolMatches(ItemPredicate.Builder.item().hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1))));
  private static final LootItemCondition.Builder SHEARS = CanToolPerformAction.canToolPerformAction(ToolActions.SHEARS_DIG);
  private static final LootItemCondition.Builder SILK_TOUCH_OR_SHEARS = SHEARS.or(SILK_TOUCH);

  protected static LootTable.Builder onlyShears(ItemLike item) {
    return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).when(SHEARS).add(LootItem.lootTableItem(item)));
  }

  /** Recreation of {@link #createShearsDispatchTable(Block, Builder)} using the tool action instead of the shears item */
  private static LootTable.Builder droppingSilkOrShears(Block block, LootPoolEntryContainer.Builder<?> alternativeLootEntry) {
    return createSelfDropDispatchTable(block, SILK_TOUCH_OR_SHEARS, alternativeLootEntry);
  }

  /** Reimplementation of {@link #createLeavesDrops(Block, Block, float...)} dropping the sticks from the loot table */
  private LootTable.Builder dropSapling(Block leaves, Block sapling, float... fortune) {
    return droppingSilkOrShears(leaves, applyExplosionCondition(leaves, LootItem.lootTableItem(sapling))
      .when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, fortune)));
  }

  private LootTable.Builder randomDropSlimeBallOrSapling(FoliageType foliageType, Block leaves, Block sapling, float... fortune) {
    LootTable.Builder builder = dropSapling(leaves, sapling, fortune);
    SlimeType slime = foliageType.asSlime();
    if (slime != null) {
      return builder.withPool(
        LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                .when(HAS_NO_SHEARS_OR_SILK_TOUCH)
                .add(applyExplosionCondition(leaves, LootItem.lootTableItem(TinkerCommons.slimeball.get(slime)))
                       .when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, 1 / 50f, 1 / 45f, 1 / 40f, 1 / 30f, 1 / 20f))));
    }
    return builder;
  }

  private LootTable.Builder droppingWithFunctions(Block block, Function<LootItem.Builder<?>,LootItem.Builder<?>> mapping) {
    return LootTable.lootTable().withPool(applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(mapping.apply(LootItem.lootTableItem(block)))));
  }

  /**
   * Registers all loot tables for a building block object
   * @param object  Object instance
   */
  private void registerBuildingLootTables(BuildingBlockObject object) {
    this.dropSelf(object.get());
    this.add(object.getSlab(), this::createSlabItemTable);
    this.dropSelf(object.getStairs());
  }

  /**
   * Registers all loot tables for a wall building block object
   * @param object  Object instance
   */
  private void registerWallBuildingLootTables(WallBuildingBlockObject object) {
    registerBuildingLootTables(object);
    this.dropSelf(object.getWall());
  }

  /**
   * Registers all loot tables for a fence building block object
   * @param object  Object instance
   */
  private void registerFenceBuildingLootTables(FenceBuildingBlockObject object) {
    registerBuildingLootTables(object);
    this.dropSelf(object.getFence());
  }

  /** Adds all loot tables relevant to the given wood object */
  private void registerWoodLootTables(WoodBlockObject object) {
    registerFenceBuildingLootTables(object);
    // basic
    this.dropSelf(object.getLog());
    this.dropSelf(object.getStrippedLog());
    this.dropSelf(object.getWood());
    this.dropSelf(object.getStrippedWood());
    // door
    this.dropSelf(object.getFenceGate());
    this.add(object.getDoor(), this::createDoorTable);
    this.dropSelf(object.getTrapdoor());
    // redstone
    this.dropSelf(object.getPressurePlate());
    this.dropSelf(object.getButton());
    // sign
    this.dropSelf(object.getSign());
    this.dropSelf(object.getHangingSign());
  }

  private final Function<Block, LootTable.Builder> ADD_TABLE = block -> droppingWithFunctions(block, (builder) ->
    builder.apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY)).apply(RetexturedLootFunction::new));

  /** Registers a block that drops with its own texture stored in NBT */
  private void dropTable(Block table) {
    this.add(table, ADD_TABLE);
  }

  /** Adds all loot tables relevant to the given geode block set */
  private void registerGeode(GeodeItemObject geode) {
    this.dropSelf(geode.getBlock());
    // cluster
    this.add(geode.getBud(BudSize.CLUSTER), block -> createSilkTouchDispatchTable(
      block, LootItem.lootTableItem(geode.get())
                     .apply(SetItemCountFunction.setCount(ConstantValue.exactly(4.0F)))
                     .apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))
                     .when(MatchTool.toolMatches(ItemPredicate.Builder.item().of(ItemTags.CLUSTER_MAX_HARVESTABLES)))
                     .otherwise(applyExplosionDecay(block, LootItem.lootTableItem(geode.get()).apply(SetItemCountFunction.setCount(ConstantValue.exactly(2.0F)))))));
    // buds
    for (BudSize size : BudSize.SIZES) {
      this.dropWhenSilkTouch(geode.getBud(size));
    }
    this.add(geode.getBudding(), noDrop());
  }
}
