package net.ryancave282.tconstruct.library.modifiers;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import slimeknights.mantle.data.registry.IdAwareComponentRegistry;
import net.ryancave282.tconstruct.TConstruct;
import net.ryancave282.tconstruct.library.modifiers.hook.armor.ArmorWalkModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.armor.DamageBlockModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.armor.ElytraFlightModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.armor.ProtectionModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.behavior.EnchantmentModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.behavior.ProcessLootModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.behavior.RepairFactorModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.behavior.ToolActionModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.behavior.ToolDamageModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.build.ConditionalStatModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import net.ryancave282.tconstruct.library.modifiers.hook.build.ModifierTraitHook;
import net.ryancave282.tconstruct.library.modifiers.hook.build.RawDataModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.build.ValidateModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.build.VolatileDataModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.combat.ArmorLootingModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.combat.DamageDealtModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.combat.LootingModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.display.DisplayNameModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.display.DurabilityDisplayModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.display.RequirementsModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.interaction.BlockInteractionModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.interaction.EntityInteractionModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.interaction.KeybindInteractModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.mining.BlockBreakModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.mining.BlockHarvestModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.mining.BreakSpeedModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.mining.HarvestEnchantmentsModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.mining.RemoveBlockModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.ranged.BowAmmoModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.special.BlockTransformModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.special.PlantHarvestModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.special.ShearsModifierHook;
import net.ryancave282.tconstruct.library.module.ModuleHook;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;
import net.ryancave282.tconstruct.library.utils.RestrictedCompoundTag;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

/** Collection of all hooks implemented by the mod natively */
public class ModifierHooks {
  ModifierHooks() {}

  /** Loader for modifier hooks */
  public static final IdAwareComponentRegistry<ModuleHook<?>> LOADER = new IdAwareComponentRegistry<>("Unknown Modifier Hook");

  public static void init() {}


  /* General */

  /** Generic hook for stats conditioned on the entity holding the tool */
  public static final ModuleHook<ConditionalStatModifierHook> CONDITIONAL_STAT = register("conditional_stat", ConditionalStatModifierHook.class, ConditionalStatModifierHook.AllMerger::new, (tool, modifier, living, stat, baseValue, multiplier) -> baseValue);

  /** Hook for modifiers checking if they can perform a tool action */
  public static final ModuleHook<ToolActionModifierHook> TOOL_ACTION = register("tool_action", ToolActionModifierHook.class, ToolActionModifierHook.AnyMerger::new, (tool, modifier, toolAction) -> false);

  /** Hook used when any {@link EquipmentSlot} changes on an entity while using at least one tool */
  public static final ModuleHook<EquipmentChangeModifierHook> EQUIPMENT_CHANGE = register("equipment_change", EquipmentChangeModifierHook.class, EquipmentChangeModifierHook.AllMerger::new, new EquipmentChangeModifierHook() {});

  /** Hook for modifying the repair amount for tools */
  public static final ModuleHook<RepairFactorModifierHook> REPAIR_FACTOR = register("repair_factor", RepairFactorModifierHook.class, RepairFactorModifierHook.ComposeMerger::new, (tool, entry, factor) -> factor);

  /** Hook for modifying the damage amount for tools */
  public static final ModuleHook<ToolDamageModifierHook> TOOL_DAMAGE = register("tool_damage", ToolDamageModifierHook.class, ToolDamageModifierHook.Merger::new, (tool, modifier, amount, holder) -> amount);

  /** Hook running while the tool is in the inventory */
  public static final ModuleHook<InventoryTickModifierHook> INVENTORY_TICK = register("inventory_tick", InventoryTickModifierHook.class, InventoryTickModifierHook.AllMerger::new, (tool, modifier, world, holder, itemSlot, isSelected, isCorrectSlot, stack) -> {});


  /* Composable only  */

