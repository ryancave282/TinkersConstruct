package net.ryancave282.tconstruct.tools.modules.armor;

import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.loadable.record.SingletonLoader;
import net.ryancave282.tconstruct.TConstruct;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.modifiers.ModifierHooks;
import net.ryancave282.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import net.ryancave282.tconstruct.library.modifiers.modules.ModifierModule;
import net.ryancave282.tconstruct.library.modifiers.modules.technical.SlotInChargeModule;
import net.ryancave282.tconstruct.library.modifiers.modules.technical.SlotInChargeModule.SlotInCharge;
import net.ryancave282.tconstruct.library.module.HookProvider;
import net.ryancave282.tconstruct.library.module.ModuleHook;
import net.ryancave282.tconstruct.library.module.ModuleHookMap.Builder;
import net.ryancave282.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import net.ryancave282.tconstruct.library.tools.context.EquipmentContext;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;
import net.ryancave282.tconstruct.library.tools.stat.ToolStats;
import net.ryancave282.tconstruct.tools.TinkerModifiers;
import net.ryancave282.tconstruct.tools.modifiers.traits.melee.InsatiableModifier;
import net.ryancave282.tconstruct.tools.stats.ToolType;

import javax.annotation.Nullable;
import java.util.List;

/** Module applying the insatiable armor effect as you take damage */
public enum KineticModule implements ModifierModule, OnAttackedModifierHook, TooltipModifierHook {
  INSTANCE;

  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<KineticModule>defaultHooks(ModifierHooks.ON_ATTACKED, ModifierHooks.TOOLTIP);
  private static final TinkerDataKey<SlotInCharge> SLOT_IN_CHARGE = TConstruct.createKey("insatiable");
  public static final SingletonLoader<KineticModule> LOADER = new SingletonLoader<>(INSTANCE);

  @Override
  public SingletonLoader<KineticModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public void addModules(Builder builder) {
    builder.addModule(new SlotInChargeModule(SLOT_IN_CHARGE));
  }

  @Override
  public void onAttacked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
    // require the damage to be entity caused, but does not strictly need to be melee damage
    if (source.getEntity() != null) {
      int level = SlotInChargeModule.getLevel(context.getTinkerData(), SLOT_IN_CHARGE, slotType);
      if (level > 0) {
        InsatiableModifier.applyEffect(context.getEntity(), ToolType.ARMOR, 10 * 20, 1, level - 1);
      }
    }
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    float bonus = modifier.getLevel();
    if (player != null && tooltipKey == TooltipKey.SHIFT) {
      // armor does not scale the effect level for its bonus
      bonus = TinkerModifiers.insatiableEffect.get(ToolType.ARMOR).getLevel(player) + 1;
    }
    if (bonus > 0) {
      TooltipModifierHook.addFlatBoost(modifier.getModifier(), TooltipModifierHook.statName(modifier.getModifier(), ToolStats.ATTACK_DAMAGE), bonus, tooltip);
    }
  }
}
