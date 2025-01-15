package net.ryancave282.tconstruct.tools.data.sprite;

import net.minecraft.world.item.ArmorItem;
import net.ryancave282.tconstruct.library.client.data.material.MaterialPartTextureGenerator;
import net.ryancave282.tconstruct.TConstruct;
import net.ryancave282.tconstruct.library.client.data.material.AbstractPartSpriteProvider;
import net.ryancave282.tconstruct.library.materials.stats.MaterialStatsId;
import net.ryancave282.tconstruct.tools.stats.HandleMaterialStats;
import net.ryancave282.tconstruct.tools.stats.LimbMaterialStats;
import net.ryancave282.tconstruct.tools.stats.PlatingMaterialStats;
import net.ryancave282.tconstruct.tools.stats.StatlessMaterialStats;

/**
 * This class handles all tool part sprites generated by Tinkers' Construct. You can freely use this in your addon to generate TiC part textures for a new material
 * Do not use both this and {@link TinkerMaterialSpriteProvider} in a single generator for an addon, if you need to use both make two instances of {@link MaterialPartTextureGenerator}
 */
public class TinkerPartSpriteProvider extends AbstractPartSpriteProvider {
  public static final MaterialStatsId WOOD = new MaterialStatsId(TConstruct.MOD_ID, "wood");
  public static final MaterialStatsId SLIMESUIT = new MaterialStatsId(TConstruct.MOD_ID, "slimesuit");
  public static final MaterialStatsId ARMOR_PLATING = new MaterialStatsId(TConstruct.MOD_ID, "armor_plating");
  public static final MaterialStatsId ARMOR_MAILLE = new MaterialStatsId(TConstruct.MOD_ID, "armor_maille");

  public TinkerPartSpriteProvider() {
    super(TConstruct.MOD_ID);
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Parts";
  }

