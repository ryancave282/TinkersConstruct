package net.ryancave282.tconstruct.tools.modifiers.ability.tool;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.modifiers.ModifierHooks;
import net.ryancave282.tconstruct.library.modifiers.hook.interaction.BlockInteractionModifierHook;
import net.ryancave282.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import net.ryancave282.tconstruct.library.modifiers.hook.mining.RemoveBlockModifierHook;
import net.ryancave282.tconstruct.library.modifiers.impl.NoLevelsModifier;
import net.ryancave282.tconstruct.library.modifiers.modules.behavior.ShowOffhandModule;
import net.ryancave282.tconstruct.library.module.ModuleHookMap.Builder;
import net.ryancave282.tconstruct.library.tools.context.ToolHarvestContext;
import net.ryancave282.tconstruct.library.tools.definition.module.ToolHooks;
import net.ryancave282.tconstruct.library.tools.definition.module.interaction.DualOptionInteraction;
import net.ryancave282.tconstruct.library.tools.helper.ToolDamageUtil;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;
import net.ryancave282.tconstruct.shared.TinkerCommons;

import javax.annotation.Nullable;

public class GlowingModifier extends NoLevelsModifier implements BlockInteractionModifierHook, RemoveBlockModifierHook {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(ShowOffhandModule.DISALLOW_BROKEN);
    hookBuilder.addHook(this, ModifierHooks.BLOCK_INTERACT, ModifierHooks.REMOVE_BLOCK);
  }

  @Override
  public int getPriority() {
    return 75; // after bucketing
  }

  @Override
  public Component getDisplayName(IToolStackView tool, ModifierEntry entry, @Nullable RegistryAccess access) {
    return DualOptionInteraction.formatModifierName(tool, this, super.getDisplayName(tool, entry, access));
  }
  
  @Override
  public InteractionResult afterBlockUse(IToolStackView tool, ModifierEntry modifier, UseOnContext context, InteractionSource source) {
    if (tool.getCurrentDurability() >= 10 && tool.getHook(ToolHooks.INTERACTION).canInteract(tool, modifier.getId(), source)) {
      Player player = context.getPlayer();
      if (!context.getLevel().isClientSide) {
        Level world = context.getLevel();
        Direction face = context.getClickedFace();
        BlockPos pos = context.getClickedPos().relative(face);
        if (TinkerCommons.glow.get().addGlow(world, pos, face.getOpposite())) {
          // damage the tool, showing animation if relevant
          if (ToolDamageUtil.damage(tool, 10, player, context.getItemInHand()) && player != null) {
            player.broadcastBreakEvent(source.getSlot(context.getHand()));
          }
          world.playSound(null, pos, world.getBlockState(pos).getSoundType(world, pos, player).getPlaceSound(), SoundSource.BLOCKS, 1.0f, 1.0f);
        }
      }
      return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
    }
    return InteractionResult.PASS;
  }

  @Nullable
  @Override
  public Boolean removeBlock(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context) {
    if (context.getState().is(TinkerCommons.glow.get()) && tool.getHook(ToolHooks.INTERACTION).canInteract(tool, getId(), InteractionSource.LEFT_CLICK)) {
      return false;
    }
    return null;
  }
}
