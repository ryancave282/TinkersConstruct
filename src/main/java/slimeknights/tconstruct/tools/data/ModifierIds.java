package slimeknights.tconstruct.tools.data;

import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierId;

/**
 * This class contains the IDs of any dynamic modifiers that are not required.
 * That is, they may be used as traits or in recipes, but nothing in code relies on them existing
 */
public class ModifierIds {
  // bonus modifier slots
  public static final ModifierId writable    = id("writable");
  public static final ModifierId recapitated = id("recapitated");
  public static final ModifierId harmonious  = id("harmonious");
  public static final ModifierId resurrected = id("resurrected");
  public static final ModifierId gilded      = id("gilded");
  public static final ModifierId draconic    = id("draconic");

  // tier upgrades
  public static final ModifierId emerald   = id("emerald");
  public static final ModifierId diamond   = id("diamond");
  public static final ModifierId netherite = id("netherite");

  // general
  public static final ModifierId worldbound = id("worldbound");
  public static final ModifierId shiny      = id("shiny");

  // armor
  public static final ModifierId wings  = id("wings");
  public static final ModifierId knockbackResistance = id("knockback_resistance");



  // traits - tier 1
  public static final ModifierId stringy = id("stringy");
  // traits - tier 2
  public static final ModifierId sturdy = id("sturdy");
  // traits - tier 3
  public static final ModifierId enhanced = id("enhanced");
  public static final ModifierId lightweight = id("lightweight");
  // traits - tier 3 compat
  public static final ModifierId ductile = id("ductile");

  // mob disguises
  public static final ModifierId creeperDisguise         = id("creeper_disguise");
  public static final ModifierId endermanDisguise        = id("enderman_disguise");
  public static final ModifierId skeletonDisguise        = id("skeleton_disguise");
  public static final ModifierId strayDisguise           = id("stray_disguise");
  public static final ModifierId witherSkeletonDisguise  = id("wither_skeleton_disguise");
  public static final ModifierId spiderDisguise          = id("spider_disguise");
  public static final ModifierId caveSpiderDisguise      = id("cave_spider_disguise");
  public static final ModifierId zombieDisguise          = id("zombie_disguise");
  public static final ModifierId huskDisguise            = id("husk_disguise");
  public static final ModifierId drownedDisguise         = id("drowned_disguise");
  public static final ModifierId blazeDisguise           = id("blaze_disguise");
  public static final ModifierId piglinDisguise          = id("piglin_disguise");
  public static final ModifierId piglinBruteDisguise     = id("piglin_brute_disguise");
  public static final ModifierId zombifiedPiglinDisguise = id("zombified_piglin_disguise");


  private ModifierIds() {}

  /**
   * Creates a new material ID
   * @param name  ID name
   * @return  Material ID object
   */
  private static ModifierId id(String name) {
    return new ModifierId(TConstruct.MOD_ID, name);
  }
}