  @Override
  protected void addAllSpites() {
    // heads
    addHead("large_plate");
    addHead("small_blade");
    // handles
    addHandle("tool_handle");
    addHandle("tough_handle");
    // bow
    addBowstring("bowstring");
    // misc
    addBinding("tool_binding");
    addBinding("tough_binding");
    addPart("repair_kit", StatlessMaterialStats.REPAIR_KIT.getIdentifier());

    // plate textures
    addPart("maille", StatlessMaterialStats.MAILLE.getIdentifier());
    for (ArmorItem.Type slot : ArmorItem.Type.values()) {
      buildTool("armor/plate/" + slot.getName())
        .addBreakablePart("plating", PlatingMaterialStats.TYPES.get(slot.ordinal()).getId())
        .addBreakablePart("maille", StatlessMaterialStats.MAILLE.getIdentifier());
    }
    addTexture("tinker_armor/plate/plating_armor", false, ARMOR_PLATING);
    addTexture("tinker_armor/plate/plating_leggings", false, ARMOR_PLATING);
    addTexture("tinker_armor/plate/maille_armor", false, ARMOR_MAILLE);
    addTexture("tinker_armor/plate/maille_leggings", false, ARMOR_MAILLE);
    addTexture("tinker_armor/plate/maille_wings", false, ARMOR_MAILLE);
    buildTool("armor/plate/shield")
      .addBreakablePart("plating", PlatingMaterialStats.SHIELD.getId())
      .addBreakablePart("core", StatlessMaterialStats.SHIELD_CORE.getIdentifier())
      // withLarge wants to use a subfolder, easier to just add another part than special casing
      .addBreakablePart("plating_large", PlatingMaterialStats.SHIELD.getId())
      .addBreakablePart("core_large", StatlessMaterialStats.SHIELD_CORE.getIdentifier());

    // shield textures
    addSprite("armor/travelers/shield_modifiers/tconstruct_embellishment", WOOD);
    addSprite("armor/travelers/shield_modifiers/broken/tconstruct_embellishment", WOOD);

    // staff
    addSprite("staff/modifiers/tconstruct_embellishment", WOOD);
    addSprite("staff/large_modifiers/tconstruct_embellishment", WOOD);

    // slimesuit textures
    addSprite("armor/slime/skull_modifiers/tconstruct_embellishment", SLIMESUIT);
    addSprite("armor/slime/skull_modifiers/broken/tconstruct_embellishment", SLIMESUIT);
    addSprite("armor/slime/wings_modifiers/tconstruct_embellishment", SLIMESUIT);
    addSprite("armor/slime/wings_modifiers/broken/tconstruct_embellishment", SLIMESUIT);
    addSprite("armor/slime/shell_modifiers/tconstruct_embellishment", SLIMESUIT);
    addSprite("armor/slime/shell_modifiers/broken/tconstruct_embellishment", SLIMESUIT);
    addSprite("armor/slime/boot_modifiers/tconstruct_embellishment", SLIMESUIT);
    addSprite("armor/slime/boot_modifiers/broken/tconstruct_embellishment", SLIMESUIT);
    addTexture("tinker_armor/slime/armor", false, SLIMESUIT);
    addTexture("tinker_armor/slime/leggings", false, SLIMESUIT);
    addTexture("tinker_armor/slime/wings", false, SLIMESUIT);

    // tools
    // pickaxe - regular variant uses handle on frypans as a grip so generate those too
    buildTool("pickaxe").addBreakableHead("head").addPart("handle", HandleMaterialStats.ID, LimbMaterialStats.ID).addBinding("binding");
    buildTool("sledge_hammer").withLarge().addBreakableHead("head").addBreakableHead("back").addBreakableHead("front").addHandle("handle");
    buildTool("vein_hammer").withLarge().addBreakableHead("head").addBinding("grip").addBreakableHead("front").addHandle("handle");
    // shovel
    buildTool("mattock").addBreakableHead("axe").addBreakableHead("pick"); // handle provided by pickaxe
    buildTool("pickadze").addBreakableHead("pick").addHead("adze"); // handle provided by pickaxe
    buildTool("excavator").withLarge().addBreakableHead("head").addBinding("binding").addHandle("handle").addHandle("grip");
    // axe
    buildTool("hand_axe").addBreakableHead("head").addBinding("binding"); // handle provided by pickaxe
    buildTool("broad_axe").withLarge().addBreakableHead("blade").addBreakableHead("back").addHandle("handle").addBinding("binding");
    // scythe
    buildTool("kama").addBreakableHead("head").addBinding("binding"); // handle provided by pickaxe
    buildTool("scythe").withLarge().addBreakableHead("head").addHandle("handle").addHandle("accessory").addBinding("binding");
    // sword
    buildTool("dagger").addBreakableHead("blade").addHandle("crossguard");
    buildTool("sword").addBreakableHead("blade").addHandle("guard").addHandle("handle");
    buildTool("cleaver").withLarge().addBreakableHead("head").addBreakableHead("shield").addHandle("handle").addHandle("guard");
    // bow
    buildTool("crossbow")
      .addLimb("limb").addGrip("body")
      .addBreakableBowstring("bowstring").addBowstring("bowstring_1").addBowstring("bowstring_2").addBowstring("bowstring_3");
    buildTool("longbow").withLarge()
      .addLimb("limb_bottom").addLimb("limb_bottom_1").addLimb("limb_bottom_2").addLimb("limb_bottom_3")
      .addLimb("limb_top").addLimb("limb_top_1").addLimb("limb_top_2").addLimb("limb_top_3")
      .addGrip("grip")
      .addBreakableBowstring("bowstring").addBowstring("bowstring_1").addBowstring("bowstring_2").addBowstring("bowstring_3");

    // ancient tools
    buildTool("melting_pan").addBreakablePart("head", PlatingMaterialStats.SHIELD.getId());
    buildTool("war_pick").addHead("limb").addLimb("body")
                         .addBreakableBowstring("bowstring").addBowstring("bowstring_1").addBowstring("bowstring_2").addBowstring("bowstring_3");
    buildTool("battlesign").addBreakableHead("head").addPart("handle", PlatingMaterialStats.SHIELD.getId());
  }
}