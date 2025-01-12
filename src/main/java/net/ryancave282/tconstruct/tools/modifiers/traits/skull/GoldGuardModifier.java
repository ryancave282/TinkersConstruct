package net.ryancave282.tconstruct.tools.modifiers.traits.skull;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.mantle.client.TooltipKey;
import net.ryancave282.tconstruct.TConstruct;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.modifiers.ModifierHooks;
import net.ryancave282.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import net.ryancave282.tconstruct.library.modifiers.impl.NoLevelsModifier;
import net.ryancave282.tconstruct.library.module.ModuleHookMap.Builder;
import net.ryancave282.tconstruct.library.tools.capability.TinkerDataCapability.ComputableDataKey;
import net.ryancave282.tconstruct.library.tools.context.EquipmentChangeContext;
import net.ryancave282.tconstruct.library.tools.item.armor.ModifiableArmorItem;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;
import net.ryancave282.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class GoldGuardModifier extends NoLevelsModifier implements EquipmentChangeModifierHook, TooltipModifierHook {
  private static final UUID GOLD_GUARD_UUID = UUID.fromString("fbae11f1-b547-47e8-ae0c-f2cf24a46d93");
  private static final ComputableDataKey<GoldGuardGold> TOTAL_GOLD = TConstruct.createKey("gold_guard", GoldGuardGold::new);

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ModifierHooks.EQUIPMENT_CHANGE, ModifierHooks.TOOLTIP);
  }

  @Override
  public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    // adding a helmet? activate bonus
    if (context.getChangedSlot() == EquipmentSlot.HEAD) {
      context.getTinkerData().ifPresent(data -> {
        GoldGuardGold gold = data.get(TOTAL_GOLD);
        if (gold == null) {
          data.computeIfAbsent(TOTAL_GOLD).initialize(context);
        } else {
          gold.setGold(EquipmentSlot.HEAD, tool.getVolatileData().getBoolean(ModifiableArmorItem.PIGLIN_NEUTRAL), context.getEntity());
        }
      });
    }
  }

  @Override
  public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (context.getChangedSlot() == EquipmentSlot.HEAD) {
      IToolStackView newTool = context.getReplacementTool();
      // when replacing with a helmet that lacks this modifier, remove bonus
      if (newTool == null || newTool.getModifierLevel(this) == 0) {
        context.getTinkerData().ifPresent(data -> data.remove(TOTAL_GOLD));
        AttributeInstance instance = context.getEntity().getAttribute(Attributes.MAX_HEALTH);
        if (instance != null) {
          instance.removeModifier(GOLD_GUARD_UUID);
        }
      }
    }
  }

  @Override
  public void onEquipmentChange(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context, EquipmentSlot slotType) {
    // adding a helmet? activate bonus
    EquipmentSlot changed = context.getChangedSlot();
    if (slotType == EquipmentSlot.HEAD && changed.getType() == Type.ARMOR) {
      LivingEntity living = context.getEntity();
      boolean hasGold = ChrysophiliteModifier.hasGold(context, changed);
      context.getTinkerData().ifPresent(data -> data.computeIfAbsent(TOTAL_GOLD).setGold(changed, hasGold, living));
    }
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry entry, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    if (player != null && tooltipKey == TooltipKey.SHIFT) {
      AttributeInstance instance = player.getAttribute(Attributes.MAX_HEALTH);
      if (instance != null) {
        AttributeModifier modifier = instance.getModifier(GOLD_GUARD_UUID);
        if (modifier != null) {
          tooltip.add(applyStyle(Component.literal(Util.BONUS_FORMAT.format(modifier.getAmount()) + " ")
                                   .append(Component.translatable(getTranslationKey() + "." + "health"))));
        }
      }
    }
  }

  /** Internal logic to update gold on the player */
  private static class GoldGuardGold extends ChrysophiliteModifier.TotalGold {
    /** Adds the health boost to the player */
    private void updateAttribute(LivingEntity living) {
      // update attribute
      AttributeInstance instance = living.getAttribute(Attributes.MAX_HEALTH);
      if (instance != null) {
        if (instance.getModifier(GOLD_GUARD_UUID) != null) {
          instance.removeModifier(GOLD_GUARD_UUID);
        }
        // +2 hearts per level, and a bonus of 2 for having the modifier
        instance.addTransientModifier(new AttributeModifier(GOLD_GUARD_UUID, "tconstruct.gold_guard", getTotalGold() * 4, Operation.ADDITION));
      }
    }

    /** Sets the slot to having gold or not and updates the attribute */
    public void setGold(EquipmentSlot slotType, boolean value, LivingEntity living) {
      if (setGold(slotType, value)) {
        updateAttribute(living);
      }
    }

    @Override
    public void initialize(EquipmentChangeContext context) {
      super.initialize(context);
      updateAttribute(context.getEntity());
    }
  }
}
