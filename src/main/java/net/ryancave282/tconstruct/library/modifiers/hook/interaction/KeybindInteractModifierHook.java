package net.ryancave282.tconstruct.library.modifiers.hook.interaction;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import slimeknights.mantle.client.TooltipKey;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

/**
 * Hooks for interacting with a tool based on a keybind.
 * If you wish to use this hook for another slot, please discuss it with us, so we can implement a solution that will benefit all addons rather than having dupliate keybinds.
 */
public interface KeybindInteractModifierHook {
  /**
   * Called when the helmet keybinding is pressed to interact with this helmet modifier
   * @param tool     Tool instance
   * @param modifier Entry calling this hook
   * @param player   Player wearing the helmet
   * @param slot     Slot containing the tool
   * @return  True if no other modifiers should process
   */
  default boolean startInteract(IToolStackView tool, ModifierEntry modifier, Player player, EquipmentSlot slot, TooltipKey keyModifier) {
    return false;
  }

  /**
   * Called when the helmet keybinding is released to interact with this modifier
   * @param tool     Tool instance
   * @param modifier Entry calling this hook
   * @param player   Player wearing the helmet
   * @param slot     Slot containing the tool
   */
  default void stopInteract(IToolStackView tool, ModifierEntry modifier, Player player, EquipmentSlot slot) {}


  /** Merger that uses the first on start interact, but runs all on stop */
  record InteractMerger(Collection<KeybindInteractModifierHook> modules) implements KeybindInteractModifierHook {
    @Override
    public boolean startInteract(IToolStackView tool, ModifierEntry modifier, Player player, EquipmentSlot slot, TooltipKey keyModifier) {
      for (KeybindInteractModifierHook module : modules) {
        if (module.startInteract(tool, modifier, player, slot, keyModifier)) {
          return true;
        }
      }
      return false;
    }

    @Override
    public void stopInteract(IToolStackView tool, ModifierEntry modifier, Player player, EquipmentSlot slot) {
      for (KeybindInteractModifierHook module : modules) {
        module.stopInteract(tool, modifier, player, slot);
      }
    }
  }
}
