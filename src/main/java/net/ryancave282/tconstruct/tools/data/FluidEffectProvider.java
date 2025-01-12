package net.ryancave282.tconstruct.tools.data;

import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.fluids.FluidType;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.mantle.data.predicate.entity.MobTypePredicate;
import slimeknights.mantle.recipe.data.FluidNameIngredient;
import slimeknights.mantle.recipe.helper.ItemOutput;
import net.ryancave282.tconstruct.TConstruct;
import net.ryancave282.tconstruct.common.TinkerDamageTypes;
import net.ryancave282.tconstruct.common.TinkerTags;
import net.ryancave282.tconstruct.fluids.TinkerFluids;
import net.ryancave282.tconstruct.library.data.tinkering.AbstractFluidEffectProvider;
import net.ryancave282.tconstruct.library.modifiers.fluid.FluidEffect;
import net.ryancave282.tconstruct.library.modifiers.fluid.FluidMobEffect;
import net.ryancave282.tconstruct.library.modifiers.fluid.TimeAction;
import net.ryancave282.tconstruct.library.modifiers.fluid.block.PlaceBlockFluidEffect;
import net.ryancave282.tconstruct.library.modifiers.fluid.block.PotionCloudFluidEffect;
import net.ryancave282.tconstruct.library.modifiers.fluid.entity.AddBreathFluidEffect;
import net.ryancave282.tconstruct.library.modifiers.fluid.entity.AwardStatFluidEffect;
import net.ryancave282.tconstruct.library.modifiers.fluid.entity.CureEffectsFluidEffect;
import net.ryancave282.tconstruct.library.modifiers.fluid.entity.DamageFluidEffect;
import net.ryancave282.tconstruct.library.modifiers.fluid.entity.FireFluidEffect;
import net.ryancave282.tconstruct.library.modifiers.fluid.entity.FreezeFluidEffect;
import net.ryancave282.tconstruct.library.modifiers.fluid.entity.PotionFluidEffect;
import net.ryancave282.tconstruct.library.modifiers.fluid.entity.RemoveEffectFluidEffect;
import net.ryancave282.tconstruct.library.modifiers.fluid.entity.RestoreHungerFluidEffect;
import net.ryancave282.tconstruct.library.recipe.FluidValues;
import net.ryancave282.tconstruct.library.recipe.TagPredicate;
import net.ryancave282.tconstruct.shared.TinkerCommons;
import net.ryancave282.tconstruct.tools.TinkerModifiers;
import net.ryancave282.tconstruct.tools.modifiers.traits.skull.StrongBonesModifier;

import java.util.Objects;
import java.util.function.Function;

public class FluidEffectProvider extends AbstractFluidEffectProvider {
  public FluidEffectProvider(PackOutput packOutput) {
    super(packOutput, TConstruct.MOD_ID);
  }

