package net.ryancave282.tconstruct.library.materials.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.mantle.util.typed.TypedMapBuilder;
import net.ryancave282.tconstruct.library.materials.MaterialRegistry;
import net.ryancave282.tconstruct.library.materials.definition.MaterialId;
import net.ryancave282.tconstruct.library.utils.Util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class UpdateMaterialStatsPacket implements IThreadsafePacket {
  private static final Logger log = Util.getLogger("NetworkSync");

  protected final Map<MaterialId, Collection<IMaterialStats>> materialToStats;

  public UpdateMaterialStatsPacket(FriendlyByteBuf buffer) {
    this(buffer, MaterialRegistry.getInstance().getStatTypeLoader());
  }

  public UpdateMaterialStatsPacket(FriendlyByteBuf buffer, Loadable<MaterialStatType<?>> statTypeLoader) {
    int materialCount = buffer.readInt();
    materialToStats = new HashMap<>(materialCount);
    for (int i = 0; i < materialCount; i++) {
      MaterialId id = new MaterialId(buffer.readResourceLocation());
      int statCount = buffer.readInt();
      List<IMaterialStats> statList = new ArrayList<>();
      for (int j = 0; j < statCount; j++) {
        try {
          MaterialStatType<?> statType = statTypeLoader.decode(buffer);
          statList.add(statType.getLoadable().decode(buffer, TypedMapBuilder.builder().put(MaterialStatType.CONTEXT_KEY, statType).build()));
        } catch (Exception e) {
          log.error("Could not deserialize stat. Are client and server in sync?", e);
        }
      }
      materialToStats.put(id, statList);
    }
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeInt(materialToStats.size());
    materialToStats.forEach((materialId, stats) -> {
      buffer.writeResourceLocation(materialId);
      buffer.writeInt(stats.size());
      stats.forEach(stat -> encodeStat(buffer, stat, stat.getType()));
    });
  }

  /**
   * Encodes a single material stat
   * @param buffer  Buffer instance
   * @param stat    Stat to encode
   */
  @SuppressWarnings("unchecked")
  private <T extends IMaterialStats> void encodeStat(FriendlyByteBuf buffer, IMaterialStats stat, MaterialStatType<T> type) {
    MaterialStatsId.PARSER.encode(buffer, type.getId());
    type.getLoadable().encode(buffer, (T) stat);
  }

  @Override
  public void handleThreadsafe(Context context) {
    MaterialRegistry.updateMaterialStatsFromServer(this);
  }
}
