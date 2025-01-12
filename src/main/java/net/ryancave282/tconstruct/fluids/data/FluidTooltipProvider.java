package net.ryancave282.tconstruct.fluids.data;

import net.minecraft.data.PackOutput;
import net.minecraftforge.fluids.FluidType;
import slimeknights.mantle.datagen.MantleTags;
import slimeknights.mantle.fluid.tooltip.AbstractFluidTooltipProvider;
import net.ryancave282.tconstruct.TConstruct;
import net.ryancave282.tconstruct.fluids.TinkerFluids;
import net.ryancave282.tconstruct.library.recipe.FluidValues;
import net.ryancave282.tconstruct.smeltery.TinkerSmeltery;
import net.ryancave282.tconstruct.smeltery.menu.AlloyerContainerMenu;
import net.ryancave282.tconstruct.smeltery.menu.MelterContainerMenu;

import java.util.Objects;

import static net.ryancave282.tconstruct.common.TinkerTags.Fluids.CLAY_TOOLTIPS;
import static net.ryancave282.tconstruct.common.TinkerTags.Fluids.GLASS_TOOLTIPS;
import static net.ryancave282.tconstruct.common.TinkerTags.Fluids.LARGE_GEM_TOOLTIPS;
import static net.ryancave282.tconstruct.common.TinkerTags.Fluids.METAL_TOOLTIPS;
import static net.ryancave282.tconstruct.common.TinkerTags.Fluids.SLIME_TOOLTIPS;
import static net.ryancave282.tconstruct.common.TinkerTags.Fluids.SMALL_GEM_TOOLTIPS;
import static net.ryancave282.tconstruct.common.TinkerTags.Fluids.SOUP_TOOLTIPS;

public class FluidTooltipProvider extends AbstractFluidTooltipProvider {
  public FluidTooltipProvider(PackOutput packOutput) {
    super(packOutput, TConstruct.MOD_ID);
  }

  @Override
  protected void addFluids() {
    // screen capacities
    add("ingots").addUnit("ingot", FluidValues.INGOT);
    addRedirect(AlloyerContainerMenu.TOOLTIP_FORMAT, id("ingots"));
    addRedirect(MelterContainerMenu.TOOLTIP_FORMAT, id("ingots"));
    addRedirect(TinkerSmeltery.smeltery.getId(), id("ingots"));
    addRedirect(TinkerSmeltery.foundry.getId(), id("ingots"));

    // standard fluids
    add("metals", METAL_TOOLTIPS)
      .addUnit("block", FluidValues.METAL_BLOCK)
      .addUnit("ingot", FluidValues.INGOT)
      .addUnit("nugget", FluidValues.NUGGET);
    add("large_gems", LARGE_GEM_TOOLTIPS)
      .addUnit("block", FluidValues.LARGE_GEM_BLOCK)
      .addUnit("gem", FluidValues.GEM)
      .addUnit("shard", FluidValues.GEM_SHARD);
    add("small_gems", SMALL_GEM_TOOLTIPS)
      .addUnit("block", FluidValues.SMALL_GEM_BLOCK)
      .addUnit("gem", FluidValues.GEM)
      .addUnit("shard", FluidValues.GEM_SHARD);

    add("clay", CLAY_TOOLTIPS)
      .addUnit("block", FluidValues.BRICK_BLOCK)
      .addUnit("brick", FluidValues.BRICK);
    add("slime", SLIME_TOOLTIPS)
      .addUnit("block", FluidValues.SLIME_BLOCK)
      .addUnit("slimeball", FluidValues.SLIMEBALL);
    add("glass", GLASS_TOOLTIPS)
      .addUnit("block", FluidValues.GLASS_BLOCK)
      .addUnit("pane", FluidValues.GLASS_PANE);

    add("water", MantleTags.Fluids.WATER)
      .addUnit("kilobucket", "mantle", FluidType.BUCKET_VOLUME * 1000)
      .addUnit("bucket",     "mantle", FluidType.BUCKET_VOLUME)
      .addUnit("bottle", FluidValues.BOTTLE);
    add("venom", TinkerFluids.venom.getTag())
      .addUnit("kilobucket", "mantle", FluidType.BUCKET_VOLUME * 1000)
      .addUnit("bucket",     "mantle", FluidType.BUCKET_VOLUME)
      .addUnit("bottle", FluidValues.BOTTLE);
    add("honey", TinkerFluids.honey.getTag())
      .addUnit("block", FluidValues.BOTTLE * 4)
      .addUnit("bottle", FluidValues.BOTTLE);
    add("soup", SOUP_TOOLTIPS)
      .addUnit("bowl", FluidValues.BOWL);

    add("potion", Objects.requireNonNull(TinkerFluids.potion.getCommonTag()))
      .addUnit("bottle", FluidValues.BOTTLE);
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Fluid Tooltip Provider";
  }
}
