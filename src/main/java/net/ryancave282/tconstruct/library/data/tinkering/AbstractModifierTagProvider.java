package net.ryancave282.tconstruct.library.data.tinkering;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.ryancave282.tconstruct.library.data.AbstractTagProvider;
import net.ryancave282.tconstruct.library.modifiers.Modifier;
import net.ryancave282.tconstruct.library.modifiers.ModifierManager;

/** Tag provider to generate modifier tags */
public abstract class AbstractModifierTagProvider extends AbstractTagProvider<Modifier> {
  protected AbstractModifierTagProvider(PackOutput packOutput, String modId, ExistingFileHelper existingFileHelper) {
    // TODO: we don't fire modifier event during datagen, should we?
    super(packOutput, modId, ModifierManager.TAG_FOLDER, Modifier::getId, id -> true/*ModifierManager.INSTANCE.containsStatic(new ModifierId(id))*/, existingFileHelper);
  }
}
