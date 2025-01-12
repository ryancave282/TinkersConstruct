package net.ryancave282.tconstruct.plugin.jsonthings;

import dev.gigaherz.jsonthings.things.serializers.FlexItemType;
import dev.gigaherz.jsonthings.things.serializers.IItemSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.util.Lazy;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.util.JsonHelper;
import net.ryancave282.tconstruct.TConstruct;
import net.ryancave282.tconstruct.library.client.armor.texture.ArmorTextureSupplier;
import net.ryancave282.tconstruct.library.materials.stats.MaterialStatsId;
import net.ryancave282.tconstruct.library.tools.definition.ToolDefinition;
import net.ryancave282.tconstruct.library.tools.item.armor.DummyArmorMaterial;
import net.ryancave282.tconstruct.plugin.jsonthings.item.FlexModifiableBowItem;
import net.ryancave282.tconstruct.plugin.jsonthings.item.FlexModifiableCrossbowItem;
import net.ryancave282.tconstruct.plugin.jsonthings.item.FlexModifiableItem;
import net.ryancave282.tconstruct.plugin.jsonthings.item.FlexPartCastItem;
import net.ryancave282.tconstruct.plugin.jsonthings.item.FlexRepairKitItem;
import net.ryancave282.tconstruct.plugin.jsonthings.item.FlexToolPartItem;
import net.ryancave282.tconstruct.plugin.jsonthings.item.IMaterialItemFactory;
import net.ryancave282.tconstruct.plugin.jsonthings.item.IToolItemFactory;
import net.ryancave282.tconstruct.plugin.jsonthings.item.armor.FlexModifiableArmorItem;
import net.ryancave282.tconstruct.plugin.jsonthings.item.armor.FlexMultilayerArmorModel;

import java.util.ArrayList;
import java.util.List;

/** Collection of custom item types added by Tinkers */
@SuppressWarnings("unused")
public class FlexItemTypes {
  /** Standard tools that need standard properties */
  static final List<Item> TOOL_ITEMS = new ArrayList<>();
  /** All crossbow items that need their predicate registered */
  static final List<Item> CROSSBOW_ITEMS = new ArrayList<>();
  /** All armor items that need the broken predicate */
  static final List<Item> ARMOR_ITEMS = new ArrayList<>();

  /** Adds a thing to a list so we can fetch the instances later */
  private static <T> T add(List<? super T> list, T item) {
    list.add(item);
    return item;
  }

  /** Initializes the item types */
  public static void init() {
    /* Register a tool part to create new tools */
    register("tool_part", data -> {
      MaterialStatsId statType = new MaterialStatsId(JsonHelper.getResourceLocation(data, "stat_type"));
      return (IMaterialItemFactory<FlexToolPartItem>)(props, builder) -> new FlexToolPartItem(props, statType);
    });

    /* Register an item that can be used to repair tools */
    register("repair_kit", data -> {
      float repairAmount = GsonHelper.getAsFloat(data, "repair_amount");
      return (IMaterialItemFactory<FlexRepairKitItem>)(props, builder) -> new FlexRepairKitItem(props, repairAmount);
    });

    /* Register a modifiable tool instance for melee/harvest tools */
    register("tool", data -> {
      boolean breakBlocksInCreative = GsonHelper.getAsBoolean(data, "break_blocks_in_creative", true);
      return (IToolItemFactory<FlexModifiableItem>)(props, builder) -> add(TOOL_ITEMS, new FlexModifiableItem(props, ToolDefinition.create(builder.getRegistryName()), breakBlocksInCreative));
    });

    /* Register a modifiable tool instance for bow like items (release on finish) */
    register("bow", data -> (IToolItemFactory<FlexModifiableBowItem>)(props, builder) -> add(TOOL_ITEMS, new FlexModifiableBowItem(props, ToolDefinition.create(builder.getRegistryName()))));

    /* Register a modifiable tool instance for crossbow like items (load on finish) */
    register("crossbow", data -> {
      boolean allowFireworks = GsonHelper.getAsBoolean(data, "allow_fireworks");
      return (IToolItemFactory<FlexModifiableCrossbowItem>)(props, builder) -> add(CROSSBOW_ITEMS, new FlexModifiableCrossbowItem(props, ToolDefinition.create(builder.getRegistryName()), allowFireworks));
    });

    /* Registries a cast item that shows a part cost in the tooltip */
    register("part_cast", data -> {
      ResourceLocation partId = JsonHelper.getResourceLocation(data, "part");
      return (props, builder) -> new FlexPartCastItem(props, builder, Lazy.of(() -> Loadables.ITEM.fromKey(partId, "part")));
    });


    /* Armor */

    /* Simple armor type with a flat texture */
    register("basic_armor", data -> {
      ResourceLocation name = JsonHelper.getResourceLocation(data, "texture_name");
      SoundEvent sound = Loadables.SOUND_EVENT.getOrDefault(data, "equip_sound", SoundEvents.ARMOR_EQUIP_GENERIC);
      ArmorItem.Type slot = JsonHelper.getAsEnum(data, "slot", ArmorItem.Type.class);
      return (IToolItemFactory<FlexModifiableArmorItem>)(props, builder) -> add(ARMOR_ITEMS, new FlexModifiableArmorItem(new DummyArmorMaterial(name, sound), slot, props, ToolDefinition.create(builder.getRegistryName())));
    });

    /* Layered armor type, used for golden, dyeable, etc */
    Loadable<List<ArmorTextureSupplier>> ARMOR_TEXTURES = ArmorTextureSupplier.LOADER.list(1);
    register("multilayer_armor", data -> {
      ResourceLocation name = JsonHelper.getResourceLocation(data, "model_name");
      SoundEvent sound = Loadables.SOUND_EVENT.getOrDefault(data, "equip_sound", SoundEvents.ARMOR_EQUIP_GENERIC);
      ArmorItem.Type slot = JsonHelper.getAsEnum(data, "slot", ArmorItem.Type.class);
      return (IToolItemFactory<FlexMultilayerArmorModel>)(props, builder) -> add(ARMOR_ITEMS, new FlexMultilayerArmorModel(new DummyArmorMaterial(name, sound), slot, props, ToolDefinition.create(builder.getRegistryName())));
    });
  }

  /** Local helper to register our stuff */
  private static <T extends Item> void register(String name, IItemSerializer<T> factory) {
    FlexItemType.register(TConstruct.resourceString(name), factory);
  }
}
