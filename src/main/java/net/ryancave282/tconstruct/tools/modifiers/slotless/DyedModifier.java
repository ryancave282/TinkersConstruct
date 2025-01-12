package net.ryancave282.tconstruct.tools.modifiers.slotless;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.ryancave282.tconstruct.TConstruct;
import net.ryancave282.tconstruct.library.modifiers.Modifier;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.modifiers.ModifierHooks;
import net.ryancave282.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import net.ryancave282.tconstruct.library.modifiers.impl.NoLevelsModifier;
import net.ryancave282.tconstruct.library.module.ModuleHookMap.Builder;
import net.ryancave282.tconstruct.library.tools.nbt.IModDataView;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

public class DyedModifier extends NoLevelsModifier implements ModifierRemovalHook {
  private static final String FORMAT_KEY = TConstruct.makeTranslationKey("modifier", "dyed.formatted");

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ModifierHooks.REMOVE);
  }

  @Override
  public Component getDisplayName(IToolStackView tool, ModifierEntry entry, @Nullable RegistryAccess access) {
    IModDataView persistentData = tool.getPersistentData();
    ResourceLocation key = getId();
    if (persistentData.contains(key, Tag.TAG_INT)) {
      int color = persistentData.getInt(key);
      return applyStyle(Component.translatable(FORMAT_KEY, String.format("%06X", color)));
    }
    return super.getDisplayName();
  }

  @Nullable
  @Override
  public Component onRemoved(IToolStackView tool, Modifier modifier) {
    tool.getPersistentData().remove(getId());
    return null;
  }
}
