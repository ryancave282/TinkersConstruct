package net.ryancave282.tconstruct.tools.modifiers.slotless;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.ryancave282.tconstruct.TConstruct;
import net.ryancave282.tconstruct.library.modifiers.Modifier;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.modifiers.ModifierHooks;
import net.ryancave282.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import net.ryancave282.tconstruct.library.modifiers.hook.build.VolatileDataModifierHook;
import net.ryancave282.tconstruct.library.modifiers.impl.NoLevelsModifier;
import net.ryancave282.tconstruct.library.module.ModuleHookMap.Builder;
import net.ryancave282.tconstruct.library.tools.SlotType;
import net.ryancave282.tconstruct.library.tools.nbt.IModDataView;
import net.ryancave282.tconstruct.library.tools.nbt.IToolContext;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;
import net.ryancave282.tconstruct.library.tools.nbt.ToolDataNBT;

import java.util.ArrayList;
import java.util.List;

/** Modifier that adds a variable number of slots to a tool. Could easily be done via Tag editing, but this makes it easier */
public class CreativeSlotModifier extends NoLevelsModifier implements VolatileDataModifierHook, ModifierRemovalHook {
  /** Key representing the slots object in the modifier */
  public static final ResourceLocation KEY_SLOTS = TConstruct.getResource("creative");

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ModifierHooks.VOLATILE_DATA, ModifierHooks.REMOVE);
  }

  @Override
  public Component onRemoved(IToolStackView tool, Modifier modifier) {
    tool.getPersistentData().remove(KEY_SLOTS);
    return null;
  }

  @Override
  public void addVolatileData(IToolContext context, ModifierEntry modifier, ToolDataNBT volatileData) {
    IModDataView persistentData = context.getPersistentData();
    if (persistentData.contains(KEY_SLOTS, Tag.TAG_COMPOUND)) {
      CompoundTag slots = persistentData.getCompound(KEY_SLOTS);
      for (String key : slots.getAllKeys()) {
        SlotType slotType = SlotType.getIfPresent(key);
        if (slotType != null) {
          volatileData.addSlots(slotType, slots.getInt(key));
        }
      }
    }
  }

  /** Formats the given slot type as a count */
  private static Component formatCount(SlotType slotType, int count) {
    return Component.literal((count > 0 ? "+" : "") + count + " ")
      .append(slotType.getDisplayName())
      .withStyle(style -> style.withColor(slotType.getColor()));
  }

  @Override
  public List<Component> getDescriptionList(IToolStackView tool, ModifierEntry entry) {
    List<Component> tooltip = getDescriptionList(entry.getLevel());
    IModDataView persistentData = tool.getPersistentData();
    if (persistentData.contains(KEY_SLOTS, Tag.TAG_COMPOUND)) {
      CompoundTag slots = persistentData.getCompound(KEY_SLOTS);

      // first one found has special behavior
      boolean first = true;
      for (String key : slots.getAllKeys()) {
        SlotType slotType = SlotType.getIfPresent(key);
        if (slotType != null) {
          if (first) {
            // found a valid slot? copy the list once then add the rest
            tooltip = new ArrayList<>(tooltip);
            first = false;
          }
          tooltip.add(formatCount(slotType, slots.getInt(key)));
        }
      }
    }
    return tooltip;
  }
}
