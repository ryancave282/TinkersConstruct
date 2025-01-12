package net.ryancave282.tconstruct.tools.modules.armor;

import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.damage.DamageSourcePredicate;
import net.ryancave282.tconstruct.library.json.LevelingValue;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.modifiers.ModifierHooks;
import net.ryancave282.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.armor.ProtectionModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import net.ryancave282.tconstruct.library.modifiers.modules.ModifierModule;
import net.ryancave282.tconstruct.library.modifiers.modules.armor.ProtectionModule;
import net.ryancave282.tconstruct.library.module.HookProvider;
import net.ryancave282.tconstruct.library.module.ModuleHook;
import net.ryancave282.tconstruct.library.tools.context.EquipmentContext;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;
import net.ryancave282.tconstruct.tools.TinkerModifiers;
import net.ryancave282.tconstruct.tools.stats.ToolType;

import javax.annotation.Nullable;
import java.util.List;

/** Module for boosting protection after taking damage */
public record RecurrentProtectionModule(LevelingValue amount) implements ModifierModule, ProtectionModifierHook, ModifyDamageModifierHook, TooltipModifierHook {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<RecurrentProtectionModule>defaultHooks(ModifierHooks.PROTECTION, ModifierHooks.MODIFY_DAMAGE, ModifierHooks.TOOLTIP);
  public static final RecordLoadable<RecurrentProtectionModule> LOADER = RecordLoadable.create(
    LevelingValue.LOADABLE.directField(RecurrentProtectionModule::amount),
    RecurrentProtectionModule::new);

  @Override
  public RecordLoadable<RecurrentProtectionModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public float getProtectionModifier(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float modifierValue) {
    if (DamageSourcePredicate.CAN_PROTECT.matches(source) && context.getEntity().hasEffect(TinkerModifiers.momentumEffect.get(ToolType.ARMOR))) {
      modifierValue += amount.compute(modifier.getEffectiveLevel());
    }
    return modifierValue;
  }

  @Override
  public float modifyDamageTaken(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
    // does not hurt to add multiple copies
    if (source.getEntity() != null) {
      TinkerModifiers.momentumEffect.get(ToolType.ARMOR).apply(context.getEntity(), 5 * 20, 0, true);
    }
    return amount;
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    float bonus = amount.compute(modifier.getEffectiveLevel());
    if (bonus > 0 && (player == null || tooltipKey != TooltipKey.SHIFT || player.hasEffect(TinkerModifiers.momentumEffect.get(ToolType.ARMOR)))) {
      ProtectionModule.addResistanceTooltip(tool, modifier.getModifier(), bonus, player, tooltip);
    }
  }
}
