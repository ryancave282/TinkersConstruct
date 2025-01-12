package net.ryancave282.tconstruct.library.modifiers.modules.build;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import net.ryancave282.tconstruct.TConstruct;
import net.ryancave282.tconstruct.library.modifiers.Modifier;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.modifiers.ModifierHooks;
import net.ryancave282.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import net.ryancave282.tconstruct.library.modifiers.hook.build.VolatileDataModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.display.DisplayNameModifierHook;
import net.ryancave282.tconstruct.library.modifiers.modules.ModifierModule;
import net.ryancave282.tconstruct.library.modifiers.modules.util.ModifierCondition;
import net.ryancave282.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import net.ryancave282.tconstruct.library.modifiers.util.ModuleWithKey;
import net.ryancave282.tconstruct.library.module.HookProvider;
import net.ryancave282.tconstruct.library.module.ModuleHook;
import net.ryancave282.tconstruct.library.tools.SlotType;
import net.ryancave282.tconstruct.library.tools.nbt.IToolContext;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;
import net.ryancave282.tconstruct.library.tools.nbt.ToolDataNBT;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Module for a extra slot modifier with multiple variants based on the slot type
 * @param key             Persistent data key containing the slot name. If null, uses the modifier ID.
 *                        Presently, changing this makes it incompatible with the swappable modifier recipe, this is added for future proofing.
 * @param slotCount       Number of slots to grant
 */
public record SwappableSlotModule(@Nullable ResourceLocation key, int slotCount, ModifierCondition<IToolContext> condition) implements VolatileDataModifierHook, DisplayNameModifierHook, ModifierRemovalHook, ModifierModule, ModuleWithKey, ConditionalModule<IToolContext> {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<SwappableSlotModule>defaultHooks(ModifierHooks.VOLATILE_DATA, ModifierHooks.DISPLAY_NAME, ModifierHooks.REMOVE);
  /** Format key for swappable variant */
  public static final String FORMAT = TConstruct.makeTranslationKey("modifier", "extra_modifier.type_format");
  public static final RecordLoadable<SwappableSlotModule> LOADER = RecordLoadable.create(
    ModuleWithKey.FIELD,
    IntLoadable.ANY_SHORT.requiredField("slots", SwappableSlotModule::slotCount),
    ModifierCondition.CONTEXT_FIELD,
    SwappableSlotModule::new);

  public SwappableSlotModule(@Nullable ResourceLocation key, int slotCount) {
    this(key, slotCount, ModifierCondition.ANY_CONTEXT);
  }

  public SwappableSlotModule(int slotCount) {
    this(null, slotCount);
  }

  @Override
  public Component getDisplayName(IToolStackView tool, ModifierEntry entry, Component name, @Nullable RegistryAccess access) {
    String slotName = tool.getPersistentData().getString(getKey(entry.getModifier()));
    if (!slotName.isEmpty()) {
      SlotType type = SlotType.getIfPresent(slotName);
      if (type != null) {
        return Component.translatable(FORMAT, name.plainCopy(), type.getDisplayName()).withStyle(style -> style.withColor(type.getColor()));
      }
    }
    return name;
  }

  @Override
  public Integer getPriority() {
    // show lower priority so they group together
    return 50;
  }

  @Override
  public void addVolatileData(IToolContext context, ModifierEntry modifier, ToolDataNBT volatileData) {
    if (condition.matches(context, modifier)) {
      String slotName = context.getPersistentData().getString(getKey(modifier.getModifier()));
      if (!slotName.isEmpty()) {
        SlotType type = SlotType.getIfPresent(slotName);
        if (type != null) {
          volatileData.addSlots(type, slotCount);
        }
      }
    }
  }

  @Nullable
  @Override
  public Component onRemoved(IToolStackView tool, Modifier modifier) {
    tool.getPersistentData().remove(getKey(modifier));
    return null;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public RecordLoadable<SwappableSlotModule> getLoader() {
    return LOADER;
  }

  /** Module to add (or remove) additional slots based on the given swappable slot type */
  public record BonusSlot(@Nullable ResourceLocation key, SlotType match, SlotType bonus, int slotCount, ModifierCondition<IToolContext> condition) implements VolatileDataModifierHook, ModifierModule, ModuleWithKey, ConditionalModule<IToolContext> {
    private static final List<ModuleHook<?>> DEFAULT_HOOKS = List.of(ModifierHooks.VOLATILE_DATA);
    public static final RecordLoadable<BonusSlot> LOADER = RecordLoadable.create(
      ModuleWithKey.FIELD,
      SlotType.LOADABLE.requiredField("match", BonusSlot::match),
      SlotType.LOADABLE.requiredField("bonus", BonusSlot::bonus),
      IntLoadable.ANY_SHORT.requiredField("slots", BonusSlot::slotCount),
      ModifierCondition.CONTEXT_FIELD,
      BonusSlot::new);

    public BonusSlot(@Nullable ResourceLocation key, SlotType match, SlotType bonus, int slotCount) {
      this(key, match, bonus, slotCount, ModifierCondition.ANY_CONTEXT);
    }

    public BonusSlot(SlotType match, SlotType penalty, int slotCount) {
      this(null, match, penalty, slotCount);
    }

    @Override
    public void addVolatileData(IToolContext context, ModifierEntry modifier, ToolDataNBT volatileData) {
      if (condition.matches(context, modifier)) {
        String slotName = context.getPersistentData().getString(getKey(modifier.getModifier()));
        if (!slotName.isEmpty() && match.getName().equals(slotName)) {
          volatileData.addSlots(bonus, slotCount);
        }
      }
    }

    @Override
    public List<ModuleHook<?>> getDefaultHooks() {
      return DEFAULT_HOOKS;
    }

    @Override
    public RecordLoadable<BonusSlot> getLoader() {
      return LOADER;
    }
  }
}
