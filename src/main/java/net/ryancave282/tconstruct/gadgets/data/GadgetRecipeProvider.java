package net.ryancave282.tconstruct.gadgets.data;

import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.ryancave282.tconstruct.common.TinkerTags;
import net.ryancave282.tconstruct.common.data.BaseRecipeProvider;
import net.ryancave282.tconstruct.fluids.TinkerFluids;
import net.ryancave282.tconstruct.gadgets.TinkerGadgets;
import net.ryancave282.tconstruct.gadgets.entity.FrameType;
import net.ryancave282.tconstruct.library.recipe.FluidValues;
import net.ryancave282.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;
import net.ryancave282.tconstruct.shared.TinkerCommons;
import net.ryancave282.tconstruct.shared.TinkerMaterials;
import net.ryancave282.tconstruct.shared.block.SlimeType;
import net.ryancave282.tconstruct.world.TinkerWorld;
import net.ryancave282.tconstruct.world.block.FoliageType;

import java.util.function.Consumer;

public class GadgetRecipeProvider extends BaseRecipeProvider {
  public GadgetRecipeProvider(PackOutput packOutput) {
    super(packOutput);
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Gadget Recipes";
  }

  @Override
  protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
    // throw balls
    String folder = "gadgets/throwball/";
    ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, TinkerGadgets.efln.get())
                       .define('#', Tags.Items.GUNPOWDER)
                       .define('X', Items.FLINT)
                       .pattern(" # ")
                       .pattern("#X#")
                       .pattern(" # ")
                       .unlockedBy("has_item", has(Tags.Items.DUSTS_GLOWSTONE))
                       .save(consumer, prefix(TinkerGadgets.efln, folder));
    ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, TinkerGadgets.glowBall.get(), 8)
                       .define('#', Items.SNOWBALL)
                       .define('X', Tags.Items.DUSTS_GLOWSTONE)
                       .pattern("###")
                       .pattern("#X#")
                       .pattern("###")
                       .unlockedBy("has_item", has(Tags.Items.DUSTS_GLOWSTONE))
                       .save(consumer, prefix(TinkerGadgets.glowBall, folder));

    // Shurikens
    folder = "gadgets/shuriken/";
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, TinkerGadgets.flintShuriken.get(), 4)
                        .define('X', Items.FLINT)
                        .pattern(" X ")
                        .pattern("X X")
                        .pattern(" X ")
                        .unlockedBy("has_item", has(Items.FLINT))
                        .save(consumer, prefix(TinkerGadgets.flintShuriken, folder));
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, TinkerGadgets.quartzShuriken.get(), 4)
                        .define('X', Items.QUARTZ)
                        .pattern(" X ")
                        .pattern("X X")
                        .pattern(" X ")
                        .unlockedBy("has_item", has(Items.QUARTZ))
                        .save(consumer, prefix(TinkerGadgets.quartzShuriken, folder));

    // piggybackpack
    folder = "gadgets/";
    ItemCastingRecipeBuilder.tableRecipe(TinkerGadgets.piggyBackpack)
                            .setCast(Items.SADDLE, true)
                            .setFluidAndTime(TinkerFluids.skySlime, FluidValues.SLIMEBALL * 4)
                            .save(consumer, prefix(TinkerGadgets.piggyBackpack, folder));
    ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerGadgets.punji)
                       .define('b', Items.BAMBOO)
                       .pattern(" b ")
                       .pattern("bbb")
                       .unlockedBy("has_item", has(Items.BAMBOO))
                       .save(consumer, prefix(TinkerGadgets.punji, folder));

    // frames
    folder = "gadgets/fancy_frame/";
    frameCrafting(consumer, Tags.Items.NUGGETS_GOLD, FrameType.GOLD);
    frameCrafting(consumer, TinkerMaterials.manyullyn.getNuggetTag(), FrameType.MANYULLYN);
    frameCrafting(consumer, TinkerTags.Items.NUGGETS_NETHERITE, FrameType.NETHERITE);
    ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerGadgets.itemFrame.get(FrameType.DIAMOND))
                       .define('e', TinkerCommons.obsidianPane)
                       .define('M', Tags.Items.GEMS_DIAMOND)
                       .pattern(" e ")
                       .pattern("eMe")
                       .pattern(" e ")
                       .unlockedBy("has_item", has(Tags.Items.GEMS_DIAMOND))
                       .group(prefix("fancy_item_frame"))
                       .save(consumer, location("gadgets/frame/" + FrameType.DIAMOND.getSerializedName()));
    ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerGadgets.itemFrame.get(FrameType.CLEAR))
                       .define('e', Tags.Items.GLASS_PANES_COLORLESS)
                       .define('M', Tags.Items.GLASS_COLORLESS)
                       .pattern(" e ")
                       .pattern("eMe")
                       .pattern(" e ")
                       .unlockedBy("has_item", has(Tags.Items.GLASS_PANES_COLORLESS))
                       .group(prefix("fancy_item_frame"))
                       .save(consumer, location(folder + FrameType.CLEAR.getSerializedName()));
    Item goldFrame = TinkerGadgets.itemFrame.get(FrameType.GOLD);
    Item reversedFrame = TinkerGadgets.itemFrame.get(FrameType.REVERSED_GOLD);
    ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, reversedFrame)
                          .requires(goldFrame)
                          .requires(Items.REDSTONE_TORCH)
                          .unlockedBy("has_item", has(goldFrame))
                          .group(prefix("reverse_fancy_item_frame"))
                          .save(consumer, location(folder + FrameType.REVERSED_GOLD.getSerializedName()));
    ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, goldFrame)
                          .requires(reversedFrame)
                          .requires(Items.REDSTONE_TORCH)
                          .unlockedBy("has_item", has(reversedFrame))
                          .group(prefix("reverse_fancy_item_frame"))
                          .save(consumer, location(folder + "reversed_reversed_gold"));

    String cakeFolder = "gadgets/cake/";
    TinkerGadgets.cake.forEach((foliage, cake) -> {
      if (foliage != FoliageType.ICHOR) {
        SlimeType slime = foliage.asSlime();
        ItemLike grass = TinkerWorld.slimeTallGrass.get(foliage);
        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, cake)
                           .define('M', slime != null ? TinkerFluids.slime.get(slime).getBucket() : TinkerFluids.honey.asItem())
                           .define('S', foliage.isNether() ? Ingredient.of(Tags.Items.DUSTS_GLOWSTONE) : Ingredient.of(Items.SUGAR))
                           .define('E', Items.EGG)
                           .define('W', TinkerWorld.slimeTallGrass.get(foliage))
                           .pattern("MMM").pattern("SES").pattern("WWW")
                           .unlockedBy("has_slime", has(grass))
                           .save(consumer, location(cakeFolder + foliage.getSerializedName()));
      }
    });
    Item bucket = TinkerFluids.magma.asItem();
    ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, TinkerGadgets.magmaCake)
                       .define('M', bucket)
                       .define('S', Ingredient.of(Tags.Items.DUSTS_GLOWSTONE))
                       .define('E', Items.EGG)
                       .define('W', Blocks.CRIMSON_ROOTS)
                       .pattern("MMM").pattern("SES").pattern("WWW")
                       .unlockedBy("has_slime", has(bucket))
                       .save(consumer, location(cakeFolder + "magma"));
  }


  /* Helpers */

  /**
   * Adds a recipe to the campfire, furnace, and smoker
   * @param consumer    Recipe consumer
   * @param input       Recipe input
   * @param output      Recipe output
   * @param experience  Experience for the recipe
   * @param folder      Folder to store the recipe
   */
  private void foodCooking(Consumer<FinishedRecipe> consumer, ItemLike input, ItemLike output, float experience, String folder) {
    SimpleCookingRecipeBuilder.campfireCooking(Ingredient.of(input), RecipeCategory.FOOD, output, experience, 600)
                              .unlockedBy("has_item", has(input))
                              .save(consumer, wrap(id(output), folder, "_campfire"));
    // furnace is 200 ticks
    ResourceLocation outputId = id(output);
    InventoryChangeTrigger.TriggerInstance criteria = has(input);
    SimpleCookingRecipeBuilder.smelting(Ingredient.of(input), RecipeCategory.FOOD, output, experience, 200)
                              .unlockedBy("has_item", criteria)
                              .save(consumer, wrap(outputId, folder, "_furnace"));
    // smoker 100 ticks
    SimpleCookingRecipeBuilder.smoking(Ingredient.of(input), RecipeCategory.FOOD, output, experience, 100)
                              .unlockedBy("has_item", criteria)
                              .save(consumer, wrap(outputId, folder, "_smoker"));
  }

  /**
   * Adds a recipe for an item frame type
   * @param consumer  Recipe consumer
   * @param edges     Edge item
   * @param type      Frame type
   */
  private void frameCrafting(Consumer<FinishedRecipe> consumer, TagKey<Item> edges, FrameType type) {
    ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerGadgets.itemFrame.get(type))
                       .define('e', edges)
                       .define('M', TinkerCommons.obsidianPane)
                       .pattern(" e ")
                       .pattern("eMe")
                       .pattern(" e ")
                       .unlockedBy("has_item", has(edges))
                       .group(prefix("fancy_item_frame"))
                       .save(consumer, location("gadgets/frame/" + type.getSerializedName()));
  }
}