  /** Hook for supporting modifiers to change the modifier display name */
  public static final ModuleHook<DisplayNameModifierHook> DISPLAY_NAME = register("display_name", DisplayNameModifierHook.class, DisplayNameModifierHook.ComposeMerger::new, (tool, entry, name, access) -> name);


  /* Display */

  /** Hook for modifiers adding additional information to the tooltip */
  public static final ModuleHook<TooltipModifierHook> TOOLTIP = register("tooltip", TooltipModifierHook.class, TooltipModifierHook.AllMerger::new, (tool, modifier, player, tooltip, tooltipKey, tooltipFlag) -> {});

  /** Hook for changing the itemstack durability bar */
  public static final ModuleHook<DurabilityDisplayModifierHook> DURABILITY_DISPLAY = register("durability_display", DurabilityDisplayModifierHook.class, DurabilityDisplayModifierHook.FirstMerger::new, new DurabilityDisplayModifierHook() {
    @Nullable
    @Override
    public Boolean showDurabilityBar(IToolStackView tool, ModifierEntry modifier) {
      return null;
    }

    @Override
    public int getDurabilityWidth(IToolStackView tool, ModifierEntry modifier) {
      return 0;
    }

    @Override
    public int getDurabilityRGB(IToolStackView tool, ModifierEntry modifier) {
      return -1;
    }
  });

  /** Hook for displaying a list of requirements on teh tool and a hint for the requirements in recipe viewers */
  public static final ModuleHook<RequirementsModifierHook> REQUIREMENTS = register("requirements", RequirementsModifierHook.class, RequirementsModifierHook.FirstMerger::new, new RequirementsModifierHook() {});


  /* Tool Building */

  /** Hook for adding raw unconditional stats to a tool */
  public static final ModuleHook<ToolStatsModifierHook> TOOL_STATS = register("modifier_stats", ToolStatsModifierHook.class, ToolStatsModifierHook.AllMerger::new, (context, modifier, builder) -> {});

  /** Hook for adding item stack attributes to a tool when in the proper slot */
  public static final ModuleHook<AttributesModifierHook> ATTRIBUTES = register("attributes", AttributesModifierHook.class, AttributesModifierHook.AllMerger::new, (tool, modifier, slot, consumer) -> {});

  /** Hook for adding item stack attributes to a tool when in the proper slot */
  public static final ModuleHook<EnchantmentModifierHook> ENCHANTMENTS = register("enchantments", EnchantmentModifierHook.class, EnchantmentModifierHook.AllMerger::new, new EnchantmentModifierHook() {
    @Override
    public int updateEnchantmentLevel(IToolStackView tool, ModifierEntry modifier, Enchantment enchantment, int level) {
      return level;
    }

    @Override
    public void updateEnchantments(IToolStackView tool, ModifierEntry modifier, Map<Enchantment,Integer> map) {}
  });

  /** Hook to add data that resets every time stats rebuild */
  public static final ModuleHook<VolatileDataModifierHook> VOLATILE_DATA = register("volatile_data", VolatileDataModifierHook.class, VolatileDataModifierHook.AllMerger::new, (context, modifier, volatileData) -> {});

  /** Hook to add and remove data directly to the tools NBT. It is generally better to use persistent data or volatile data when possible. */
  public static final ModuleHook<RawDataModifierHook> RAW_DATA = register("raw_data", RawDataModifierHook.class, RawDataModifierHook.AllMerger::new, new RawDataModifierHook() {
    @Override
    public void addRawData(IToolStackView tool, ModifierEntry modifier, RestrictedCompoundTag tag) {}

    @Override
    public void removeRawData(IToolStackView tool, Modifier modifier, RestrictedCompoundTag tag) {}
  });

  /** Hook called to give a modifier a chance to clean up data while on the tool and to reject the current tool state */
  public static final ModuleHook<ValidateModifierHook> VALIDATE = register("validate", ValidateModifierHook.class, ValidateModifierHook.AllMerger::new, (tool, modifier) -> null);

