package net.ryancave282.tconstruct.smeltery.data;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.DifferenceIngredient;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.crafting.conditions.TrueCondition;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import slimeknights.mantle.datagen.MantleTags;
import slimeknights.mantle.recipe.crafting.ShapedRetexturedRecipeBuilder;
import slimeknights.mantle.recipe.data.ConsumerWrapperBuilder;
import slimeknights.mantle.recipe.data.ICommonRecipeHelper;
import slimeknights.mantle.recipe.data.ItemNameIngredient;
import slimeknights.mantle.recipe.data.ItemNameOutput;
import slimeknights.mantle.recipe.data.NBTNameIngredient;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.helper.TagEmptyCondition;
import slimeknights.mantle.recipe.ingredient.EntityIngredient;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.mantle.registration.object.FluidObject;
import net.ryancave282.tconstruct.common.TinkerTags;
import net.ryancave282.tconstruct.common.data.BaseRecipeProvider;
import net.ryancave282.tconstruct.common.json.ConfigEnabledCondition;
import net.ryancave282.tconstruct.common.registration.GeodeItemObject;
import net.ryancave282.tconstruct.common.registration.GeodeItemObject.BudSize;
import net.ryancave282.tconstruct.fluids.TinkerFluids;
import net.ryancave282.tconstruct.fluids.fluids.PotionFluidType;
import net.ryancave282.tconstruct.gadgets.TinkerGadgets;
import net.ryancave282.tconstruct.library.data.recipe.ISmelteryRecipeHelper;
import net.ryancave282.tconstruct.library.recipe.FluidValues;
import net.ryancave282.tconstruct.library.recipe.alloying.AlloyRecipeBuilder;
import net.ryancave282.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;
import net.ryancave282.tconstruct.library.recipe.casting.PotionCastingRecipeBuilder;
import net.ryancave282.tconstruct.library.recipe.casting.container.ContainerFillingRecipeBuilder;
import net.ryancave282.tconstruct.library.recipe.entitymelting.EntityMeltingRecipeBuilder;
import net.ryancave282.tconstruct.library.recipe.fuel.MeltingFuelBuilder;
import net.ryancave282.tconstruct.library.recipe.ingredient.BlockTagIngredient;
import net.ryancave282.tconstruct.library.recipe.ingredient.NoContainerIngredient;
import net.ryancave282.tconstruct.library.recipe.melting.IMeltingContainer.OreRateType;
import net.ryancave282.tconstruct.library.recipe.melting.IMeltingRecipe;
import net.ryancave282.tconstruct.library.recipe.melting.MeltingRecipeBuilder;
import net.ryancave282.tconstruct.library.recipe.molding.MoldingRecipeBuilder;
import net.ryancave282.tconstruct.shared.TinkerCommons;
import net.ryancave282.tconstruct.shared.TinkerMaterials;
import net.ryancave282.tconstruct.shared.block.SlimeType;
import net.ryancave282.tconstruct.smeltery.TinkerSmeltery;
import net.ryancave282.tconstruct.smeltery.block.component.SearedTankBlock.TankType;
import net.ryancave282.tconstruct.tools.TinkerModifiers;
import net.ryancave282.tconstruct.world.TinkerHeadType;
import net.ryancave282.tconstruct.world.TinkerWorld;
import net.ryancave282.tconstruct.world.block.FoliageType;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static slimeknights.mantle.Mantle.COMMON;
import static slimeknights.mantle.Mantle.commonResource;

public class SmelteryRecipeProvider extends BaseRecipeProvider implements ISmelteryRecipeHelper, ICommonRecipeHelper {
  public SmelteryRecipeProvider(PackOutput packOutput) {
    super(packOutput);
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Smeltery Recipes";
  }

  @Override
  protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
    this.addCraftingRecipes(consumer);
    this.addSmelteryRecipes(consumer);
    this.addFoundryRecipes(consumer);
    this.addMeltingRecipes(consumer);
    this.addCastingRecipes(consumer);
    this.addAlloyRecipes(consumer);
    this.addEntityMeltingRecipes(consumer);

