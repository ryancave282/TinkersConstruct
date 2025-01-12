package net.ryancave282.tconstruct.smeltery.data;

import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import slimeknights.mantle.datagen.MantleTags;
import slimeknights.mantle.fluid.transfer.AbstractFluidContainerTransferProvider;
import slimeknights.mantle.fluid.transfer.FillFluidContainerTransfer;
import slimeknights.mantle.fluid.transfer.FillFluidWithNBTTransfer;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.mantle.registration.object.FluidObject;
import net.ryancave282.tconstruct.TConstruct;
import net.ryancave282.tconstruct.common.TinkerTags;
import net.ryancave282.tconstruct.fluids.TinkerFluids;
import net.ryancave282.tconstruct.fluids.item.EmptyPotionTransfer;
import net.ryancave282.tconstruct.library.recipe.FluidValues;
import net.ryancave282.tconstruct.shared.block.SlimeType;

import javax.annotation.Nullable;

public class FluidContainerTransferProvider extends AbstractFluidContainerTransferProvider {
  public FluidContainerTransferProvider(PackOutput packOutput) {
    super(packOutput, TConstruct.MOD_ID);
  }

  @Override
  protected void addTransfers() {
    addFillEmpty("honey_bottle_",  Items.HONEY_BOTTLE,  Items.GLASS_BOTTLE, TinkerFluids.honey,        FluidValues.BOTTLE, false);
    addFillEmpty("beetroot_soup_", Items.BEETROOT_SOUP, Items.BOWL,         TinkerFluids.beetrootSoup, FluidValues.BOWL,   false);
    addFillEmpty("mushroom_stew_", Items.MUSHROOM_STEW, Items.BOWL,         TinkerFluids.mushroomStew, FluidValues.BOWL,   false);
    addFillEmpty("rabbit_stew_",   Items.RABBIT_STEW,   Items.BOWL,         TinkerFluids.rabbitStew,   FluidValues.BOWL,   false);
    addFillEmpty("meat_soup_",     TinkerFluids.meatSoupBowl, Items.BOWL,   TinkerFluids.meatSoup,     FluidValues.BOWL,   false);
    // potions
    addPotion("potion_",           Items.POTION,           Items.GLASS_BOTTLE,           null);
    addPotion("potion_splash_",    Items.SPLASH_POTION,    TinkerFluids.splashBottle,    TinkerTags.Items.SPLASH_BOTTLE);
    addPotion("potion_lingering_", Items.LINGERING_POTION, TinkerFluids.lingeringBottle, TinkerTags.Items.LINGERING_BOTTLE);
    // these bottles are fluid handlers, but glass bottles are not
    addBottleFill("venom_bottle_fill", TinkerFluids.venomBottle, TinkerFluids.venom);
    addBottleFill("earth_slime_bottle_fill", TinkerFluids.slimeBottle.get(SlimeType.EARTH), TinkerFluids.earthSlime);
    addBottleFill("sky_slime_bottle_fill",   TinkerFluids.slimeBottle.get(SlimeType.SKY),   TinkerFluids.skySlime);
    addBottleFill("ender_slime_bottle_fill", TinkerFluids.slimeBottle.get(SlimeType.ENDER), TinkerFluids.enderSlime);
    addBottleFill("magma_bottle_fill",       TinkerFluids.magmaBottle,                      TinkerFluids.magma);
  }

  /** Adds generic fill and empty for a container */
  protected void addPotion(String prefix, ItemLike filled, ItemLike containerItem, @Nullable TagKey<Item> containerTag) {
    // water bottles are 1/3 of a bucket, to prevent water dupes we round up on fill and down on empty
    addTransfer(prefix + "empty",  new EmptyPotionTransfer(Ingredient.of(filled), ItemOutput.fromItem(containerItem), TinkerFluids.potion.result(FluidValues.BOTTLE)));
    Ingredient container = containerTag == null ? Ingredient.of(containerItem) : Ingredient.of(containerTag);
    addTransfer(prefix + "fill", new FillFluidWithNBTTransfer(container, ItemOutput.fromItem(filled), TinkerFluids.potion.ingredient(FluidValues.BOTTLE)));
    addTransfer(prefix + "water", new FillFluidContainerTransfer(
      container,
      ItemOutput.fromStack(PotionUtils.setPotion(new ItemStack(filled), Potions.WATER)),
      FluidIngredient.of(MantleTags.Fluids.WATER, FluidValues.BOTTLE * 2)));
  }

  /** Adds a recipe for a bottle that fills with 250mb of fluid, emptying is assumed handled */
  protected void addBottleFill(String name, ItemLike output, FluidObject<?> fluid) {
    addTransfer(name, new FillFluidContainerTransfer(Ingredient.of(Items.GLASS_BOTTLE), ItemOutput.fromItem(output), fluid.ingredient(FluidValues.BOTTLE)));
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Fluid Container Transfer";
  }
}
