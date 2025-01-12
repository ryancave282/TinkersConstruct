package net.ryancave282.tconstruct.library.modifiers.modules.armor;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import net.ryancave282.tconstruct.library.json.LevelingValue;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.modifiers.ModifierHooks;
import net.ryancave282.tconstruct.library.modifiers.hook.behavior.ToolActionModifierHook;
import net.ryancave282.tconstruct.library.modifiers.modules.ModifierModule;
import net.ryancave282.tconstruct.library.modifiers.modules.util.ModifierCondition;
import net.ryancave282.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import net.ryancave282.tconstruct.library.modifiers.modules.util.ModuleBuilder;
import net.ryancave282.tconstruct.library.module.HookProvider;
import net.ryancave282.tconstruct.library.module.ModuleHook;
import net.ryancave282.tconstruct.library.tools.helper.ToolDamageUtil;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;
import net.ryancave282.tconstruct.library.utils.MutableUseOnContext;
import net.ryancave282.tconstruct.library.utils.Util;
import net.ryancave282.tconstruct.tools.TinkerModifiers;

import java.util.List;

/**
 * Boot module that transforms walked on blocks using a tool action
 * @param action     Transforming action
 * @param sound      Sound to play when transforming
 * @param radius     Radius to cover
 * @param condition  Standard module condition
 */
public record ToolActionWalkerTransformModule(ToolAction action, SoundEvent sound, LevelingValue radius, ModifierCondition<IToolStackView> condition) implements ModifierModule, ArmorWalkRadiusModule<MutableUseOnContext>, ToolActionModifierHook, ConditionalModule<IToolStackView> {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<ToolActionWalkerTransformModule>defaultHooks(ModifierHooks.BOOT_WALK, ModifierHooks.TOOL_ACTION);
  public static final RecordLoadable<ToolActionWalkerTransformModule> LOADER = RecordLoadable.create(
    Loadables.TOOL_ACTION.requiredField("tool_action", ToolActionWalkerTransformModule::action),
    Loadables.SOUND_EVENT.requiredField("sound", ToolActionWalkerTransformModule::sound),
    LevelingValue.LOADABLE.requiredField("radius", ToolActionWalkerTransformModule::radius),
    ModifierCondition.TOOL_FIELD,
    ToolActionWalkerTransformModule::new);

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public float getRadius(IToolStackView tool, ModifierEntry modifier) {
    return radius.compute(modifier.getLevel() + tool.getModifierLevel(TinkerModifiers.expanded.getId()));
  }

  @Override
  public boolean canPerformAction(IToolStackView tool, ModifierEntry modifier, ToolAction toolAction) {
    return condition.matches(tool, modifier) && toolAction == this.action;
  }

  @Override
  public void onWalk(IToolStackView tool, ModifierEntry modifier, LivingEntity living, BlockPos prevPos, BlockPos newPos) {
    if (condition.matches(tool, modifier)) {
      ArmorWalkRadiusModule.super.onWalk(tool, modifier, living, prevPos, newPos);
    }
  }

  @Override
  public MutableUseOnContext getContext(IToolStackView tool, ModifierEntry modifier, LivingEntity living, BlockPos prevPos, BlockPos newPos) {
    return new MutableUseOnContext(living.level(), living instanceof Player p ? p : null, InteractionHand.MAIN_HAND, living.getItemBySlot(EquipmentSlot.FEET), Util.createTraceResult(newPos, Direction.UP, false));
  }

  @Override
  public void walkOn(IToolStackView tool, ModifierEntry entry, LivingEntity living, Level world, BlockPos target, MutableBlockPos mutable, MutableUseOnContext context) {
    BlockState state = world.getBlockState(target);
    if (state.canBeReplaced()) {
      mutable.set(target.getX(), target.getY() - 1, target.getZ());
      context.setOffsetPos(mutable);
      // transform the block
      BlockState original = world.getBlockState(mutable);
      BlockState transformed = original.getToolModifiedState(context, action, false);
      if (transformed != null) {
        world.setBlock(mutable, transformed, Block.UPDATE_ALL_IMMEDIATE);
        world.destroyBlock(target, true);
        world.playSound(null, mutable, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
        ToolDamageUtil.damageAnimated(tool, 1, living, EquipmentSlot.FEET);
      }
    }
  }

  @Override
  public RecordLoadable<ToolActionWalkerTransformModule> getLoader() {
    return LOADER;
  }
  

  /* Builder */

  /** Creates a builder instance */
  public static Builder builder(ToolAction action, SoundEvent sound) {
    return new Builder(action, sound);
  }

  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder extends ModuleBuilder.Stack<Builder> implements LevelingValue.Builder<ToolActionWalkerTransformModule> {
    private final ToolAction action;
    private final SoundEvent sound;

    @Override
    public ToolActionWalkerTransformModule amount(float flat, float eachLevel) {
      return new ToolActionWalkerTransformModule(action, sound, new LevelingValue(flat, eachLevel), condition);
    }
  }
}
