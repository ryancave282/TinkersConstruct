package net.ryancave282.tconstruct.library.recipe.modifiers.adding;

import com.google.common.collect.Streams;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.ryancave282.tconstruct.library.json.IntRange;
import net.ryancave282.tconstruct.library.modifiers.Modifier;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.modifiers.ModifierHooks;
import net.ryancave282.tconstruct.library.tools.SlotType;
import net.ryancave282.tconstruct.library.tools.SlotType.SlotCount;
import net.ryancave282.tconstruct.library.tools.context.ToolRebuildContext;
import net.ryancave282.tconstruct.library.tools.definition.ToolDefinition;
import net.ryancave282.tconstruct.library.tools.item.IModifiableDisplay;
import net.ryancave282.tconstruct.library.tools.nbt.MaterialNBT;
import net.ryancave282.tconstruct.library.tools.nbt.ModDataNBT;
import net.ryancave282.tconstruct.library.tools.nbt.ModifierNBT;
import net.ryancave282.tconstruct.library.tools.nbt.ToolDataNBT;
import net.ryancave282.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/** Common interface for modifier recipes that can show in JEI */
public interface IDisplayModifierRecipe extends IModifierRecipe {
  /** Gets the number of inputs for this recipe */
  int getInputCount();

  /**
   * Gets an ingredients to display in JEI.
   * @param  slot  Slot index to display
   * @return  Display item list
   */
  List<ItemStack> getDisplayItems(int slot);

  /** Gets the result tool before adding the modifier */
  List<ItemStack> getToolWithoutModifier();

  /** Gets the result tool with this modifier added */
  List<ItemStack> getToolWithModifier();

  /** Gets the modifier output of this recipe */
  ModifierEntry getDisplayResult();

  @Override
  default Modifier getModifier() {
    return getDisplayResult().getModifier();
  }

  /**
   * Gets the range this modifier is valid for
   * @return  level range, defaults to {@link ModifierEntry#VALID_LEVEL}
   */
  default IntRange getLevel() {
    return ModifierEntry.VALID_LEVEL;
  }

  /**
   * Gets the swappable variant for this recipe. Mutually exclusive with level for recipe display.
   * @return  Variant text for the modifier, or null if not a variant.
   */
  @Nullable
  default Component getVariant() {
    return null;
  }

  /** Gets the slot type used by this modifier */
  @Nullable
  default SlotCount getSlots() {
    return null;
  }

  @Nullable
  @Override
  default SlotType getSlotType() {
    SlotCount count = getSlots();
    if (count == null) {
      return null;
    }
    return count.type();
  }

  /** If true, this recipe can be applied incrementally */
  default boolean isIncremental() {
    return false;
  }


  /* Helpers */

  /** Maps the stream from tool items to applicable tool stacks */
  Function<Item,ItemStack> MAP_TOOL_FOR_RENDERING = IModifiableDisplay::getDisplayStack;

  /**
   * Gets the list of modifiers to display for the given result
   * @param result  Resulting modifier
   * @param self    Current modifier to display, will typically be result or a lower level of result
   * @return  List of modifiers
   */
  static List<ModifierEntry> modifiersForResult(ModifierEntry result, @Nullable ModifierEntry self) {
    List<ModifierEntry> requirements = result.getHook(ModifierHooks.REQUIREMENTS).displayModifiers(result);
    if (self != null) {
      return Streams.concat(requirements.stream(), Stream.of(self)).toList();
    }
    return requirements;
  }

  /* Gets a copy of the stack with the given modifiers */
  static ItemStack withModifiers(ItemStack stack, List<ModifierEntry> modifiers) {
    return withModifiers(stack, modifiers, data -> {});
  }

  /* Gets a copy of the stack with the given modifiers */
  static ItemStack withModifiers(ItemStack stack, List<ModifierEntry> modifierList, Consumer<ModDataNBT> persistentDataConsumer) {
    ItemStack output = stack.copy();
    CompoundTag nbt = output.getOrCreateTag();

    // build modifiers list
    // go through the builder to ensure they are merged properly
    ModifierNBT modifiers = ModifierNBT.builder().add(modifierList).build();
    ListTag list = modifiers.serializeToNBT();
    nbt.put(ToolStack.TAG_UPGRADES, list);
    nbt.put(ToolStack.TAG_MODIFIERS, list);

    // build persistent and volatile NBT
    CompoundTag persistentNBT = new CompoundTag();
    ModDataNBT persistentData = ModDataNBT.readFromNBT(persistentNBT);
    CompoundTag volatileNBT = new CompoundTag();
    ToolDataNBT volatileData = ToolDataNBT.readFromNBT(volatileNBT);
    persistentDataConsumer.accept(persistentData);
    ToolRebuildContext context = new ToolRebuildContext(stack.getItem(), ToolDefinition.EMPTY, MaterialNBT.EMPTY, modifiers, modifiers, persistentData);
    for (ModifierEntry entry : modifiers.getModifiers()) {
      entry.getHook(ModifierHooks.VOLATILE_DATA).addVolatileData(context, entry, volatileData);
    }
    nbt.put(ToolStack.TAG_VOLATILE_MOD_DATA, volatileNBT);
    nbt.put(ToolStack.TAG_PERSISTENT_MOD_DATA, persistentNBT);

    return output;
  }
}
