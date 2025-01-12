package net.ryancave282.tconstruct.library.materials.json;

import com.google.gson.JsonElement;
import lombok.RequiredArgsConstructor;
import net.minecraft.resources.ResourceLocation;
import net.ryancave282.tconstruct.library.materials.stats.MaterialStatsManager;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;

/**
 * This json is mostly used for automatic consistency checks and for easier deserialization.
 * The actual stats deserialization is done in {@link MaterialStatsManager}
 */
@RequiredArgsConstructor
public class MaterialStatJson {
  @Nullable
  private final Map<ResourceLocation,JsonElement> stats;

  public Map<ResourceLocation, JsonElement> getStats() {
    if (stats == null) {
      return Collections.emptyMap();
    }
    return stats;
  }
}
