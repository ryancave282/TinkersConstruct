package net.ryancave282.tconstruct.library.tools.nbt;

import com.google.common.collect.ImmutableSet;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.ryancave282.tconstruct.TConstruct;
import net.ryancave282.tconstruct.common.config.Config;
import net.ryancave282.tconstruct.library.materials.definition.MaterialVariantId;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.modifiers.ModifierHooks;
import net.ryancave282.tconstruct.library.modifiers.ModifierId;
import net.ryancave282.tconstruct.library.modifiers.hook.build.ModifierTraitHook.TraitBuilder;
import net.ryancave282.tconstruct.library.tools.SlotType;
import net.ryancave282.tconstruct.library.tools.context.ToolRebuildContext;
import net.ryancave282.tconstruct.library.tools.definition.ToolDefinition;
import net.ryancave282.tconstruct.library.tools.definition.ToolDefinitionData;
import net.ryancave282.tconstruct.library.tools.definition.module.ToolHooks;
import net.ryancave282.tconstruct.library.tools.helper.TooltipUtil;
import net.ryancave282.tconstruct.library.tools.item.IModifiable;
import net.ryancave282.tconstruct.library.tools.stat.ModifierStatsBuilder;
import net.ryancave282.tconstruct.library.tools.stat.ToolStats;
import net.ryancave282.tconstruct.library.utils.RestrictedCompoundTag;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Class handling parsing all tool related NBT
 */
@RequiredArgsConstructor(staticName = "from")
public class ToolStack implements IToolStackView {
  /** Error messages for when there are not enough remaining modifiers */
  private static final String KEY_VALIDATE_SLOTS = TConstruct.makeTranslationKey("recipe", "modifier.validate_slots");

  // persistent NBT
  /** Tag for list of materials */
  public static final String TAG_MATERIALS = "tic_materials";
  /** Tag for extra arbitrary modifier data */
  public static final String TAG_PERSISTENT_MOD_DATA = "tic_persistent";
  /** Tag for recipe based modifier */
  public static final String TAG_UPGRADES = "tic_upgrades";
  /** Tag marking a tool as broken */
  public static final String TAG_BROKEN = "tic_broken";

  // volatile NBT
  /** Tag for calculated stats */
  protected static final String TAG_STATS = "tic_stats";
  /** Tag for tool stat global multipliers */
  protected static final String TAG_MULTIPLIERS = "tic_multipliers";
  /** Tag for arbitrary modifier data rebuilt on stat rebuild */
  public static final String TAG_VOLATILE_MOD_DATA = "tic_volatile_data"; // TODO: consider dropping "_data" from the key for consistency
  /** Tag for merged modifiers of upgrades and traits */
  public static final String TAG_MODIFIERS = "tic_modifiers";

  // vanilla tags
  protected static final String TAG_DAMAGE = "Damage";
  private static final String TAG_UNBREAKABLE = "Unbreakable";
  private static final String TAG_HIDE_FLAGS = "HideFlags";

  /** List of tags to disallow editing for the relevant modifier hooks, disallows all tags we touch. Ignores unbreakable as we only look at that tag for vanilla compat */
  private static final Set<String> RESTRICTED_TAGS = ImmutableSet.of(TAG_MATERIALS, TAG_STATS, TAG_MULTIPLIERS, TAG_PERSISTENT_MOD_DATA, TAG_VOLATILE_MOD_DATA, TAG_UPGRADES, TAG_MODIFIERS, TAG_BROKEN, TAG_DAMAGE, TAG_HIDE_FLAGS);

  /** Item representing this tool */
  @Getter
  private final Item item;
  /** Tool definition, describing part count and alike */
  @Getter
  private final ToolDefinition definition;
  /** Original tool NBT */
  @Getter(AccessLevel.PROTECTED)
  private final CompoundTag nbt;
  /** Public view of the internal NBT, to give to modifier hooks */
  private RestrictedCompoundTag restrictedNBT;

  // durability
  /** Current damage of the tool, -1 means unloaded */
  private int damage = -1;
  /** If true, tool is broken. Null means unloaded */
  @Nullable
  private Boolean broken;

  // tool data: these properties describe the tool
  /** Data object containing materials */
  @Nullable
  private MaterialNBT materials;
  /** Upgrades are modifiers that come from recipes. Abilities are included with these in NBT */
  @Nullable
  private ModifierNBT upgrades;
  /** Data object containing modifier data that persists on stat rebuild */
  @Nullable
  private ToolDataNBT persistentModData;

