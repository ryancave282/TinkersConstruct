package net.ryancave282.tconstruct.library.modifiers.modules.combat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.primitive.StringLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import net.ryancave282.tconstruct.library.json.LevelingValue;
import net.ryancave282.tconstruct.library.json.TinkerLoadables;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.modifiers.ModifierHooks;
import net.ryancave282.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import net.ryancave282.tconstruct.library.modifiers.modules.ModifierModule;
import net.ryancave282.tconstruct.library.modifiers.modules.util.ModifierCondition;
import net.ryancave282.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import net.ryancave282.tconstruct.library.modifiers.modules.util.ModuleBuilder;
import net.ryancave282.tconstruct.library.module.HookProvider;
import net.ryancave282.tconstruct.library.module.ModuleHook;
import net.ryancave282.tconstruct.library.tools.context.ToolAttackContext;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * Adds an attribute modifier to the mob before hitting, then removes the modifier after hitting.
 * @param unique     Unique string used to generate the UUID and as the attribute name
 * @param attribute  Attribute to apply
 * @param uuid       UUID generated via {@link UUID#nameUUIDFromBytes(byte[])}
 * @param operation  Attribute operation
 * @param amount     Amount of the attribute to apply
 * @param condition  Standard modifier conditions
 */
public record MeleeAttributeModule(String unique, Attribute attribute, UUID uuid, Operation operation, LevelingValue amount, ModifierCondition<IToolStackView> condition) implements ModifierModule, MeleeHitModifierHook, ConditionalModule<IToolStackView> {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<MeleeAttributeModule>defaultHooks(ModifierHooks.MELEE_HIT);
  public static final RecordLoadable<MeleeAttributeModule> LOADER = RecordLoadable.create(
    StringLoadable.DEFAULT.requiredField("unique", MeleeAttributeModule::unique),
    Loadables.ATTRIBUTE.requiredField("attribute", MeleeAttributeModule::attribute),
    TinkerLoadables.OPERATION.requiredField("operation", MeleeAttributeModule::operation),
    LevelingValue.LOADABLE.directField(MeleeAttributeModule::amount),
    ModifierCondition.TOOL_FIELD,
    MeleeAttributeModule::new);

  public MeleeAttributeModule(String unique, Attribute attribute, Operation operation, LevelingValue amount, ModifierCondition<IToolStackView> condition) {
    this(unique, attribute, UUID.nameUUIDFromBytes(unique.getBytes()), operation, amount, condition);
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
    if (condition.matches(tool, modifier)) {
      LivingEntity target = context.getLivingTarget();
      if (target != null) {
        AttributeInstance instance = target.getAttribute(attribute);
        if (instance != null) {
          instance.addTransientModifier(new AttributeModifier(uuid, unique, amount.compute(modifier.getEffectiveLevel()), operation));
        }
      }
    }
    return knockback;
  }

  private void removeAttribute(@Nullable LivingEntity target) {
    if (target != null) {
      AttributeInstance instance = target.getAttribute(attribute);
      if (instance != null) {
        instance.removeModifier(uuid);
      }
    }
  }

  @Override
  public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
    removeAttribute(context.getLivingTarget());
  }

  @Override
  public void failedMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageAttempted) {
    removeAttribute(context.getLivingTarget());
  }

  @Override
  public RecordLoadable<MeleeAttributeModule> getLoader() {
    return LOADER;
  }


  /** Creates a new builder instance */
  public static Builder builder(Attribute attribute, Operation operation) {
    return new Builder(attribute, operation);
  }

  @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
  public static class Builder extends ModuleBuilder.Stack<Builder> implements LevelingValue.Builder<MeleeAttributeModule>  {
    protected final Attribute attribute;
    protected final Operation operation;
    protected String unique;

    /**
     * Sets the unique string directly
     */
    public Builder unique(String unique) {
      this.unique = unique;
      return this;
    }

    /**
     * Sets the unique string using a resource location
     */
    public Builder uniqueFrom(ResourceLocation id) {
      return unique(id.getNamespace() + ".modifier." + id.getPath());
    }

    @Override
    public MeleeAttributeModule amount(float flat, float eachLevel) {
      if (unique == null) {
        throw new IllegalStateException("Must set unique for attributes");
      }
      return new MeleeAttributeModule(unique, attribute, operation, new LevelingValue(flat, eachLevel), condition);
    }
  }
}
