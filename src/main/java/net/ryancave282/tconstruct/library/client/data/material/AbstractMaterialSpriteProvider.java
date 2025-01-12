package net.ryancave282.tconstruct.library.client.data.material;

import com.google.common.collect.ImmutableSet;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.resources.ResourceLocation;
import net.ryancave282.tconstruct.library.client.data.spritetransformer.IColorMapping;
import net.ryancave282.tconstruct.library.client.data.spritetransformer.ISpriteTransformer;
import net.ryancave282.tconstruct.library.client.data.spritetransformer.RecolorSpriteTransformer;
import net.ryancave282.tconstruct.library.client.materials.MaterialRenderInfoJson.MaterialGeneratorJson;
import net.ryancave282.tconstruct.library.materials.MaterialRegistry;
import net.ryancave282.tconstruct.library.materials.definition.MaterialId;
import net.ryancave282.tconstruct.library.materials.definition.MaterialVariantId;
import net.ryancave282.tconstruct.library.materials.stats.MaterialStatType;
import net.ryancave282.tconstruct.library.materials.stats.MaterialStatsId;
import net.ryancave282.tconstruct.tools.data.sprite.TinkerPartSpriteProvider;
import net.ryancave282.tconstruct.tools.stats.GripMaterialStats;
import net.ryancave282.tconstruct.tools.stats.HandleMaterialStats;
import net.ryancave282.tconstruct.tools.stats.HeadMaterialStats;
import net.ryancave282.tconstruct.tools.stats.LimbMaterialStats;
import net.ryancave282.tconstruct.tools.stats.PlatingMaterialStats;
import net.ryancave282.tconstruct.tools.stats.StatlessMaterialStats;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Logic for getting lists of materials for generating sprites, for use in {@link MaterialPartTextureGenerator}
 */
public abstract class AbstractMaterialSpriteProvider {
  /** All materials to generate */
  private final Map<ResourceLocation, MaterialSpriteInfoBuilder> materialBuilders = new HashMap<>();
  /** List of built materials */
  private Map<ResourceLocation, MaterialSpriteInfo> builtMaterials = null;

  /** Gets the name of this material list */
  public abstract String getName();

  /** Adds all materials to the list */
  protected abstract void addAllMaterials();

  /** Gets a list of all materials for this provider */
  public Map<ResourceLocation, MaterialSpriteInfo> getMaterials() {
    if (builtMaterials == null) {
      addAllMaterials();
      builtMaterials = materialBuilders.values().stream().map(MaterialSpriteInfoBuilder::build).collect(Collectors.toMap(MaterialSpriteInfo::getTexture, Function.identity()));
      materialBuilders.clear();
    }
    return builtMaterials;
  }

  /** Gets the info for the given material */
  @Nullable
  public MaterialSpriteInfo getMaterialInfo(ResourceLocation name) {
    return getMaterials().get(name);
  }

  /** Adds a new texture to the data generator */
  protected MaterialSpriteInfoBuilder buildMaterial(ResourceLocation name) {
    if (builtMaterials != null) {
      throw new IllegalStateException("Attempted to add a material when materials already built");
    }
    return materialBuilders.computeIfAbsent(name, MaterialSpriteInfoBuilder::new);
  }

  /** Adds a new material to the data generator */
  protected MaterialSpriteInfoBuilder buildMaterial(MaterialId name) {
    return buildMaterial((ResourceLocation)name);
  }

  /** Adds a new material variant to the data generator */
  protected MaterialSpriteInfoBuilder buildMaterial(MaterialVariantId name) {
    return buildMaterial(name.getLocation('_'));
  }

  /** Data for material rendering */
  public static class MaterialSpriteInfo extends MaterialGeneratorJson {
    /** Material texture name for the material */
    @Getter
    private transient final ResourceLocation texture;
    /** List of fallbacks, first present one will be the base for building. If none exist, uses the default base */
    @Getter
    private transient final String[] fallbacks;

    public MaterialSpriteInfo(ResourceLocation texture, String[] fallbacks, MaterialGeneratorJson generatorJson) {
      super(generatorJson);
      this.texture = texture;
      this.fallbacks = fallbacks;
    }