  // nbt cache: these values are calculated tool data
  /** Combination of modifiers from upgrades and material traits */
  @Nullable
  private ModifierNBT modifiers;
  /** Data object containing the original tool stats */
  @Nullable
  private StatsNBT stats;
  /** Data object containing stat multipliers for each stat */
  @Nullable
  private MultiplierNBT multipliers;
  /** Data object containing modifier data that is recreated when the modifier list changes */
  @Nullable
  private IModDataView volatileModData;

  /* Creating */

  /**
   * Creates a tool stack from an item stack
   * @param stack    Base stack
   * @param copyNbt  If true, NBT is copied from the stack
   * @return  Tool stack
   */
  private static ToolStack from(ItemStack stack, boolean copyNbt) {
    Item item = stack.getItem();
    ToolDefinition definition = item instanceof IModifiable mod
                                ? mod.getToolDefinition()
                                : ToolDefinition.EMPTY;
    CompoundTag nbt = stack.getTag();
    if (nbt == null) {
      nbt = new CompoundTag();
      if (!copyNbt) {
        // only a wrongly made tool will have an empty definition. check preferred to a tag check as tags may not be loaded when this is first called
        if (definition != ToolDefinition.EMPTY) {
          // bypass the setter as vanilla insists on setting damage values there, along with verifying the tag
          // both are things we will do later, doing so now causes us to recursively call this method (though not infinite)
          stack.tag = nbt;
        } else {
          switch (Config.COMMON.logInvalidToolStack.get()) {
            case STACKTRACE ->
              TConstruct.LOG.warn("Tool stack constructed using non-modifiable tool, this may cause issues as it has no NBT. Stacktrace can be disabled in config.", new Exception("Stack trace"));
            case WARNING ->
              TConstruct.LOG.warn("Tool stack constructed using non-modifiable tool, this may cause issues as it has no NBT. To debug this issue or disable the warning, use logInvalidToolStack in the config.");
          }
        }
      }
    } else if (copyNbt) {
      nbt = nbt.copy();
    }
    return from(item, definition, nbt);
  }

  /**
   * Creates a tool stack from the given item stack, not copying NBT
   * @param stack  Stack
   * @return  Tool stack
   */
  public static ToolStack from(ItemStack stack) {
    return from(stack, false);
  }

  /**
   * Creates a tool stack from the given item stack, copying the NBT
   * @param stack  Stack
   * @return  Tool stack
   */
  public static ToolStack copyFrom(ItemStack stack) {
    return from(stack, true);
  }

  /**
   * Creates a new tool stack for a completely new tool
   * @param item        Item
   * @param definition  Tool definition
   * @return  Tool stack
   */
  public static ToolStack createTool(Item item, ToolDefinition definition, MaterialNBT materials) {
    ToolStack tool = from(item, definition, new CompoundTag());
    // set cached to empty, saves a NBT lookup or two
    tool.damage = 0;
    tool.broken = false;
    tool.upgrades = ModifierNBT.EMPTY;
    // update the materials, this will also rebuild the stats
    tool.setMaterials(materials);
    return tool;
  }

  /**
   * Creates a copy of this tool to prevent modifications to the original.
   * Will copy over cached parsed NBT when possible, making this more efficient than calling {@link #copyFrom(ItemStack)}.
   * @return  Copy of this tool
   */
  public ToolStack copy() {
    ToolStack tool = from(item, definition, nbt.copy());
    // copy over relevant loaded data
    tool.damage = this.damage;
    tool.broken = this.broken;
    tool.materials = this.materials;
    tool.upgrades = this.upgrades;
    tool.modifiers = this.modifiers;
    tool.stats = this.stats;
    // skipping mod data as those are mutable, so not safe to share the same instance
    return tool;
  }

  /** Clears all cached data, used with capabilities to prevent cached data from being out of sync due to external changes */
  public void clearCache() {
    this.damage = -1;
    this.broken = null;
    this.materials = null;
    this.upgrades = null;
    this.modifiers = null;
    this.stats = null;
    this.multipliers = null;
    this.volatileModData = null;
    this.persistentModData = null;
  }

  /** Creates an item stack from this tool stack */
  public ItemStack createStack(int size) {
    ItemStack stack = new ItemStack(item, size);
    stack.setTag(nbt);
    return stack;
  }

