package net.ryancave282.tconstruct.library.data.tinkering;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.ryancave282.tconstruct.library.data.AbstractTagProvider;
import net.ryancave282.tconstruct.library.materials.definition.IMaterial;
import net.ryancave282.tconstruct.library.materials.definition.MaterialManager;

/** Tag provider for materials */
public abstract class AbstractMaterialTagProvider extends AbstractTagProvider<IMaterial> {
  protected AbstractMaterialTagProvider(PackOutput packOutput, String modId, ExistingFileHelper existingFileHelper) {
    super(packOutput, modId, MaterialManager.TAG_FOLDER, IMaterial::getIdentifier, id -> true, existingFileHelper);
  }
}
