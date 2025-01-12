package net.ryancave282.tconstruct.library.json.predicate.tool;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import net.ryancave282.tconstruct.TConstruct;
import net.ryancave282.tconstruct.common.TinkerTags.Items;
import net.ryancave282.tconstruct.library.tools.nbt.IToolContext;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;
import net.ryancave282.tconstruct.library.tools.nbt.ToolStack;
import net.ryancave282.tconstruct.library.utils.JsonUtils;

/** Variant of ItemPredicate for matching Tinker tools using {@link ToolStackItemPredicate} */
@RequiredArgsConstructor(staticName = "ofTool")
public class ToolStackItemPredicate extends ItemPredicate {
  public static final ResourceLocation ID = TConstruct.getResource("tool_stack");

  private final IJsonPredicate<IToolStackView> predicate;

  public static ToolStackItemPredicate ofContext(IJsonPredicate<IToolContext> predicate) {
    return new ToolStackItemPredicate(ToolStackPredicate.context(predicate));
  }

  @Override
  public boolean matches(ItemStack stack) {
    // tag check is important to prevent accidently modifying the NBT of non-tools
    return stack.is(Items.MODIFIABLE) && predicate.matches(ToolStack.from(stack));
  }

  @Override
  public JsonElement serializeToJson() {
    JsonObject json = JsonUtils.withType(ID);
    json.add("predicate", ToolStackPredicate.LOADER.serialize(predicate));
    return json;
  }

  /** Deserializes the tool predicate from JSON */
  public static ToolStackItemPredicate deserialize(JsonObject json) {
    return new ToolStackItemPredicate(ToolStackPredicate.LOADER.getIfPresent(json, "predicate"));
  }
}
