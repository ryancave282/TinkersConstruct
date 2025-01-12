package net.ryancave282.tconstruct.tools.modifiers.traits.skull;

import lombok.Getter;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.ryancave282.tconstruct.TConstruct;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.modifiers.ModifierHooks;
import net.ryancave282.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import net.ryancave282.tconstruct.library.modifiers.impl.NoLevelsModifier;
import net.ryancave282.tconstruct.library.module.ModuleHookMap.Builder;
import net.ryancave282.tconstruct.library.tools.capability.TinkerDataCapability;
import net.ryancave282.tconstruct.library.tools.capability.TinkerDataCapability.ComputableDataKey;
import net.ryancave282.tconstruct.library.tools.context.EquipmentChangeContext;
import net.ryancave282.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import net.ryancave282.tconstruct.library.tools.item.armor.ModifiableArmorItem;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.Optional;

public class ChrysophiliteModifier extends NoLevelsModifier implements EquipmentChangeModifierHook {
  public static final ComputableDataKey<TotalGold> TOTAL_GOLD = TConstruct.createKey("chrysophilite", TotalGold::new);
  public ChrysophiliteModifier() {
    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, LivingDropsEvent.class, ChrysophiliteModifier::onLivingDrops);
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ModifierHooks.EQUIPMENT_CHANGE);
  }

  @Override
  public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    // adding a helmet? activate bonus
    if (context.getChangedSlot() == EquipmentSlot.HEAD) {
      context.getTinkerData().ifPresent(data -> {
        TotalGold gold = data.get(TOTAL_GOLD);
        if (gold == null) {
          data.computeIfAbsent(TOTAL_GOLD).initialize(context);
        } else {
          gold.setGold(EquipmentSlot.HEAD, tool.getVolatileData().getBoolean(ModifiableArmorItem.PIGLIN_NEUTRAL));
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
      }
    }
  }

  @Override
  public void onEquipmentChange(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context, EquipmentSlot slotType) {
    // adding a helmet? activate bonus
    EquipmentSlot changed = context.getChangedSlot();
    if (slotType == EquipmentSlot.HEAD && changed.getType() == Type.ARMOR) {
      boolean hasGold = ChrysophiliteModifier.hasGold(context, changed);
      context.getTinkerData().ifPresent(data -> data.computeIfAbsent(TOTAL_GOLD).setGold(changed, hasGold));
    }
  }

  /** Checks if the entity has gold in the given slot */
  public static boolean hasGold(EquipmentChangeContext context, EquipmentSlot slotType) {
    IToolStackView tool = context.getToolInSlot(slotType);
    if (tool != null) {
      return tool.getVolatileData().getBoolean(ModifiableArmorItem.PIGLIN_NEUTRAL);
    } else {
      LivingEntity living = context.getEntity();
      return living.getItemBySlot(slotType).makesPiglinsNeutral(living);
    }
  }

  /** Gets the level of the modifier on an entity */
  public static int getTotalGold(@Nullable Entity entity) {
    return Optional.ofNullable(entity)
                   .flatMap(e -> e.getCapability(TinkerDataCapability.CAPABILITY).resolve())
                   .map(data -> data.get(ChrysophiliteModifier.TOTAL_GOLD))
                   .map(TotalGold::getTotalGold)
                   .orElse(0);
  }

  /** Causes more gold armor to drop */
  private static void onLivingDrops(LivingDropsEvent event) {
    DamageSource source = event.getSource();
    if (source != null) {
      int gold = getTotalGold(source.getEntity());
      if (gold > 0) {
        float extraChance = 0.04f * gold;
        LivingEntity target = event.getEntity();
        // check each slot for gold
        for (EquipmentSlot slot : EquipmentSlot.values()) {
          ItemStack stack = target.getItemBySlot(slot);
          RandomSource random = target.getRandom();
          // if the stack is gold, and it drops, we get it
          // don't have to worry about checking if it already dropped, the stacks are removed on drop
          if (!stack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(stack) && stack.makesPiglinsNeutral(target) && random.nextFloat() < extraChance) {
            // mobs damage items, its kinda weird
            if (stack.isDamageableItem()) {
              stack.setDamageValue(stack.getMaxDamage() - random.nextInt(1 + random.nextInt(Math.max(stack.getMaxDamage() - 3, 1))));
            }
            // remove stack to prevent further drops
            event.getDrops().add(target.spawnAtLocation(stack));
            target.setItemSlot(slot, ItemStack.EMPTY);
          }
        }
      }
    }
  }

  /** Tracker to count how many slots contain gold */
  public static class TotalGold {
    private final boolean[] hasGold = new boolean[4];
    /** Gold value of the modifier, will be 1 for the modifier, and +1 for each golden armor piece */
    @Getter
    private int totalGold = 0;

    /**
     * Updates the status of gold in a slot on the entity
     * @param slotType  Slot to update
     * @param value     New value
     */
    protected boolean setGold(EquipmentSlot slotType, boolean value) {
      if (slotType.getType() == Type.ARMOR) {
        int index = slotType.getIndex();
        if (hasGold[index] != value) {
          hasGold[index] = value;
          if (value) {
            totalGold++;
          } else {
            totalGold--;
          }
          return true;
        }
      }
      return false;
    }

    /** Initializes the gold data */
    public void initialize(EquipmentChangeContext context) {
      totalGold = 1;
      for (EquipmentSlot slotType : ModifiableArmorMaterial.ARMOR_SLOTS) {
        boolean gold = hasGold(context, slotType);
        hasGold[slotType.getIndex()] = gold;
        if (gold) {
          totalGold++;
        }
      }
    }
  }
}