  /** Creates an item stack from this tool stack */
  public ItemStack createStack() {
    return createStack(1);
  }

  /**
   * Sets the NBT on the given stack
   * @param stack  Stack instance
   * @return  New NBT
   */
  public ItemStack updateStack(ItemStack stack) {
    if (stack.getItem() != item) {
      throw new IllegalArgumentException("Wrong item in stack");
    }
    stack.setTag(nbt.copy());
    return stack;
  }

  /**
   * Gets a restricted view of the tools NBT
   * @return  Tool NBT without access to internal tags
   */
  public RestrictedCompoundTag getRestrictedNBT() {
    if (restrictedNBT == null) {
      restrictedNBT = new RestrictedCompoundTag(nbt, RESTRICTED_TAGS);
    }
    return restrictedNBT;
  }

  /* Damaging */

  /**
   * Checks if this tool is currently broken
   * @return  True if broken
   */
  @Override
  public boolean isBroken() {
    if (broken == null) {
      broken = nbt.getBoolean(TAG_BROKEN);
    }
    return broken;
  }

  @Override
  public boolean isUnbreakable() {
    return nbt.getBoolean(TAG_UNBREAKABLE);
  }

  /**
   * Sets the broken state on the tool
   * @param broken  New broken value
   */
  protected void setBrokenRaw(boolean broken) {
    this.broken = broken;
    nbt.putBoolean(TAG_BROKEN, broken);
  }

  /**
   * Breaks the tool
   */
  protected void breakTool() {
    setDamage(getStats().getInt(ToolStats.DURABILITY));
  }

  /**
   * Gets damage, ignoring broken checks
   * @return  Damage ignoring broken state
   */
  protected int getDamageRaw() {
    if (damage == -1) {
      damage = nbt.getInt(TAG_DAMAGE);
    }
    return damage;
  }

  /**
   * Gets the tools current damage from NBT
   * @return  Current damage
   */
  @Override
  public int getDamage() {
    // if broken, return full damage
    int durability = getStats().getInt(ToolStats.DURABILITY);
    if (isBroken()) {
      return durability;
    }
    // ensure we never return a number larger than max
    return Math.min(getDamageRaw(), durability - 1);
  }

  /**
   * Gets the current durability remaining for this tool
   * @return  Tool durability
   */
  @Override
  public int getCurrentDurability() {
    if (isBroken()) {
      return 0;
    }
    // ensure we never return a number smaller than 0
    return Math.max(0, getStats().getInt(ToolStats.DURABILITY) - getDamageRaw());
  }

  /**
   * Sets the tools damage
   * @param  damage  New damage
   */
  @Override
  public void setDamage(int damage) {
    int durability = getStats().getInt(ToolStats.DURABILITY);
    if (damage >= durability) {
      damage = Math.max(0, durability);
      setBrokenRaw(true);
    } else {
      setBrokenRaw(false);
    }
    this.damage = damage;
    nbt.putInt(TAG_DAMAGE, damage);
  }

  /* Stats */

  /**
   * Gets the tool stats if parsed, or parses from NBT if not yet parsed
   * @return stats
   */
  @Override
  public StatsNBT getStats() {
    if (stats == null) {
      stats = StatsNBT.readFromNBT(nbt.get(TAG_STATS));
    }
    return stats;
  }

  /**
   * Sets the tool stats, and stores it in NBT
   * @param stats  Stats instance
   */
  protected void setStats(StatsNBT stats) {
    this.stats = stats;
    nbt.put(TAG_STATS, stats.serializeToNBT());
    // if we no longer have enough durability, decrease the damage and mark it broken
    int newMax = getStats().getInt(ToolStats.DURABILITY);
    if (getDamageRaw() >= newMax) {
      setDamage(newMax);
    }
  }

  @Override
  public MultiplierNBT getMultipliers() {
    if (multipliers == null) {
      multipliers = MultiplierNBT.readFromNBT(nbt.get(TAG_MULTIPLIERS));
    }
    return multipliers;
  }

  /**
   * Sets the tool multipliers, and stores it in NBT
   * @param multipliers  Stats instance
   */
  protected void setMultipliers(MultiplierNBT multipliers) {
    if (multipliers.getContainedStats().isEmpty()) {
      this.multipliers = MultiplierNBT.EMPTY;
      nbt.remove(TAG_MULTIPLIERS);
    } else {
      this.multipliers = multipliers;
      nbt.put(TAG_MULTIPLIERS, multipliers.serializeToNBT());
    }
  }