  /** Hook called when a modifier is removed to give it a chance to clean up data */
  public static final ModuleHook<ModifierRemovalHook> REMOVE = register("remove", ModifierRemovalHook.class, ModifierRemovalHook.FirstMerger::new, (tool, modifier) -> null);

  /** Hook for a modifier to add other modifiers to the builder */
  public static final ModuleHook<ModifierTraitHook> MODIFIER_TRAITS = register("modifier_traits", ModifierTraitHook.class, ModifierTraitHook.AllMerger::new, (context, modifier, builder, firstEncounter) -> {});

  /* Combat */

  /** Hook to adjust melee damage when a weapon is attacking an entity */
  public static final ModuleHook<MeleeDamageModifierHook> MELEE_DAMAGE = register("melee_damage", MeleeDamageModifierHook.class, MeleeDamageModifierHook.AllMerger::new, (tool, modifier, context, baseDamage, damage) -> damage);

  /** Hook called when an entity is attacked to apply special effects */
  public static final ModuleHook<MeleeHitModifierHook> MELEE_HIT = register("melee_hit", MeleeHitModifierHook.class, MeleeHitModifierHook.AllMerger::new, new MeleeHitModifierHook() {});

  /** Hook called when taking damage wearing this armor to reduce the damage, runs after {@link #MODIFY_HURT} and before {@link #MODIFY_DAMAGE} */
  public static final ModuleHook<ProtectionModifierHook> PROTECTION = register("protection", ProtectionModifierHook.class, ProtectionModifierHook.AllMerger::new, (tool, modifier, context, slotType, source, modifierValue) -> modifierValue);

  /** Hook called when taking damage wearing this armor to cancel the damage */
  public static final ModuleHook<DamageBlockModifierHook> DAMAGE_BLOCK = register("damage_block", DamageBlockModifierHook.class, DamageBlockModifierHook.AnyMerger::new, (tool, modifier, context, slotType, source, amount) -> false);
  /** Hook called when taking damage to apply secondary effects such as counterattack or healing. Runs after {@link #DAMAGE_BLOCK} but before vanilla effects that cancel damage. */
  public static final ModuleHook<OnAttackedModifierHook> ON_ATTACKED = register("on_attacked", OnAttackedModifierHook.class, OnAttackedModifierHook.AllMerger::new, (tool, modifier, context, slotType, source, amount, isDirectDamage) -> {});

  /** Hook allowing modifying damage taken or responding when damage is taken. Runs after {@link #ON_ATTACKED} and any vanilla effects that cancel damage, but before armor reduction and {@link #PROTECTION}.  */
  public static final ModuleHook<ModifyDamageModifierHook> MODIFY_HURT;
  /** Hook allowing modifying damage taken or responding when damage is taken. Runs after {@link #PROTECTION}, armor damage reduction, and absorption.  */
  public static final ModuleHook<ModifyDamageModifierHook> MODIFY_DAMAGE;
  static {
    Function<Collection<ModifyDamageModifierHook>,ModifyDamageModifierHook> merger = ModifyDamageModifierHook.AllMerger::new;
    ModifyDamageModifierHook fallback = (tool, modifier, context, slotType, source, amount, isDirectDamage) -> amount;
    MODIFY_HURT = register("modify_hurt", ModifyDamageModifierHook.class, merger, fallback);
    MODIFY_DAMAGE = register("modify_damage", ModifyDamageModifierHook.class, merger, fallback);
  }

  /** Hook called when dealing damage while wearing this equipment */
  public static final ModuleHook<DamageDealtModifierHook> DAMAGE_DEALT = register("damage_dealt", DamageDealtModifierHook.class, DamageDealtModifierHook.AllMerger::new, (tool, modifier, context, slotType, target, source, amount, isDirectDamage) -> {});

  /* Loot */

