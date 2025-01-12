package net.ryancave282.tconstruct.shared;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.ryancave282.tconstruct.shared.block.SlimeType;
import net.ryancave282.tconstruct.world.block.FoliageType;

@SuppressWarnings("WeakerAccess")
public final class TinkerFood {
  private TinkerFood() {}
  /** Bacon. What more is there to say? */
  public static final FoodProperties BACON = (new FoodProperties.Builder()).nutrition(4).saturationMod(0.6F).build();

  /** Cheese is used for both the block and the ingot, eating the block returns 3 ingots */
  public static final FoodProperties CHEESE = (new FoodProperties.Builder()).nutrition(3).saturationMod(0.4F).build();

  /** For the modifier */
  public static final FoodProperties JEWELED_APPLE = (new FoodProperties.Builder()).nutrition(4).saturationMod(1.2F).effect(() -> new MobEffectInstance(MobEffects.DIG_SPEED, 1200, 0), 1.0F).effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 2400, 0), 1.0F).alwaysEat().build();

  /* Cake block is set up to take food as a parameter */
  public static final FoodProperties EARTH_CAKE = new FoodProperties.Builder().nutrition(1).saturationMod(0.1f).alwaysEat().effect(() -> new MobEffectInstance(MobEffects.LUCK, 20 * 15, 0), 1.0f).build();
  public static final FoodProperties SKY_CAKE   = new FoodProperties.Builder().nutrition(1).saturationMod(0.1f).alwaysEat().effect(() -> new MobEffectInstance(MobEffects.JUMP, 20 * 20, 1), 1.0f).build();
  public static final FoodProperties ICHOR_CAKE = new FoodProperties.Builder().nutrition(3).saturationMod(0.1f).alwaysEat().effect(() -> new MobEffectInstance(MobEffects.ABSORPTION, 20 * 30, 0), 1.0f).build();
  public static final FoodProperties BLOOD_CAKE = new FoodProperties.Builder().nutrition(1).saturationMod(0.2f).alwaysEat().effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 50 * 3, 0), 1.0f).build();
  public static final FoodProperties MAGMA_CAKE = new FoodProperties.Builder().nutrition(1).saturationMod(0.2f).alwaysEat().effect(() -> new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 20 * 30, 0), 1.0f).build();
  public static final FoodProperties ENDER_CAKE = new FoodProperties.Builder().nutrition(2).saturationMod(0.2f).alwaysEat().effect(() -> new MobEffectInstance(MobEffects.LEVITATION, 20 * 10, 0), 1.0f).build();

  public static final FoodProperties EARTH_BOTTLE = new FoodProperties.Builder().alwaysEat().effect(() -> new MobEffectInstance(MobEffects.LUCK,      1500), 1.0f).effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 900), 1.0f).build();
  public static final FoodProperties SKY_BOTTLE   = new FoodProperties.Builder().alwaysEat().effect(() -> new MobEffectInstance(MobEffects.JUMP,      1800), 1.0f).effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 900), 1.0f).build();
  public static final FoodProperties ICHOR_BOTTLE = new FoodProperties.Builder().alwaysEat().effect(() -> new MobEffectInstance(MobEffects.ABSORPTION, 500), 1.0f).effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 900), 1.0f).build();
  public static final FoodProperties ENDER_BOTTLE = new FoodProperties.Builder().alwaysEat().effect(() -> new MobEffectInstance(MobEffects.LEVITATION, 450), 1.0f).effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 900), 1.0f).build();
  public static final FoodProperties MAGMA_BOTTLE = new FoodProperties.Builder().alwaysEat().effect(() -> new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 3600), 1.0f).build();
  public static final FoodProperties VENOM_BOTTLE = new FoodProperties.Builder().alwaysEat().effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1800), 1.0f).effect(() -> new MobEffectInstance(MobEffects.POISON, 450), 1.0f).build();

  public static final FoodProperties MEAT_SOUP = new FoodProperties.Builder().nutrition(8).saturationMod(0.6f).build();

  /**
   * Gets the cake for the given slime type
   * @param slime  Slime type
   * @return  Cake food
   */
  public static FoodProperties getCake(FoliageType slime) {
    return switch (slime) {
      default -> EARTH_CAKE;
      case SKY -> SKY_CAKE;
      case ICHOR -> ICHOR_CAKE;
      case BLOOD -> BLOOD_CAKE;
      case ENDER -> ENDER_CAKE;
    };
  }

  /**
   * Gets the cake for the given slime type
   * @param slime  Slime type
   * @return  Cake food
   */
  public static FoodProperties getBottle(SlimeType slime) {
    return switch (slime) {
      default -> EARTH_BOTTLE;
      case SKY -> SKY_BOTTLE;
      case ICHOR -> ICHOR_BOTTLE;
      case ENDER -> ENDER_BOTTLE;
    };
  }
}