    public MaterialSpriteInfo(ResourceLocation texture, String[] fallbacks, ISpriteTransformer transformer, Set<MaterialStatsId> supportedStats) {
      super(transformer, supportedStats, false);
      this.texture = texture;
      this.fallbacks = fallbacks;
    }

    @Override
    public boolean supportStatType(MaterialStatsId statType) {
      if (super.supportStatType(statType)) {
        return true;
      }
      // if material registry is loaded and we are not ignoring it, allow checking that
      if (!ignoreMaterialStats && MaterialRegistry.isFullyLoaded()) {
        return  MaterialRegistry.getInstance().getMaterialStats(new MaterialId(texture), statType).isPresent();
      }
      return false;
    }
  }

  /** Builder for material sprite info */
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  protected static class MaterialSpriteInfoBuilder {
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    private final ResourceLocation texture;
    private String[] fallbacks = EMPTY_STRING_ARRAY;
    private final ImmutableSet.Builder<MaterialStatsId> statTypes = ImmutableSet.builder();

    /** Transformer to modify textures */
    @Setter @Accessors(fluent = true)
    private ISpriteTransformer transformer;

    /** Sets the fallbacks */
    public MaterialSpriteInfoBuilder fallbacks(String... fallbacks) {
      this.fallbacks = fallbacks;
      return this;
    }

    /** Sets the transformer to a color mapping transform */
    @CanIgnoreReturnValue
    public MaterialSpriteInfoBuilder colorMapper(IColorMapping mapping) {
      return transformer(new RecolorSpriteTransformer(mapping));
    }

    /** Adds a stat type as supported */
    public MaterialSpriteInfoBuilder statType(MaterialStatsId statsId) {
      statTypes.add(statsId);
      return this;
    }

    /** Adds a stat type as supported */
    public MaterialSpriteInfoBuilder statType(MaterialStatsId... statsId) {
      statTypes.add(statsId);
      return this;
    }

    /** Adds repair kits */
    public MaterialSpriteInfoBuilder repairKit() {
      return statType(StatlessMaterialStats.REPAIR_KIT.getIdentifier());
    }

    /** Adds stat types for melee and harvest tools - head, handle and extra */
    public MaterialSpriteInfoBuilder meleeHarvest() {
      statType(HeadMaterialStats.ID);
      statType(HandleMaterialStats.ID);
      statType(StatlessMaterialStats.BINDING.getIdentifier());
      repairKit();
      return this;
    }

    /** Adds stat types for ranged tools - includes limb and grip */
    public MaterialSpriteInfoBuilder ranged() {
      statType(LimbMaterialStats.ID);
      statType(GripMaterialStats.ID);
      repairKit();
      return this;
    }

    /** Adds stat types for maille */
    public MaterialSpriteInfoBuilder maille() {
      statType(StatlessMaterialStats.MAILLE.getIdentifier());
      statType(TinkerPartSpriteProvider.ARMOR_MAILLE);
      return this;
    }

    /** Adds stat types for armor, all plating plus maille */
    public MaterialSpriteInfoBuilder armor() {
      statType(TinkerPartSpriteProvider.ARMOR_PLATING);
      for (MaterialStatType<?> type : PlatingMaterialStats.TYPES) {
        statType(type.getId());
      }
      maille();
      repairKit();
      return this;
    }

    /** Makes this work as the wood part for a shield */
    public MaterialSpriteInfoBuilder shieldCore() {
      return statType(StatlessMaterialStats.SHIELD_CORE.getIdentifier());
    }

    /** Builds a material sprite info */
    private MaterialSpriteInfo build() {
      if (transformer == null) {
        throw new IllegalStateException("Material must have a transformer for a sprite provider");
      }
      Set<MaterialStatsId> supportedStats = this.statTypes.build();
      if (supportedStats.isEmpty()) {
        throw new IllegalStateException("Material must support at least one stat type");
      }
      return new MaterialSpriteInfo(texture, fallbacks, transformer, supportedStats);
    }
  }
}
