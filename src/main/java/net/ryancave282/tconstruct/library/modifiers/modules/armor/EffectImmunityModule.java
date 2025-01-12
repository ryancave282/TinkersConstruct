package net.ryancave282.tconstruct.library.modifiers.modules.armor;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import net.minecraft.world.effect.MobEffect;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import net.ryancave282.tconstruct.TConstruct;
import net.ryancave282.tconstruct.common.TinkerTags;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.modifiers.ModifierHooks;
import net.ryancave282.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import net.ryancave282.tconstruct.library.modifiers.modules.ModifierModule;
import net.ryancave282.tconstruct.library.modifiers.modules.technical.ArmorLevelModule;
import net.ryancave282.tconstruct.library.modifiers.modules.util.ModifierCondition;
import net.ryancave282.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import net.ryancave282.tconstruct.library.module.HookProvider;
import net.ryancave282.tconstruct.library.module.ModuleHook;
import net.ryancave282.tconstruct.library.tools.capability.TinkerDataCapability.ComputableDataKey;
import net.ryancave282.tconstruct.library.tools.context.EquipmentChangeContext;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

/**
 * Module for armor modifiers that makes the wearer immune to a mob effect
 */
public record EffectImmunityModule(MobEffect effect, ModifierCondition<IToolStackView> condition) implements ModifierModule, EquipmentChangeModifierHook, ConditionalModule<IToolStackView> {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<MobDisguiseModule>defaultHooks(ModifierHooks.EQUIPMENT_CHANGE);
  public static final ComputableDataKey<Multiset<MobEffect>> EFFECT_IMMUNITY = TConstruct.createKey("effect_immunity", HashMultiset::create);
  public static final RecordLoadable<EffectImmunityModule> LOADER = RecordLoadable.create(
    Loadables.MOB_EFFECT.requiredField("effect", EffectImmunityModule::effect),
    ModifierCondition.TOOL_FIELD,
    EffectImmunityModule::new);

  public EffectImmunityModule(MobEffect effect) {
    this(effect, ModifierCondition.ANY_TOOL);
  }

  @Override
  public RecordLoadable<EffectImmunityModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (!tool.isBroken() && ArmorLevelModule.validSlot(tool, context.getChangedSlot(), TinkerTags.Items.HELD_ARMOR) && condition.matches(tool, modifier)) {
      context.getTinkerData().ifPresent(data -> data.computeIfAbsent(EFFECT_IMMUNITY).add(effect));
    }
  }

  @Override
  public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (!tool.isBroken() && ArmorLevelModule.validSlot(tool, context.getChangedSlot(), TinkerTags.Items.HELD_ARMOR) && condition.matches(tool, modifier)) {
      context.getTinkerData().ifPresent(data -> {
        Multiset<MobEffect> effects = data.get(EFFECT_IMMUNITY);
        if (effects != null) {
          effects.remove(effect);
        }
      });
    }
  }
}
