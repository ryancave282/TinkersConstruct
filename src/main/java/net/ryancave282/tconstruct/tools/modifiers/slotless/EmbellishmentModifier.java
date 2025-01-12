package net.ryancave282.tconstruct.tools.modifiers.slotless;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.ryancave282.tconstruct.TConstruct;
import net.ryancave282.tconstruct.library.client.materials.MaterialTooltipCache;
import net.ryancave282.tconstruct.library.materials.MaterialRegistry;
import net.ryancave282.tconstruct.library.materials.definition.MaterialId;
import net.ryancave282.tconstruct.library.materials.definition.MaterialVariantId;
import net.ryancave282.tconstruct.library.modifiers.Modifier;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.modifiers.ModifierHooks;
import net.ryancave282.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import net.ryancave282.tconstruct.library.modifiers.hook.build.RawDataModifierHook;
import net.ryancave282.tconstruct.library.modifiers.impl.NoLevelsModifier;
import net.ryancave282.tconstruct.library.module.ModuleHookMap.Builder;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;
import net.ryancave282.tconstruct.library.tools.nbt.ModDataNBT;
import net.ryancave282.tconstruct.library.utils.RestrictedCompoundTag;

import javax.annotation.Nullable;

public class EmbellishmentModifier extends NoLevelsModifier implements ModifierRemovalHook, RawDataModifierHook {
  private static final String FORMAT_KEY = TConstruct.makeTranslationKey("modifier", "embellishment.formatted");

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ModifierHooks.REMOVE, ModifierHooks.RAW_DATA);
  }

  @Override
  public Component getDisplayName(IToolStackView tool, ModifierEntry entry, @Nullable RegistryAccess access) {
    MaterialVariantId materialVariant = MaterialVariantId.tryParse(tool.getPersistentData().getString(getId()));
    if (materialVariant != null) {
      return Component.translatable(FORMAT_KEY, MaterialTooltipCache.getDisplayName(materialVariant)).withStyle(style -> style.withColor(MaterialTooltipCache.getColor(materialVariant)));
    }
    return super.getDisplayName();
  }

  @Override
  public void addRawData(IToolStackView tool, ModifierEntry modifier, RestrictedCompoundTag tag) {
    // on build, migrate material redirects
    ModDataNBT data = tool.getPersistentData();
    ResourceLocation key = getId();
    MaterialVariantId materialVariant = MaterialVariantId.tryParse(data.getString(key));
    if (materialVariant != null) {
      MaterialId original = materialVariant.getId();
      MaterialId resolved = MaterialRegistry.getInstance().resolve(original);
      // instance check is safe here as resolve returns same instance if no redirect
      if (resolved != original) {
        data.putString(key, MaterialVariantId.create(resolved, materialVariant.getVariant()).toString());
      }
    }
  }

  @Override
  public void removeRawData(IToolStackView tool, Modifier modifier, RestrictedCompoundTag tag) {}

  @Nullable
  @Override
  public Component onRemoved(IToolStackView tool, Modifier modifier) {
    tool.getPersistentData().remove(getId());
    return null;
  }
}