  /* Materials */

  @Override
  public MaterialNBT getMaterials() {
    if (!getDefinition().hasMaterials()) {
      return MaterialNBT.EMPTY;
    }
    if (materials == null) {
      materials = MaterialNBT.readFromNBT(nbt.get(TAG_MATERIALS));
    }
    return materials;
  }

  /**
   * Sets the materials without updating the tool stats
   * @param materials  New materials
   */
  protected void setMaterialsRaw(MaterialNBT materials) {
    this.materials = materials;
    if (materials == MaterialNBT.EMPTY) {
      this.nbt.remove(TAG_MATERIALS);
    } else {
      this.nbt.put(TAG_MATERIALS, materials.serializeToNBT());
    }
  }

  /**
   * Sets the materials on this tool stack, updating tool stats
   * @param materials  New materials NBT
   */
  public void setMaterials(MaterialNBT materials) {
    setMaterialsRaw(materials);
    rebuildStats();
  }

  /**
   * Replaces the material at the given index
   * @param index        Index to replace
   * @param replacement  New material
   * @throws IndexOutOfBoundsException  If the index is invalid
   */
  public void replaceMaterial(int index, MaterialVariantId replacement) {
    setMaterials(getMaterials().replaceMaterial(index, replacement));
  }


  /* Modifiers */

  /**
   * Gets a list of modifiers added from recipes.
   * In general you should use {@link #getModifiers()} when performing modifier actions to include traits.
   * @return  Recipe modifier list
   */
  @Override
  public ModifierNBT getUpgrades() {
    if (upgrades == null) {
      upgrades = ModifierNBT.readFromNBT(nbt.get(TAG_UPGRADES));
    }
    return upgrades;
  }

  /**
   * Updates the upgrades list on the tool
   * @param modifiers  New upgrades
   */
  public void setUpgrades(ModifierNBT modifiers) {
    this.upgrades = modifiers;
    nbt.put(TAG_UPGRADES, modifiers.serializeToNBT());
    rebuildStats();
  }

  /**
   * Adds a single modifier to this tool
   * @param modifier  Modifier to add
   * @param level     Level to add
   */
  public void addModifier(ModifierId modifier, int level) {
    if (level <= 0) {
      throw new IllegalArgumentException("Invalid level, must be above 0");
    }
    setUpgrades(getUpgrades().withModifier(modifier, level));
  }

  /**
   * Adds a single modifier to this tool
   * @param modifier  Modifier to add
   * @param amount    Amount to add
   * @param needed    Amount needed for a full level
   */
  public void addModifierAmount(ModifierId modifier, int amount, int needed) {
    if (needed <= 0) {
      throw new IllegalArgumentException("Invalid needed, must be above 0");
    }
    if (amount > 0) {
      setUpgrades(getUpgrades().addAmount(modifier, amount, needed));
    }
  }

  /**
   * Removes a single modifier to this tool
   * @param modifier  Modifier to remove
   * @param level     Level to remove
   */
  public void removeModifier(ModifierId modifier, int level) {
    if (level <= 0) {
      throw new IllegalArgumentException("Invalid level, must be above 0");
    }
    ModifierNBT newModifiers = getUpgrades().withoutModifier(modifier, level);
    this.upgrades = newModifiers;
    nbt.put(TAG_UPGRADES, newModifiers.serializeToNBT());
    rebuildStats();
  }

  @Override
  public ModifierNBT getModifiers() {
    if (modifiers == null) {
      modifiers = ModifierNBT.readFromNBT(nbt.get(TAG_MODIFIERS));
    }
    return modifiers;
  }

  /**
   * Updates the list of all modifiers in NBT, called in {@link #rebuildStats()}
   * @param modifiers  New modifiers
   */
  protected void setModifiers(ModifierNBT modifiers) {
    this.modifiers = modifiers;
    nbt.put(TAG_MODIFIERS, this.modifiers.serializeToNBT());
  }


  /* Data */

  @Override
  public ToolDataNBT getPersistentData() {
    if (persistentModData == null) {
      // parse if the tag already exists
      if (nbt.contains(TAG_PERSISTENT_MOD_DATA, Tag.TAG_COMPOUND)) {
        persistentModData = ToolDataNBT.readFromNBT(nbt.getCompound(TAG_PERSISTENT_MOD_DATA));
      } else {
        // if no tag exists, create it
        CompoundTag tag = new CompoundTag();
        nbt.put(TAG_PERSISTENT_MOD_DATA, tag);
        persistentModData = ToolDataNBT.readFromNBT(tag);
      }
    }
    return persistentModData;
  }

