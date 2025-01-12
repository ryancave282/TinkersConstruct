package net.ryancave282.tconstruct.common.data.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraftforge.client.model.generators.CustomLoaderBuilder;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.ryancave282.tconstruct.TConstruct;

public class MaterialModelBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T> {
  private JsonArray offset = null;
  public MaterialModelBuilder(T parent, ExistingFileHelper existingFileHelper) {
    super(TConstruct.getResource("material"), parent, existingFileHelper);
  }

  /** Sets the offset */
  public MaterialModelBuilder<T> offset(int x, int y) {
    this.offset = new JsonArray(2);
    this.offset.add(x);
    this.offset.add(y);
    return this;
  }

  @Override
  public JsonObject toJson(JsonObject json) {
    super.toJson(json);
    if (offset != null) {
      json.add("offset", offset);
    }
    return json;
  }
}