  /** Hook to modify the results of a loot table */
  public static final ModuleHook<ProcessLootModifierHook> PROCESS_LOOT = register("process_loot", ProcessLootModifierHook.class, ProcessLootModifierHook.AllMerger::new, (tool, modifier, loot, context) -> {});
  /** Hook for a tool boosting the looting value */
  public static final ModuleHook<LootingModifierHook> WEAPON_LOOTING = register("weapon_looting", LootingModifierHook.class, LootingModifierHook.ComposeMerger::new, (tool, modifier, context, looting) -> looting);
  /** Hook for leggings boosting the tool's looting level */
  public static final ModuleHook<ArmorLootingModifierHook> ARMOR_LOOTING = register("armor_looting", ArmorLootingModifierHook.class, ArmorLootingModifierHook.ComposeMerger::new, (tool, modifier, context, equipment, slot, looting) -> looting);
  /** Hook for armor adding harvest enchantments to a held tool based on the tool's modifiers */
  public static final ModuleHook<HarvestEnchantmentsModifierHook> HARVEST_ENCHANTMENTS = register("harvest_enchantments", HarvestEnchantmentsModifierHook.class, HarvestEnchantmentsModifierHook.AllMerger::new, (tool, modifier, context, equipment, slot, map) -> {});


  /* Harvest */

  /** Hook for conditionally modifying the break speed of a block */
  public static final ModuleHook<BreakSpeedModifierHook> BREAK_SPEED = register("break_speed", BreakSpeedModifierHook.class, BreakSpeedModifierHook.AllMerger::new, (tool, modifier, event, sideHit, isEffective, miningSpeedModifier) -> {});

  /** Called when a block is broken by a tool to allow the modifier to take over the block removing logic */
  public static final ModuleHook<RemoveBlockModifierHook> REMOVE_BLOCK = register("remove_block", RemoveBlockModifierHook.class, RemoveBlockModifierHook.FirstMerger::new, (tool, modifier, context) -> null);

  /** Called after a block is broken by a tool for every block in the AOE */
  public static final ModuleHook<BlockBreakModifierHook> BLOCK_BREAK = register("block_break", BlockBreakModifierHook.class, BlockBreakModifierHook.AllMerger::new, (tool, modifier, context) -> {});

  /** Called after all blocks in the AOE are broken */
  public static final ModuleHook<BlockHarvestModifierHook> BLOCK_HARVEST = register("block_harvest", BlockHarvestModifierHook.class, BlockHarvestModifierHook.AllMerger::new, (tool, modifier, context, didHarvest) -> {});


  /* Ranged */

  /** Hook for firing arrows or other projectiles to modify the entity post firing */
  public static final ModuleHook<ProjectileLaunchModifierHook> PROJECTILE_LAUNCH = register("projectile_launch", ProjectileLaunchModifierHook.class, ProjectileLaunchModifierHook.AllMerger::new, (tool, modifier, shooter, projectile, arrow, persistentData, primary) -> {});
  /** Hook called when an arrow hits an entity or block */
  public static final ModuleHook<ProjectileHitModifierHook> PROJECTILE_HIT = register("projectile_hit", ProjectileHitModifierHook.class, ProjectileHitModifierHook.AllMerger::new, new ProjectileHitModifierHook() {});
  /** Hook called when a bow is looking for ammo. Does not support merging multiple hooks on one modifier */
  public static final ModuleHook<BowAmmoModifierHook> BOW_AMMO = register("bow_ammo", BowAmmoModifierHook.class, BowAmmoModifierHook.EMPTY);

  /* Misc Armor */

  /** Hook for when the player flies using an elytra, called on the chestplate slot */
  public static final ModuleHook<ElytraFlightModifierHook> ELYTRA_FLIGHT = register("elytra_flight", ElytraFlightModifierHook.class, ElytraFlightModifierHook.FirstMerger::new, (tool, modifier, entity, flightTicks) -> false);

