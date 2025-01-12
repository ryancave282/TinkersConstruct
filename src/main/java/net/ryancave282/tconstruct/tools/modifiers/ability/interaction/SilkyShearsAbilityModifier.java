package net.ryancave282.tconstruct.tools.modifiers.ability.interaction;

import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;
import net.ryancave282.tconstruct.tools.TinkerModifiers;

public class SilkyShearsAbilityModifier extends ShearsAbilityModifier {
  public SilkyShearsAbilityModifier(int range, int priority) {
    super(range, priority);
  }
  
  @Override
  protected boolean isShears(IToolStackView tool) {
    return tool.getModifierLevel(TinkerModifiers.silky.getId()) > 0;
  }
}
