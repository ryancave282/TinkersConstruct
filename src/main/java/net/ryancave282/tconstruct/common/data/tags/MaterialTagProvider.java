package net.ryancave282.tconstruct.common.data.tags;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.ryancave282.tconstruct.TConstruct;
import net.ryancave282.tconstruct.common.TinkerTags;
import net.ryancave282.tconstruct.library.data.tinkering.AbstractMaterialTagProvider;
import net.ryancave282.tconstruct.tools.data.material.MaterialIds;

public class MaterialTagProvider extends AbstractMaterialTagProvider {
  public MaterialTagProvider(PackOutput packOutput, ExistingFileHelper existingFileHelper) {
    super(packOutput, TConstruct.MOD_ID, existingFileHelper);
  }

  @Override
  protected void addTags() {
    tag(TinkerTags.Materials.NETHER).add(
      // tier 1
      MaterialIds.wood, MaterialIds.flint, MaterialIds.rock, MaterialIds.bone,
      MaterialIds.leather, MaterialIds.vine, MaterialIds.string,
      // tier 2
      MaterialIds.iron, MaterialIds.scorchedStone, MaterialIds.slimewood, MaterialIds.necroticBone,
      // tier 3
      MaterialIds.nahuatl, MaterialIds.cobalt,
      MaterialIds.darkthread,
      // tier 4
      MaterialIds.manyullyn, MaterialIds.queensSlime, MaterialIds.blazingBone, MaterialIds.blazewood,
      MaterialIds.ancientHide
    );
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Material Tag Provider";
  }
}
