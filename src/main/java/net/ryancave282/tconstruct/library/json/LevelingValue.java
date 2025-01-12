package net.ryancave282.tconstruct.library.json;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;

/**
 * Represents a float value that has a part that scales with level and a part that does not scale.
 * @param flat      Value that does not change with level
 * @param eachLevel  Value that changes each level
 * @see RandomLevelingValue
 */
public record LevelingValue(float flat, float eachLevel) {
  /** Loadable instance for parsing */
  public static final RecordLoadable<LevelingValue> LOADABLE = RecordLoadable.create(
    FloatLoadable.ANY.defaultField("flat", 0f, LevelingValue::flat),
    FloatLoadable.ANY.defaultField("each_level", 0f, LevelingValue::eachLevel),
    LevelingValue::new);

  /** Computes the value for the given level */
  public float compute(float level) {
    return this.flat + this.eachLevel * level;
  }

  /** Computes the value for the given level but returns 0 if level is 0 */
  public float computeForLevel(float level) {
    return level > 0 ? compute(level) : 0;
  }


  /* JSON */

  /** @deprecated use {@link #LOADABLE} with {@link RecordLoadable#serialize(Object, JsonObject)} (JsonObject)} */
  @Deprecated(forRemoval = true)
  public JsonObject serialize(JsonObject json) {
    LOADABLE.serialize(this, json);
    return json;
  }

  /** @deprecated use {@link #LOADABLE} with {@link RecordLoadable#deserialize(JsonObject)} */
  @Deprecated(forRemoval = true)
  public static LevelingValue deserialize(JsonObject json) {
    return LOADABLE.deserialize(json);
  }


  /* Network */

  /** @deprecated use {@link #LOADABLE} with {@link slimeknights.mantle.data.loadable.Loadable#encode(FriendlyByteBuf, Object)} */
  @Deprecated(forRemoval = true)
  public void toNetwork(FriendlyByteBuf buffer) {
    LOADABLE.encode(buffer, this);
  }

  /** @deprecated use {@link #LOADABLE} with {@link slimeknights.mantle.data.loadable.Loadable#decode(FriendlyByteBuf)} */
  @Deprecated(forRemoval = true)
  public static LevelingValue fromNetwork(FriendlyByteBuf buffer) {
    return LOADABLE.decode(buffer);
  }


  /* Construction */

  /** Creates a value that stays constant each level */
  public static LevelingValue flat(float flat) {
    return new LevelingValue(flat, 0);
  }

  /** Creates a value that is multiplied by the level */
  public static LevelingValue eachLevel(float eachLevel) {
    return new LevelingValue(0, eachLevel);
  }

  /** Trait to mix into a builder using leveling values */
  public interface Builder<M> {
    /** Creates an instance with a flat value and a leveling value*/
    M amount(float flat, float eachLevel);

    /** Creates an instance with a value that ignores level */
    default M flat(float flat) {
      return amount(flat, 0);
    }

    /** Creates an instance with a value that increases each level */
    default M eachLevel(float eachLevel) {
      return amount(0, eachLevel);
    }
  }
}
