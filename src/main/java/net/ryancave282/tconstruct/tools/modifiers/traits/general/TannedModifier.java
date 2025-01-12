package net.ryancave282.tconstruct.tools.modifiers.traits.general;

import net.minecraft.world.entity.LivingEntity;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.modifiers.ModifierHooks;
import net.ryancave282.tconstruct.library.modifiers.hook.behavior.ToolDamageModifierHook;
import net.ryancave282.tconstruct.library.modifiers.impl.NoLevelsModifier;
import net.ryancave282.tconstruct.library.module.ModuleHookMap.Builder;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

public class TannedModifier extends NoLevelsModifier implements ToolDamageModifierHook {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ModifierHooks.TOOL_DAMAGE);
  }

  @Override
  public int getPriority() {
    // higher than stoneshield, overslime, and reinforced
    return 200;
  }

  @Override
  public int onDamageTool(IToolStackView tool, ModifierEntry modifier, int amount, @Nullable LivingEntity holder) {
    return amount >= 1 ? 1 : 0;
  }
}
