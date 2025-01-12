package net.ryancave282.tconstruct.library.modifiers.modules.util;

import net.minecraft.util.Mth;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;

/** Helper to handle effective levels in a usecase that requires int levels */
public interface IntLevelModule {
  LoadableField<Integer,IntLevelModule> FIELD = IntLoadable.ANY_SHORT.defaultField("level", 1, true, IntLevelModule::level);

  /** Level of the leveling thing */
  int level();

  /** Gets the level to use for the module */
  default int getLevel(ModifierEntry modifier) {
    return Mth.floor(modifier.getEffectiveLevel() * level());
  }
}
