package net.ryancave282.tconstruct.tools;

import net.ryancave282.tconstruct.common.Sounds;
import net.ryancave282.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import net.ryancave282.tconstruct.library.tools.definition.ToolDefinition;

import static net.ryancave282.tconstruct.TConstruct.getResource;

public class ArmorDefinitions {
   /** Balanced armor set */
  public static final ModifiableArmorMaterial TRAVELERS = ModifiableArmorMaterial.create(getResource("travelers"), Sounds.EQUIP_TRAVELERS.getSound());
  public static final ToolDefinition TRAVELERS_SHIELD = ToolDefinition.create(TinkerTools.travelersShield);

  /** High defense armor set */
  public static final ModifiableArmorMaterial PLATE = ModifiableArmorMaterial.create(getResource("plate"), Sounds.EQUIP_PLATE.getSound());
  public static final ToolDefinition PLATE_SHIELD = ToolDefinition.create(TinkerTools.plateShield);

  /** High modifiers armor set */
  public static final ModifiableArmorMaterial SLIMESUIT = ModifiableArmorMaterial.create(getResource("slime"), Sounds.EQUIP_SLIME.getSound());
}
