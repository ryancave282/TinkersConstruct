package net.ryancave282.tconstruct.tools.data;

import net.minecraft.data.PackOutput;
import net.ryancave282.tconstruct.library.client.armor.texture.ArmorTextureSupplier;
import net.ryancave282.tconstruct.library.client.armor.texture.DyedArmorTextureSupplier;
import net.ryancave282.tconstruct.library.client.armor.texture.FirstArmorTextureSupplier;
import net.ryancave282.tconstruct.library.client.armor.texture.FixedArmorTextureSupplier;
import net.ryancave282.tconstruct.library.client.armor.texture.MaterialArmorTextureSupplier;
import net.ryancave282.tconstruct.library.client.armor.texture.TrimArmorTextureSupplier;
import net.ryancave282.tconstruct.library.client.data.AbstractArmorModelProvider;
import net.ryancave282.tconstruct.tools.ArmorDefinitions;
import net.ryancave282.tconstruct.tools.TinkerModifiers;
import net.ryancave282.tconstruct.tools.data.material.MaterialIds;

public class ArmorModelProvider extends AbstractArmorModelProvider {
  public ArmorModelProvider(PackOutput packOutput) {
    super(packOutput);
  }

  @Override
  protected void addModels() {
    addModel(ArmorDefinitions.TRAVELERS, name -> new ArmorTextureSupplier[] {
      new FirstArmorTextureSupplier(
        FixedArmorTextureSupplier.builder(name, "/golden_").modifier(TinkerModifiers.golden.getId()).build(),
        FixedArmorTextureSupplier.builder(name, "/base_").build()),
      new DyedArmorTextureSupplier(name, "/overlay_", TinkerModifiers.dyed.getId(), null),
      TrimArmorTextureSupplier.INSTANCE
    });
    addModel(ArmorDefinitions.PLATE, name -> new ArmorTextureSupplier[] {
      new MaterialArmorTextureSupplier.Material(name, "/plating_", 0),
      new MaterialArmorTextureSupplier.Material(name, "/maille_", 1),
      TrimArmorTextureSupplier.INSTANCE
    });
    addModel(ArmorDefinitions.SLIMESUIT, name -> new ArmorTextureSupplier[] {
      new FirstArmorTextureSupplier(
        FixedArmorTextureSupplier.builder(name, "/").materialSuffix(MaterialIds.gold).modifier(TinkerModifiers.golden.getId()).build(),
        new MaterialArmorTextureSupplier.PersistentData(name, "/", TinkerModifiers.embellishment.getId()),
        FixedArmorTextureSupplier.builder(name, "/").materialSuffix(MaterialIds.enderslime).build()),
      TrimArmorTextureSupplier.INSTANCE
    });
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Armor Models";
  }
}