  @Override
  public IModDataView getVolatileData() {
    if (volatileModData == null) {
      // parse if the tag already exists
      if (nbt.contains(TAG_VOLATILE_MOD_DATA, Tag.TAG_COMPOUND)) {
        volatileModData = ToolDataNBT.readFromNBT(nbt.getCompound(TAG_VOLATILE_MOD_DATA));
      } else {
        // if no tag exists, return empty
        volatileModData = IModDataView.EMPTY;
      }
    }
    return volatileModData;
  }

  /**
   * Updates the volatile mod data in NBT, called in {@link #rebuildStats()}
   * @param modData  New data
   */
  protected void setVolatileModData(ToolDataNBT modData) {
    CompoundTag data = modData.getData();
    if (data.isEmpty()) {
      volatileModData = IModDataView.EMPTY;
      nbt.remove(TAG_VOLATILE_MOD_DATA);
    } else {
      volatileModData = modData;
      nbt.put(TAG_VOLATILE_MOD_DATA, data);
    }
  }


  /* Utilities */

  @Nullable
  public Component tryValidate() {
    // first check slot counts
    for (SlotType slotType : SlotType.getAllSlotTypes()) {
      if (getFreeSlots(slotType) < 0) {
        return Component.translatable(KEY_VALIDATE_SLOTS, slotType.getDisplayName());
      }
    }
    // next, ensure modifiers validate
    Component result;
    for (ModifierEntry entry : getModifierList()) {
      result = entry.getHook(ModifierHooks.VALIDATE).validate(this, entry);
      if (result != null) {
        return result;
      }
    }
    return null;
  }

  /** Called on inventory tick to ensure the tool has all required data including materials and starting slots, prevents tools with no stats from existing */
  public void ensureHasData() {
    // if we try initializing before datapacks load we will get garbage data
    if (definition.isDataLoaded()) {
      // build data if we either lack data (signified by no stats) or we lack materials but expect them
      boolean needsMaterials = definition.hasMaterials() && !nbt.contains(TAG_MATERIALS, Tag.TAG_LIST);
      if (needsMaterials || !isInitialized(nbt)) {
        // randomize materials if missing
        if (needsMaterials) {
          setMaterialsRaw(definition.getHook(ToolHooks.MISSING_MATERIALS).fillMaterials(definition, RandomSource.create()));
        }
        rebuildStats();
      }
    }
  }

  /**
   * Recalculates any relevant cached data. Called after either the materials or modifiers list changes
   */
  public void rebuildStats() {
    // add tool slots to volatile data, ensures it is there even from an empty tool, and properly updates on datapack update
    ToolDefinitionData toolData = getDefinitionData();

    // first, determine the list of modifiers, this is done in a couple stages
    // we start by cloning upgrades and adding tool traits and material traits
    MaterialNBT materials = getMaterials();
    ModifierNBT.Builder modBuilder = ModifierNBT.builder();
    modBuilder.add(getUpgrades());
    toolData.getHook(ToolHooks.TOOL_TRAITS).addTraits(definition, materials, modBuilder);
    ModifierNBT beforeTraits = modBuilder.build();

    // temporary context while we add modifier traits, will recreate if we have modifiers
    // clear out volatile data, mostly affects the volatile data hook
    ToolRebuildContext context = new ToolRebuildContext(item, definition, materials, getUpgrades(), beforeTraits, getPersistentData());

    // if we have modifiers, apply modifier traits, saves creating some builders if empty
    List<ModifierEntry> modifierList = Collections.emptyList();
    if (beforeTraits.isEmpty()) {
      // if no modifiers, just clear modifiers
      setModifiers(ModifierNBT.EMPTY);
    } else {
      modBuilder = ModifierNBT.builder();
      TraitBuilder traitBuilder = new TraitBuilder(context, modBuilder);
      traitBuilder.add(beforeTraits);

      // set the final modifier list on the tool
      ModifierNBT allMods = modBuilder.build();
      setModifiers(allMods);
      modifierList = allMods.getModifiers();
      // context for further modifier hooks
      context = context.withModifiers(allMods);
    }

    // build volatile data first, it's a parameter to the other hooks
    ToolDataNBT volatileData = new ToolDataNBT();
    toolData.getHook(ToolHooks.VOLATILE_DATA).addVolatileData(context, volatileData);
    for (ModifierEntry entry : modifierList) {
      entry.getHook(ModifierHooks.VOLATILE_DATA).addVolatileData(context, entry, volatileData);
    }
    setVolatileModData(volatileData);

    // regular stats last so we can include volatile data
    ModifierStatsBuilder statBuilder = ModifierStatsBuilder.builder();
    toolData.getHook(ToolHooks.TOOL_STATS).addToolStats(context, statBuilder);
    for (ModifierEntry entry : modifierList) {
      entry.getHook(ModifierHooks.TOOL_STATS).addToolStats(context, entry, statBuilder);
    }
    setStats(statBuilder.build(item));
    setMultipliers(statBuilder.buildMultipliers(item));

    // finally, update raw data, called last to make the parameters more convenient mostly, plus no other hooks should be responding to this data
    for (ModifierEntry entry : modifierList) {
      entry.getHook(ModifierHooks.RAW_DATA).addRawData(this, entry, getRestrictedNBT());
    }
  }