  @Override
  protected void addFluids() {
    // vanilla
    addFluid(Fluids.WATER, FluidType.BUCKET_VOLUME / 20)
      .addEntityEffect(LivingEntityPredicate.WATER_SENSITIVE, new DamageFluidEffect(2f, TinkerDamageTypes.WATER))
      .addEntityEffect(FluidEffect.EXTINGUISH_FIRE);
    addFluid(Fluids.LAVA, FluidType.BUCKET_VOLUME / 20)
      .addEntityEffect(LivingEntityPredicate.FIRE_IMMUNE.inverted(), new DamageFluidEffect(2f, TinkerDamageTypes.FLUID_FIRE))
      .addEntityEffect(new FireFluidEffect(TimeAction.SET, 10))
      .addBlockEffect(new PlaceBlockFluidEffect(Blocks.FIRE));
    addFluid(Tags.Fluids.MILK, FluidType.BUCKET_VOLUME / 10)
      .addEntityEffect(new CureEffectsFluidEffect(Items.MILK_BUCKET))
      .addEntityEffect(StrongBonesModifier.FLUID_EFFECT);
    addFluid(Objects.requireNonNull(TinkerFluids.powderedSnow.getCommonTag()), FluidType.BUCKET_VOLUME / 10)
      .addEntityEffect(new FreezeFluidEffect(TimeAction.ADD, 160))
      .addBlockEffect(new PlaceBlockFluidEffect(Blocks.SNOW));

    // blaze - more damage, less fire
    burningFluid("blazing_blood", TinkerFluids.blazingBlood.getTag(), FluidType.BUCKET_VOLUME / 20, 3f, 5);

    // slime
    int slimeballPiece = FluidValues.SLIMEBALL / 5;
    // earth - lucky
    addFluid(TinkerFluids.earthSlime.getTag(), slimeballPiece).addEffect(TimeAction.SET, FluidMobEffect.builder().effect(MobEffects.LUCK, 15 * 20).effect(MobEffects.MOVEMENT_SLOWDOWN, 15 * 20));
    // sky - jump boost
    addFluid(TinkerFluids.skySlime.getTag(), slimeballPiece).addEffect(TimeAction.SET, FluidMobEffect.builder().effect(MobEffects.JUMP, 20*20).effect(MobEffects.MOVEMENT_SLOWDOWN, 20*15));
    // ender - levitation
    addFluid(TinkerFluids.enderSlime.getTag(), slimeballPiece).addEffect(TimeAction.SET, FluidMobEffect.builder().effect(MobEffects.LEVITATION, 20*5).effect(MobEffects.MOVEMENT_SLOWDOWN, 20*15));
    // slimelike
    // venom - poison & strength
    addFluid(TinkerFluids.venom.getTag(), slimeballPiece).addEffect(TimeAction.SET, FluidMobEffect.builder().effect(MobEffects.POISON, 20*5).effect(MobEffects.DAMAGE_BOOST, 20*10));
    // magma - fire resistance
    addFluid(TinkerFluids.magma.getTag(), slimeballPiece).addEffect(TimeAction.SET, FluidMobEffect.builder().effect(MobEffects.FIRE_RESISTANCE, 20 * 25));
    // soul - slowness and blindness
    addFluid(TinkerFluids.liquidSoul.getTag(), FluidType.BUCKET_VOLUME / 20).addEffect(TimeAction.SET, FluidMobEffect.builder().effect(MobEffects.MOVEMENT_SLOWDOWN, 20*25, 2).effect(MobEffects.BLINDNESS, 20*5));
    // ender - teleporting
    addFluid(TinkerFluids.moltenEnder.getTag(), FluidType.BUCKET_VOLUME / 20)
      .addEntityEffect(new DamageFluidEffect(1f, TinkerDamageTypes.FLUID_MAGIC))
      .addEntityEffect(FluidEffect.TELEPORT);

    // foods - setup to give equivelent saturation on a full bowl/bottle to their food counterparts, though hunger may be slightly different
    addFluid(TinkerFluids.honey.getTag(), slimeballPiece)
      .addEntityEffect(new RestoreHungerFluidEffect(1, 0.12f, false, ItemOutput.fromItem(Items.HONEY_BOTTLE)))
      .addEntityEffect(new RemoveEffectFluidEffect(MobEffects.POISON));
    // soups
    int bowlSip = FluidValues.BOWL / 5;
    addFluid(TinkerFluids.beetrootSoup.getTag(), bowlSip).addEntityEffect(new RestoreHungerFluidEffect(1, 0.72f, false, ItemOutput.fromItem(Items.BEETROOT_SOUP)));
    addFluid(TinkerFluids.mushroomStew.getTag(), bowlSip).addEntityEffect(new RestoreHungerFluidEffect(1, 0.72f, false, ItemOutput.fromItem(Items.MUSHROOM_STEW)));
    addFluid(TinkerFluids.rabbitStew.getTag(), bowlSip).addEntityEffect(new RestoreHungerFluidEffect(2, 0.6f, false, ItemOutput.fromItem(Items.RABBIT_STEW)));
    addFluid(TinkerFluids.meatSoup.getTag(), bowlSip).addEntityEffect(new RestoreHungerFluidEffect(2, 0.48f, false, ItemOutput.fromItem(TinkerFluids.meatSoupBowl)));
    // pig iron fills you up food, but still hurts
    addFluid(TinkerFluids.moltenPigIron.getTag(), FluidValues.NUGGET)
      .addEntityEffect(new RestoreHungerFluidEffect(2, 0.7f, false, ItemOutput.fromItem(TinkerCommons.bacon)))
      .addEntityEffect(new FireFluidEffect(TimeAction.SET, 2));

    // metals, lose reference to mistborn (though a true fan would probably get angry at how much I stray from the source)
    metalborn(TinkerFluids.moltenIron.getTag(), 2f).addEffect(TimeAction.SET, FluidMobEffect.builder().effect(TinkerModifiers.magneticEffect.get(), 20 * 4, 2));
    metalborn(TinkerFluids.moltenSteel.getTag(), 2f).addEffect(TimeAction.SET, FluidMobEffect.builder().effect(TinkerModifiers.repulsiveEffect.get(), 20 * 4, 2));
    metalborn(TinkerFluids.moltenCopper.getTag(), 1.5f).addEntityEffect(new AddBreathFluidEffect(80));
    metalborn(TinkerFluids.moltenBronze.getTag(), 2f).addEntityEffect(new AwardStatFluidEffect(Stats.TIME_SINCE_REST, - 2000));
    metalborn(TinkerFluids.moltenAmethystBronze.getTag(), 1.5f).addEntityEffect(new AwardStatFluidEffect(Stats.TIME_SINCE_REST, 2000));
    metalborn(TinkerFluids.moltenZinc.getTag(), 1.5f).addEffect(TimeAction.SET, FluidMobEffect.builder().effect(MobEffects.MOVEMENT_SPEED, 20 * 10));
    metalborn(TinkerFluids.moltenBrass.getTag(), 2f).addEffect(TimeAction.SET, FluidMobEffect.builder().effect(MobEffects.FIRE_RESISTANCE, 20 * 8));
    metalborn(TinkerFluids.moltenTin.getTag(), 1.5f).addEffect(TimeAction.SET, FluidMobEffect.builder().effect(MobEffects.NIGHT_VISION, 20 * 8));
    metalborn(TinkerFluids.moltenPewter.getTag(), 2f).addEffect(TimeAction.SET, FluidMobEffect.builder().effect(MobEffects.DAMAGE_BOOST, 20 * 7));
    addFluid(TinkerFluids.moltenGold.getTag(), FluidValues.NUGGET)
      .addEntityEffect(new MobTypePredicate(MobType.UNDEAD), new DamageFluidEffect(2f, TinkerDamageTypes.FLUID_MAGIC))
      .addEffect(TimeAction.SET, FluidMobEffect.builder().effect(MobEffects.REGENERATION, 20*6, 1));
    addFluid(TinkerFluids.moltenElectrum.getTag(), FluidValues.NUGGET)
      .addEntityEffect(new MobTypePredicate(MobType.UNDEAD), new DamageFluidEffect(2f, TinkerDamageTypes.FLUID_MAGIC))
      .addEffect(TimeAction.SET, FluidMobEffect.builder().effect(MobEffects.DIG_SPEED, 20*8, 1));
    addFluid(TinkerFluids.moltenRoseGold.getTag(), FluidValues.NUGGET)
      .addEntityEffect(new MobTypePredicate(MobType.UNDEAD), new DamageFluidEffect(2f, TinkerDamageTypes.FLUID_MAGIC))
      .addEffect(TimeAction.SET, FluidMobEffect.builder().effect(MobEffects.HEALTH_BOOST, 20*15, 1));
    metalborn(TinkerFluids.moltenAluminum.getTag(), 1f).addEntityEffect(new CureEffectsFluidEffect(Items.MILK_BUCKET));
    addFluid(TinkerFluids.moltenSilver.getTag(), FluidValues.NUGGET)
      .addEntityEffect(new MobTypePredicate(MobType.UNDEAD), new DamageFluidEffect(2f, TinkerDamageTypes.FLUID_MAGIC))
      .addEntityEffect(new RemoveEffectFluidEffect(MobEffects.WITHER));

    metalborn(TinkerFluids.moltenLead.getTag(), 1.5f).addEffect(TimeAction.SET, FluidMobEffect.builder().effect(MobEffects.MOVEMENT_SLOWDOWN, 20 * 6, 1));
    metalborn(TinkerFluids.moltenNickel.getTag(), 1.5f).addEffect(TimeAction.SET, FluidMobEffect.builder().effect(MobEffects.WEAKNESS, 20 * 7, 1));
    metalborn(TinkerFluids.moltenInvar.getTag(), 2f).addEffect(TimeAction.SET, FluidMobEffect.builder().effect(MobEffects.HUNGER, 20 * 10, 1));
    metalborn(TinkerFluids.moltenConstantan.getTag(), 2f).addEffect(TimeAction.SET, FluidMobEffect.builder().effect(MobEffects.HUNGER, 20 * 10, 1));
    burningFluid(TinkerFluids.moltenUranium.getTag(), 1.5f, 3).addEffect(TimeAction.SET, FluidMobEffect.builder().effect(MobEffects.POISON, 20 * 10, 1));

    metalborn(TinkerFluids.moltenCobalt.getTag(), 1f).addEffect(TimeAction.SET, FluidMobEffect.builder().effect(MobEffects.DIG_SPEED, 20 * 7, 1).effect(MobEffects.MOVEMENT_SPEED, 20 * 7, 1));
    metalborn(TinkerFluids.moltenManyullyn.getTag(), 3f).addEffect(TimeAction.SET, FluidMobEffect.builder().effect(MobEffects.DAMAGE_RESISTANCE, 20 * 15, 1));
    metalborn(TinkerFluids.moltenHepatizon.getTag(), 2.5f).addEffect(TimeAction.SET, FluidMobEffect.builder().effect(MobEffects.DAMAGE_RESISTANCE, 20 * 10, 1));
    burningFluid(TinkerFluids.moltenNetherite.getTag(), 3.5f, 4).addEffect(TimeAction.SET, FluidMobEffect.builder().effect(MobEffects.BLINDNESS, 20 * 15, 1));

    metalborn(TinkerFluids.moltenSlimesteel.getTag(), 1f).addEffect(TimeAction.SET, FluidMobEffect.builder().effect(MobEffects.SLOW_FALLING, 20*5, 1));
    metalborn(TinkerFluids.moltenQueensSlime.getTag(), 1f).addEffect(TimeAction.SET, FluidMobEffect.builder().effect(MobEffects.LEVITATION, 20*5, 1));

    // multi-recipes
    burningFluid("glass",           TinkerTags.Fluids.GLASS_SPILLING,           FluidType.BUCKET_VOLUME / 10, 1f,   3);
    burningFluid("clay",            TinkerTags.Fluids.CLAY_SPILLING,            FluidValues.BRICK / 5,        1.5f, 3);
    burningFluid("metal_cheap",     TinkerTags.Fluids.CHEAP_METAL_SPILLING,     FluidValues.NUGGET,           1.5f, 7);
    burningFluid("metal_average",   TinkerTags.Fluids.AVERAGE_METAL_SPILLING,   FluidValues.NUGGET,           2f,   7);
    burningFluid("metal_expensive", TinkerTags.Fluids.EXPENSIVE_METAL_SPILLING, FluidValues.NUGGET,           3f,   7);

    // potion fluid compat
    // standard potion is 250 mb, but we want a smaller number. divide into 5 pieces at 25% a piece (so healing is 1 health), means you gain 25% per potion
    int bottleSip = FluidValues.BOTTLE / 5;
    addFluid("potion_fluid", Objects.requireNonNull(TinkerFluids.potion.getCommonTag()), bottleSip)
      .addEntityEffect(new PotionFluidEffect(0.25f, TagPredicate.ANY))
      .addBlockEffect(new PotionCloudFluidEffect(0.25f, TagPredicate.ANY));

    // create has three types of bottles stored on their fluid, react to it to boost
    Function<String,TagPredicate> createBottle = value -> {
      CompoundTag compound = new CompoundTag();
      compound.putString("Bottle", value);
      return new TagPredicate(compound);
    };
    String create = "create";
    addFluid("potion_create", FluidNameIngredient.of(new ResourceLocation(create, "potion"), bottleSip))
      .addCondition(new ModLoadedCondition(create))
      .addEntityEffect(new PotionFluidEffect(0.25f, createBottle.apply("REGULAR")))
      .addEntityEffect(new PotionFluidEffect(0.5f, createBottle.apply("SPLASH")))
      .addEntityEffect(new PotionFluidEffect(0.75f, createBottle.apply("LINGERING")))
      .addBlockEffect(new PotionCloudFluidEffect(0.25f, createBottle.apply("REGULAR")))
      .addBlockEffect(new PotionCloudFluidEffect(0.5f, createBottle.apply("SPLASH")))
      .addBlockEffect(new PotionCloudFluidEffect(0.75f, createBottle.apply("LINGERING")));
  }

  /** Builder for an effect based metal */
  private Builder metalborn(TagKey<Fluid> tag, float damage) {
    return burningFluid(tag.location().getPath(), tag, FluidValues.NUGGET, damage, 0);
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Spilling Fluid Provider";
  }
}