    this.addCompatRecipes(consumer);
  }

  private void addCraftingRecipes(Consumer<FinishedRecipe> consumer) {
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TinkerSmeltery.copperCan, 3)
                       .define('c', Tags.Items.INGOTS_COPPER)
                       .pattern("c c")
                       .pattern(" c ")
                       .unlockedBy("has_item", has(Tags.Items.INGOTS_COPPER))
                       .save(consumer, prefix(TinkerSmeltery.copperCan, "smeltery/"));

    // sand casts
    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, TinkerSmeltery.blankSandCast, 4)
                          .requires(Tags.Items.SAND_COLORLESS)
                          .unlockedBy("has_casting", has(TinkerSmeltery.searedTable))
                          .save(consumer, location("smeltery/sand_cast"));
    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, TinkerSmeltery.blankRedSandCast, 4)
                          .requires(Tags.Items.SAND_RED)
                          .unlockedBy("has_casting", has(TinkerSmeltery.searedTable))
                          .save(consumer, location("smeltery/red_sand_cast"));

    // pick up sand casts from the table
    MoldingRecipeBuilder.moldingTable(TinkerSmeltery.blankSandCast)
                        .setMaterial(TinkerTags.Items.SAND_CASTS)
                        .save(consumer, location("smeltery/sand_cast_pickup"));
    MoldingRecipeBuilder.moldingTable(TinkerSmeltery.blankRedSandCast)
                        .setMaterial(TinkerTags.Items.RED_SAND_CASTS)
                        .save(consumer, location("smeltery/red_sand_cast_pickup"));
  }

  private void addSmelteryRecipes(Consumer<FinishedRecipe> consumer) {
    String folder = "smeltery/seared/";
    // grout crafting
    ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.grout, 2)
                          .requires(Items.CLAY_BALL)
                          .requires(ItemTags.SAND)
                          .requires(Blocks.GRAVEL)
                          .unlockedBy("has_item", has(Items.CLAY_BALL))
                          .save(consumer, prefix(id(TinkerSmeltery.grout), folder));
    ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.grout, 8)
                          .requires(Blocks.CLAY)
                          .requires(ItemTags.SAND).requires(ItemTags.SAND).requires(ItemTags.SAND).requires(ItemTags.SAND)
                          .requires(Blocks.GRAVEL).requires(Blocks.GRAVEL).requires(Blocks.GRAVEL).requires(Blocks.GRAVEL)
                          .unlockedBy("has_item", has(Blocks.CLAY))
                          .save(consumer, wrap(TinkerSmeltery.grout, folder, "_multiple"));

    // seared bricks from grout
    SimpleCookingRecipeBuilder.smelting(Ingredient.of(TinkerSmeltery.grout), RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.searedBrick, 0.3f, 200)
                        .unlockedBy("has_item", has(TinkerSmeltery.grout))
                        .save(consumer, prefix(TinkerSmeltery.searedBrick, folder));
    Consumer<Consumer<FinishedRecipe>> fastGrout = c ->
      SimpleCookingRecipeBuilder.blasting(Ingredient.of(TinkerSmeltery.grout), RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.searedBrick, 0.3f, 100)
                          .unlockedBy("has_item", has(TinkerSmeltery.grout)).save(c);
    ConditionalRecipe.builder()
                     .addCondition(new ModLoadedCondition("ceramics"))
                     .addRecipe(c -> fastGrout.accept(ConsumerWrapperBuilder.wrap(new ResourceLocation("ceramics", "kiln")).build(c)))
                     .addCondition(TrueCondition.INSTANCE)
                     .addRecipe(fastGrout)
                     .generateAdvancement()
                     .build(consumer, wrap(TinkerSmeltery.searedBrick, folder, "_kiln"));


    // block from bricks
    ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.searedBricks)
                       .define('b', TinkerSmeltery.searedBrick)
                       .pattern("bb")
                       .pattern("bb")
                       .unlockedBy("has_item", has(TinkerSmeltery.searedBrick))
                       .save(consumer, wrap(TinkerSmeltery.searedBricks, folder, "_from_brick"));
    // ladder from bricks
    ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.searedLadder, 4)
                       .define('b', TinkerSmeltery.searedBrick)
                       .define('B', TinkerTags.Items.SEARED_BRICKS)
                       .pattern("b b")
                       .pattern("b b")
                       .pattern("BBB")
                       .unlockedBy("has_item", has(TinkerSmeltery.searedBrick))
                       .save(consumer, prefix(TinkerSmeltery.searedLadder, folder));

    // cobble -> stone
    SimpleCookingRecipeBuilder.smelting(Ingredient.of(TinkerSmeltery.searedCobble.get()), RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.searedStone, 0.1f, 200)
                        .unlockedBy("has_item", has(TinkerSmeltery.searedCobble.get()))
                        .save(consumer, wrap(TinkerSmeltery.searedStone, folder, "_smelting"));
    // stone -> paver
    SimpleCookingRecipeBuilder.smelting(Ingredient.of(TinkerSmeltery.searedStone.get()), RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.searedPaver, 0.1f, 200)
                        .unlockedBy("has_item", has(TinkerSmeltery.searedStone.get()))
                        .save(consumer, wrap(TinkerSmeltery.searedPaver, folder, "_smelting"));
    // stone -> bricks
    ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.searedBricks, 4)
                       .define('b', TinkerSmeltery.searedStone)
                       .pattern("bb")
                       .pattern("bb")
                       .unlockedBy("has_item", has(TinkerSmeltery.searedStone))
                       .save(consumer, wrap(TinkerSmeltery.searedBricks, folder, "_crafting"));
    // bricks -> cracked
    SimpleCookingRecipeBuilder.smelting(Ingredient.of(TinkerSmeltery.searedBricks), RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.searedCrackedBricks, 0.1f, 200)
                        .unlockedBy("has_item", has(TinkerSmeltery.searedBricks))
                        .save(consumer, wrap(TinkerSmeltery.searedCrackedBricks, folder, "_smelting"));
    // brick slabs -> fancy
    ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.searedFancyBricks)
                       .define('s', TinkerSmeltery.searedBricks.getSlab())
                       .pattern("s")
                       .pattern("s")
                       .unlockedBy("has_item", has(TinkerSmeltery.searedBricks.getSlab()))
                       .save(consumer, wrap(TinkerSmeltery.searedFancyBricks, folder, "_crafting"));
    // bricks or stone as input
    this.searedStonecutter(consumer, TinkerSmeltery.searedBricks, folder);
    this.searedStonecutter(consumer, TinkerSmeltery.searedFancyBricks, folder);
    this.searedStonecutter(consumer, TinkerSmeltery.searedTriangleBricks, folder);

    // seared glass
    ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.searedGlass)
                       .define('b', TinkerSmeltery.searedBrick)
                       .define('G', Tags.Items.GLASS_COLORLESS)
                       .pattern(" b ")
                       .pattern("bGb")
                       .pattern(" b ")
                       .unlockedBy("has_item", has(TinkerSmeltery.searedBrick))
                       .save(consumer, prefix(TinkerSmeltery.searedGlass, folder));
    ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerSmeltery.searedGlassPane, 16)
                       .define('#', TinkerSmeltery.searedGlass)
                       .pattern("###")
                       .pattern("###")
                       .unlockedBy("has_item", has(TinkerSmeltery.searedGlass))
                       .save(consumer, prefix(TinkerSmeltery.searedGlassPane, folder));
    ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.searedSoulGlass)
                       .define('b', TinkerSmeltery.searedBrick)
                       .define('G', TinkerCommons.soulGlass)
                       .pattern(" b ")
                       .pattern("bGb")
                       .pattern(" b ")
                       .unlockedBy("has_item", has(TinkerSmeltery.searedBrick))
                       .save(consumer, prefix(TinkerSmeltery.searedSoulGlass, folder));
    ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerSmeltery.searedSoulGlassPane, 16)
                       .define('#', TinkerSmeltery.searedSoulGlass)
                       .pattern("###")
                       .pattern("###")
                       .unlockedBy("has_item", has(TinkerSmeltery.searedSoulGlass))
                       .save(consumer, prefix(TinkerSmeltery.searedSoulGlassPane, folder));
    ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.searedTintedGlass)
                       .define('b', TinkerSmeltery.searedBrick)
                       .define('G', Tags.Items.GLASS_TINTED)
                       .pattern(" b ")
                       .pattern("bGb")
                       .pattern(" b ")
                       .unlockedBy("has_item", has(TinkerSmeltery.searedBrick))
                       .save(consumer, prefix(TinkerSmeltery.searedTintedGlass, folder));

    // stairs and slabs
    this.slabStairsCrafting(consumer, TinkerSmeltery.searedStone, folder, true);
    this.stairSlabWallCrafting(consumer, TinkerSmeltery.searedCobble, folder, true);
    this.slabStairsCrafting(consumer, TinkerSmeltery.searedPaver, folder, true);
    this.stairSlabWallCrafting(consumer, TinkerSmeltery.searedBricks, folder, true);

    // tanks
    ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.searedTank.get(TankType.FUEL_TANK))
                       .define('#', TinkerSmeltery.searedBrick)
                       .define('B', Tags.Items.GLASS)
                       .pattern("###")
                       .pattern("#B#")
                       .pattern("###")
                       .unlockedBy("has_item", has(TinkerSmeltery.searedBrick))
                       .save(consumer, location(folder + "fuel_tank"));
    ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.searedTank.get(TankType.FUEL_GAUGE))
                       .define('#', TinkerSmeltery.searedBrick)
                       .define('B', Tags.Items.GLASS)
                       .pattern("#B#")
                       .pattern("BBB")
                       .pattern("#B#")
                       .unlockedBy("has_item", has(TinkerSmeltery.searedBrick))
                       .save(consumer, location(folder + "fuel_gauge"));
    ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.searedTank.get(TankType.INGOT_TANK))
                       .define('#', TinkerSmeltery.searedBrick)
                       .define('B', Tags.Items.GLASS)
                       .pattern("#B#")
                       .pattern("#B#")
                       .pattern("#B#")
                       .unlockedBy("has_item", has(TinkerSmeltery.searedBrick))
                       .save(consumer, location(folder + "ingot_tank"));
    ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.searedTank.get(TankType.INGOT_GAUGE))
                       .define('#', TinkerSmeltery.searedBrick)
                       .define('B', Tags.Items.GLASS)
                       .pattern("B#B")
                       .pattern("#B#")
                       .pattern("B#B")
                       .unlockedBy("has_item", has(TinkerSmeltery.searedBrick))
                       .save(consumer, location(folder + "ingot_gauge"));
    ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerSmeltery.searedLantern.get(), 3)
                       .define('C', Tags.Items.INGOTS_IRON)
                       .define('B', TinkerSmeltery.searedBrick)
                       .define('P', TinkerSmeltery.searedGlassPane)
                       .pattern(" C ")
                       .pattern("PPP")
                       .pattern("BBB")
                       .unlockedBy("has_item", has(TinkerSmeltery.searedBrick))
                       .save(consumer, location(folder + "lantern"));

    // fluid transfer
    ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.searedFaucet.get(), 3)
                       .define('#', TinkerSmeltery.searedBrick)
                       .pattern("# #")
                       .pattern(" # ")
                       .unlockedBy("has_item", has(TinkerSmeltery.searedBrick))
                       .save(consumer, location(folder + "faucet"));
    ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.searedChannel.get(), 5)
                       .define('#', TinkerSmeltery.searedBrick)
                       .pattern("# #")
                       .pattern("###")
                       .unlockedBy("has_item", has(TinkerSmeltery.searedBrick))
                       .save(consumer, location(folder + "channel"));

    // casting
    ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerSmeltery.searedBasin.get())
                       .define('#', TinkerSmeltery.searedBrick)
                       .pattern("# #")
                       .pattern("# #")
                       .pattern("###")
                       .unlockedBy("has_item", has(TinkerSmeltery.searedBrick))
                       .save(consumer, location(folder + "basin"));
    ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerSmeltery.searedTable.get())
                       .define('#', TinkerSmeltery.searedBrick)
                       .pattern("###")
                       .pattern("# #")
                       .pattern("# #")
                       .unlockedBy("has_item", has(TinkerSmeltery.searedBrick))
                       .save(consumer, location(folder + "table"));

    // peripherals
    ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.searedDrain)
                       .define('#', TinkerSmeltery.searedBrick)
                       .define('C', Tags.Items.INGOTS_COPPER)
                       .pattern("# #")
                       .pattern("C C")
                       .pattern("# #")
                       .unlockedBy("has_item", has(TinkerSmeltery.searedBrick))
                       .save(consumer, location(folder + "drain"));
    ShapedRetexturedRecipeBuilder.fromShaped(
      ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.searedDrain)
                         .define('#', TinkerTags.Items.SMELTERY_BRICKS)
                         .define('C', Tags.Items.INGOTS_COPPER)
                         .pattern("C#C")
                         .unlockedBy("has_item", has(TinkerTags.Items.SMELTERY_BRICKS)))
                                 .setSource(TinkerTags.Items.SMELTERY_BRICKS)
                                 .build(consumer, location(folder + "drain_retextured"));
    ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.searedChute)
                       .define('#', TinkerSmeltery.searedBrick)
                       .define('C', Tags.Items.INGOTS_COPPER)
                       .pattern("#C#")
                       .pattern("   ")
                       .pattern("#C#")
                       .unlockedBy("has_item", has(TinkerSmeltery.searedBrick))
                       .save(consumer, location(folder + "chute"));
    ShapedRetexturedRecipeBuilder.fromShaped(
      ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.searedChute)
                         .define('#', TinkerTags.Items.SMELTERY_BRICKS)
                         .define('C', Tags.Items.INGOTS_COPPER)
                         .pattern("C")
                         .pattern("#")
                         .pattern("C")
                         .unlockedBy("has_item", has(TinkerTags.Items.SMELTERY_BRICKS)))
                                 .setSource(TinkerTags.Items.SMELTERY_BRICKS)
                                 .build(consumer, location(folder + "chute_retextured"));
    ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.searedDuct)
                       .define('#', TinkerSmeltery.searedBrick)
                       .define('C', Tags.Items.INGOTS_GOLD)
                       .pattern("# #")
                       .pattern("C C")
                       .pattern("# #")
                       .unlockedBy("has_item", has(TinkerMaterials.cobalt.getIngotTag()))
                       .save(consumer, location(folder + "duct"));
    ShapedRetexturedRecipeBuilder.fromShaped(
      ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.searedDuct)
                         .define('#', TinkerTags.Items.SMELTERY_BRICKS)
                         .define('C', Tags.Items.INGOTS_GOLD)
                         .pattern("C#C")
                         .unlockedBy("has_item", has(TinkerTags.Items.SMELTERY_BRICKS)))
                                 .setSource(TinkerTags.Items.SMELTERY_BRICKS)
                                 .build(consumer, location(folder + "duct_retextured"));

    // controllers
    ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerSmeltery.searedMelter)
                       .define('G', NoContainerIngredient.of(TinkerSmeltery.searedTank.get(TankType.FUEL_GAUGE), TinkerSmeltery.searedTank.get(TankType.INGOT_GAUGE)))
                       .define('B', TinkerSmeltery.searedBrick)
                       .pattern("BGB")
                       .pattern("BBB")
                       .unlockedBy("has_item", has(TinkerSmeltery.searedBrick))
                       .save(consumer, location(folder + "melter"));
    ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerSmeltery.searedHeater)
                       .define('B', TinkerSmeltery.searedBrick)
                       .pattern("BBB")
                       .pattern("B B")
                       .pattern("BBB")
                       .unlockedBy("has_item", has(TinkerSmeltery.searedBrick))
                       .save(consumer, location(folder + "heater"));

    // casting
    String castingFolder = "smeltery/casting/seared/";

    ItemCastingRecipeBuilder.basinRecipe(TinkerSmeltery.searedStone)
                            .setFluidAndTime(TinkerFluids.searedStone, FluidValues.BRICK_BLOCK)
                            .save(consumer, location(castingFolder + "stone/block_from_seared"));
    this.ingotCasting(consumer, TinkerFluids.searedStone, FluidValues.BRICK, TinkerSmeltery.searedBrick, castingFolder + "brick");
    ItemCastingRecipeBuilder.basinRecipe(TinkerSmeltery.searedGlass)
                            .setFluidAndTime(TinkerFluids.searedStone, FluidValues.BRICK_BLOCK)
                            .setCast(Tags.Items.GLASS_COLORLESS, true)
                            .save(consumer, location(castingFolder + "glass"));
    ItemCastingRecipeBuilder.basinRecipe(TinkerSmeltery.searedSoulGlass)
                            .setFluidAndTime(TinkerFluids.searedStone, FluidValues.BRICK_BLOCK)
                            .setCast(TinkerCommons.soulGlass, true)
                            .save(consumer, location(castingFolder + "glass_soul"));
    ItemCastingRecipeBuilder.basinRecipe(TinkerSmeltery.searedTintedGlass)
                            .setFluidAndTime(TinkerFluids.searedStone, FluidValues.BRICK_BLOCK)
                            .setCast(Tags.Items.GLASS_TINTED, true)
                            .save(consumer, location(castingFolder + "glass_tinted"));
    // discount for casting panes
    ItemCastingRecipeBuilder.tableRecipe(TinkerSmeltery.searedGlassPane)
                            .setFluidAndTime(TinkerFluids.searedStone, FluidValues.BRICK)
                            .setCast(Tags.Items.GLASS_PANES_COLORLESS, true)
                            .save(consumer, location(castingFolder + "glass_pane"));
    ItemCastingRecipeBuilder.tableRecipe(TinkerSmeltery.searedSoulGlassPane)
                            .setFluidAndTime(TinkerFluids.searedStone, FluidValues.BRICK)
                            .setCast(TinkerCommons.soulGlassPane, true)
                            .save(consumer, location(castingFolder + "glass_pane_soul"));

    // smeltery controller
    ItemCastingRecipeBuilder.retexturedBasinRecipe(ItemOutput.fromItem(TinkerSmeltery.smelteryController))
                            .setCast(TinkerTags.Items.SMELTERY_BRICKS, true)
                            .setFluidAndTime(TinkerFluids.moltenCopper, FluidValues.INGOT * 4)
                            .save(consumer, prefix(TinkerSmeltery.smelteryController, castingFolder));

    // craft seared stone from clay and stone
    // button is the closest we have to a single stone brick, just go with it, better than not having the recipe
    ItemCastingRecipeBuilder.tableRecipe(TinkerSmeltery.searedBrick)
                            .setFluidAndTime(TinkerFluids.moltenClay, FluidValues.BRICK / 2)
                            .setCast(Items.STONE_BUTTON, true)
                            .save(consumer, location(castingFolder + "brick_composite"));
    // cobble
    searedCasting(consumer, TinkerSmeltery.searedCobble, CompoundIngredient.of(Ingredient.of(Tags.Items.COBBLESTONE), Ingredient.of(Blocks.GRAVEL)), castingFolder + "cobble/block");
    searedSlabCasting(consumer, TinkerSmeltery.searedCobble.getSlab(), Ingredient.of(Blocks.COBBLESTONE_SLAB), castingFolder + "cobble/slab");
    searedCasting(consumer, TinkerSmeltery.searedCobble.getStairs(), Ingredient.of(Blocks.COBBLESTONE_STAIRS), castingFolder + "cobble/stairs");
    searedCasting(consumer, TinkerSmeltery.searedCobble.getWall(), Ingredient.of(Blocks.COBBLESTONE_WALL), castingFolder + "cobble/wall");
    // stone
    searedCasting(consumer, TinkerSmeltery.searedStone, Ingredient.of(Tags.Items.STONE), castingFolder + "stone/block_from_clay");
    searedSlabCasting(consumer, TinkerSmeltery.searedStone.getSlab(), Ingredient.of(Blocks.STONE_SLAB), castingFolder + "stone/slab");
    searedCasting(consumer, TinkerSmeltery.searedStone.getStairs(), Ingredient.of(Blocks.STONE_STAIRS), castingFolder + "stone/stairs");
    // stone bricks
    searedCasting(consumer, TinkerSmeltery.searedBricks, Ingredient.of(Blocks.STONE_BRICKS), castingFolder + "bricks/block");
    searedSlabCasting(consumer, TinkerSmeltery.searedBricks.getSlab(), Ingredient.of(Blocks.STONE_BRICK_SLAB), castingFolder + "bricks/slab");
    searedCasting(consumer, TinkerSmeltery.searedBricks.getStairs(), Ingredient.of(Blocks.STONE_BRICK_STAIRS), castingFolder + "bricks/stairs");
    searedCasting(consumer, TinkerSmeltery.searedBricks.getWall(), Ingredient.of(Blocks.STONE_BRICK_WALL), castingFolder + "bricks/wall");
    // other seared
    searedCasting(consumer, TinkerSmeltery.searedCrackedBricks, Ingredient.of(Blocks.CRACKED_STONE_BRICKS), castingFolder + "cracked");
    searedCasting(consumer, TinkerSmeltery.searedFancyBricks, Ingredient.of(Blocks.CHISELED_STONE_BRICKS), castingFolder + "chiseled");
    searedCasting(consumer, TinkerSmeltery.searedPaver, Ingredient.of(Blocks.SMOOTH_STONE), castingFolder + "paver");

    // seared blocks
    String meltingFolder = "smeltery/melting/seared/";

    // double efficiency when using smeltery for grout
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.grout), TinkerFluids.searedStone, FluidValues.BRICK * 2, 1.5f)
                        .save(consumer, location(meltingFolder + "grout"));
    // seared stone
    // stairs are here since the cheapest stair recipe is stone cutter, 1 to 1
    MeltingRecipeBuilder.melting(CompoundIngredient.of(Ingredient.of(TinkerTags.Items.SEARED_BLOCKS),
                                                       Ingredient.of(TinkerSmeltery.searedLadder, TinkerSmeltery.searedCobble.getWall(), TinkerSmeltery.searedBricks.getWall(),
                                                                     TinkerSmeltery.searedCobble.getStairs(), TinkerSmeltery.searedStone.getStairs(), TinkerSmeltery.searedBricks.getStairs(), TinkerSmeltery.searedPaver.getStairs())),
																 TinkerFluids.searedStone, FluidValues.BRICK_BLOCK, 2.0f)
                        .save(consumer, location(meltingFolder + "block"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.searedCobble.getSlab(), TinkerSmeltery.searedStone.getSlab(), TinkerSmeltery.searedBricks.getSlab(), TinkerSmeltery.searedPaver.getSlab()),
																 TinkerFluids.searedStone, FluidValues.BRICK_BLOCK / 2, 1.5f)
                        .save(consumer, location(meltingFolder + "slab"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.searedBrick), TinkerFluids.searedStone, FluidValues.BRICK, 1.0f)
                        .save(consumer, location(meltingFolder + "brick"));

    // melt down smeltery components
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.searedFaucet, TinkerSmeltery.searedChannel), TinkerFluids.searedStone, FluidValues.BRICK, 1.5f)
                        .save(consumer, location(meltingFolder + "faucet"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.searedBasin, TinkerSmeltery.searedTable), TinkerFluids.searedStone, FluidValues.BRICK * 7, 2.5f)
                        .save(consumer, location(meltingFolder + "casting"));
    // tanks
    MeltingRecipeBuilder.melting(NoContainerIngredient.of(TinkerSmeltery.searedTank.get(TankType.FUEL_TANK)), TinkerFluids.searedStone, FluidValues.BRICK * 8, 3f)
                        .addByproduct(TinkerFluids.moltenGlass.result(FluidValues.GLASS_BLOCK))
                        .save(consumer, location(meltingFolder + "fuel_tank"));
    MeltingRecipeBuilder.melting(NoContainerIngredient.of(TinkerSmeltery.searedTank.get(TankType.INGOT_TANK)), TinkerFluids.searedStone, FluidValues.BRICK * 6, 2.5f)
                        .addByproduct(TinkerFluids.moltenGlass.result(FluidValues.GLASS_BLOCK * 3))
                        .save(consumer, location(meltingFolder + "ingot_tank"));
    MeltingRecipeBuilder.melting(NoContainerIngredient.of(TinkerSmeltery.searedTank.get(TankType.FUEL_GAUGE), TinkerSmeltery.searedTank.get(TankType.INGOT_GAUGE)), TinkerFluids.searedStone, FluidValues.BRICK * 4, 2f)
                        .addByproduct(TinkerFluids.moltenGlass.result(FluidValues.GLASS_BLOCK * 5))
                        .save(consumer, location(meltingFolder + "gauge"));
    MeltingRecipeBuilder.melting(NoContainerIngredient.of(TinkerSmeltery.searedLantern), TinkerFluids.searedStone, FluidValues.BRICK * 2, 1.0f)
                        .addByproduct(TinkerFluids.moltenGlass.result(FluidValues.GLASS_PANE))
                        .addByproduct(TinkerFluids.moltenIron.result(FluidValues.INGOT / 3))
                        .save(consumer, location(meltingFolder + "lantern"));
    // glass
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.searedGlass), TinkerFluids.searedStone, FluidValues.BRICK * 4, 2f)
                        .addByproduct(TinkerFluids.moltenGlass.result( FluidValues.GLASS_BLOCK))
                        .save(consumer, location(meltingFolder + "glass"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.searedSoulGlass), TinkerFluids.searedStone, FluidValues.BRICK * 4, 2f)
                        .addByproduct(TinkerFluids.liquidSoul.result( FluidValues.GLASS_BLOCK))
                        .save(consumer, location(meltingFolder + "glass_soul"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.searedTintedGlass), TinkerFluids.searedStone, FluidValues.BRICK * 4, 2f)
                        .addByproduct(TinkerFluids.moltenGlass.result(FluidValues.GLASS_BLOCK))
                        .addByproduct(TinkerFluids.moltenAmethyst.result(FluidValues.GEM * 2))
                        .save(consumer, location(meltingFolder + "glass_tinted"));
    // panes
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.searedGlassPane), TinkerFluids.searedStone, FluidValues.BRICK, 1.0f)
                        .addByproduct(TinkerFluids.moltenGlass.result(FluidValues.GLASS_PANE))
                        .save(consumer, location(meltingFolder + "pane"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.searedSoulGlassPane), TinkerFluids.searedStone, FluidValues.BRICK, 1.0f)
                        .addByproduct(TinkerFluids.liquidSoul.result(FluidValues.GLASS_PANE))
                        .save(consumer, location(meltingFolder + "pane_soul"));
    // controllers
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.searedMelter), TinkerFluids.searedStone, FluidValues.BRICK * 9, 3.5f)
                        .addByproduct(TinkerFluids.moltenGlass.result(FluidValues.GLASS_PANE * 5))
                        .save(consumer, location(meltingFolder + "melter"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.searedHeater), TinkerFluids.searedStone, FluidValues.BRICK * 8, 3f)
                        .save(consumer, location(meltingFolder + "heater"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.smelteryController), TinkerFluids.moltenCopper, FluidValues.INGOT * 4, 3.5f)
                        .addByproduct(TinkerFluids.searedStone.result(FluidValues.BRICK * 4))
                        .save(consumer, location("smeltery/melting/metal/copper/smeltery_controller"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.searedDrain, TinkerSmeltery.searedChute), TinkerFluids.moltenCopper, FluidValues.INGOT * 2, 2.5f)
                        .addByproduct(TinkerFluids.searedStone.result(FluidValues.BRICK * 4))
                        .save(consumer, location("smeltery/melting/metal/copper/smeltery_io"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.searedDuct), TinkerFluids.moltenGold, FluidValues.INGOT * 2, 2.5f)
                        .addByproduct(TinkerFluids.searedStone.result(FluidValues.BRICK * 4))
                        .save(consumer, location("smeltery/melting/metal/cobalt/seared_duct"));
  }

  private void addFoundryRecipes(Consumer<FinishedRecipe> consumer) {
    String folder = "smeltery/scorched/";
    // grout crafting
    ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.netherGrout, 2)
                          .requires(Items.MAGMA_CREAM)
                          .requires(Ingredient.of(Blocks.SOUL_SAND, Blocks.SOUL_SOIL))
                          .requires(Blocks.GRAVEL)
                          .unlockedBy("has_item", has(Items.MAGMA_CREAM))
                          .save(consumer, prefix(TinkerSmeltery.netherGrout, folder));

    // scorched bricks from grout
    SimpleCookingRecipeBuilder.smelting(Ingredient.of(TinkerSmeltery.netherGrout), RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.scorchedBrick, 0.3f, 200)
                              .unlockedBy("has_item", has(TinkerSmeltery.netherGrout))
                              .save(consumer, prefix(TinkerSmeltery.scorchedBrick, folder));
    Consumer<Consumer<FinishedRecipe>> fastGrout = c ->
      SimpleCookingRecipeBuilder.blasting(Ingredient.of(TinkerSmeltery.netherGrout), RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.scorchedBrick, 0.3f, 100)
                                .unlockedBy("has_item", has(TinkerSmeltery.netherGrout)).save(c);
    ConditionalRecipe.builder()
                     .addCondition(new ModLoadedCondition("ceramics"))
                     .addRecipe(c -> fastGrout.accept(ConsumerWrapperBuilder.wrap(new ResourceLocation("ceramics", "kiln")).build(c)))
                     .addCondition(TrueCondition.INSTANCE)
                     .addRecipe(fastGrout)
                     .generateAdvancement()
                     .build(consumer, wrap(TinkerSmeltery.scorchedBrick, folder, "_kiln"));

    // block from bricks
    ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.scorchedBricks)
                       .define('b', TinkerSmeltery.scorchedBrick)
                       .pattern("bb")
                       .pattern("bb")
                       .unlockedBy("has_item", has(TinkerSmeltery.scorchedBrick))
                       .save(consumer, wrap(TinkerSmeltery.scorchedBricks, folder, "_from_brick"));
    // ladder from bricks
    ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.scorchedLadder, 4)
                       .define('b', TinkerSmeltery.scorchedBrick)
                       .define('B', TinkerTags.Items.SCORCHED_BLOCKS)
                       .pattern("b b")
                       .pattern("b b")
                       .pattern("BBB")
                       .unlockedBy("has_item", has(TinkerSmeltery.scorchedBrick))
                       .save(consumer, prefix(TinkerSmeltery.scorchedLadder, folder));

    // stone -> polished
    ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.polishedScorchedStone, 4)
                       .define('b', TinkerSmeltery.scorchedStone)
                       .pattern("bb")
                       .pattern("bb")
                       .unlockedBy("has_item", has(TinkerSmeltery.scorchedStone))
                       .save(consumer, wrap(TinkerSmeltery.polishedScorchedStone, folder, "_crafting"));
    // polished -> bricks
    ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.scorchedBricks, 4)
                       .define('b', TinkerSmeltery.polishedScorchedStone)
                       .pattern("bb")
                       .pattern("bb")
                       .unlockedBy("has_item", has(TinkerSmeltery.polishedScorchedStone))
                       .save(consumer, wrap(TinkerSmeltery.scorchedBricks, folder, "_crafting"));
    // stone -> road
    SimpleCookingRecipeBuilder.smelting(Ingredient.of(TinkerSmeltery.scorchedStone), RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.scorchedRoad, 0.1f, 200)
                        .unlockedBy("has_item", has(TinkerSmeltery.scorchedStone))
                        .save(consumer, wrap(TinkerSmeltery.scorchedRoad, folder, "_smelting"));
    // brick slabs -> chiseled
    ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.chiseledScorchedBricks)
                       .define('s', TinkerSmeltery.scorchedBricks.getSlab())
                       .pattern("s")
                       .pattern("s")
                       .unlockedBy("has_item", has(TinkerSmeltery.scorchedBricks.getSlab()))
                       .save(consumer, wrap(TinkerSmeltery.chiseledScorchedBricks, folder, "_crafting"));
    // stonecutting
    this.scorchedStonecutter(consumer, TinkerSmeltery.polishedScorchedStone, folder);
    this.scorchedStonecutter(consumer, TinkerSmeltery.scorchedBricks, folder);
    this.scorchedStonecutter(consumer, TinkerSmeltery.chiseledScorchedBricks, folder);

    // scorched glass
    ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.scorchedGlass)
                       .define('b', TinkerSmeltery.scorchedBrick)
                       .define('G', Tags.Items.GEMS_QUARTZ)
                       .pattern(" b ")
                       .pattern("bGb")
                       .pattern(" b ")
                       .unlockedBy("has_item", has(TinkerSmeltery.scorchedBrick))
                       .save(consumer, prefix(TinkerSmeltery.scorchedGlass, folder));
    ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.scorchedSoulGlass)
                       .define('b', TinkerSmeltery.scorchedBrick)
                       .define('G', TinkerCommons.soulGlass)
                       .pattern(" b ")
                       .pattern("bGb")
                       .pattern(" b ")
                       .unlockedBy("has_item", has(TinkerSmeltery.scorchedBrick))
                       .save(consumer, prefix(TinkerSmeltery.scorchedSoulGlass, folder));
    ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.scorchedTintedGlass)
                       .define('b', TinkerSmeltery.scorchedBrick)
                       .define('G', Tags.Items.GLASS_TINTED)
                       .pattern(" b ")
                       .pattern("bGb")
                       .pattern(" b ")
                       .unlockedBy("has_item", has(TinkerSmeltery.scorchedBrick))
                       .save(consumer, prefix(TinkerSmeltery.scorchedTintedGlass, folder));
    ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerSmeltery.scorchedGlassPane, 16)
                       .define('#', TinkerSmeltery.scorchedGlass)
                       .pattern("###")
                       .pattern("###")
                       .unlockedBy("has_item", has(TinkerSmeltery.scorchedGlass))
                       .save(consumer, prefix(TinkerSmeltery.scorchedGlassPane, folder));
    ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerSmeltery.scorchedSoulGlassPane, 16)
                       .define('#', TinkerSmeltery.scorchedSoulGlass)
                       .pattern("###")
                       .pattern("###")
                       .unlockedBy("has_item", has(TinkerSmeltery.scorchedSoulGlass))
                       .save(consumer, prefix(TinkerSmeltery.scorchedSoulGlassPane, folder));

    // stairs, slabs, and fences
    this.slabStairsCrafting(consumer, TinkerSmeltery.scorchedBricks, folder, true);
    this.slabStairsCrafting(consumer, TinkerSmeltery.scorchedRoad, folder, true);
    ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.scorchedBricks.getFence(), 6)
                       .define('B', TinkerSmeltery.scorchedBricks)
                       .define('b', TinkerSmeltery.scorchedBrick)
                       .pattern("BbB")
                       .pattern("BbB")
                       .unlockedBy("has_item", has(TinkerSmeltery.scorchedBricks))
                       .save(consumer, prefix(id(TinkerSmeltery.scorchedBricks.getFence()), folder));

    // tanks
    ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.scorchedTank.get(TankType.FUEL_TANK))
                       .define('#', TinkerSmeltery.scorchedBrick)
                       .define('B', Tags.Items.GEMS_QUARTZ)
                       .pattern("###")
                       .pattern("#B#")
                       .pattern("###")
                       .unlockedBy("has_item", has(TinkerSmeltery.scorchedBrick))
                       .save(consumer, location(folder + "fuel_tank"));
    ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.scorchedTank.get(TankType.FUEL_GAUGE))
                       .define('#', TinkerSmeltery.scorchedBrick)
                       .define('B', Tags.Items.GEMS_QUARTZ)
                       .pattern("#B#")
                       .pattern("BBB")
                       .pattern("#B#")
                       .unlockedBy("has_item", has(TinkerSmeltery.scorchedBrick))
                       .save(consumer, location(folder + "fuel_gauge"));
    ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.scorchedTank.get(TankType.INGOT_TANK))
                       .define('#', TinkerSmeltery.scorchedBrick)
                       .define('B', Tags.Items.GEMS_QUARTZ)
                       .pattern("#B#")
                       .pattern("#B#")
                       .pattern("#B#")
                       .unlockedBy("has_item", has(TinkerSmeltery.scorchedBrick))
                       .save(consumer, location(folder + "ingot_tank"));
    ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.scorchedTank.get(TankType.INGOT_GAUGE))
                       .define('#', TinkerSmeltery.scorchedBrick)
                       .define('B', Tags.Items.GEMS_QUARTZ)
                       .pattern("B#B")
                       .pattern("#B#")
                       .pattern("B#B")
                       .unlockedBy("has_item", has(TinkerSmeltery.scorchedBrick))
                       .save(consumer, location(folder + "ingot_gauge"));
    ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerSmeltery.scorchedLantern.get(), 3)
                       .define('C', Tags.Items.INGOTS_IRON)
                       .define('B', TinkerSmeltery.scorchedBrick)
                       .define('P', TinkerSmeltery.scorchedGlassPane)
                       .pattern(" C ")
                       .pattern("PPP")
                       .pattern("BBB")
                       .unlockedBy("has_item", has(TinkerSmeltery.scorchedBrick))
                       .save(consumer, location(folder + "lantern"));

    // fluid transfer
    ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.scorchedFaucet.get(), 3)
                       .define('#', TinkerSmeltery.scorchedBrick)
                       .pattern("# #")
                       .pattern(" # ")
                       .unlockedBy("has_item", has(TinkerSmeltery.scorchedBrick))
                       .save(consumer, location(folder + "faucet"));
    ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.scorchedChannel.get(), 5)
                       .define('#', TinkerSmeltery.scorchedBrick)
                       .pattern("# #")
                       .pattern("###")
                       .unlockedBy("has_item", has(TinkerSmeltery.scorchedBrick))
                       .save(consumer, location(folder + "channel"));

    // casting
    ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerSmeltery.scorchedBasin.get())
                       .define('#', TinkerSmeltery.scorchedBrick)
                       .pattern("# #")
                       .pattern("# #")
                       .pattern("###")
                       .unlockedBy("has_item", has(TinkerSmeltery.scorchedBrick))
                       .save(consumer, location(folder + "basin"));
    ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerSmeltery.scorchedTable.get())
                       .define('#', TinkerSmeltery.scorchedBrick)
                       .pattern("###")
                       .pattern("# #")
                       .pattern("# #")
                       .unlockedBy("has_item", has(TinkerSmeltery.scorchedBrick))
                       .save(consumer, location(folder + "table"));


    // peripherals
    ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.scorchedDrain)
                       .define('#', TinkerSmeltery.scorchedBrick)
                       .define('C', TinkerCommons.obsidianPane)
                       .pattern("# #")
                       .pattern("C C")
                       .pattern("# #")
                       .unlockedBy("has_item", has(TinkerSmeltery.scorchedBrick))
                       .save(consumer, location(folder + "drain"));
    ShapedRetexturedRecipeBuilder.fromShaped(
      ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.scorchedDrain)
                         .define('#', TinkerTags.Items.FOUNDRY_BRICKS)
                         .define('C', TinkerCommons.obsidianPane)
                         .pattern("C#C")
                         .unlockedBy("has_item", has(TinkerTags.Items.FOUNDRY_BRICKS)))
                                 .setSource(TinkerTags.Items.FOUNDRY_BRICKS)
                                 .build(consumer, location(folder + "drain_retextured"));
    ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.scorchedChute)
                       .define('#', TinkerSmeltery.scorchedBrick)
                       .define('C', TinkerCommons.obsidianPane)
                       .pattern("#C#")
                       .pattern("   ")
                       .pattern("#C#")
                       .unlockedBy("has_item", has(TinkerSmeltery.scorchedBrick))
                       .save(consumer, location(folder + "chute"));
    ShapedRetexturedRecipeBuilder.fromShaped(
      ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.scorchedChute)
                         .define('#', TinkerTags.Items.FOUNDRY_BRICKS)
                         .define('C', TinkerCommons.obsidianPane)
                         .pattern("C")
                         .pattern("#")
                         .pattern("C")
                         .unlockedBy("has_item", has(TinkerTags.Items.FOUNDRY_BRICKS)))
                                 .setSource(TinkerTags.Items.FOUNDRY_BRICKS)
                                 .build(consumer, location(folder + "chute_retextured"));
    ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.scorchedDuct)
                       .define('#', TinkerSmeltery.scorchedBrick)
                       .define('C', Tags.Items.INGOTS_GOLD)
                       .pattern("# #")
                       .pattern("C C")
                       .pattern("# #")
                       .unlockedBy("has_item", has(TinkerMaterials.cobalt.getIngotTag()))
                       .save(consumer, location(folder + "duct"));
    ShapedRetexturedRecipeBuilder.fromShaped(
      ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.scorchedDuct)
                         .define('#', TinkerTags.Items.FOUNDRY_BRICKS)
                         .define('C', Tags.Items.INGOTS_GOLD)
                         .pattern("C#C")
                         .unlockedBy("has_item", has(TinkerTags.Items.FOUNDRY_BRICKS)))
                                 .setSource(TinkerTags.Items.FOUNDRY_BRICKS)
                                 .build(consumer, location(folder + "duct_retextured"));

    // controllers
    ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerSmeltery.scorchedAlloyer)
                       .define('G', NoContainerIngredient.of(TinkerSmeltery.scorchedTank.get(TankType.INGOT_GAUGE), TinkerSmeltery.scorchedTank.get(TankType.FUEL_GAUGE)))
                       .define('B', TinkerSmeltery.scorchedBrick)
                       .pattern("BGB")
                       .pattern("BBB")
                       .unlockedBy("has_item", has(TinkerSmeltery.scorchedBrick))
                       .save(consumer, location(folder + "alloyer"));

    // casting
    String castingFolder = "smeltery/casting/scorched/";
    ItemCastingRecipeBuilder.basinRecipe(TinkerSmeltery.scorchedStone)
                            .setFluidAndTime(TinkerFluids.scorchedStone, FluidValues.BRICK_BLOCK)
                            .save(consumer, location(castingFolder + "stone_from_scorched"));
    this.ingotCasting(consumer, TinkerFluids.scorchedStone, FluidValues.BRICK, TinkerSmeltery.scorchedBrick, castingFolder + "brick");
    ItemCastingRecipeBuilder.basinRecipe(TinkerSmeltery.scorchedGlass)
                            .setFluidAndTime(TinkerFluids.moltenQuartz, FluidValues.GEM)
                            .setCast(TinkerSmeltery.scorchedBricks, true)
                            .save(consumer, location(castingFolder + "glass"));
    ItemCastingRecipeBuilder.basinRecipe(TinkerSmeltery.scorchedSoulGlass)
                            .setFluidAndTime(TinkerFluids.scorchedStone, FluidValues.BRICK_BLOCK)
                            .setCast(TinkerCommons.soulGlass, true)
                            .save(consumer, location(castingFolder + "glass_soul"));
    ItemCastingRecipeBuilder.basinRecipe(TinkerSmeltery.scorchedTintedGlass)
                            .setFluidAndTime(TinkerFluids.scorchedStone, FluidValues.BRICK_BLOCK)
                            .setCast(Tags.Items.GLASS_TINTED, true)
                            .save(consumer, location(castingFolder + "glass_tinted"));
    // discount for casting panes
    ItemCastingRecipeBuilder.tableRecipe(TinkerSmeltery.scorchedGlassPane)
                            .setFluidAndTime(TinkerFluids.moltenQuartz, FluidValues.GEM_SHARD)
                            .setCast(TinkerSmeltery.scorchedBrick, true)
                            .save(consumer, location(castingFolder + "glass_pane"));
    ItemCastingRecipeBuilder.tableRecipe(TinkerSmeltery.scorchedSoulGlassPane)
                            .setFluidAndTime(TinkerFluids.scorchedStone, FluidValues.BRICK)
                            .setCast(TinkerCommons.soulGlassPane, true)
                            .save(consumer, location(castingFolder + "glass_pane_soul"));
    // craft scorched stone from magma and basalt
    // flint is almost a brick
    ItemCastingRecipeBuilder.tableRecipe(TinkerSmeltery.scorchedBrick)
                            .setFluidAndTime(TinkerFluids.magma, FluidValues.SLIMEBALL / 2)
                            .setCast(Items.FLINT, true)
                            .save(consumer, location(castingFolder + "brick_composite"));
    scorchedCasting(consumer, TinkerSmeltery.scorchedStone, Ingredient.of(Blocks.BASALT , Blocks.GRAVEL), castingFolder + "stone_from_magma");
    scorchedCasting(consumer, TinkerSmeltery.polishedScorchedStone, Ingredient.of(Blocks.POLISHED_BASALT), castingFolder + "polished_from_magma");
    // foundry controller
    ItemCastingRecipeBuilder.retexturedBasinRecipe(ItemOutput.fromItem(TinkerSmeltery.foundryController))
                            .setCast(TinkerTags.Items.FOUNDRY_BRICKS, true)
                            .setFluidAndTime(TinkerFluids.moltenObsidian, FluidValues.GLASS_BLOCK)
                            .save(consumer, prefix(TinkerSmeltery.foundryController, castingFolder));


    // melting
    String meltingFolder = "smeltery/melting/scorched/";

    // double efficiency when using smeltery for grout
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.netherGrout), TinkerFluids.scorchedStone, FluidValues.BRICK * 2, 1.5f)
                        .save(consumer, location(meltingFolder + "grout"));

    // scorched stone
    // stairs are here since the cheapest stair recipe is stone cutter, 1 to 1
    MeltingRecipeBuilder.melting(CompoundIngredient.of(Ingredient.of(TinkerTags.Items.SCORCHED_BLOCKS),
                                                       Ingredient.of(TinkerSmeltery.scorchedLadder, TinkerSmeltery.scorchedBricks.getStairs(), TinkerSmeltery.scorchedRoad.getStairs())),
																 TinkerFluids.scorchedStone, FluidValues.BRICK_BLOCK, 2.0f)
                        .save(consumer, location(meltingFolder + "block"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.scorchedBricks.getSlab(), TinkerSmeltery.scorchedBricks.getSlab(), TinkerSmeltery.scorchedRoad.getSlab()),
																 TinkerFluids.scorchedStone, FluidValues.BRICK_BLOCK / 2, 1.5f)
                        .save(consumer, location(meltingFolder + "slab"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.scorchedBrick), TinkerFluids.scorchedStone, FluidValues.BRICK, 1.0f)
                        .save(consumer, location(meltingFolder + "brick"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.scorchedBricks.getFence()), TinkerFluids.scorchedStone, FluidValues.BRICK * 3, 1.0f)
                        .save(consumer, location(meltingFolder + "fence"));

    // melt down foundry components
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.scorchedFaucet, TinkerSmeltery.scorchedChannel), TinkerFluids.scorchedStone, FluidValues.BRICK, 1.5f)
                        .save(consumer, location(meltingFolder + "faucet"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.scorchedBasin, TinkerSmeltery.scorchedTable), TinkerFluids.scorchedStone, FluidValues.BRICK * 7, 2.5f)
                        .save(consumer, location(meltingFolder + "casting"));
    // tanks
    MeltingRecipeBuilder.melting(NoContainerIngredient.of(TinkerSmeltery.scorchedTank.get(TankType.FUEL_TANK)), TinkerFluids.scorchedStone, FluidValues.BRICK * 8, 3f)
                        .addByproduct(TinkerFluids.moltenQuartz.result(FluidValues.GEM))
                        .save(consumer, location(meltingFolder + "fuel_tank"));
    MeltingRecipeBuilder.melting(NoContainerIngredient.of(TinkerSmeltery.scorchedTank.get(TankType.INGOT_TANK)), TinkerFluids.scorchedStone, FluidValues.BRICK * 6, 2.5f)
                        .addByproduct(TinkerFluids.moltenQuartz.result(FluidValues.GEM * 3))
                        .save(consumer, location(meltingFolder + "ingot_tank"));
    MeltingRecipeBuilder.melting(NoContainerIngredient.of(TinkerSmeltery.scorchedTank.get(TankType.FUEL_GAUGE), TinkerSmeltery.scorchedTank.get(TankType.INGOT_GAUGE)), TinkerFluids.scorchedStone, FluidValues.BRICK * 4, 2f)
                        .addByproduct(TinkerFluids.moltenQuartz.result(FluidValues.GEM * 5))
                        .save(consumer, location(meltingFolder + "gauge"));
    MeltingRecipeBuilder.melting(NoContainerIngredient.of(TinkerSmeltery.scorchedLantern), TinkerFluids.scorchedStone, FluidValues.BRICK * 2, 1.0f)
                        .addByproduct(TinkerFluids.moltenQuartz.result(FluidValues.GEM_SHARD))
                        .addByproduct(TinkerFluids.moltenIron.result(FluidValues.NUGGET * 3))
                        .save(consumer, location(meltingFolder + "lantern"));
    // glass
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.scorchedGlass), TinkerFluids.scorchedStone, FluidValues.BRICK * 4, 2f)
                        .addByproduct(TinkerFluids.moltenQuartz.result(FluidValues.GEM))
                        .save(consumer, location(meltingFolder + "glass"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.scorchedSoulGlass), TinkerFluids.scorchedStone, FluidValues.BRICK * 4, 2f)
                        .addByproduct(TinkerFluids.liquidSoul.result(FluidValues.GLASS_BLOCK))
                        .save(consumer, location(meltingFolder + "glass_soul"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.scorchedTintedGlass), TinkerFluids.scorchedStone, FluidValues.BRICK * 4, 2f)
                        .addByproduct(TinkerFluids.moltenGlass.result(FluidValues.GLASS_BLOCK))
                        .addByproduct(TinkerFluids.moltenAmethyst.result(FluidValues.GEM * 2))
                        .save(consumer, location(meltingFolder + "glass_tinted"));
    // panes
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.scorchedGlassPane), TinkerFluids.scorchedStone, FluidValues.BRICK, 1.0f)
                        .addByproduct(TinkerFluids.moltenQuartz.result(FluidValues.GEM_SHARD))
                        .save(consumer, location(meltingFolder + "pane"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.scorchedSoulGlassPane), TinkerFluids.scorchedStone, FluidValues.BRICK, 1.0f)
                        .addByproduct(TinkerFluids.liquidSoul.result(FluidValues.GLASS_PANE))
                        .save(consumer, location(meltingFolder + "pane_soul"));
    // controllers
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.scorchedAlloyer), TinkerFluids.scorchedStone, FluidValues.BRICK * 9, 3.5f)
                        .addByproduct(TinkerFluids.moltenQuartz.result(FluidValues.GEM * 5))
                        .save(consumer, location(meltingFolder + "melter"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.foundryController), TinkerFluids.moltenObsidian, FluidValues.GLASS_BLOCK, 3.5f)
                        .addByproduct(TinkerFluids.scorchedStone.result(FluidValues.BRICK * 4))
                        .save(consumer, location("smeltery/melting/obsidian/foundry_controller"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.scorchedDrain, TinkerSmeltery.scorchedChute), TinkerFluids.moltenObsidian, FluidValues.GLASS_PANE * 2, 2.5f)
                        .addByproduct(TinkerFluids.scorchedStone.result(FluidValues.BRICK * 4))
                        .save(consumer, location("smeltery/melting/obsidian/foundry_io"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.scorchedDuct), TinkerFluids.moltenGold, FluidValues.INGOT * 2, 2.5f)
                        .addByproduct(TinkerFluids.scorchedStone.result(FluidValues.BRICK * 4))
                        .save(consumer, location("smeltery/melting/metal/cobalt/scorched_duct"));
  }

  private void addCastingRecipes(Consumer<FinishedRecipe> consumer) {
    // Pure Fluid Recipes
    String folder = "smeltery/casting/";

    // container filling
    ContainerFillingRecipeBuilder.tableRecipe(Items.BUCKET, FluidType.BUCKET_VOLUME)
                                 .save(consumer, location(folder + "filling/bucket"));
    ContainerFillingRecipeBuilder.tableRecipe(TinkerSmeltery.copperCan, FluidValues.INGOT)
                                 .save(consumer, location(folder + "filling/copper_can"));
    // potion filling
    FluidIngredient potionBottle = TinkerFluids.potion.ingredient(FluidValues.BOTTLE);
    PotionCastingRecipeBuilder.tableRecipe(Items.POTION)
                              .setBottle(Items.GLASS_BOTTLE)
                              .setFluid(potionBottle)
                              .save(consumer, location(folder + "filling/bottle"));
    PotionCastingRecipeBuilder.tableRecipe(Items.SPLASH_POTION)
                              .setBottle(TinkerTags.Items.SPLASH_BOTTLE)
                              .setFluid(potionBottle)
                              .save(consumer, location(folder + "filling/lingerng_bottle"));
    PotionCastingRecipeBuilder.tableRecipe(Items.LINGERING_POTION)
                              .setBottle(TinkerTags.Items.LINGERING_BOTTLE)
                              .setFluid(potionBottle)
                              .save(consumer, location(folder + "filling/splash_bottle"));
    PotionCastingRecipeBuilder.tableRecipe(Items.TIPPED_ARROW)
                              .setBottle(Items.ARROW)
                              .setFluid(TinkerFluids.potion.ingredient(FluidValues.BOTTLE / 10))
                              .setCoolingTime(20)
                              .save(consumer, location(folder + "filling/tipped_arrow"));
    // tank filling - seared
    ContainerFillingRecipeBuilder.basinRecipe(TinkerSmeltery.searedTank.get(TankType.INGOT_TANK), FluidValues.INGOT)
                                 .save(consumer, location(folder + "filling/seared_ingot_tank"));
    ContainerFillingRecipeBuilder.basinRecipe(TinkerSmeltery.searedTank.get(TankType.INGOT_GAUGE), FluidValues.INGOT)
                                 .save(consumer, location(folder + "filling/seared_ingot_gauge"));
    ContainerFillingRecipeBuilder.basinRecipe(TinkerSmeltery.searedTank.get(TankType.FUEL_TANK), FluidType.BUCKET_VOLUME / 4)
                                 .save(consumer, location(folder + "filling/seared_fuel_tank"));
    ContainerFillingRecipeBuilder.basinRecipe(TinkerSmeltery.searedTank.get(TankType.FUEL_GAUGE), FluidType.BUCKET_VOLUME / 4)
                                 .save(consumer, location(folder + "filling/seared_fuel_gauge"));
    ContainerFillingRecipeBuilder.tableRecipe(TinkerSmeltery.searedLantern, FluidValues.NUGGET)
                                 .save(consumer, location(folder + "filling/seared_lantern_pixel"));
    ContainerFillingRecipeBuilder.basinRecipe(TinkerSmeltery.searedLantern, FluidValues.LANTERN_CAPACITY)
                                 .save(consumer, location(folder + "filling/seared_lantern_full"));
    // tank filling - scorched
    ContainerFillingRecipeBuilder.basinRecipe(TinkerSmeltery.scorchedTank.get(TankType.INGOT_TANK), FluidValues.INGOT)
                                 .save(consumer, location(folder + "filling/scorched_ingot_tank"));
    ContainerFillingRecipeBuilder.basinRecipe(TinkerSmeltery.scorchedTank.get(TankType.INGOT_GAUGE), FluidValues.INGOT)
                                 .save(consumer, location(folder + "filling/scorched_ingot_gauge"));
    ContainerFillingRecipeBuilder.basinRecipe(TinkerSmeltery.scorchedTank.get(TankType.FUEL_TANK), FluidType.BUCKET_VOLUME / 4)
                                 .save(consumer, location(folder + "filling/scorched_fuel_tank"));
    ContainerFillingRecipeBuilder.basinRecipe(TinkerSmeltery.scorchedTank.get(TankType.FUEL_GAUGE), FluidType.BUCKET_VOLUME / 4)
                                 .save(consumer, location(folder + "filling/scorched_fuel_gauge"));
    ContainerFillingRecipeBuilder.tableRecipe(TinkerSmeltery.scorchedLantern, FluidValues.NUGGET)
                                 .save(consumer, location(folder + "filling/scorched_lantern_pixel"));
    ContainerFillingRecipeBuilder.basinRecipe(TinkerSmeltery.scorchedLantern, FluidValues.LANTERN_CAPACITY)
                                 .save(consumer, location(folder + "filling/scorched_lantern_full"));

    // Slime
    String slimeFolder = folder + "slime/";
    this.slimeCasting(consumer, TinkerFluids.earthSlime, true, SlimeType.EARTH, slimeFolder);
    this.slimeCasting(consumer, TinkerFluids.skySlime, false, SlimeType.SKY, slimeFolder);
    this.slimeCasting(consumer, TinkerFluids.enderSlime, false, SlimeType.ENDER, slimeFolder);
    // magma cream
    ItemCastingRecipeBuilder.basinRecipe(Blocks.MAGMA_BLOCK)
                            .setFluidAndTime(TinkerFluids.magma, FluidValues.SLIME_CONGEALED)
                            .save(consumer, location(slimeFolder + "magma_block"));
    ItemCastingRecipeBuilder.tableRecipe(TinkerFluids.magmaBottle)
                            .setFluid(TinkerFluids.magma.getTag(), FluidValues.SLIMEBALL)
                            .setCoolingTime(1)
                            .setCast(Items.GLASS_BOTTLE, true)
                            .save(consumer, location(slimeFolder + "magma_bottle"));

    // glass
    ItemCastingRecipeBuilder.basinRecipe(TinkerCommons.clearGlass)
                            .setFluidAndTime(TinkerFluids.moltenGlass, FluidValues.GLASS_BLOCK)
                            .save(consumer, location(folder + "glass/block"));
    ItemCastingRecipeBuilder.tableRecipe(TinkerCommons.clearGlassPane)
                            .setFluidAndTime(TinkerFluids.moltenGlass, FluidValues.GLASS_PANE)
                            .save(consumer, location(folder + "glass/pane"));
    // soul glass
    ItemCastingRecipeBuilder.basinRecipe(TinkerCommons.soulGlass)
                            .setFluidAndTime(TinkerFluids.liquidSoul, FluidValues.GLASS_BLOCK)
                            .save(consumer, location(folder + "soul/glass"));
    ItemCastingRecipeBuilder.tableRecipe(TinkerCommons.soulGlassPane)
                            .setFluidAndTime(TinkerFluids.liquidSoul, FluidValues.GLASS_PANE)
                            .save(consumer, location(folder + "soul/pane"));

    // clay
    ItemCastingRecipeBuilder.basinRecipe(Blocks.TERRACOTTA)
                            .setFluidAndTime(TinkerFluids.moltenClay, FluidValues.SLIME_CONGEALED)
                            .save(consumer, location(folder + "clay/block"));
    this.ingotCasting(consumer, TinkerFluids.moltenClay, FluidValues.SLIMEBALL, Items.BRICK, folder + "clay/brick");
    this.tagCasting(consumer, TinkerFluids.moltenClay, FluidValues.SLIMEBALL, TinkerSmeltery.plateCast, "plates/brick", folder + "clay/plate", true);

    // emeralds
    this.gemCasting(consumer, TinkerFluids.moltenEmerald, Items.EMERALD, folder + "emerald/gem");
    ItemCastingRecipeBuilder.basinRecipe(Blocks.EMERALD_BLOCK)
                            .setFluidAndTime(TinkerFluids.moltenEmerald, FluidValues.LARGE_GEM_BLOCK)
                            .save(consumer, location(folder + "emerald/block"));

    // quartz
    this.gemCasting(consumer, TinkerFluids.moltenQuartz, Items.QUARTZ, folder + "quartz/gem");
    ItemCastingRecipeBuilder.basinRecipe(Blocks.QUARTZ_BLOCK)
                            .setFluidAndTime(TinkerFluids.moltenQuartz, FluidValues.SMALL_GEM_BLOCK)
                            .save(consumer, location(folder + "quartz/block"));

    // amethyst
    this.gemCasting(consumer, TinkerFluids.moltenAmethyst, Items.AMETHYST_SHARD, folder + "amethyst/shard");
    ItemCastingRecipeBuilder.basinRecipe(Blocks.AMETHYST_BLOCK)
                            .setFluidAndTime(TinkerFluids.moltenAmethyst, FluidValues.SMALL_GEM_BLOCK)
                            .save(consumer, location(folder + "amethyst/block"));
    ItemCastingRecipeBuilder.basinRecipe(TinkerCommons.clearTintedGlass)
                            .setCast(Tags.Items.GLASS_COLORLESS, true)
                            .setFluidAndTime(TinkerFluids.moltenAmethyst, FluidValues.GEM * 2)
                            .save(consumer, location(folder + "amethyst/glass"));

    // diamond
    this.gemCasting(consumer, TinkerFluids.moltenDiamond, Items.DIAMOND, folder + "diamond/gem");
    ItemCastingRecipeBuilder.basinRecipe(Blocks.DIAMOND_BLOCK)
                            .setFluidAndTime(TinkerFluids.moltenDiamond, FluidValues.LARGE_GEM_BLOCK)
                            .save(consumer, location(folder + "diamond/block"));
    ItemCastingRecipeBuilder.tableDuplication()
                            .setCast(CompoundIngredient.of(Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), Ingredient.of(ItemTags.TRIM_TEMPLATES)), false)
                            .setFluidAndTime(TinkerFluids.moltenDiamond, FluidValues.GEM * 5)
                            .save(consumer, location(folder + "diamond/smithing_template"));

    // ender pearls
    ItemCastingRecipeBuilder.tableRecipe(Items.ENDER_PEARL)
                            .setFluidAndTime(TinkerFluids.moltenEnder, FluidValues.SLIMEBALL)
                            .save(consumer, location(folder + "ender/pearl"));
    ItemCastingRecipeBuilder.tableRecipe(Items.ENDER_EYE)
                            .setFluidAndTime(TinkerFluids.moltenEnder, FluidValues.SLIMEBALL)
                            .setCast(Items.BLAZE_POWDER, true)
                            .save(consumer, location(folder + "ender/eye"));

    // obsidian
    ItemCastingRecipeBuilder.basinRecipe(Blocks.OBSIDIAN)
                            .setFluidAndTime(TinkerFluids.moltenObsidian, FluidValues.GLASS_BLOCK)
                            .save(consumer, location(folder + "obsidian/block"));
    ItemCastingRecipeBuilder.tableRecipe(TinkerCommons.obsidianPane)
                            .setFluidAndTime(TinkerFluids.moltenObsidian, FluidValues.GLASS_PANE)
                            .save(consumer, location(folder + "obsidian/pane"));
    // Molten objects with Bucket, Block, Ingot, and Nugget forms with standard values
    String metalFolder = folder + "metal/";
    this.metalCasting(consumer, TinkerFluids.moltenIron,      Blocks.IRON_BLOCK,      Items.IRON_INGOT,      Items.IRON_NUGGET, metalFolder, "iron");
    this.metalCasting(consumer, TinkerFluids.moltenGold,      Blocks.GOLD_BLOCK,      Items.GOLD_INGOT,      Items.GOLD_NUGGET, metalFolder, "gold");
    this.metalCasting(consumer, TinkerFluids.moltenCopper,    Blocks.COPPER_BLOCK,    Items.COPPER_INGOT,    null, metalFolder, "copper");
    this.metalCasting(consumer, TinkerFluids.moltenNetherite, Blocks.NETHERITE_BLOCK, Items.NETHERITE_INGOT, null, metalFolder, "netherite");
    this.ingotCasting(consumer, TinkerFluids.moltenDebris, Items.NETHERITE_SCRAP, metalFolder + "netherite/scrap");
    this.tagCasting(consumer, TinkerFluids.moltenCopper,    FluidValues.NUGGET, TinkerSmeltery.nuggetCast, TinkerTags.Items.NUGGETS_COPPER.location().getPath(),          metalFolder + "copper/nugget", false);
    this.tagCasting(consumer, TinkerFluids.moltenNetherite, FluidValues.NUGGET, TinkerSmeltery.nuggetCast, TinkerTags.Items.NUGGETS_NETHERITE.location().getPath(),       metalFolder + "netherite/nugget", false);
    this.tagCasting(consumer, TinkerFluids.moltenDebris,    FluidValues.NUGGET, TinkerSmeltery.nuggetCast, TinkerTags.Items.NUGGETS_NETHERITE_SCRAP.location().getPath(), metalFolder + "netherite/debris_nugget", false);

    // anything common uses tag output, if its unique to us (slime metals mostly), use direct output
    // ores
    this.metalTagCasting(consumer, TinkerFluids.moltenCobalt, "cobalt", metalFolder, true);
    // tier 3 alloys
    this.metalTagCasting(consumer, TinkerFluids.moltenAmethystBronze, "amethyst_bronze", metalFolder, true);
    this.metalTagCasting(consumer, TinkerFluids.moltenRoseGold, "rose_gold", metalFolder, true);
    this.metalCasting(consumer, TinkerFluids.moltenSlimesteel, TinkerMaterials.slimesteel, metalFolder, "slimesteel");
    this.metalCasting(consumer, TinkerFluids.moltenPigIron, TinkerMaterials.pigIron, metalFolder, "pig_iron");
    // tier 4 alloys
    this.metalTagCasting(consumer, TinkerFluids.moltenManyullyn, "manyullyn", metalFolder, true);
    this.metalTagCasting(consumer, TinkerFluids.moltenHepatizon, "hepatizon", metalFolder, true);
    this.metalCasting(consumer, TinkerFluids.moltenQueensSlime, TinkerMaterials.queensSlime, metalFolder, "queens_slime");
    this.metalCasting(consumer, TinkerFluids.moltenSoulsteel, TinkerMaterials.soulsteel, metalFolder, "soulsteel");
    // tier 5 alloys
    this.metalCasting(consumer, TinkerFluids.moltenKnightslime, TinkerMaterials.knightslime, metalFolder, "knightslime");

    // compat
    for (SmelteryCompat compat : SmelteryCompat.values()) {
      this.metalTagCasting(consumer, compat.getFluid(), compat.getName(), metalFolder, false);
    }

    // water
    String waterFolder = folder + "water/";
    ItemCastingRecipeBuilder.basinRecipe(Blocks.MUD)
                            .setFluidAndTime(new FluidStack(Fluids.WATER, FluidValues.BOTTLE))
                            .setCast(new BlockTagIngredient(BlockTags.CONVERTABLE_TO_MUD), true)
                            .save(consumer, location(waterFolder + "mud"));
    ItemCastingRecipeBuilder.tableRecipe(ItemOutput.fromStack(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER)))
                            .setFluid(MantleTags.Fluids.WATER, FluidValues.BOTTLE * 2)
                            .setCoolingTime(1)
                            .setCast(Items.GLASS_BOTTLE, true)
                            .save(consumer, location(waterFolder + "bottle"));
    ItemCastingRecipeBuilder.tableRecipe(ItemOutput.fromStack(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), Potions.WATER)))
                            .setFluid(MantleTags.Fluids.WATER, FluidValues.BOTTLE * 2)
                            .setCoolingTime(1)
                            .setCast(TinkerTags.Items.SPLASH_BOTTLE, true)
                            .save(consumer, location(waterFolder + "splash"));
    ItemCastingRecipeBuilder.tableRecipe(ItemOutput.fromStack(PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), Potions.WATER)))
                            .setFluid(MantleTags.Fluids.WATER, FluidValues.BOTTLE * 2)
                            .setCoolingTime(1)
                            .setCast(TinkerTags.Items.LINGERING_BOTTLE, true)
                            .save(consumer, location(waterFolder + "lingering"));
    // casting concrete
    BiConsumer<Block,Block> concreteCasting = (powder, block) ->
      ItemCastingRecipeBuilder.basinRecipe(block)
                              .setFluidAndTime(new FluidStack(Fluids.WATER, FluidType.BUCKET_VOLUME / 10))
                              .setCast(powder, true)
                              .save(consumer, prefix(id(block), waterFolder));
    concreteCasting.accept(Blocks.WHITE_CONCRETE_POWDER,      Blocks.WHITE_CONCRETE);
    concreteCasting.accept(Blocks.ORANGE_CONCRETE_POWDER,     Blocks.ORANGE_CONCRETE);
    concreteCasting.accept(Blocks.MAGENTA_CONCRETE_POWDER,    Blocks.MAGENTA_CONCRETE);
    concreteCasting.accept(Blocks.LIGHT_BLUE_CONCRETE_POWDER, Blocks.LIGHT_BLUE_CONCRETE);
    concreteCasting.accept(Blocks.YELLOW_CONCRETE_POWDER,     Blocks.YELLOW_CONCRETE);
    concreteCasting.accept(Blocks.LIME_CONCRETE_POWDER,       Blocks.LIME_CONCRETE);
    concreteCasting.accept(Blocks.PINK_CONCRETE_POWDER,       Blocks.PINK_CONCRETE);
    concreteCasting.accept(Blocks.GRAY_CONCRETE_POWDER,       Blocks.GRAY_CONCRETE);
    concreteCasting.accept(Blocks.LIGHT_GRAY_CONCRETE_POWDER, Blocks.LIGHT_GRAY_CONCRETE);
    concreteCasting.accept(Blocks.CYAN_CONCRETE_POWDER,       Blocks.CYAN_CONCRETE);
    concreteCasting.accept(Blocks.PURPLE_CONCRETE_POWDER,     Blocks.PURPLE_CONCRETE);
    concreteCasting.accept(Blocks.BLUE_CONCRETE_POWDER,       Blocks.BLUE_CONCRETE);
    concreteCasting.accept(Blocks.BROWN_CONCRETE_POWDER,      Blocks.BROWN_CONCRETE);
    concreteCasting.accept(Blocks.GREEN_CONCRETE_POWDER,      Blocks.GREEN_CONCRETE);
    concreteCasting.accept(Blocks.RED_CONCRETE_POWDER,        Blocks.RED_CONCRETE);
    concreteCasting.accept(Blocks.BLACK_CONCRETE_POWDER,      Blocks.BLACK_CONCRETE);

    // blazewood
    ItemCastingRecipeBuilder.basinRecipe(TinkerMaterials.blazewood)
                            .setFluidAndTime(TinkerFluids.blazingBlood, FluidType.BUCKET_VOLUME / 5)
                            .setCast(TinkerWorld.bloodshroom, true)
                            .save(consumer, prefix(TinkerMaterials.blazewood, folder));
    ItemCastingRecipeBuilder.basinRecipe(TinkerMaterials.blazewood.getSlab())
                            .setFluidAndTime(TinkerFluids.blazingBlood, FluidType.BUCKET_VOLUME / 10)
                            .setCast(TinkerWorld.bloodshroom.getSlab(), true)
                            .save(consumer, wrap(TinkerMaterials.blazewood, folder, "_slab"));
    ItemCastingRecipeBuilder.basinRecipe(TinkerMaterials.blazewood.getStairs())
                            .setFluidAndTime(TinkerFluids.blazingBlood, FluidType.BUCKET_VOLUME / 5)
                            .setCast(TinkerWorld.bloodshroom.getStairs(), true)
                            .save(consumer, wrap(TinkerMaterials.blazewood, folder, "_stairs"));
    ItemCastingRecipeBuilder.basinRecipe(TinkerMaterials.blazewood.getFence())
                            .setFluidAndTime(TinkerFluids.blazingBlood, FluidType.BUCKET_VOLUME / 5)
                            .setCast(TinkerWorld.bloodshroom.getFence(), true)
                            .save(consumer, wrap(TinkerMaterials.blazewood, folder, "_fence"));

    // cast molten blaze into blazing stuff
    castingWithCast(consumer, TinkerFluids.blazingBlood, FluidType.BUCKET_VOLUME / 10, TinkerSmeltery.rodCast, Items.BLAZE_ROD, folder + "blaze/rod");
    ItemCastingRecipeBuilder.tableRecipe(Items.MAGMA_CREAM)
                            .setFluidAndTime(TinkerFluids.blazingBlood, FluidType.BUCKET_VOLUME / 20)
                            .setCast(Tags.Items.SLIMEBALLS, true)
                            .save(consumer, location(folder + "blaze/cream"));
    ItemCastingRecipeBuilder.basinRecipe(Blocks.MAGMA_BLOCK)
                            .setFluidAndTime(TinkerFluids.blazingBlood, FluidType.BUCKET_VOLUME / 5)
                            .setCast(TinkerTags.Items.CONGEALED_SLIME, true)
                            .save(consumer, location(folder + "blaze/congealed"));
    ItemCastingRecipeBuilder.tableRecipe(TinkerMaterials.blazingBone)
                            .setFluidAndTime(TinkerFluids.blazingBlood, FluidType.BUCKET_VOLUME / 5)
                            .setCast(TinkerTags.Items.WITHER_BONES, true)
                            .save(consumer, location(folder + "blaze/bone"));

    // honey
    ItemCastingRecipeBuilder.tableRecipe(Items.HONEY_BOTTLE)
                            .setFluid(TinkerFluids.honey.getTag(), FluidValues.BOTTLE)
                            .setCoolingTime(1)
                            .setCast(Items.GLASS_BOTTLE, true)
                            .save(consumer, location(folder + "honey/bottle"));
    ItemCastingRecipeBuilder.basinRecipe(Items.HONEY_BLOCK)
                            .setFluidAndTime(TinkerFluids.honey, FluidValues.BOTTLE * 4)
                            .save(consumer, location(folder + "honey/block"));
    // soup
    ItemCastingRecipeBuilder.tableRecipe(Items.BEETROOT_SOUP)
                            .setFluid(TinkerFluids.beetrootSoup.getTag(), FluidValues.BOWL)
                            .setCast(Items.BOWL, true)
                            .setCoolingTime(1)
                            .save(consumer, location(folder + "soup/beetroot"));
    ItemCastingRecipeBuilder.tableRecipe(Items.MUSHROOM_STEW)
                            .setFluid(TinkerFluids.mushroomStew.getTag(), FluidValues.BOWL)
                            .setCast(Items.BOWL, true)
                            .setCoolingTime(1)
                            .save(consumer, location(folder + "soup/mushroom"));
    ItemCastingRecipeBuilder.tableRecipe(Items.RABBIT_STEW)
                            .setFluid(TinkerFluids.rabbitStew.getTag(), FluidValues.BOWL)
                            .setCast(Items.BOWL, true)
                            .setCoolingTime(1)
                            .save(consumer, location(folder + "soup/rabbit"));
    ItemCastingRecipeBuilder.tableRecipe(TinkerFluids.meatSoupBowl)
                            .setFluid(TinkerFluids.meatSoup.getTag(), FluidValues.BOWL)
                            .setCast(Items.BOWL, true)
                            .setCoolingTime(1)
                            .save(consumer, location(folder + "soup/meat"));
    // venom
    ItemCastingRecipeBuilder.tableRecipe(TinkerFluids.venomBottle)
                            .setFluid(TinkerFluids.venom.getTag(), FluidValues.BOTTLE)
                            .setCoolingTime(1)
                            .setCast(Items.GLASS_BOTTLE, true)
                            .save(consumer, location(folder + "venom_bottle"));
    ItemCastingRecipeBuilder.tableRecipe(TinkerMaterials.venombone)
                            .setFluidAndTime(TinkerFluids.venom, FluidValues.SLIMEBALL)
                            .setCast(Tags.Items.BONES, true)
                            .save(consumer, location(slimeFolder + "venombone"));

    // cheese
    ItemCastingRecipeBuilder.tableRecipe(TinkerCommons.cheeseIngot)
                            .setFluid(ForgeMod.MILK.get(), FluidValues.BOTTLE)
                            .setCast(TinkerSmeltery.ingotCast.getMultiUseTag(), false)
                            .setCoolingTime(20*60*2)
                            .save(consumer, location(folder + "cheese_ingot_gold_cast"));
    ItemCastingRecipeBuilder.tableRecipe(TinkerCommons.cheeseIngot)
                            .setFluid(ForgeMod.MILK.get(), FluidValues.BOTTLE)
                            .setCast(TinkerSmeltery.ingotCast.getSingleUseTag(), true)
                            .setCoolingTime(20*60*2)
                            .save(consumer, location(folder + "cheese_ingot_sand_cast"));
    ItemCastingRecipeBuilder.basinRecipe(TinkerCommons.cheeseBlock)
                            .setFluid(ForgeMod.MILK.get(), FluidType.BUCKET_VOLUME)
                            .setCoolingTime(20*60*5)
                            .save(consumer, location(folder + "cheese_block"));


    String castFolder = "smeltery/casts/";
    this.castCreation(consumer, Tags.Items.INGOTS, TinkerSmeltery.ingotCast, castFolder);
    this.castCreation(consumer, Tags.Items.NUGGETS, TinkerSmeltery.nuggetCast, castFolder);
    this.castCreation(consumer, Tags.Items.GEMS, TinkerSmeltery.gemCast, castFolder);
    this.castCreation(consumer, Tags.Items.RODS, TinkerSmeltery.rodCast, castFolder);
    // other casts are added if needed
    this.castCreation(withCondition(consumer, tagCondition("plates")), getItemTag(COMMON, "plates"), TinkerSmeltery.plateCast, castFolder);
    this.castCreation(withCondition(consumer, tagCondition("gears")),  getItemTag(COMMON, "gears"), TinkerSmeltery.gearCast, castFolder);
    this.castCreation(withCondition(consumer, tagCondition("coins")),  getItemTag(COMMON, "coins"), TinkerSmeltery.coinCast, castFolder);
    this.castCreation(withCondition(consumer, tagCondition("wires")),  getItemTag(COMMON, "wires"), TinkerSmeltery.wireCast, castFolder);

    // misc casting - gold
    ItemCastingRecipeBuilder.tableRecipe(TinkerCommons.goldBars)
                            .setFluidAndTime(TinkerFluids.moltenGold, FluidValues.NUGGET * 3)
                            .save(consumer, location(metalFolder + "gold/bars"));
    ItemCastingRecipeBuilder.tableRecipe(Items.GOLDEN_APPLE)
                            .setFluidAndTime(TinkerFluids.moltenGold, FluidValues.INGOT * 8)
                            .setCast(Items.APPLE, true)
                            .save(consumer, location(metalFolder + "gold/apple"));
    ItemCastingRecipeBuilder.tableRecipe(Items.GLISTERING_MELON_SLICE)
                            .setFluidAndTime(TinkerFluids.moltenGold, FluidValues.NUGGET * 8)
                            .setCast(Items.MELON_SLICE, true)
                            .save(consumer, location(metalFolder + "gold/melon"));
    ItemCastingRecipeBuilder.tableRecipe(Items.GOLDEN_CARROT)
                            .setFluidAndTime(TinkerFluids.moltenGold, FluidValues.NUGGET * 8)
                            .setCast(Items.CARROT, true)
                            .save(consumer, location(metalFolder + "gold/carrot"));
    ItemCastingRecipeBuilder.tableRecipe(Items.CLOCK)
                            .setFluidAndTime(TinkerFluids.moltenGold, FluidValues.INGOT * 4)
                            .setCast(Items.REDSTONE, true)
                            .save(consumer, location(metalFolder + "gold/clock"));
    // misc casting - iron
    ItemCastingRecipeBuilder.tableRecipe(Blocks.IRON_BARS)  // cheaper by 6mb, not a duplication as the melting recipe was adjusted too (like panes)
                            .setFluidAndTime(TinkerFluids.moltenIron, FluidValues.NUGGET * 3)
                            .save(consumer, location(metalFolder + "iron/bars"));
    ItemCastingRecipeBuilder.tableRecipe(Items.LANTERN)
                            .setFluidAndTime(TinkerFluids.moltenIron, FluidValues.NUGGET * 8)
                            .setCast(Blocks.TORCH, true)
                            .save(consumer, location(metalFolder + "iron/lantern"));
    ItemCastingRecipeBuilder.tableRecipe(Items.SOUL_LANTERN)
                            .setFluidAndTime(TinkerFluids.moltenIron, FluidValues.NUGGET * 8)
                            .setCast(Blocks.SOUL_TORCH, true)
                            .save(consumer, location(metalFolder + "iron/soul_lantern"));
    ItemCastingRecipeBuilder.tableRecipe(Items.COMPASS)
                            .setFluidAndTime(TinkerFluids.moltenIron, FluidValues.INGOT * 4)
                            .setCast(Items.REDSTONE, true)
                            .save(consumer, location(metalFolder + "iron/compass"));
    // ender chest
    ItemCastingRecipeBuilder.basinRecipe(Blocks.ENDER_CHEST)
                            .setFluidAndTime(TinkerFluids.moltenObsidian, FluidValues.GLASS_BLOCK * 8)
                            .setCast(Items.ENDER_EYE, true)
                            .save(consumer, location(folder + "obsidian/chest"));
    ItemCastingRecipeBuilder.basinRecipe(TinkerMaterials.nahuatl)
                            .setFluidAndTime(TinkerFluids.moltenObsidian, FluidType.BUCKET_VOLUME)
                            .setCast(ItemTags.PLANKS, true)
                            .save(consumer, location(folder + "obsidian/nahuatl"));
    ItemCastingRecipeBuilder.basinRecipe(TinkerMaterials.nahuatl.getSlab())
                            .setFluidAndTime(TinkerFluids.moltenObsidian, FluidType.BUCKET_VOLUME / 2)
                            .setCast(ItemTags.WOODEN_SLABS, true)
                            .save(consumer, location(folder + "obsidian/nahuatl_slab"));
    ItemCastingRecipeBuilder.basinRecipe(TinkerMaterials.nahuatl.getStairs())
                            .setFluidAndTime(TinkerFluids.moltenObsidian, FluidType.BUCKET_VOLUME)
                            .setCast(ItemTags.WOODEN_STAIRS, true)
                            .save(consumer, location(folder + "obsidian/nahuatl_stairs"));
    ItemCastingRecipeBuilder.basinRecipe(TinkerMaterials.nahuatl.getFence())
                            .setFluidAndTime(TinkerFluids.moltenObsidian, FluidType.BUCKET_VOLUME)
                            .setCast(ItemTags.WOODEN_FENCES, true)
                            .save(consumer, location(folder + "obsidian/nahuatl_fence"));
    // overworld stones from quartz
    ItemCastingRecipeBuilder.basinRecipe(Blocks.ANDESITE)
                            .setFluidAndTime(TinkerFluids.moltenQuartz, FluidValues.GEM / 2)
                            .setCast(Tags.Items.COBBLESTONE, true)
                            .save(consumer, prefix(id(Blocks.ANDESITE), folder + "quartz/"));
    ItemCastingRecipeBuilder.basinRecipe(Blocks.DIORITE)
                            .setFluidAndTime(TinkerFluids.moltenQuartz, FluidValues.GEM / 2)
                            .setCast(Blocks.ANDESITE, true)
                            .save(consumer, prefix(id(Blocks.DIORITE), folder + "quartz/"));
    ItemCastingRecipeBuilder.basinRecipe(Blocks.GRANITE)
                            .setFluidAndTime(TinkerFluids.moltenQuartz, FluidValues.GEM)
                            .setCast(Blocks.DIORITE, true)
                            .save(consumer, prefix(id(Blocks.GRANITE), folder + "quartz/"));
  }

  private void addMeltingRecipes(Consumer<FinishedRecipe> consumer) {
    String folder = "smeltery/melting/";

    // water from ice
    MeltingRecipeBuilder.melting(Ingredient.of(Items.ICE), Fluids.WATER, FluidType.BUCKET_VOLUME, 1.0f)
                        .save(consumer, location(folder + "water/ice"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.PACKED_ICE), Fluids.WATER, FluidType.BUCKET_VOLUME * 9, 3.0f)
                        .save(consumer, location(folder + "water/packed_ice"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.BLUE_ICE), Fluids.WATER, FluidType.BUCKET_VOLUME * 81, 9.0f)
                        .save(consumer, location(folder + "water/blue_ice"));
    // water from snow
    MeltingRecipeBuilder.melting(Ingredient.of(Items.SNOWBALL), Fluids.WATER, FluidType.BUCKET_VOLUME / 8, 0.5f)
                        .save(consumer, location(folder + "water/snowball"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.SNOW_BLOCK), Fluids.WATER, FluidType.BUCKET_VOLUME / 2, 0.75f)
                        .save(consumer, location(folder + "water/snow_block"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.SNOW), Fluids.WATER, FluidType.BUCKET_VOLUME / 8, 0.5f)
                        .save(consumer, location(folder + "water/snow_layer"));

    // ores
    String metalFolder = folder + "metal/";
    metalMelting(consumer, TinkerFluids.moltenIron,   "iron",   true, true, metalFolder, false, Byproduct.NICKEL, Byproduct.COPPER);
    metalMelting(consumer, TinkerFluids.moltenGold,   "gold",   true, true, metalFolder, false, Byproduct.COPPER);
    metalMelting(consumer, TinkerFluids.moltenCopper, "copper", true, true, metalFolder, false, Byproduct.SMALL_GOLD);
    metalMelting(consumer, TinkerFluids.moltenCobalt, "cobalt", true, true, metalFolder, false, Byproduct.IRON);

    MeltingRecipeBuilder.melting(Ingredient.of(Tags.Items.ORES_NETHERITE_SCRAP), TinkerFluids.moltenDebris, FluidValues.INGOT, 2.0f)
                        .setOre(OreRateType.METAL, OreRateType.GEM, OreRateType.METAL)
                        .addByproduct(TinkerFluids.moltenDiamond.result(FluidValues.GEM))
                        .addByproduct(TinkerFluids.moltenGold.result(FluidValues.INGOT * 3))
                        .save(consumer, location(metalFolder + "molten_debris/ore"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerTags.Items.INGOTS_NETHERITE_SCRAP), TinkerFluids.moltenDebris, FluidValues.INGOT, 1.0f)
                        .save(consumer, location(metalFolder + "molten_debris/scrap"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerTags.Items.NUGGETS_NETHERITE_SCRAP), TinkerFluids.moltenDebris, FluidValues.NUGGET, 1 / 3f)
                        .save(consumer, location(metalFolder + "molten_debris/debris_nugget"));
    
    // tier 3
    metalMelting(consumer, TinkerFluids.moltenSlimesteel, "slimesteel", false, false, metalFolder, false);
    metalMelting(consumer, TinkerFluids.moltenAmethystBronze, "amethyst_bronze", false, true, metalFolder, false);
    metalMelting(consumer, TinkerFluids.moltenRoseGold, "rose_gold", false, true, metalFolder, false);
    metalMelting(consumer, TinkerFluids.moltenPigIron, "pig_iron", false, false, metalFolder, false);
    // tier 4
    metalMelting(consumer, TinkerFluids.moltenManyullyn, "manyullyn", false, true, metalFolder, false);
    metalMelting(consumer, TinkerFluids.moltenHepatizon, "hepatizon", false, true, metalFolder, false);
    metalMelting(consumer, TinkerFluids.moltenQueensSlime, "queens_slime", false, false, metalFolder, false);
    //metalMelting(consumer, TinkerFluids.moltenSoulsteel, "soulsteel", false, false, metalFolder, false);
    metalMelting(consumer, TinkerFluids.moltenNetherite, "netherite", false, true, metalFolder, false);
    // tier 5
    //metalMelting(consumer, TinkerFluids.moltenKnightslime, "knightslime", false, false, metalFolder, false);

    // compat
    for (SmelteryCompat compat : SmelteryCompat.values()) {
      this.metalMelting(consumer, compat.getFluid(), compat.getName(), compat.isOre(), compat.hasDust(), metalFolder, true, compat.getByproducts());
    }

    // venom
    MeltingRecipeBuilder.melting(Ingredient.of(Items.SPIDER_EYE), TinkerFluids.venom, FluidValues.BOTTLE / 5, 1.0f)
                        .save(consumer, location(folder + "venom/eye"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.FERMENTED_SPIDER_EYE), TinkerFluids.venom, FluidValues.BOTTLE * 2 / 5, 1.0f)
                        .save(consumer, location(folder + "venom/fermented_eye"));

    // glass
    MeltingRecipeBuilder.melting(Ingredient.of(ItemTags.SMELTS_TO_GLASS), TinkerFluids.moltenGlass, FluidValues.GLASS_BLOCK, 1.5f)
                        .save(consumer, location(folder + "glass/sand"));
    MeltingRecipeBuilder.melting(Ingredient.of(Tags.Items.GLASS_SILICA), TinkerFluids.moltenGlass, FluidValues.GLASS_BLOCK, 1.0f)
                        .save(consumer, location(folder + "glass/block"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerTags.Items.GLASS_PANES_SILICA), TinkerFluids.moltenGlass, FluidValues.GLASS_PANE, 0.5f)
                        .save(consumer, location(folder + "glass/pane"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.GLASS_BOTTLE), TinkerFluids.moltenGlass, FluidValues.GLASS_BLOCK, 1.25f)
                        .save(consumer, location(folder + "glass/bottle"));
    // melt extra sand casts back
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.blankSandCast, TinkerSmeltery.blankRedSandCast),
                                 TinkerFluids.moltenGlass, FluidValues.GLASS_PANE, 0.75f)
                        .save(consumer, location(folder + "glass/sand_cast"));

    // liquid soul
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.SOUL_SAND, Blocks.SOUL_SOIL), TinkerFluids.liquidSoul, FluidValues.GLASS_BLOCK, 1.5f)
                        .save(consumer, location(folder + "soul/sand"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerCommons.soulGlass), TinkerFluids.liquidSoul, FluidValues.GLASS_BLOCK, 1.0f)
                        .save(consumer, location(folder + "soul/glass"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerCommons.soulGlassPane), TinkerFluids.liquidSoul, FluidValues.GLASS_PANE, 0.5f)
                        .save(consumer, location(folder + "soul/pane"));

    // clay
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.CLAY), TinkerFluids.moltenClay, FluidValues.BRICK_BLOCK, 1.0f)
                        .save(consumer, location(folder + "clay/block"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.CLAY_BALL), TinkerFluids.moltenClay, FluidValues.BRICK, 0.5f)
                        .save(consumer, location(folder + "clay/ball"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.FLOWER_POT), TinkerFluids.moltenClay, FluidValues.BRICK * 3, 2.0f)
                        .save(consumer, location(folder + "clay/pot"));
    tagMelting(consumer, TinkerFluids.moltenClay, FluidValues.BRICK, "plates/brick", 1.0f, folder + "clay/plate", true);
    // terracotta
    Ingredient terracottaBlock = Ingredient.of(
      Blocks.TERRACOTTA, Blocks.BRICKS, Blocks.BRICK_WALL, Blocks.BRICK_STAIRS,
      Blocks.WHITE_TERRACOTTA, Blocks.ORANGE_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA, Blocks.LIGHT_BLUE_TERRACOTTA,
      Blocks.YELLOW_TERRACOTTA, Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA, Blocks.GRAY_TERRACOTTA,
      Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.CYAN_TERRACOTTA, Blocks.PURPLE_TERRACOTTA, Blocks.BLUE_TERRACOTTA,
      Blocks.BROWN_TERRACOTTA, Blocks.GREEN_TERRACOTTA, Blocks.RED_TERRACOTTA, Blocks.BLACK_TERRACOTTA,
      Blocks.WHITE_GLAZED_TERRACOTTA, Blocks.ORANGE_GLAZED_TERRACOTTA, Blocks.MAGENTA_GLAZED_TERRACOTTA, Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA,
      Blocks.YELLOW_GLAZED_TERRACOTTA, Blocks.LIME_GLAZED_TERRACOTTA, Blocks.PINK_GLAZED_TERRACOTTA, Blocks.GRAY_GLAZED_TERRACOTTA,
      Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA, Blocks.CYAN_GLAZED_TERRACOTTA, Blocks.PURPLE_GLAZED_TERRACOTTA, Blocks.BLUE_GLAZED_TERRACOTTA,
      Blocks.BROWN_GLAZED_TERRACOTTA, Blocks.GREEN_GLAZED_TERRACOTTA, Blocks.RED_GLAZED_TERRACOTTA, Blocks.BLACK_GLAZED_TERRACOTTA, Blocks.DECORATED_POT);
    MeltingRecipeBuilder.melting(terracottaBlock, TinkerFluids.moltenClay, FluidValues.BRICK_BLOCK, 2.0f)
                        .save(consumer, location(folder + "clay/terracotta"));
    MeltingRecipeBuilder.melting(CompoundIngredient.of(Ingredient.of(Items.BRICK), Ingredient.of(ItemTags.DECORATED_POT_SHERDS)), TinkerFluids.moltenClay, FluidValues.BRICK, 1.0f)
                        .save(consumer, location(folder + "clay/brick"));
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.BRICK_SLAB),
                                 TinkerFluids.moltenClay, FluidValues.BRICK_BLOCK / 2, 1.5f)
                        .save(consumer, location(folder + "clay/brick_slab"));

    // slime
    String slimeFolder = folder + "slime/";
    slimeMelting(consumer, TinkerFluids.earthSlime, SlimeType.EARTH, slimeFolder);
    slimeMelting(consumer, TinkerFluids.skySlime, SlimeType.SKY, slimeFolder);
    slimeMelting(consumer, TinkerFluids.enderSlime, SlimeType.ENDER, slimeFolder);
    // magma cream
    MeltingRecipeBuilder.melting(Ingredient.of(Items.MAGMA_CREAM), TinkerFluids.magma, FluidValues.SLIMEBALL, 1.0f)
                        .save(consumer, location(slimeFolder + "magma/ball"));
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.MAGMA_BLOCK), TinkerFluids.magma, FluidValues.SLIME_CONGEALED, 3.0f)
                        .save(consumer, location(slimeFolder + "magma/block"));

    // copper cans if empty
    MeltingRecipeBuilder.melting(NoContainerIngredient.of(TinkerSmeltery.copperCan), TinkerFluids.moltenCopper, FluidValues.INGOT, 1.0f)
                        .save(consumer, location(metalFolder + "copper/can"));
    // ender
    MeltingRecipeBuilder.melting(
      CompoundIngredient.of(Ingredient.of(Tags.Items.ENDER_PEARLS), Ingredient.of(Items.ENDER_EYE)),
      TinkerFluids.moltenEnder, FluidValues.SLIMEBALL, 1.0f)
                        .save(consumer, location(folder + "ender/pearl"));

    // obsidian
    MeltingRecipeBuilder.melting(Ingredient.of(Tags.Items.OBSIDIAN), TinkerFluids.moltenObsidian, FluidValues.GLASS_BLOCK, 2.0f)
                        .save(consumer, location(folder + "obsidian/block"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerCommons.obsidianPane), TinkerFluids.moltenObsidian, FluidValues.GLASS_PANE, 1.5f)
                        .save(consumer, location(folder + "obsidian/pane"));
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.ENDER_CHEST), TinkerFluids.moltenObsidian, FluidValues.GLASS_BLOCK * 8, 5.0f)
                        .addByproduct(TinkerFluids.moltenEnder.result(FluidValues.SLIMEBALL))
                        .save(consumer, location(folder + "obsidian/chest"));
    tagMelting(consumer, TinkerFluids.moltenObsidian, FluidValues.GLASS_PANE, "dusts/obsidian", 1.0f, folder + "obsidian/dust", true);

    // emerald
    gemMelting(consumer, TinkerFluids.moltenEmerald, "emerald", true, 9, folder, false, Byproduct.DIAMOND);
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerModifiers.emeraldReinforcement), TinkerFluids.moltenEmerald, FluidValues.GEM_SHARD)
                        .addByproduct(TinkerFluids.moltenObsidian.result(FluidValues.GLASS_PANE))
                        .save(consumer, location(metalFolder + "emerald/reinforcement"));

    // quartz
    gemMelting(consumer, TinkerFluids.moltenQuartz, "quartz", true, 4, folder, false, Byproduct.AMETHYST);
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.SMOOTH_QUARTZ, Blocks.QUARTZ_PILLAR, Blocks.QUARTZ_BRICKS, Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_STAIRS, Blocks.SMOOTH_QUARTZ_STAIRS),
      TinkerFluids.moltenQuartz, FluidValues.SMALL_GEM_BLOCK, 2.0f)
                        .save(consumer, location(folder + "quartz/decorative_block"));
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.QUARTZ_SLAB, Blocks.SMOOTH_QUARTZ_SLAB), TinkerFluids.moltenQuartz, FluidValues.GEM * 2, 1.5f)
                        .save(consumer, location(folder + "quartz/slab"));

    // amethyst
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.AMETHYST_CLUSTER), TinkerFluids.moltenAmethyst, FluidValues.GEM * 4, 4.0f)
                        .addByproduct(TinkerFluids.moltenQuartz.result(FluidValues.GEM * 4))
                        .setOre(OreRateType.GEM)
                        .save(consumer, location(folder + "amethyst/cluster"));
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.SMALL_AMETHYST_BUD), TinkerFluids.moltenAmethyst, FluidValues.GEM, 1.0f)
                        .addByproduct(TinkerFluids.moltenQuartz.result(FluidValues.GEM))
                        .setOre(OreRateType.GEM)
                        .save(consumer, location(folder + "amethyst/bud_small"));
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.MEDIUM_AMETHYST_BUD), TinkerFluids.moltenAmethyst, FluidValues.GEM * 2, 2.0f)
                        .addByproduct(TinkerFluids.moltenQuartz.result(FluidValues.GEM * 2))
                        .setOre(OreRateType.GEM)
                        .save(consumer, location(folder + "amethyst/bud_medium"));
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.LARGE_AMETHYST_BUD), TinkerFluids.moltenAmethyst, FluidValues.GEM * 3, 3.0f)
                        .addByproduct(TinkerFluids.moltenQuartz.result(FluidValues.GEM * 3))
                        .setOre(OreRateType.GEM)
                        .save(consumer, location(folder + "amethyst/bud_large"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.AMETHYST_SHARD), TinkerFluids.moltenAmethyst, FluidValues.GEM, 1.0f)
                        .save(consumer, location(folder + "amethyst/shard"));
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.AMETHYST_BLOCK), TinkerFluids.moltenAmethyst, FluidValues.SMALL_GEM_BLOCK, 2.0f)
                        .save(consumer, location(folder + "amethyst/block"));

    // diamond
    gemMelting(consumer, TinkerFluids.moltenDiamond, "diamond", true, 9, folder, false, Byproduct.QUARTZ);

    // iron melting - standard values
    MeltingRecipeBuilder.melting(Ingredient.of(Items.ACTIVATOR_RAIL, Items.DETECTOR_RAIL, Blocks.STONECUTTER, Blocks.PISTON, Blocks.STICKY_PISTON), TinkerFluids.moltenIron, FluidValues.INGOT)
                        .save(consumer, location(metalFolder + "iron/ingot_1"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.HEAVY_WEIGHTED_PRESSURE_PLATE, Items.IRON_DOOR, Blocks.SMITHING_TABLE), TinkerFluids.moltenIron, FluidValues.INGOT * 2)
                        .save(consumer, location(metalFolder + "iron/ingot_2"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.BUCKET), TinkerFluids.moltenIron, FluidValues.INGOT * 3)
                        .save(consumer, location(metalFolder + "iron/bucket"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.COMPASS, Blocks.IRON_TRAPDOOR), TinkerFluids.moltenIron, FluidValues.INGOT * 4)
                        .save(consumer, location(metalFolder + "iron/ingot_4"));
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.BLAST_FURNACE, Blocks.HOPPER, Items.MINECART), TinkerFluids.moltenIron, FluidValues.INGOT * 5)
                        .save(consumer, location(metalFolder + "iron/ingot_5"));
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.CAULDRON), TinkerFluids.moltenIron, FluidValues.INGOT * 7)
                        .save(consumer, location(metalFolder + "iron/cauldron"));
    // non-standard
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.CHAIN), TinkerFluids.moltenIron, FluidValues.INGOT + FluidValues.NUGGET * 2)
                        .save(consumer, location(metalFolder + "iron/chain"));
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.ANVIL, Blocks.CHIPPED_ANVIL, Blocks.DAMAGED_ANVIL), TinkerFluids.moltenIron, FluidValues.INGOT * 4 + FluidValues.METAL_BLOCK * 3)
                        .save(consumer, location(metalFolder + "iron/anvil"));
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.IRON_BARS, Blocks.RAIL), TinkerFluids.moltenIron, FluidValues.NUGGET * 3)
                        .save(consumer, location(metalFolder + "iron/nugget_3"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerCommons.ironPlatform), TinkerFluids.moltenIron, FluidValues.NUGGET * 10)
                        .save(consumer, location(metalFolder + "iron/platform"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.TRIPWIRE_HOOK), TinkerFluids.moltenIron, FluidValues.NUGGET * 4)
                        .save(consumer, location(metalFolder + "iron/tripwire"));
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.LANTERN, Blocks.SOUL_LANTERN), TinkerFluids.moltenIron, FluidValues.NUGGET * 8)
                        .save(consumer, location(metalFolder + "iron/lantern"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerModifiers.ironReinforcement), TinkerFluids.moltenIron, FluidValues.INGOT)
                        .save(consumer, location(metalFolder + "iron/reinforcement"));
    // armor
    MeltingRecipeBuilder.melting(Ingredient.of(Items.IRON_HELMET), TinkerFluids.moltenIron, FluidValues.INGOT * 5)
                        .setDamagable(FluidValues.NUGGET)
                        .save(consumer, location(metalFolder + "iron/helmet"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.IRON_CHESTPLATE), TinkerFluids.moltenIron, FluidValues.INGOT * 8)
                        .setDamagable(FluidValues.NUGGET)
                        .save(consumer, location(metalFolder + "iron/chestplate"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.IRON_LEGGINGS), TinkerFluids.moltenIron, FluidValues.INGOT * 7)
                        .setDamagable(FluidValues.NUGGET)
                        .save(consumer, location(metalFolder + "iron/leggings"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.IRON_BOOTS), TinkerFluids.moltenIron, FluidValues.INGOT * 4)
                        .setDamagable(FluidValues.NUGGET)
                        .save(consumer, location(metalFolder + "iron/boots"));
    // tools
    MeltingRecipeBuilder.melting(Ingredient.of(Items.IRON_AXE, Items.IRON_PICKAXE), TinkerFluids.moltenIron, FluidValues.INGOT * 3)
                        .setDamagable(FluidValues.NUGGET)
                        .save(consumer, location(metalFolder + "iron/axes"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.IRON_SWORD, Items.IRON_HOE, Items.SHEARS), TinkerFluids.moltenIron, FluidValues.INGOT * 2)
                        .setDamagable(FluidValues.NUGGET)
                        .save(consumer, location(metalFolder + "iron/weapon"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.IRON_SHOVEL, Items.FLINT_AND_STEEL, Items.SHIELD), TinkerFluids.moltenIron, FluidValues.INGOT)
                        .setDamagable(FluidValues.NUGGET)
                        .save(consumer, location(metalFolder + "iron/small"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.CROSSBOW), TinkerFluids.moltenIron, FluidValues.NUGGET * 13) // tripwire hook is 4 nuggets, ingot is 9 nuggets
                        .setDamagable(FluidValues.NUGGET)
                        .save(consumer, location(metalFolder + "iron/crossbow"));
    // unique melting
    MeltingRecipeBuilder.melting(Ingredient.of(Items.IRON_HORSE_ARMOR), TinkerFluids.moltenIron, FluidValues.INGOT * 7)
                        .save(consumer, location(metalFolder + "iron/horse_armor"));


    // gold melting
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerTags.Items.GOLD_CASTS), TinkerFluids.moltenGold, FluidValues.INGOT)
                        .save(consumer, location(metalFolder + "gold/cast"));
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.POWERED_RAIL), TinkerFluids.moltenGold, FluidValues.INGOT)
                        .save(consumer, location(metalFolder + "gold/powered_rail"));
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE), TinkerFluids.moltenGold, FluidValues.INGOT * 2)
                        .save(consumer, location(metalFolder + "gold/plate"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.CLOCK), TinkerFluids.moltenGold, FluidValues.INGOT * 4)
                        .save(consumer, location(metalFolder + "gold/clock"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.GOLDEN_APPLE), TinkerFluids.moltenGold, FluidValues.INGOT * 8)
                        .save(consumer, location(metalFolder + "gold/apple"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.GLISTERING_MELON_SLICE, Items.GOLDEN_CARROT), TinkerFluids.moltenGold, FluidValues.NUGGET * 8)
                        .save(consumer, location(metalFolder + "gold/produce"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerModifiers.goldReinforcement), TinkerFluids.moltenGold, FluidValues.INGOT)
                        .save(consumer, location(metalFolder + "gold/reinforcement"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerCommons.goldBars), TinkerFluids.moltenGold, FluidValues.NUGGET * 3)
                        .save(consumer, location(metalFolder + "gold/nugget_3"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerCommons.goldPlatform), TinkerFluids.moltenGold, FluidValues.NUGGET * 10)
                        .save(consumer, location(metalFolder + "gold/platform"));
    // armor
    MeltingRecipeBuilder.melting(Ingredient.of(Items.GOLDEN_HELMET), TinkerFluids.moltenGold, FluidValues.INGOT * 5)
                        .setDamagable(FluidValues.NUGGET)
                        .save(consumer, location(metalFolder + "gold/helmet"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.GOLDEN_CHESTPLATE), TinkerFluids.moltenGold, FluidValues.INGOT * 8)
                        .setDamagable(FluidValues.NUGGET)
                        .save(consumer, location(metalFolder + "gold/chestplate"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.GOLDEN_LEGGINGS), TinkerFluids.moltenGold, FluidValues.INGOT * 7)
                        .setDamagable(FluidValues.NUGGET)
                        .save(consumer, location(metalFolder + "gold/leggings"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.GOLDEN_BOOTS), TinkerFluids.moltenGold, FluidValues.INGOT * 4)
                        .setDamagable(FluidValues.NUGGET)
                        .save(consumer, location(metalFolder + "gold/boots"));
    // tools
    MeltingRecipeBuilder.melting(Ingredient.of(Items.GOLDEN_AXE, Items.GOLDEN_PICKAXE), TinkerFluids.moltenGold, FluidValues.INGOT * 3)
                        .setDamagable(FluidValues.NUGGET)
                        .save(consumer, location(metalFolder + "gold/axes"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.GOLDEN_SWORD, Items.GOLDEN_HOE), TinkerFluids.moltenGold, FluidValues.INGOT * 2)
                        .setDamagable(FluidValues.NUGGET)
                        .save(consumer, location(metalFolder + "gold/weapon"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.GOLDEN_SHOVEL), TinkerFluids.moltenGold, FluidValues.INGOT)
                        .setDamagable(FluidValues.NUGGET)
                        .save(consumer, location(metalFolder + "gold/shovel"));
    // unique melting
    MeltingRecipeBuilder.melting(Ingredient.of(Items.GOLDEN_HORSE_ARMOR), TinkerFluids.moltenGold, FluidValues.INGOT * 7)
                        .save(consumer, location(metalFolder + "gold/horse_armor"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.ENCHANTED_GOLDEN_APPLE), TinkerFluids.moltenGold, FluidValues.METAL_BLOCK * 8)
                        .save(consumer, location(metalFolder + "gold/enchanted_apple"));
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.GILDED_BLACKSTONE), TinkerFluids.moltenGold, FluidValues.NUGGET * 6) // bit better than mining before ore bonus
                        .setOre(OreRateType.METAL)
                        .save(consumer, location(metalFolder + "gold/gilded_blackstone"));
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.BELL), TinkerFluids.moltenGold, FluidValues.INGOT * 4) // bit arbitrary, I am happy to change the value if someone has a better one
                        .save(consumer, location(metalFolder + "gold/bell"));


    // copper melting
    MeltingRecipeBuilder.melting(Ingredient.of(
      Blocks.EXPOSED_COPPER, Blocks.WEATHERED_COPPER, Blocks.OXIDIZED_COPPER,
      Blocks.WAXED_COPPER_BLOCK, Blocks.WAXED_EXPOSED_COPPER, Blocks.WAXED_WEATHERED_COPPER, Blocks.WAXED_OXIDIZED_COPPER),
                                 TinkerFluids.moltenCopper, FluidValues.METAL_BLOCK)
                        .save(consumer, location(metalFolder + "copper/decorative_block"));
    MeltingRecipeBuilder.melting(Ingredient.of(
                                   Blocks.CUT_COPPER, Blocks.EXPOSED_CUT_COPPER, Blocks.WEATHERED_CUT_COPPER, Blocks.OXIDIZED_CUT_COPPER,
                                   Blocks.CUT_COPPER_STAIRS, Blocks.EXPOSED_CUT_COPPER_STAIRS, Blocks.WEATHERED_CUT_COPPER_STAIRS, Blocks.OXIDIZED_CUT_COPPER_STAIRS,
                                   Blocks.WAXED_CUT_COPPER, Blocks.WAXED_EXPOSED_CUT_COPPER, Blocks.WAXED_WEATHERED_CUT_COPPER, Blocks.WAXED_OXIDIZED_CUT_COPPER,
                                   Blocks.WAXED_CUT_COPPER_STAIRS, Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS, Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS, Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS),
                                 TinkerFluids.moltenCopper, FluidValues.NUGGET * 20)
                        .save(consumer, location(metalFolder + "copper/cut_block"));
    MeltingRecipeBuilder.melting(Ingredient.of(
                                   Blocks.CUT_COPPER_SLAB, Blocks.EXPOSED_CUT_COPPER_SLAB, Blocks.WEATHERED_CUT_COPPER_SLAB, Blocks.OXIDIZED_CUT_COPPER_SLAB,
                                   Blocks.WAXED_CUT_COPPER_SLAB, Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB, Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB, Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB),
                                 TinkerFluids.moltenCopper, FluidValues.NUGGET * 10)
                        .save(consumer, location(metalFolder + "copper/cut_slab"));
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.LIGHTNING_ROD), TinkerFluids.moltenCopper, FluidValues.INGOT * 3)
                        .save(consumer, location(metalFolder + "copper/lightning_rod"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerTags.Items.COPPER_PLATFORMS), TinkerFluids.moltenCopper, FluidValues.NUGGET * 10)
                        .save(consumer, location(metalFolder + "copper/platform"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.BRUSH), TinkerFluids.moltenCopper, FluidValues.INGOT)
                        .setDamagable(FluidValues.NUGGET)
                        .save(consumer, location(metalFolder + "copper/brush"));

    // amethyst melting
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.TINTED_GLASS, TinkerCommons.clearTintedGlass), TinkerFluids.moltenAmethyst, FluidValues.GEM * 2)
                        .addByproduct(TinkerFluids.moltenGlass.result(FluidValues.GLASS_BLOCK / 2))
                        .save(consumer, location(folder + "amethyst/tinted_glass"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.SPYGLASS), TinkerFluids.moltenAmethyst, FluidValues.GEM)
                        .addByproduct(TinkerFluids.moltenCopper.result(FluidValues.INGOT * 2))
                        .save(consumer, location(folder + "amethyst/spyglass"));

    // diamond melting
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.JUKEBOX), TinkerFluids.moltenDiamond, FluidValues.GEM)
                        .save(consumer, location(folder + "diamond/jukebox"));
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.ENCHANTING_TABLE), TinkerFluids.moltenDiamond, FluidValues.GEM * 2)
                        .addByproduct(TinkerFluids.moltenObsidian.result(FluidValues.GLASS_BLOCK * 4))
                        .save(consumer, location(folder + "diamond/enchanting_table"));
    // not the full copy cost as we have a discount recipe
    MeltingRecipeBuilder.melting(CompoundIngredient.of(Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), Ingredient.of(ItemTags.TRIM_TEMPLATES)), TinkerFluids.moltenDiamond, FluidValues.GEM * 5)
                        .save(consumer, location(folder + "diamond/smithing_template"));
    // armor
    MeltingRecipeBuilder.melting(Ingredient.of(Items.DIAMOND_HELMET), TinkerFluids.moltenDiamond, FluidValues.GEM * 5)
                        .setDamagable(FluidValues.GEM_SHARD)
                        .save(consumer, location(folder + "diamond/helmet"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.DIAMOND_CHESTPLATE), TinkerFluids.moltenDiamond, FluidValues.GEM * 8)
                        .setDamagable(FluidValues.GEM_SHARD)
                        .save(consumer, location(folder + "diamond/chestplate"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.DIAMOND_LEGGINGS), TinkerFluids.moltenDiamond, FluidValues.GEM * 7)
                        .setDamagable(FluidValues.GEM_SHARD)
                        .save(consumer, location(folder + "diamond/leggings"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.DIAMOND_BOOTS), TinkerFluids.moltenDiamond, FluidValues.GEM * 4)
                        .setDamagable(FluidValues.GEM_SHARD)
                        .save(consumer, location(folder + "diamond/boots"));
    // tools
    MeltingRecipeBuilder.melting(Ingredient.of(Items.DIAMOND_AXE, Items.DIAMOND_PICKAXE), TinkerFluids.moltenDiamond, FluidValues.GEM * 3)
                        .setDamagable(FluidValues.GEM_SHARD)
                        .save(consumer, location(folder + "diamond/axes"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.DIAMOND_SWORD, Items.DIAMOND_HOE), TinkerFluids.moltenDiamond, FluidValues.GEM * 2)
                        .setDamagable(FluidValues.GEM_SHARD)
                        .save(consumer, location(folder + "diamond/weapon"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.DIAMOND_SHOVEL), TinkerFluids.moltenDiamond, FluidValues.GEM)
                        .setDamagable(FluidValues.GEM_SHARD)
                        .save(consumer, location(folder + "diamond/shovel"));
    // unique melting
    MeltingRecipeBuilder.melting(Ingredient.of(Items.DIAMOND_HORSE_ARMOR), TinkerFluids.moltenDiamond, FluidValues.GEM * 7)
                        .save(consumer, location(folder + "diamond/horse_armor"));

    // netherite
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.LODESTONE), TinkerFluids.moltenNetherite, FluidValues.INGOT)
                        .save(consumer, location(metalFolder + "netherite/lodestone"));
    // armor
    int[] netheriteSizes = {FluidValues.NUGGET, FluidValues.GEM_SHARD};
    MeltingRecipeBuilder.melting(Ingredient.of(Items.NETHERITE_HELMET), TinkerFluids.moltenNetherite, FluidValues.INGOT)
                        .setDamagable(netheriteSizes)
                        .addByproduct(TinkerFluids.moltenDiamond.result(FluidValues.GEM * 5))
                        .save(consumer, location(metalFolder + "netherite/helmet"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.NETHERITE_CHESTPLATE), TinkerFluids.moltenNetherite, FluidValues.INGOT)
                        .setDamagable(netheriteSizes)
                        .addByproduct(TinkerFluids.moltenDiamond.result(FluidValues.GEM * 8))
                        .save(consumer, location(metalFolder + "netherite/chestplate"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.NETHERITE_LEGGINGS), TinkerFluids.moltenNetherite, FluidValues.INGOT)
                        .setDamagable(netheriteSizes)
                        .addByproduct(TinkerFluids.moltenDiamond.result(FluidValues.GEM * 7))
                        .save(consumer, location(metalFolder + "netherite/leggings"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.NETHERITE_BOOTS), TinkerFluids.moltenNetherite, FluidValues.INGOT)
                        .setDamagable(netheriteSizes)
                        .addByproduct(TinkerFluids.moltenDiamond.result(FluidValues.GEM * 4))
                        .save(consumer, location(metalFolder + "netherite/boots"));
    // tools
    MeltingRecipeBuilder.melting(Ingredient.of(Items.NETHERITE_AXE, Items.NETHERITE_PICKAXE), TinkerFluids.moltenNetherite, FluidValues.INGOT)
                        .setDamagable(netheriteSizes)
                        .addByproduct(TinkerFluids.moltenDiamond.result(FluidValues.GEM * 3))
                        .save(consumer, location(metalFolder + "netherite/axes"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.NETHERITE_SWORD, Items.NETHERITE_HOE), TinkerFluids.moltenNetherite, FluidValues.INGOT)
                        .setDamagable(netheriteSizes)
                        .addByproduct(TinkerFluids.moltenDiamond.result(FluidValues.GEM * 2))
                        .save(consumer, location(metalFolder + "netherite/weapon"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.NETHERITE_SHOVEL), TinkerFluids.moltenNetherite, FluidValues.INGOT)
                        .setDamagable(netheriteSizes)
                        .addByproduct(TinkerFluids.moltenDiamond.result(FluidValues.GEM))
                        .save(consumer, location(metalFolder + "netherite/shovel"));

    // quartz
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.OBSERVER, Blocks.COMPARATOR, TinkerGadgets.quartzShuriken), TinkerFluids.moltenQuartz, FluidValues.GEM)
                        .save(consumer, location(folder + "quartz/gem_1"));
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.DAYLIGHT_DETECTOR), TinkerFluids.moltenQuartz, FluidValues.GEM * 3)
                        .addByproduct(TinkerFluids.moltenGlass.result(FluidValues.GLASS_BLOCK * 3))
                        .save(consumer, location(folder + "quartz/daylight_detector"));

    // obsidian, if you are crazy i guess
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.BEACON), TinkerFluids.moltenObsidian, FluidValues.GLASS_BLOCK * 3)
                        .addByproduct(TinkerFluids.moltenGlass.result(FluidValues.GLASS_BLOCK * 5))
                        .save(consumer, location(folder + "obsidian/beacon"));

    // ender
    MeltingRecipeBuilder.melting(Ingredient.of(Items.END_CRYSTAL), TinkerFluids.moltenEnder, FluidValues.SLIMEBALL)
                        .addByproduct(TinkerFluids.moltenGlass.result(FluidValues.GLASS_BLOCK * 7))
                        .save(consumer, location(folder + "ender/end_crystal"));
    // it may be silky, but its still rose gold
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerModifiers.silkyCloth), TinkerFluids.moltenRoseGold, FluidValues.INGOT)
                        .save(consumer, location(metalFolder + "rose_gold/silky_cloth"));

    // durability reinforcements
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerModifiers.slimesteelReinforcement), TinkerFluids.moltenSlimesteel, FluidValues.NUGGET * 3)
                        .addByproduct(TinkerFluids.moltenObsidian.result(FluidValues.GLASS_PANE))
                        .save(consumer, location(metalFolder + "slimesteel/reinforcement"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerModifiers.obsidianReinforcement), TinkerFluids.moltenObsidian, FluidValues.GLASS_BLOCK)
                        .save(consumer, location(metalFolder + "obsidian/reinforcement"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerModifiers.cobaltReinforcement), TinkerFluids.moltenCobalt, FluidValues.INGOT)
                        .save(consumer, location(metalFolder + "cobalt/reinforcement"));

    MeltingRecipeBuilder.melting(Ingredient.of(TinkerCommons.cobaltPlatform), TinkerFluids.moltenCobalt, FluidValues.NUGGET * 10)
                        .save(consumer, location(metalFolder + "cobalt/platform"));

    // geode stuff
    crystalMelting(consumer, TinkerWorld.earthGeode, TinkerFluids.earthSlime, slimeFolder + "earth/");
    crystalMelting(consumer, TinkerWorld.skyGeode,   TinkerFluids.skySlime,   slimeFolder + "sky/");
    crystalMelting(consumer, TinkerWorld.enderGeode, TinkerFluids.enderSlime, slimeFolder + "ender/");

    // recycle saplings
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerWorld.slimeSapling.get(FoliageType.EARTH)), TinkerFluids.earthSlime, FluidValues.SLIMEBALL)
                        .save(consumer, location(slimeFolder + "earth/sapling"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerWorld.slimeSapling.get(FoliageType.SKY)), TinkerFluids.skySlime, FluidValues.SLIMEBALL)
                        .save(consumer, location(slimeFolder + "sky/sapling"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerWorld.slimeSapling.get(FoliageType.ENDER)), TinkerFluids.enderSlime, FluidValues.SLIMEBALL)
                        .save(consumer, location(slimeFolder + "ender/sapling"));

    // honey
    MeltingRecipeBuilder.melting(Ingredient.of(Items.HONEY_BLOCK), TinkerFluids.honey, FluidValues.BOTTLE * 4)
                        .save(consumer, location(slimeFolder + "honey_block"));
    // soup
    MeltingRecipeBuilder.melting(Ingredient.of(Items.BEETROOT), TinkerFluids.beetrootSoup, FluidValues.BOTTLE / 5, 1)
                        .save(consumer, location(slimeFolder + "beetroot_soup"));
    MeltingRecipeBuilder.melting(Ingredient.of(Tags.Items.MUSHROOMS), TinkerFluids.mushroomStew, FluidValues.BOTTLE / 2, 1)
                        .save(consumer, location(slimeFolder + "mushroom_stew"));

    // fuels
    MeltingFuelBuilder.solid(800)
                      .save(consumer, location(folder + "fuel/solid"));
    MeltingFuelBuilder.fuel(new FluidStack(Fluids.LAVA, 50), 100)
                      .save(consumer, location(folder + "fuel/lava"));
    MeltingFuelBuilder.fuel(new FluidStack(TinkerFluids.blazingBlood.get(), 50), 150)
                      .save(consumer, location(folder + "fuel/blaze"));
  }


  private void addAlloyRecipes(Consumer<FinishedRecipe> consumer) {
    String folder = "smeltery/alloys/";

    // alloy recipes are in terms of ingots

    // tier 3

    // slimesteel: 1 iron + 1 skyslime + 1 seared brick = 2
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenSlimesteel, FluidValues.INGOT * 2)
                      .addInput(TinkerFluids.moltenIron.ingredient(FluidValues.INGOT))
                      .addInput(TinkerFluids.skySlime.ingredient(FluidValues.SLIMEBALL))
                      .addInput(TinkerFluids.searedStone.ingredient(FluidValues.BRICK))
                      .save(consumer, prefix(TinkerFluids.moltenSlimesteel, folder));

    // amethyst bronze: 1 copper + 1 amethyst = 1
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenAmethystBronze, FluidValues.INGOT)
                      .addInput(TinkerFluids.moltenCopper.ingredient(FluidValues.INGOT))
                      .addInput(TinkerFluids.moltenAmethyst.ingredient(FluidValues.GEM))
                      .save(consumer, prefix(TinkerFluids.moltenAmethystBronze, folder));

    // rose gold: 1 copper + 1 gold = 2
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenRoseGold, FluidValues.INGOT * 2)
                      .addInput(TinkerFluids.moltenCopper.ingredient(FluidValues.INGOT))
                      .addInput(TinkerFluids.moltenGold.ingredient(FluidValues.INGOT))
                      .save(consumer, prefix(TinkerFluids.moltenRoseGold, folder));
    // pig iron: 1 iron + 2 blood + 1 honey = 2
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenPigIron, FluidValues.INGOT * 2)
                      .addInput(TinkerFluids.moltenIron.ingredient(FluidValues.INGOT))
                      .addInput(TinkerFluids.meatSoup.ingredient(FluidValues.SLIMEBALL * 2))
                      .addInput(TinkerFluids.honey.ingredient(FluidValues.BOTTLE))
                      .save(consumer, prefix(TinkerFluids.moltenPigIron, folder));
    // obsidian: 1 water + 1 lava = 2
    // note this is not a progression break, as the same tier lets you combine glass and copper for same mining level
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenObsidian, FluidValues.GLASS_BLOCK / 10)
                      .addInput(Fluids.WATER, FluidType.BUCKET_VOLUME / 20)
                      .addInput(Fluids.LAVA, FluidType.BUCKET_VOLUME / 10)
                      .save(consumer, prefix(TinkerFluids.moltenObsidian, folder));
    // nether obsidian recipe: when water is rare, use the soup
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenObsidian, FluidValues.GLASS_BLOCK / 4)
                      .addInput(TinkerFluids.mushroomStew.ingredient(FluidValues.BOWL))
                      .addInput(Fluids.LAVA, FluidType.BUCKET_VOLUME / 4)
                      .save(consumer, wrap(TinkerFluids.moltenObsidian, folder, "_from_soup"));

    // tier 4

    // queens slime: 1 cobalt + 1 gold + 1 magma cream = 2
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenQueensSlime, FluidValues.INGOT * 2)
                      .addInput(TinkerFluids.moltenCobalt.ingredient(FluidValues.INGOT))
                      .addInput(TinkerFluids.moltenGold.ingredient(FluidValues.INGOT))
                      .addInput(TinkerFluids.magma.ingredient(FluidValues.SLIMEBALL))
                      .save(consumer, prefix(TinkerFluids.moltenQueensSlime, folder));

    // manyullyn: 3 cobalt + 1 debris = 3
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenManyullyn, FluidValues.INGOT * 4)
                      .addInput(TinkerFluids.moltenCobalt.ingredient(FluidValues.INGOT * 3))
                      .addInput(TinkerFluids.moltenDebris.ingredient(FluidValues.INGOT))
                      .save(consumer, prefix(TinkerFluids.moltenManyullyn, folder));

    // heptazion: 2 copper + 1 cobalt + 1/4 obsidian = 2
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenHepatizon, FluidValues.INGOT * 2)
                      .addInput(TinkerFluids.moltenCopper.ingredient(FluidValues.INGOT * 2))
                      .addInput(TinkerFluids.moltenCobalt.ingredient(FluidValues.INGOT))
                      .addInput(TinkerFluids.moltenQuartz.ingredient(FluidValues.GEM * 4))
                      .save(consumer, prefix(TinkerFluids.moltenHepatizon, folder));

    // netherrite: 4 debris + 4 gold = 1 (why is this so dense vanilla?)
    ConditionalRecipe.builder()
                     .addCondition(ConfigEnabledCondition.CHEAPER_NETHERITE_ALLOY)
                     .addRecipe(
                       AlloyRecipeBuilder.alloy(TinkerFluids.moltenNetherite, FluidValues.NUGGET)
                                         .addInput(TinkerFluids.moltenDebris.ingredient(FluidValues.NUGGET * 4))
                                         .addInput(TinkerFluids.moltenGold.ingredient(FluidValues.NUGGET * 2))::save)
                     .addCondition(TrueCondition.INSTANCE) // fallback
                     .addRecipe(
                       AlloyRecipeBuilder.alloy(TinkerFluids.moltenNetherite, FluidValues.NUGGET)
                                         .addInput(TinkerFluids.moltenDebris.ingredient(FluidValues.NUGGET * 4))
                                         .addInput(TinkerFluids.moltenGold.ingredient(FluidValues.NUGGET * 4))::save)
                     .build(consumer, prefix(TinkerFluids.moltenNetherite, folder));


    // tier 3 compat
    Consumer<FinishedRecipe> wrapped;

    // bronze
    wrapped = withCondition(consumer, tagCondition("ingots/tin"));
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenBronze, FluidValues.INGOT * 4)
                      .addInput(TinkerFluids.moltenCopper.ingredient(FluidValues.INGOT * 3))
                      .addInput(TinkerFluids.moltenTin.ingredient(FluidValues.INGOT))
                      .save(wrapped, prefix(TinkerFluids.moltenBronze, folder));

    // brass
    wrapped = withCondition(consumer, tagCondition("ingots/zinc"));
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenBrass, FluidValues.INGOT * 2)
                      .addInput(TinkerFluids.moltenCopper.ingredient(FluidValues.INGOT))
                      .addInput(TinkerFluids.moltenZinc.ingredient(FluidValues.INGOT))
                      .save(wrapped, prefix(TinkerFluids.moltenBrass, folder));

    // electrum
    wrapped = withCondition(consumer, tagCondition("ingots/silver"));
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenElectrum, FluidValues.INGOT * 2)
                      .addInput(TinkerFluids.moltenGold.ingredient(FluidValues.INGOT))
                      .addInput(TinkerFluids.moltenSilver.ingredient(FluidValues.INGOT))
                      .save(wrapped, prefix(TinkerFluids.moltenElectrum, folder));

    // invar
    wrapped = withCondition(consumer, tagCondition("ingots/nickel"));
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenInvar, FluidValues.INGOT * 3)
                      .addInput(TinkerFluids.moltenIron.ingredient(FluidValues.INGOT * 2))
                      .addInput(TinkerFluids.moltenNickel.ingredient(FluidValues.INGOT))
                      .save(wrapped, prefix(TinkerFluids.moltenInvar, folder));

    // constantan
    wrapped = withCondition(consumer, tagCondition("ingots/nickel"));
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenConstantan, FluidValues.INGOT * 2)
                      .addInput(TinkerFluids.moltenCopper.ingredient(FluidValues.INGOT))
                      .addInput(TinkerFluids.moltenNickel.ingredient(FluidValues.INGOT))
                      .save(wrapped, prefix(TinkerFluids.moltenConstantan, folder));

    // pewter
    wrapped = withCondition(consumer, tagCondition("ingots/pewter"), tagCondition("ingots/lead"));
    ConditionalRecipe.builder()
                     // when available, alloy pewter with tin
                     // we mainly add it to support Edilon which uses iron to reduce ores, but the author thinks tin is fine balance wise
                     .addCondition(tagCondition("ingots/tin"))
                     .addRecipe(
                       // ratio from Allomancy mod
                       AlloyRecipeBuilder.alloy(TinkerFluids.moltenPewter, FluidValues.INGOT * 3)
                                         .addInput(TinkerFluids.moltenTin.ingredient(FluidValues.INGOT * 2))
                                         .addInput(TinkerFluids.moltenLead.ingredient(FluidValues.INGOT))::save)
                     .addCondition(TrueCondition.INSTANCE) // fallback
                     .addRecipe(
                       // ratio from Edilon mod
                       AlloyRecipeBuilder.alloy(TinkerFluids.moltenPewter, FluidValues.INGOT * 2)
                                         .addInput(TinkerFluids.moltenIron.ingredient(FluidValues.INGOT))
                                         .addInput(TinkerFluids.moltenLead.ingredient(FluidValues.INGOT))::save)
                     .build(wrapped, prefix(TinkerFluids.moltenPewter, folder));

    // thermal alloys
    Function<String,ICondition> fluidTagLoaded = name -> new NotCondition(new TagEmptyCondition<>(Registries.FLUID, commonResource(name)));
    Function<String,TagKey<Fluid>> fluidTag = name -> FluidTags.create(commonResource(name));
    // enderium
    wrapped = withCondition(consumer, tagCondition("ingots/enderium"), tagCondition("ingots/lead"));
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenEnderium, FluidValues.INGOT * 2)
                      .addInput(TinkerFluids.moltenLead.ingredient(FluidValues.INGOT * 3))
                      .addInput(TinkerFluids.moltenDiamond.ingredient(FluidValues.GEM))
                      .addInput(TinkerFluids.moltenEnder.ingredient(FluidValues.SLIMEBALL * 2))
                      .save(wrapped, prefix(TinkerFluids.moltenEnderium, folder));
    // lumium
    wrapped = withCondition(consumer, tagCondition("ingots/lumium"), tagCondition("ingots/tin"), tagCondition("ingots/silver"), fluidTagLoaded.apply("glowstone"));
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenLumium, FluidValues.INGOT * 4)
                      .addInput(TinkerFluids.moltenTin.ingredient(FluidValues.INGOT * 3))
                      .addInput(TinkerFluids.moltenSilver.ingredient(FluidValues.INGOT))
                      .addInput(FluidIngredient.of(fluidTag.apply("glowstone"), FluidValues.SLIMEBALL * 2))
                      .save(wrapped, prefix(TinkerFluids.moltenLumium, folder));
    // signalum
    wrapped = withCondition(consumer, tagCondition("ingots/signalum"), tagCondition("ingots/copper"), tagCondition("ingots/silver"), fluidTagLoaded.apply("redstone"));
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenSignalum, FluidValues.INGOT * 4)
                      .addInput(TinkerFluids.moltenCopper.ingredient(FluidValues.INGOT * 3))
                      .addInput(TinkerFluids.moltenSilver.ingredient(FluidValues.INGOT))
                      .addInput(FluidIngredient.of(fluidTag.apply("redstone"), 400))
                      .save(wrapped, prefix(TinkerFluids.moltenSignalum, folder));

    // refined obsidian, note glowstone is done as a composite
    wrapped = withCondition(consumer, tagCondition("ingots/refined_obsidian"), tagCondition("ingots/osmium"));
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenRefinedObsidian, FluidValues.INGOT)
                      .addInput(TinkerFluids.moltenObsidian.ingredient(FluidValues.GLASS_PANE))
                      .addInput(TinkerFluids.moltenDiamond.ingredient(FluidValues.GEM))
                      .addInput(TinkerFluids.moltenOsmium.ingredient(FluidValues.INGOT))
                      .save(wrapped, prefix(TinkerFluids.moltenRefinedObsidian, folder));
  }

  private void addEntityMeltingRecipes(Consumer<FinishedRecipe> consumer) {
    String folder = "smeltery/entity_melting/";
    String headFolder = "smeltery/entity_melting/heads/";

    // meat soup just comes from edible creatures
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.CHICKEN, EntityType.COW, EntityType.MOOSHROOM, EntityType.PIG, EntityType.RABBIT, EntityType.SHEEP, EntityType.GOAT, EntityType.COD, EntityType.HOGLIN, EntityType.SALMON, EntityType.TROPICAL_FISH),
                                       TinkerFluids.meatSoup.result(FluidValues.BOWL / 5)).save(consumer, location(folder + "meat_soup"));

    // zombies give iron, they drop it sometimes
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.ZOMBIE, EntityType.HUSK, EntityType.ZOMBIE_HORSE), TinkerFluids.moltenIron.result(FluidValues.NUGGET), 4)
                              .save(consumer, location(folder + "zombie"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.ZOMBIE_HEAD, TinkerWorld.heads.get(TinkerHeadType.HUSK)), TinkerFluids.moltenIron, FluidValues.INGOT)
                        .save(consumer, location(headFolder + "zombie"));
    // drowned drop copper instead
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.DROWNED), TinkerFluids.moltenCopper.result(FluidValues.NUGGET), 4)
                              .save(consumer, location(folder + "drowned"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerWorld.heads.get(TinkerHeadType.DROWNED)), TinkerFluids.moltenCopper, FluidValues.INGOT)
                        .save(consumer, location(headFolder + "drowned"));
    // and piglins gold
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.PIGLIN, EntityType.PIGLIN_BRUTE, EntityType.ZOMBIFIED_PIGLIN), TinkerFluids.moltenGold.result(FluidValues.NUGGET), 4)
                              .save(consumer, location(folder + "piglin"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.PIGLIN_HEAD, TinkerWorld.heads.get(TinkerHeadType.PIGLIN_BRUTE), TinkerWorld.heads.get(TinkerHeadType.ZOMBIFIED_PIGLIN)), TinkerFluids.moltenGold, FluidValues.INGOT)
                        .save(consumer, location(headFolder + "piglin"));

    // melt spiders into venom
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.SPIDER, EntityType.CAVE_SPIDER), TinkerFluids.venom.result(FluidValues.BOTTLE / 10), 2)
                              .save(consumer, location(folder + "spider"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerWorld.heads.get(TinkerHeadType.SPIDER), TinkerWorld.heads.get(TinkerHeadType.CAVE_SPIDER)), TinkerFluids.venom, FluidValues.SLIMEBALL * 2)
                        .save(consumer, location(headFolder + "spider"));

    // creepers are based on explosives, tnt is explosive, tnt is made from sand, sand melts into glass. therefore, creepers melt into glass
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.CREEPER), TinkerFluids.moltenGlass.result(FluidValues.GLASS_BLOCK / 20), 2)
                              .save(consumer, location(folder + "creeper"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.CREEPER_HEAD), TinkerFluids.moltenGlass, FluidType.BUCKET_VOLUME / 4)
                        .save(consumer, location(headFolder + "creeper"));

    // ghasts melt into potions, because ghast tears or something, idk
    // axolotls like regen too, you monster!
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.GHAST, EntityType.AXOLOTL), PotionFluidType.potionResult(Potions.REGENERATION, FluidValues.BOTTLE / 5), 2)
                              .save(consumer, location(folder + "regeneration"));
    // likewise, phantoms give slow falling
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.PHANTOM), PotionFluidType.potionResult(Potions.SLOW_FALLING, FluidValues.BOTTLE / 5), 4)
                              .save(consumer, location(folder + "phantom"));
    // its not quite levitation, but close enough
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.SHULKER), PotionFluidType.potionResult(Potions.LEAPING, FluidValues.BOTTLE / 10), 3)
                              .save(consumer, location(folder + "shulker"));
    // frogs leap too
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.FROG), PotionFluidType.potionResult(Potions.LEAPING, FluidValues.BOTTLE / 5), 2)
                              .save(consumer, location(folder + "frog"));
    // just making brewing recipes now
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.SQUID, EntityType.PUFFERFISH), PotionFluidType.potionResult(Potions.WATER_BREATHING, FluidValues.BOTTLE / 5), 2)
                              .save(consumer, location(folder + "water_breathing"));
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.BAT, EntityType.GLOW_SQUID), PotionFluidType.potionResult(Potions.NIGHT_VISION, FluidValues.BOTTLE / 5), 2)
                              .save(consumer, location(folder + "night_vision"));
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.TURTLE), PotionFluidType.potionResult(Potions.TURTLE_MASTER, FluidValues.BOTTLE / 10), 3)
                              .save(consumer, location(folder + "turtle"));
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.DOLPHIN, EntityType.FOX, EntityType.HORSE, EntityType.DONKEY, EntityType.MULE, EntityType.LLAMA, EntityType.TRADER_LLAMA, EntityType.OCELOT),
                                       PotionFluidType.potionResult(Potions.SWIFTNESS, FluidValues.BOTTLE / 5), 2)
                              .save(consumer, location(folder + "swiftness"));
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.STRIDER), PotionFluidType.potionResult(Potions.FIRE_RESISTANCE, FluidValues.BOTTLE / 5), 4)
                              .save(consumer, location(folder + "strider"));
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.POLAR_BEAR, EntityType.PANDA, EntityType.RAVAGER, EntityType.ZOGLIN), PotionFluidType.potionResult(Potions.STRENGTH, FluidValues.BOTTLE / 5), 4)
                              .save(consumer, location(folder + "strength"));

    // melt skeletons to get the milk out
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityIngredient.of(EntityTypeTags.SKELETONS), EntityIngredient.of(EntityType.SKELETON_HORSE)),
                                       new FluidStack(ForgeMod.MILK.get(), FluidType.BUCKET_VOLUME / 10))
                              .save(consumer, location(folder + "skeletons"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.SKELETON_SKULL, Items.WITHER_SKELETON_SKULL, TinkerWorld.heads.get(TinkerHeadType.STRAY)), ForgeMod.MILK.get(), FluidType.BUCKET_VOLUME / 4)
                        .save(consumer, location(headFolder + "skeleton"));

    // slimes melt into slime, shocker
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.SLIME), TinkerFluids.earthSlime.result(FluidValues.SLIMEBALL / 10))
                              .save(consumer, location(folder + "slime"));
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(TinkerWorld.skySlimeEntity.get()), TinkerFluids.skySlime.result(FluidValues.SLIMEBALL / 10))
                              .save(consumer, prefix(TinkerWorld.skySlimeEntity, folder));
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(TinkerWorld.enderSlimeEntity.get()), TinkerFluids.enderSlime.result(FluidValues.SLIMEBALL / 10))
                              .save(consumer, prefix(TinkerWorld.enderSlimeEntity, folder));
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(TinkerWorld.terracubeEntity.get()), TinkerFluids.moltenClay.result(FluidValues.SLIMEBALL / 10))
                              .save(consumer, prefix(TinkerWorld.terracubeEntity, folder));
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.MAGMA_CUBE), TinkerFluids.magma.result(FluidValues.SLIMEBALL / 10))
                              .save(consumer, location(folder + "magma_cube"));
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.BEE), TinkerFluids.honey.result(FluidValues.BOTTLE / 10))
                              .save(consumer, location(folder + "bee"));

    // iron golems can be healed using an iron ingot 25 health
    // 4 * 9 gives 36, which is larger
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.IRON_GOLEM), TinkerFluids.moltenIron.result(FluidValues.NUGGET), 4)
                              .save(consumer, location(folder + "iron_golem"));
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.SNOW_GOLEM), new FluidStack(Fluids.WATER, FluidType.BUCKET_VOLUME / 10))
                              .save(consumer, location(folder + "snow_golem"));

    // "melt" blazes to get fuel
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.BLAZE), TinkerFluids.blazingBlood.result(FluidType.BUCKET_VOLUME / 50), 2)
                              .save(consumer, location(folder + "blaze"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerWorld.heads.get(TinkerHeadType.BLAZE)), TinkerFluids.blazingBlood.result(FluidType.BUCKET_VOLUME / 10), 1000, IMeltingRecipe.calcTime(1500, 1.0f))
                        .save(consumer, location(headFolder + "blaze"));

    // guardians are rock, seared stone is rock, don't think about it too hard
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.GUARDIAN, EntityType.ELDER_GUARDIAN), TinkerFluids.searedStone.result(FluidValues.BRICK / 5), 4)
                              .save(consumer, location(folder + "guardian"));
    // silverfish also seem like rock, sorta?
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.SILVERFISH), TinkerFluids.searedStone.result(FluidValues.BRICK / 5), 2)
                              .save(consumer, location(folder + "silverfish"));

    // villagers melt into emerald, but they die quite quick
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(TinkerTags.EntityTypes.VILLAGERS), TinkerFluids.moltenEmerald.result(FluidValues.GEM_SHARD), 5)
                              .save(consumer, location(folder + "villager"));
    // illagers are more resistant, they resist the villager culture afterall
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(TinkerTags.EntityTypes.ILLAGERS), TinkerFluids.moltenEmerald.result(FluidValues.GEM_SHARD), 2)
                              .save(consumer, location(folder + "illager"));

    // melt ender for the molten ender
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.ENDERMAN, EntityType.ENDERMITE, EntityType.ENDER_DRAGON),
                                       TinkerFluids.moltenEnder.result(FluidValues.SLIMEBALL / 10), 2)
                              .save(consumer, location(folder + "ender"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerWorld.heads.get(TinkerHeadType.ENDERMAN)), TinkerFluids.moltenEnder, FluidValues.SLIMEBALL * 2)
                        .save(consumer, location(headFolder + "enderman"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.DRAGON_HEAD), TinkerFluids.moltenEnder, FluidValues.SLIMEBALL * 4)
                        .save(consumer, location(headFolder + "ender_dragon"));

    // if you can get him to stay, wither is a source of free liquid soul
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.WITHER), TinkerFluids.liquidSoul.result(FluidValues.GLASS_BLOCK / 20), 2)
                              .save(consumer, location(folder + "wither"));
  }

  private void addCompatRecipes(Consumer<FinishedRecipe> consumer) {
    String folder = "compat/";
    // create - cast andesite alloy
    ItemOutput andesiteAlloy = ItemNameOutput.fromName(new ResourceLocation("create", "andesite_alloy"));
    Consumer<FinishedRecipe> createConsumer = withCondition(consumer, new ModLoadedCondition("create"));
    ItemCastingRecipeBuilder.basinRecipe(andesiteAlloy)
                            .setCast(Blocks.ANDESITE, true)
                            .setFluidAndTime(TinkerFluids.moltenIron, FluidValues.NUGGET)
                            .save(createConsumer, location(folder + "create/andesite_alloy_iron"));
    ItemCastingRecipeBuilder.basinRecipe(andesiteAlloy)
                            .setCast(Blocks.ANDESITE, true)
                            .setFluidAndTime(TinkerFluids.moltenZinc, FluidValues.NUGGET)
                            .save(createConsumer, location(folder + "create/andesite_alloy_zinc"));

    // immersive engineering - casting treated wood
    ItemCastingRecipeBuilder.basinRecipe(ItemNameOutput.fromName(new ResourceLocation("immersiveengineering", "treated_wood_horizontal")))
                            .setCast(ItemTags.PLANKS, true)
                            .setFluid(TagKey.create(Registries.FLUID, commonResource("creosote")), 125)
                            .setCoolingTime(100)
                            .save(withCondition(consumer, new ModLoadedCondition("immersiveengineering")), location(folder + "immersiveengineering/treated_wood"));

    // ceramics compat: a lot of melting and some casting
    String ceramics = "ceramics";
    String ceramicsFolder = folder + ceramics + "/";
    Function<String,ResourceLocation> ceramicsId = name -> new ResourceLocation(ceramics, name);
    Function<String,TagKey<Item>> ceramicsTag = name -> TagKey.create(Registries.ITEM, new ResourceLocation(ceramics, name));
    Consumer<FinishedRecipe> ceramicsConsumer = withCondition(consumer, new ModLoadedCondition(ceramics));

    // fill clay and cracked clay buckets
    ContainerFillingRecipeBuilder.tableRecipe(ceramicsId.apply("clay_bucket"), FluidType.BUCKET_VOLUME)
                                 .save(ceramicsConsumer, location(ceramicsFolder + "filling_clay_bucket"));
    ContainerFillingRecipeBuilder.tableRecipe(ceramicsId.apply("cracked_clay_bucket"), FluidType.BUCKET_VOLUME)
                                 .save(ceramicsConsumer, location(ceramicsFolder + "filling_cracked_clay_bucket"));

    // porcelain for ceramics
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenPorcelain, FluidValues.BRICK * 4)
                      .addInput(TinkerFluids.moltenClay.getTag(), FluidValues.BRICK * 3)
                      .addInput(TinkerFluids.moltenQuartz.getTag(), FluidValues.GEM)
                      .save(ceramicsConsumer, location(ceramicsFolder + "alloy_porcelain"));

    // melting clay
    String clayFolder = ceramicsFolder + "clay/";

    // unfired clay
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("unfired_clay_plate")), TinkerFluids.moltenClay, FluidValues.SLIMEBALL, 0.5f)
                        .save(ceramicsConsumer, location(clayFolder + "clay_1"));
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("clay_faucet"), ceramicsId.apply("clay_channel")), TinkerFluids.moltenClay, FluidValues.SLIMEBALL * 2, 0.65f)
                        .save(ceramicsConsumer, location(clayFolder + "clay_2"));
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("unfired_clay_bucket"), ceramicsId.apply("clay_cistern")), TinkerFluids.moltenClay, FluidValues.SLIMEBALL * 3, 0.9f)
                        .save(ceramicsConsumer, location(clayFolder + "clay_3"));

    // 2 bricks
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(
      ceramicsId.apply("dark_bricks_slab"), ceramicsId.apply("dragon_bricks_slab"),
      ceramicsId.apply("terracotta_faucet"), ceramicsId.apply("terracotta_channel")), TinkerFluids.moltenClay, FluidValues.SLIMEBALL * 2, 1.33f)
                        .save(ceramicsConsumer, location(clayFolder + "bricks_2"));
    // 3 bricks
    MeltingRecipeBuilder.melting(CompoundIngredient.of(
      Ingredient.of(ceramicsTag.apply("terracotta_cisterns")),
      NBTNameIngredient.from(ceramicsId.apply("clay_bucket")),
      NBTNameIngredient.from(ceramicsId.apply("cracked_clay_bucket"))),
                                 TinkerFluids.moltenClay, FluidValues.SLIMEBALL * 3, 1.67f)
                        .save(ceramicsConsumer, location(clayFolder + "bricks_3"));
    // 4 bricks
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(
      ceramicsId.apply("dark_bricks"), ceramicsId.apply("dark_bricks_stairs"), ceramicsId.apply("dark_bricks_wall"),
      ceramicsId.apply("dragon_bricks"), ceramicsId.apply("dragon_bricks_stairs"), ceramicsId.apply("dragon_bricks_wall")
    ), TinkerFluids.moltenClay, FluidValues.SLIMEBALL * 4, 2.0f)
                        .save(ceramicsConsumer, location(clayFolder + "block"));
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("kiln")), TinkerFluids.moltenClay, FluidValues.SLIME_CONGEALED * 3 + FluidValues.SLIMEBALL * 5, 4.0f)
                        .save(ceramicsConsumer, location(clayFolder + "kiln"));
    // lava bricks, lava byproduct
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("lava_bricks_slab")), TinkerFluids.moltenClay, FluidValues.SLIMEBALL * 2, 1.33f)
                        .addByproduct(new FluidStack(Fluids.LAVA, FluidType.BUCKET_VOLUME / 20))
                        .save(ceramicsConsumer, location(clayFolder + "lava_bricks_slab"));
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(
      ceramicsId.apply("lava_bricks"), ceramicsId.apply("lava_bricks_stairs"), ceramicsId.apply("lava_bricks_wall")
    ), TinkerFluids.moltenClay, FluidValues.SLIMEBALL * 4, 2f)
                        .addByproduct(new FluidStack(Fluids.LAVA, FluidType.BUCKET_VOLUME / 10))
                        .save(ceramicsConsumer, location(clayFolder + "lava_bricks_block"));
    // gauge, partially glass
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("terracotta_gauge")), TinkerFluids.moltenClay, FluidValues.SLIMEBALL, 1f)
                        .addByproduct(TinkerFluids.moltenGlass.result(FluidValues.GLASS_PANE / 4))
                        .save(ceramicsConsumer, location(clayFolder + "gauge"));
    // clay armor
    int slimeballPart = FluidValues.SLIMEBALL / 5;
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("clay_helmet")), TinkerFluids.moltenClay, FluidValues.SLIMEBALL * 5, 2.25f)
                        .setDamagable(slimeballPart)
                        .save(ceramicsConsumer, location(clayFolder + "clay_helmet"));
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("clay_chestplate")), TinkerFluids.moltenClay, FluidValues.SLIMEBALL * 8, 3f)
                        .setDamagable(slimeballPart)
                        .save(ceramicsConsumer, location(clayFolder + "clay_chestplate"));
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("clay_leggings")), TinkerFluids.moltenClay, FluidValues.SLIMEBALL * 7, 2.75f)
                        .setDamagable(slimeballPart)
                        .save(ceramicsConsumer, location(clayFolder + "clay_leggings"));
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("clay_boots")), TinkerFluids.moltenClay, FluidValues.SLIMEBALL * 4, 2f)
                        .setDamagable(slimeballPart)
                        .save(ceramicsConsumer, location(clayFolder + "clay_boots"));

    // melting porcelain
    String porcelainFolder = ceramicsFolder + "porcelain/";
    // unfired
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("unfired_porcelain")), TinkerFluids.moltenPorcelain, FluidValues.SLIMEBALL, 0.5f)
                        .save(ceramicsConsumer, location(porcelainFolder + "unfired_1"));
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("unfired_faucet"), ceramicsId.apply("unfired_channel")), TinkerFluids.moltenClay, FluidValues.SLIMEBALL * 2, 0.65f)
                        .save(ceramicsConsumer, location(porcelainFolder + "unfired_2"));
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("unfired_cistern")), TinkerFluids.moltenClay, FluidValues.SLIMEBALL * 3, 0.9f)
                        .save(ceramicsConsumer, location(porcelainFolder + "unfired_3"));
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("unfired_porcelain_block")), TinkerFluids.moltenPorcelain, FluidValues.SLIME_CONGEALED, 1f)
                        .save(ceramicsConsumer, location(porcelainFolder + "unfired_4"));

    // 1 brick
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("porcelain_brick")), TinkerFluids.moltenPorcelain, FluidValues.SLIMEBALL, 1f)
                        .save(ceramicsConsumer, location(porcelainFolder + "bricks_1"));
    // 2 bricks
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(
      ceramicsId.apply("porcelain_bricks_slab"), ceramicsId.apply("monochrome_bricks_slab"), ceramicsId.apply("marine_bricks_slab"), ceramicsId.apply("rainbow_bricks_slab"),
      ceramicsId.apply("porcelain_faucet"), ceramicsId.apply("porcelain_channel")
    ), TinkerFluids.moltenPorcelain, FluidValues.SLIMEBALL * 2, 1.33f)
                        .save(ceramicsConsumer, location(porcelainFolder + "bricks_2"));
    // 3 bricks
    MeltingRecipeBuilder.melting(Ingredient.of(ceramicsTag.apply("porcelain_cisterns")), TinkerFluids.moltenPorcelain, FluidValues.SLIMEBALL * 3, 1.67f)
                        .save(ceramicsConsumer, location(porcelainFolder + "bricks_3"));
    // 4 bricks
    MeltingRecipeBuilder.melting(CompoundIngredient.of(
      Ingredient.of(ceramicsTag.apply("porcelain_block")),
      Ingredient.of(ceramicsTag.apply("rainbow_porcelain")),
      ItemNameIngredient.from(
        ceramicsId.apply("porcelain_bricks"), ceramicsId.apply("porcelain_bricks_stairs"), ceramicsId.apply("porcelain_bricks_wall"),
        ceramicsId.apply("monochrome_bricks"), ceramicsId.apply("monochrome_bricks_stairs"), ceramicsId.apply("monochrome_bricks_wall"),
        ceramicsId.apply("marine_bricks"), ceramicsId.apply("marine_bricks_stairs"), ceramicsId.apply("marine_bricks_wall"),
        ceramicsId.apply("rainbow_bricks"), ceramicsId.apply("rainbow_bricks_stairs"), ceramicsId.apply("rainbow_bricks_wall")
      )), TinkerFluids.moltenPorcelain, FluidValues.SLIMEBALL * 4, 2.0f)
                        .save(ceramicsConsumer, location(porcelainFolder + "blocks"));
    // gold bricks, skipping gold byproduct as its so small and does not divide nicely
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("golden_bricks_slab")), TinkerFluids.moltenPorcelain, FluidValues.SLIMEBALL * 2, 1.33f)
                        .save(ceramicsConsumer, location(porcelainFolder + "golden_bricks_slab"));
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(
      ceramicsId.apply("golden_bricks"), ceramicsId.apply("golden_bricks_stairs"), ceramicsId.apply("golden_bricks_wall")
    ), TinkerFluids.moltenPorcelain, FluidValues.SLIMEBALL * 4, 2f)
                        .save(ceramicsConsumer, location(porcelainFolder + "golden_bricks_block"));
    // gauge, partially glass
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("porcelain_gauge")), TinkerFluids.moltenPorcelain, FluidValues.SLIMEBALL, 1f)
                        .addByproduct(TinkerFluids.moltenGlass.result(FluidValues.GLASS_PANE / 4))
                        .save(ceramicsConsumer, location(porcelainFolder + "gauge"));

    // casting bricks
    String castingFolder = ceramicsFolder + "casting/";
    castingWithCast(ceramicsConsumer, TinkerFluids.moltenPorcelain, FluidValues.SLIMEBALL, TinkerSmeltery.ingotCast, ItemNameOutput.fromName(ceramicsId.apply("porcelain_brick")), castingFolder + "porcelain_brick");
    ItemCastingRecipeBuilder.basinRecipe(ItemNameOutput.fromName(ceramicsId.apply("white_porcelain")))
                            .setFluidAndTime(TinkerFluids.moltenPorcelain, FluidValues.SLIME_CONGEALED)
                            .save(ceramicsConsumer, location(castingFolder + "porcelain"));
    // lava bricks
    ItemCastingRecipeBuilder.basinRecipe(ItemNameOutput.fromName(ceramicsId.apply("lava_bricks")))
                            .setCast(Blocks.BRICKS, true)
                            .setFluidAndTime(new FluidStack(Fluids.LAVA, FluidType.BUCKET_VOLUME / 10))
                            .save(ceramicsConsumer, location(castingFolder + "lava_bricks"));
    ItemCastingRecipeBuilder.basinRecipe(ItemNameOutput.fromName(ceramicsId.apply("lava_bricks_slab")))
                            .setCast(Blocks.BRICK_SLAB, true)
                            .setFluidAndTime(new FluidStack(Fluids.LAVA, FluidType.BUCKET_VOLUME / 20))
                            .save(ceramicsConsumer, location(castingFolder + "lava_bricks_slab"));
    ItemCastingRecipeBuilder.basinRecipe(ItemNameOutput.fromName(ceramicsId.apply("lava_bricks_stairs")))
                            .setCast(Blocks.BRICK_STAIRS, true)
                            .setFluidAndTime(new FluidStack(Fluids.LAVA, FluidType.BUCKET_VOLUME / 10))
                            .save(ceramicsConsumer, location(castingFolder + "lava_bricks_stairs"));
    ItemCastingRecipeBuilder.basinRecipe(ItemNameOutput.fromName(ceramicsId.apply("lava_bricks_wall")))
                            .setCast(Blocks.BRICK_WALL, true)
                            .setFluidAndTime(new FluidStack(Fluids.LAVA, FluidType.BUCKET_VOLUME / 10))
                            .save(ceramicsConsumer, location(castingFolder + "lava_bricks_wall"));

    // refined glowstone composite
    Consumer<FinishedRecipe> wrapped = withCondition(consumer, tagCondition("ingots/refined_glowstone"), tagCondition("ingots/osmium"));
    ItemCastingRecipeBuilder.tableRecipe(ItemOutput.fromTag(getItemTag(COMMON, "ingots/refined_glowstone")))
                            .setCast(Tags.Items.DUSTS_GLOWSTONE, true)
                            .setFluidAndTime(TinkerFluids.moltenOsmium, FluidValues.INGOT)
                            .save(wrapped, location(folder + "refined_glowstone_ingot"));
    wrapped = withCondition(consumer, tagCondition("ingots/refined_obsidian"), tagCondition("ingots/osmium"));
    ItemCastingRecipeBuilder.tableRecipe(ItemOutput.fromTag(getItemTag(COMMON, "ingots/refined_obsidian")))
                            .setCast(getItemTag(COMMON, "dusts/refined_obsidian"), true)
                            .setFluidAndTime(TinkerFluids.moltenOsmium, FluidValues.INGOT)
                            .save(wrapped, location(folder + "refined_obsidian_ingot"));
    ItemCastingRecipeBuilder.tableRecipe(TinkerMaterials.necroniumBone)
                            .setFluidAndTime(TinkerFluids.moltenUranium, FluidValues.INGOT)
                            .setCast(TinkerTags.Items.WITHER_BONES, true)
                            .save(withCondition(consumer, tagCondition("ingots/uranium")), location(folder + "necronium_bone"));
  }


  /* Seared casting */

  /**
   * Adds a stonecutting recipe with automatic name and criteria
   * @param consumer  Recipe consumer
   * @param output    Recipe output
   * @param folder    Recipe folder path
   */
  private void searedStonecutter(Consumer<FinishedRecipe> consumer, ItemLike output, String folder) {
    SingleItemRecipeBuilder.stonecutting(
      CompoundIngredient.of(
        Ingredient.of(TinkerSmeltery.searedStone),
        DifferenceIngredient.of(Ingredient.of(TinkerTags.Items.SEARED_BRICKS), Ingredient.of(output))), RecipeCategory.BUILDING_BLOCKS, output, 1)
                           .unlockedBy("has_stone", has(TinkerSmeltery.searedStone))
                           .unlockedBy("has_bricks", has(TinkerTags.Items.SEARED_BRICKS))
                           .save(consumer, wrap(id(output), folder, "_stonecutting"));
  }

  /**
   * Adds a recipe to create the given seared block using molten clay on stone
   * @param consumer  Recipe consumer
   * @param block     Output block
   * @param cast      Cast item
   * @param location  Recipe location
   */
  private void searedCasting(Consumer<FinishedRecipe> consumer, ItemLike block, Ingredient cast, String location) {
    searedCasting(consumer, block, cast, FluidValues.SLIMEBALL * 2, location);
  }

  /**
   * Adds a recipe to create the given seared slab block using molten clay on stone
   * @param consumer  Recipe consumer
   * @param block     Output block
   * @param cast      Cast item
   * @param location  Recipe location
   */
  private void searedSlabCasting(Consumer<FinishedRecipe> consumer, ItemLike block, Ingredient cast, String location) {
    searedCasting(consumer, block, cast, FluidValues.SLIMEBALL, location);
  }

  /**
   * Adds a recipe to create the given seared block using molten clay on stone
   * @param consumer  Recipe consumer
   * @param block     Output block
   * @param cast      Cast item
   * @param amount    Amount of fluid needed
   * @param location  Recipe location
   */
  private void searedCasting(Consumer<FinishedRecipe> consumer, ItemLike block, Ingredient cast, int amount, String location) {
    ItemCastingRecipeBuilder.basinRecipe(block)
                            .setFluidAndTime(TinkerFluids.moltenClay, amount)
                            .setCast(cast, true)
                            .save(consumer, location(location));
  }


  /* Scorched casting */

  /**
   * Adds a stonecutting recipe with automatic name and criteria
   * @param consumer  Recipe consumer
   * @param output    Recipe output
   * @param folder    Recipe folder path
   */
  private void scorchedStonecutter(Consumer<FinishedRecipe> consumer, ItemLike output, String folder) {
    SingleItemRecipeBuilder.stonecutting(DifferenceIngredient.of(Ingredient.of(TinkerTags.Items.SCORCHED_BLOCKS), Ingredient.of(output)), RecipeCategory.BUILDING_BLOCKS, output, 1)
                           .unlockedBy("has_block", has(TinkerTags.Items.SCORCHED_BLOCKS))
                           .save(consumer, wrap(id(output), folder, "_stonecutting"));
  }

  /**
   * Adds a recipe to create the given seared block using molten clay on stone
   * @param consumer  Recipe consumer
   * @param block     Output block
   * @param cast      Cast item
   * @param location  Recipe location
   */
  private void scorchedCasting(Consumer<FinishedRecipe> consumer, ItemLike block, Ingredient cast, String location) {
    scorchedCasting(consumer, block, cast, FluidValues.SLIMEBALL * 2, location);
  }

  /**
   * Adds a recipe to create the given seared block using molten clay on stone
   * @param consumer  Recipe consumer
   * @param block     Output block
   * @param cast      Cast item
   * @param amount    Amount of fluid needed
   * @param location  Recipe location
   */
  private void scorchedCasting(Consumer<FinishedRecipe> consumer, ItemLike block, Ingredient cast, int amount, String location) {
    ItemCastingRecipeBuilder.basinRecipe(block)
                            .setFluidAndTime(TinkerFluids.magma, amount)
                            .setCast(cast, true)
                            .save(consumer, location(location));
  }


  /* Casting */

  /**
   * Adds melting recipes for slime
   * @param consumer  Consumer
   * @param fluid     Fluid
   * @param type      Slime type
   * @param folder    Output folder
   */
  private void slimeMelting(Consumer<FinishedRecipe> consumer, FluidObject<?> fluid, SlimeType type, String folder) {
    String slimeFolder = folder + type.getSerializedName() + "/";
    MeltingRecipeBuilder.melting(Ingredient.of(type.getSlimeballTag()), fluid, FluidValues.SLIMEBALL, 1.0f)
                        .save(consumer, location(slimeFolder + "ball"));
    ItemLike item = TinkerWorld.congealedSlime.get(type);
    MeltingRecipeBuilder.melting(Ingredient.of(item), fluid, FluidValues.SLIME_CONGEALED, 2.0f)
                        .save(consumer, location(slimeFolder + "congealed"));
    item = TinkerWorld.slime.get(type);
    MeltingRecipeBuilder.melting(Ingredient.of(item), fluid, FluidValues.SLIME_BLOCK, 3.0f)
                        .save(consumer, location(slimeFolder + "block"));
  }

  /**
   * Adds slime related casting recipes
   * @param consumer    Recipe consumer
   * @param fluid       Fluid matching the slime type
   * @param commonTag   If true, uses a tag in the common namespace. If false, uses a local ingredient
   * @param slimeType   SlimeType for this recipe
   * @param folder      Output folder
   */
  private void slimeCasting(Consumer<FinishedRecipe> consumer, FluidObject<?> fluid, boolean commonTag, SlimeType slimeType, String folder) {
    String colorFolder = folder + slimeType.getSerializedName() + "/";
    ItemCastingRecipeBuilder.basinRecipe(TinkerWorld.congealedSlime.get(slimeType))
                            .setFluidAndTime(fluid, FluidValues.SLIME_CONGEALED)
                            .save(consumer, location(colorFolder + "congealed"));
    ItemCastingRecipeBuilder.basinRecipe(TinkerWorld.slimyEnderbarkRoots.get(slimeType))
                            .setFluidAndTime(fluid, FluidValues.SLIME_CONGEALED)
                            .setCast(TinkerWorld.enderbarkRoots, true)
                            .save(consumer, location(colorFolder + "roots"));
    ItemCastingRecipeBuilder.basinRecipe(TinkerWorld.slime.get(slimeType))
                            .setFluidAndTime(fluid, FluidValues.SLIME_BLOCK - FluidValues.SLIME_CONGEALED)
                            .setCast(TinkerWorld.congealedSlime.get(slimeType), true)
                            .save(consumer, location(colorFolder + "block"));
    ItemCastingRecipeBuilder.tableRecipe(TinkerCommons.slimeball.get(slimeType))
                            .setFluidAndTime(fluid, FluidValues.SLIMEBALL)
                            .save(consumer, location(colorFolder + "slimeball"));
    ItemCastingRecipeBuilder.tableRecipe(TinkerFluids.slimeBottle.get(slimeType))
                            .setFluid(fluid.ingredient(FluidValues.SLIMEBALL))
                            .setCoolingTime(1)
                            .setCast(Items.GLASS_BOTTLE, true)
                            .save(consumer, location(colorFolder + "bottle"));
    ItemCastingRecipeBuilder.basinRecipe(TinkerWorld.slimeDirt.get(slimeType.asDirt()))
                            .setFluidAndTime(fluid, FluidValues.SLIME_CONGEALED)
                            .setCast(Blocks.DIRT, true)
                            .save(consumer, location(colorFolder + "dirt"));
  }

  /** Adds recipes for melting slime crystals */
  private void crystalMelting(Consumer<FinishedRecipe> consumer, GeodeItemObject geode, FluidObject<?> fluid, String folder) {
    MeltingRecipeBuilder.melting(Ingredient.of(geode), fluid, FluidValues.SLIMEBALL, 1.0f).save(consumer, location(folder + "crystal"));
    MeltingRecipeBuilder.melting(Ingredient.of(geode.getBlock()), fluid, FluidValues.SLIMEBALL * 4, 2.0f).save(consumer, location(folder + "crystal_block"));
    for (BudSize bud : BudSize.values()) {
      int size = bud.getSize();
      MeltingRecipeBuilder.melting(Ingredient.of(geode.getBud(bud)), fluid, FluidValues.SLIMEBALL * size, (size + 1) / 2f)
                          .setOre(OreRateType.GEM)
                          .save(consumer, location(folder + "bud_" + bud.getName()));
    }
  }
}