  /* Static helpers */

  /**
   * Checks if the given tool stats have been initialized, used as a marker to indicate slots are not yet applied
   * @param stack  Stack to check
   * @return  True if initialized
   */
  public static boolean isInitialized(ItemStack stack) {
    CompoundTag tag = stack.getTag();
    return tag != null && isInitialized(tag);
  }

  /**
   * Checks if the given tool stats have been initialized, used as a marker to indicate slots are not yet applied
   * @param tag  Tag to check
   * @return  True if initialized
   */
  public static boolean isInitialized(CompoundTag tag) {
    return tag.contains(TAG_STATS, Tag.TAG_COMPOUND);
  }

  /**
   * Checks if the given tool stats have been initialized, used as a marker to indicate slots are not yet applied
   * @param stack  Stack to check
   * @return  True if initialized
   */
  public static boolean hasMaterials(ItemStack stack) {
    CompoundTag nbt = stack.getTag();
    return nbt != null && nbt.contains(TAG_MATERIALS, Tag.TAG_LIST);
  }

  /**
   * Ensures the given item stack is initialized. Called in crafting hooks
   * @param stack ItemStack to initialize
   */
  public static void ensureInitialized(ItemStack stack) {
    if (stack.getItem() instanceof IModifiable modifiable) {
      ensureInitialized(stack, modifiable.getToolDefinition());
    }
  }

  /**
   * Ensures the given item stack is initialized. Intended to be called in {@link Item#onCraftedBy(ItemStack, Level, Player)}
   * @param stack           ItemStack to initialize
   * @param toolDefinition  Tool definition
   */
  public static void ensureInitialized(ItemStack stack, ToolDefinition toolDefinition) {
    // must be loaded
    if (!toolDefinition.isDataLoaded()) {
      return;
    }
    CompoundTag tag = stack.getTag();
    // already initialized? nothing to do
    if (tag != null && isInitialized(tag)) {
      return;
    }
    // time to initialize
    ToolStack.from(stack).ensureHasData();
  }

  /**
   * Rebuilds the item stack when loaded from NBT
   * stops things from being wrong if modifiers or materials change
   * @param item        Item to build
   * @param tag         Stack tag
   * @param definition  Tool definition
   */
  public static void verifyTag(Item item, CompoundTag tag, ToolDefinition definition) {
    // this function is sometimes called before datapack contents load, do nothing then
    if (!definition.isDataLoaded() || tag.getBoolean(TooltipUtil.KEY_DISPLAY)) {
      return;
    }

    // resolve all material redirects
    boolean hasMaterials = tag.contains(ToolStack.TAG_MATERIALS, Tag.TAG_LIST);
    if (hasMaterials) {
      MaterialIdNBT stored = MaterialIdNBT.readFromNBT(tag.getList(ToolStack.TAG_MATERIALS, Tag.TAG_STRING));
      MaterialIdNBT resolved = stored.resolveRedirects();
      if (resolved != stored) {
        resolved.updateNBT(tag);
      }
    }
    // rebuild stats if we have required data (skip if multipart with no materials)
    ToolStack tool = ToolStack.from(item, definition, tag);
    if (hasMaterials || !definition.hasMaterials()) {
      tool.rebuildStats();
    }
  }
}