  /** Hook for when the player walks from one position to another, called on the boots slot */
  public static final ModuleHook<ArmorWalkModifierHook> BOOT_WALK = register("boot_walk", ArmorWalkModifierHook.class, ArmorWalkModifierHook.AllMerger::new, (tool, modifier, living, prevPos, newPos) -> {});


  /* Interaction */

  /**
   * Hook for regular interactions not targeting blocks or entities. Needed for charged interactions, while other hooks may be better for most interactions.
   * Note the charged interaction hooks will only fire for the modifier that called {@link GeneralInteractionModifierHook#startUsing(IToolStackView, ModifierId, LivingEntity, InteractionHand)},
   * meaning there is no need to manually track that you were called.
   */
  public static final ModuleHook<GeneralInteractionModifierHook> GENERAL_INTERACT = register("general_interact", GeneralInteractionModifierHook.class, GeneralInteractionModifierHook.FirstMerger::new, ((tool, modifier, player, hand, source) -> InteractionResult.PASS));
  /** Hook for interacting with blocks */
  public static final ModuleHook<BlockInteractionModifierHook> BLOCK_INTERACT = register("block_interact", BlockInteractionModifierHook.class, BlockInteractionModifierHook.FirstMerger::new, new BlockInteractionModifierHook() {});
  /** Hook for interacting with entities */
  public static final ModuleHook<EntityInteractionModifierHook> ENTITY_INTERACT = register("entity_interact", EntityInteractionModifierHook.class, EntityInteractionModifierHook.FirstMerger::new, new EntityInteractionModifierHook() {});
  /** Hook for when the player interacts with an armor slot. Currently, only implemented for helmets and leggings */
  public static final ModuleHook<KeybindInteractModifierHook> ARMOR_INTERACT = register("armor_interact", KeybindInteractModifierHook.class, KeybindInteractModifierHook.InteractMerger::new, new KeybindInteractModifierHook() {});


  /* Modifier sub-hooks */

  /** Hook called on all tool modifiers after the harvest modifier harvests a crop */
  public static final ModuleHook<PlantHarvestModifierHook> PLANT_HARVEST = register("plant_harvest", PlantHarvestModifierHook.class, PlantHarvestModifierHook.AllMerger::new, (tool, modifier, context, world, state, pos) -> {});

  /** Hook called on all tool modifiers after shearing an entity */
  public static final ModuleHook<ShearsModifierHook> SHEAR_ENTITY = register("shear_entity", ShearsModifierHook.class, ShearsModifierHook.AllMerger::new, (tool, modifier, player, entity, isTarget) -> {});

  /** Hook called on all tool modifiers after transforming a block */
  public static final ModuleHook<BlockTransformModifierHook> BLOCK_TRANSFORM = register("block_transform", BlockTransformModifierHook.class, BlockTransformModifierHook.AllMerger::new, (tool, modifier, context, state, pos, action) -> {});


  /* Registration */

  /** Registers a new modifier hook */
  public static <T> ModuleHook<T> register(ResourceLocation name, Class<T> filter, @Nullable Function<Collection<T>,T> merger, T defaultInstance) {
    return LOADER.register(new ModuleHook<>(name, filter, merger, defaultInstance));
  }

  /** Registers a new unmergable modifier hook */
  public static <T> ModuleHook<T> register(ResourceLocation name, Class<T> filter, T defaultInstance) {
    return register(name, filter, null, defaultInstance);
  }

  /** Registers a new modifier hook under {@code tconstruct} */
  private static <T> ModuleHook<T> register(String name, Class<T> filter, @Nullable Function<Collection<T>,T> merger, T defaultInstance) {
    return register(TConstruct.getResource(name), filter, merger, defaultInstance);
  }

  /** Registers a new modifier hook under {@code tconstruct}  that cannot merge */
  @SuppressWarnings("SameParameterValue")
  private static <T> ModuleHook<T> register(String name, Class<T> filter, T defaultInstance) {
    return register(name, filter, null, defaultInstance);
  }
}
