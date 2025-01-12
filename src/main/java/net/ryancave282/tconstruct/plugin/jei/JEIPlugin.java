package net.ryancave282.tconstruct.plugin.jei;

import com.google.common.collect.ImmutableList;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IModIdHelper;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fml.ModList;
import slimeknights.mantle.recipe.helper.RecipeHelper;
import slimeknights.mantle.util.RetexturedHelper;
import net.ryancave282.tconstruct.common.TinkerTags;
import net.ryancave282.tconstruct.common.config.Config;
import net.ryancave282.tconstruct.fluids.TinkerFluids;
import net.ryancave282.tconstruct.fluids.fluids.PotionFluidType;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.modifiers.ModifierId;
import net.ryancave282.tconstruct.library.recipe.TinkerRecipeTypes;
import net.ryancave282.tconstruct.library.recipe.alloying.AlloyRecipe;
import net.ryancave282.tconstruct.library.recipe.casting.IDisplayableCastingRecipe;
import net.ryancave282.tconstruct.library.recipe.entitymelting.EntityMeltingRecipe;
import net.ryancave282.tconstruct.library.recipe.fuel.MeltingFuel;
import net.ryancave282.tconstruct.library.recipe.material.MaterialRecipe;
import net.ryancave282.tconstruct.library.recipe.melting.MeltingRecipe;
import net.ryancave282.tconstruct.library.recipe.modifiers.ModifierRecipeLookup;
import net.ryancave282.tconstruct.library.recipe.modifiers.adding.IDisplayModifierRecipe;
import net.ryancave282.tconstruct.library.recipe.modifiers.severing.SeveringRecipe;
import net.ryancave282.tconstruct.library.recipe.molding.MoldingRecipe;
import net.ryancave282.tconstruct.library.recipe.partbuilder.IDisplayPartBuilderRecipe;
import net.ryancave282.tconstruct.library.recipe.tinkerstation.building.ToolBuildingRecipe;
import net.ryancave282.tconstruct.library.recipe.worktable.IModifierWorktableRecipe;
import net.ryancave282.tconstruct.library.tools.SlotType;
import net.ryancave282.tconstruct.library.tools.definition.module.build.ToolTraitHook;
import net.ryancave282.tconstruct.library.tools.helper.ToolBuildHandler;
import net.ryancave282.tconstruct.library.tools.item.IModifiable;
import net.ryancave282.tconstruct.library.tools.item.IModifiableDisplay;
import net.ryancave282.tconstruct.library.tools.layout.StationSlotLayoutLoader;
import net.ryancave282.tconstruct.library.tools.nbt.MaterialNBT;
import net.ryancave282.tconstruct.library.tools.nbt.ModifierNBT;
import net.ryancave282.tconstruct.library.tools.part.IMaterialItem;
import net.ryancave282.tconstruct.plugin.jei.casting.CastingBasinCategory;
import net.ryancave282.tconstruct.plugin.jei.casting.CastingTableCategory;
import net.ryancave282.tconstruct.plugin.jei.entity.DefaultEntityMeltingRecipe;
import net.ryancave282.tconstruct.plugin.jei.entity.EntityMeltingRecipeCategory;
import net.ryancave282.tconstruct.plugin.jei.entity.SeveringCategory;
import net.ryancave282.tconstruct.plugin.jei.melting.FoundryCategory;
import net.ryancave282.tconstruct.plugin.jei.melting.MeltingCategory;
import net.ryancave282.tconstruct.plugin.jei.melting.MeltingFuelHandler;
import net.ryancave282.tconstruct.plugin.jei.modifiers.ModifierBookmarkIngredientRenderer;
import net.ryancave282.tconstruct.plugin.jei.modifiers.ModifierIngredientHelper;
import net.ryancave282.tconstruct.plugin.jei.modifiers.ModifierRecipeCategory;
import net.ryancave282.tconstruct.plugin.jei.modifiers.ModifierWorktableCategory;
import net.ryancave282.tconstruct.plugin.jei.partbuilder.MaterialItemList;
import net.ryancave282.tconstruct.plugin.jei.partbuilder.PartBuilderCategory;
import net.ryancave282.tconstruct.plugin.jei.partbuilder.PatternIngredientHelper;
import net.ryancave282.tconstruct.plugin.jei.partbuilder.PatternIngredientRenderer;
import net.ryancave282.tconstruct.plugin.jei.transfer.CraftingStationTransferInfo;
import net.ryancave282.tconstruct.plugin.jei.transfer.TinkerStationTransferInfo;
import net.ryancave282.tconstruct.plugin.jei.util.GuiContainerTankHandler;
import net.ryancave282.tconstruct.plugin.jei.util.PotionSubtypeInterpreter;
import net.ryancave282.tconstruct.plugin.jei.util.ToolPartSubtypeInterpreter;
import net.ryancave282.tconstruct.plugin.jei.util.ToolSubtypeInterpreter;
import net.ryancave282.tconstruct.smeltery.TinkerSmeltery;
import net.ryancave282.tconstruct.smeltery.block.component.SearedTankBlock.TankType;
import net.ryancave282.tconstruct.smeltery.client.screen.HeatingStructureScreen;
import net.ryancave282.tconstruct.smeltery.client.screen.MelterScreen;
import net.ryancave282.tconstruct.smeltery.data.SmelteryCompat;
import net.ryancave282.tconstruct.smeltery.item.CopperCanItem;
import net.ryancave282.tconstruct.smeltery.item.TankItem;
import net.ryancave282.tconstruct.tables.TinkerTables;
import net.ryancave282.tconstruct.tools.TinkerModifiers;
import net.ryancave282.tconstruct.tools.TinkerTools;
import net.ryancave282.tconstruct.tools.item.CreativeSlotItem;
import net.ryancave282.tconstruct.tools.item.ModifierCrystalItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static slimeknights.mantle.Mantle.commonResource;

@SuppressWarnings("unused")
@JeiPlugin
public class JEIPlugin implements IModPlugin {
  public static IModIdHelper modIdHelper;

  @Override
  public ResourceLocation getPluginUid() {
    return TConstructJEIConstants.PLUGIN;
  }

  @Override
  public void registerCategories(IRecipeCategoryRegistration registry) {
    final IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
    // casting
    registry.addRecipeCategories(new CastingBasinCategory(guiHelper));
    registry.addRecipeCategories(new CastingTableCategory(guiHelper));
    registry.addRecipeCategories(new MoldingRecipeCategory(guiHelper));
    // melting and casting
    registry.addRecipeCategories(new MeltingCategory(guiHelper));
    registry.addRecipeCategories(new AlloyRecipeCategory(guiHelper));
    registry.addRecipeCategories(new EntityMeltingRecipeCategory(guiHelper));
    registry.addRecipeCategories(new FoundryCategory(guiHelper));
    // tinker station
    registry.addRecipeCategories(new ModifierRecipeCategory(guiHelper));
    registry.addRecipeCategories(new SeveringCategory(guiHelper));
    registry.addRecipeCategories(new ToolBuildingCategory(guiHelper));
    // part builder
    registry.addRecipeCategories(new PartBuilderCategory(guiHelper));
    // modifier worktable
    registry.addRecipeCategories(new ModifierWorktableCategory(guiHelper));
  }

  @Override
  public void registerIngredients(IModIngredientRegistration registration) {
    assert Minecraft.getInstance().level != null;
    RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
    List<ModifierEntry> modifiers = Collections.emptyList();
    if (Config.CLIENT.showModifiersInJEI.get()) {
      modifiers = ModifierRecipeLookup.getRecipeModifierList();
    }
    registration.register(TConstructJEIConstants.MODIFIER_TYPE, modifiers, new ModifierIngredientHelper(), ModifierBookmarkIngredientRenderer.INSTANCE);
    registration.register(TConstructJEIConstants.PATTERN_TYPE, Collections.emptyList(), new PatternIngredientHelper(), PatternIngredientRenderer.INSTANCE);
  }

  @Override
  public void registerRecipes(IRecipeRegistration register) {
    Level level = Minecraft.getInstance().level;
    assert level != null;
    RegistryAccess access = level.registryAccess();
    RecipeManager manager = level.getRecipeManager();
    // casting
    List<IDisplayableCastingRecipe> castingBasinRecipes = RecipeHelper.getJEIRecipes(access, manager, TinkerRecipeTypes.CASTING_BASIN.get(), IDisplayableCastingRecipe.class);
    register.addRecipes(TConstructJEIConstants.CASTING_BASIN, castingBasinRecipes);
    List<IDisplayableCastingRecipe> castingTableRecipes = RecipeHelper.getJEIRecipes(access, manager, TinkerRecipeTypes.CASTING_TABLE.get(), IDisplayableCastingRecipe.class);
    register.addRecipes(TConstructJEIConstants.CASTING_TABLE, castingTableRecipes);

    // melting
    List<MeltingRecipe> meltingRecipes = RecipeHelper.getJEIRecipes(access, manager, TinkerRecipeTypes.MELTING.get(), MeltingRecipe.class);
    register.addRecipes(TConstructJEIConstants.MELTING, meltingRecipes);
    register.addRecipes(TConstructJEIConstants.FOUNDRY, meltingRecipes);
    MeltingFuelHandler.setMeltngFuels(RecipeHelper.getRecipes(manager, TinkerRecipeTypes.FUEL.get(), MeltingFuel.class));

    // entity melting
    List<EntityMeltingRecipe> entityMeltingRecipes = RecipeHelper.getJEIRecipes(access, manager, TinkerRecipeTypes.ENTITY_MELTING.get(), EntityMeltingRecipe.class);
    // generate a "default" recipe for all other entity types
    entityMeltingRecipes.add(new DefaultEntityMeltingRecipe(entityMeltingRecipes));
    register.addRecipes(TConstructJEIConstants.ENTITY_MELTING, entityMeltingRecipes);

    // alloying
    List<AlloyRecipe> alloyRecipes = RecipeHelper.getJEIRecipes(access, manager, TinkerRecipeTypes.ALLOYING.get(), AlloyRecipe.class);
    register.addRecipes(TConstructJEIConstants.ALLOY, alloyRecipes);

    // molding
    List<MoldingRecipe> moldingRecipes = ImmutableList.<MoldingRecipe>builder()
      .addAll(RecipeHelper.getJEIRecipes(access, manager, TinkerRecipeTypes.MOLDING_TABLE.get(), MoldingRecipe.class))
      .addAll(RecipeHelper.getJEIRecipes(access, manager, TinkerRecipeTypes.MOLDING_BASIN.get(), MoldingRecipe.class))
      .build();
    register.addRecipes(TConstructJEIConstants.MOLDING, moldingRecipes);

    // modifiers
    List<IDisplayModifierRecipe> modifierRecipes = RecipeHelper.getJEIRecipes(access, manager, TinkerRecipeTypes.TINKER_STATION.get(), IDisplayModifierRecipe.class)
                                                               .stream()
                                                               .sorted((r1, r2) -> {
                                                                 SlotType t1 = r1.getSlotType();
                                                                 SlotType t2 = r2.getSlotType();
                                                                 String n1 = t1 == null ? "zzzzzzzzzz" : t1.getName();
                                                                 String n2 = t2 == null ? "zzzzzzzzzz" : t2.getName();
                                                                 return n1.compareTo(n2);
                                                               }).collect(Collectors.toList());
    register.addRecipes(TConstructJEIConstants.MODIFIERS, modifierRecipes);

    // beheading
    List<SeveringRecipe> severingRecipes = RecipeHelper.getJEIRecipes(access, manager, TinkerRecipeTypes.SEVERING.get(), SeveringRecipe.class);
    register.addRecipes(TConstructJEIConstants.SEVERING, severingRecipes);

    // tool building
    List<ToolBuildingRecipe> toolBuilding = RecipeHelper.getJEIRecipes(access, manager, TinkerRecipeTypes.TINKER_STATION.get(), ToolBuildingRecipe.class)
      .stream()
      .sorted(Comparator.comparingInt(r -> StationSlotLayoutLoader.getInstance().get(r.getLayoutSlotId()).getSortIndex()))
      .toList();
    register.addRecipes(TConstructJEIConstants.TOOL_BUILDING, toolBuilding);

    // part builder
    MaterialItemList.setRecipes(RecipeHelper.getRecipes(manager, TinkerRecipeTypes.MATERIAL.get(), MaterialRecipe.class));
    register.addRecipes(TConstructJEIConstants.PART_BUILDER, RecipeHelper.getJEIRecipes(access, manager, TinkerRecipeTypes.PART_BUILDER.get(), IDisplayPartBuilderRecipe.class));

    // modifier worktable
    register.addRecipes(TConstructJEIConstants.MODIFIER_WORKTABLE, RecipeHelper.getJEIRecipes(access, manager, TinkerRecipeTypes.MODIFIER_WORKTABLE.get(), IModifierWorktableRecipe.class));
  }

  /**
   * Adds an item as a casting catalyst, and as a molding catalyst if it has molding recipes
   * @param registry     Catalyst regisry
   * @param item         Item to add
   * @param ownCategory  Category to always add
   * @param type         Molding recipe type
   */
  private static <T extends Recipe<C>, C extends Container> void addCastingCatalyst(IRecipeCatalystRegistration registry, ItemLike item, mezz.jei.api.recipe.RecipeType<IDisplayableCastingRecipe> ownCategory, RecipeType<MoldingRecipe> type) {
    ItemStack stack = new ItemStack(item);
    registry.addRecipeCatalyst(stack, ownCategory);
    assert Minecraft.getInstance().level != null;
    if (!Minecraft.getInstance().level.getRecipeManager().byType(type).isEmpty()) {
      registry.addRecipeCatalyst(stack, TConstructJEIConstants.MOLDING);
    }
  }

  @Override
  public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
    // tables
    registry.addRecipeCatalyst(new ItemStack(TinkerTables.partBuilder), TConstructJEIConstants.PART_BUILDER);
    registry.addRecipeCatalyst(new ItemStack(TinkerTables.tinkerStation), TConstructJEIConstants.MODIFIERS, TConstructJEIConstants.TOOL_BUILDING);
    registry.addRecipeCatalyst(new ItemStack(TinkerTables.tinkersAnvil), TConstructJEIConstants.MODIFIERS, TConstructJEIConstants.TOOL_BUILDING);
    registry.addRecipeCatalyst(new ItemStack(TinkerTables.scorchedAnvil), TConstructJEIConstants.MODIFIERS, TConstructJEIConstants.TOOL_BUILDING);
    registry.addRecipeCatalyst(new ItemStack(TinkerTables.modifierWorktable), TConstructJEIConstants.MODIFIER_WORKTABLE);

    // smeltery
    registry.addRecipeCatalyst(new ItemStack(TinkerSmeltery.searedMelter), TConstructJEIConstants.MELTING);
    registry.addRecipeCatalyst(new ItemStack(TinkerSmeltery.searedHeater), RecipeTypes.FUELING);
    addCastingCatalyst(registry, TinkerSmeltery.searedTable, TConstructJEIConstants.CASTING_TABLE, TinkerRecipeTypes.MOLDING_TABLE.get());
    addCastingCatalyst(registry, TinkerSmeltery.searedBasin, TConstructJEIConstants.CASTING_BASIN, TinkerRecipeTypes.MOLDING_BASIN.get());
    registry.addRecipeCatalyst(new ItemStack(TinkerSmeltery.smelteryController), TConstructJEIConstants.MELTING, TConstructJEIConstants.ALLOY, TConstructJEIConstants.ENTITY_MELTING);

    // foundry
    registry.addRecipeCatalyst(new ItemStack(TinkerSmeltery.scorchedAlloyer), TConstructJEIConstants.ALLOY);
    addCastingCatalyst(registry, TinkerSmeltery.scorchedTable, TConstructJEIConstants.CASTING_TABLE, TinkerRecipeTypes.MOLDING_TABLE.get());
    addCastingCatalyst(registry, TinkerSmeltery.scorchedBasin, TConstructJEIConstants.CASTING_BASIN, TinkerRecipeTypes.MOLDING_BASIN.get());
    registry.addRecipeCatalyst(new ItemStack(TinkerSmeltery.foundryController), TConstructJEIConstants.FOUNDRY);

    // modifiers
    registry.addRecipeCatalyst(TConstructJEIConstants.MODIFIER_TYPE, new ModifierEntry(TinkerModifiers.severing, 1), TConstructJEIConstants.SEVERING);
    registry.addRecipeCatalyst(TConstructJEIConstants.MODIFIER_TYPE, new ModifierEntry(TinkerModifiers.melting, 1), TConstructJEIConstants.MELTING, TConstructJEIConstants.ENTITY_MELTING);
    for (Holder<Item> item : BuiltInRegistries.ITEM.getTagOrEmpty(TinkerTags.Items.MODIFIABLE)) {
      if (item.get() instanceof IModifiableDisplay modifiable) {
        // add any tools with a severing trait to severing
        ModifierNBT traits = ToolTraitHook.getTraits(modifiable.getToolDefinition(), MaterialNBT.EMPTY);
        if (traits.getLevel(TinkerModifiers.severing.getId()) > 0) {
          registry.addRecipeCatalyst(modifiable.getRenderTool(), TConstructJEIConstants.SEVERING);
        }
        // add any tools with a melting trait to melting
        if (traits.getLevel(TinkerModifiers.melting.getId()) > 0) {
          // only add to entity melting if its melee too
          if (item.containsTag(TinkerTags.Items.MELEE)) {
            registry.addRecipeCatalyst(modifiable.getRenderTool(), TConstructJEIConstants.MELTING, TConstructJEIConstants.ENTITY_MELTING);
          } else {
            registry.addRecipeCatalyst(modifiable.getRenderTool(), TConstructJEIConstants.MELTING);
          }
        }
      }
    }
  }

  @Override
  public void registerItemSubtypes(ISubtypeRegistration registry) {
    // retexturable blocks
    IIngredientSubtypeInterpreter<ItemStack> tables = (stack, context) -> {
      if (context == UidContext.Ingredient) {
        return RetexturedHelper.getTextureName(stack);
      }
      return IIngredientSubtypeInterpreter.NONE;
    };
    registry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, TinkerTables.craftingStation.asItem(), tables);
    registry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, TinkerTables.partBuilder.asItem(), tables);
    registry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, TinkerTables.tinkerStation.asItem(), tables);
    registry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, TinkerTables.tinkersAnvil.asItem(), tables);
    registry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, TinkerTables.modifierWorktable.asItem(), tables);
    registry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, TinkerTables.scorchedAnvil.asItem(), tables);
    registry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, TinkerSmeltery.smelteryController.asItem(), tables);
    registry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, TinkerSmeltery.searedDrain.asItem(), tables);
    registry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, TinkerSmeltery.searedDuct.asItem(), tables);
    registry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, TinkerSmeltery.searedChute.asItem(), tables);
    registry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, TinkerSmeltery.foundryController.asItem(), tables);
    registry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, TinkerSmeltery.scorchedDrain.asItem(), tables);
    registry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, TinkerSmeltery.scorchedDuct.asItem(), tables);
    registry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, TinkerSmeltery.scorchedChute.asItem(), tables);
    registry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, TinkerFluids.potion.asItem(), (PotionSubtypeInterpreter<ItemStack>)ItemStack::getTag);
    registry.registerSubtypeInterpreter(ForgeTypes.FLUID_STACK, TinkerFluids.potion.get(), (PotionSubtypeInterpreter<FluidStack>)FluidStack::getTag);

    // parts
    for (Holder<Item> item : BuiltInRegistries.ITEM.getTagOrEmpty(TinkerTags.Items.TOOL_PARTS)) {
      registry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, item.value(), ToolPartSubtypeInterpreter.INSTANCE);
    }

    // tools
    Item slimeskull = TinkerTools.slimesuit.get(ArmorItem.Type.HELMET);
    registry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, slimeskull, ToolSubtypeInterpreter.ALWAYS);
    for (Holder<Item> holder : BuiltInRegistries.ITEM.getTagOrEmpty(TinkerTags.Items.MULTIPART_TOOL)) {
      Item item = holder.value();
      if (item != slimeskull) {
        registry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, item, ToolSubtypeInterpreter.INGREDIENT);
      }
    }

    // fluid containers have types based on fluid, don't bother with different sizes
    registry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, TinkerSmeltery.copperCan.get(), (stack, context) -> CopperCanItem.getSubtype(stack));
    IIngredientSubtypeInterpreter<ItemStack> tankInterpreter = (stack, context) -> TankItem.getSubtype(stack);
    for (TankType type : TankType.values()) {
      registry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, TinkerSmeltery.searedTank.get(type).asItem(), tankInterpreter);
      registry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, TinkerSmeltery.scorchedTank.get(type).asItem(), tankInterpreter);
    }
    registry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, TinkerSmeltery.searedLantern.asItem(), tankInterpreter);
    registry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, TinkerSmeltery.scorchedLantern.asItem(), tankInterpreter);

    registry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, TinkerModifiers.creativeSlotItem.get(), (stack, context) -> {
      SlotType slotType = CreativeSlotItem.getSlot(stack);
      return slotType != null ? slotType.getName() : "";
    });
    registry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, TinkerModifiers.modifierCrystal.get(), (stack, context) -> {
      ModifierId id = ModifierCrystalItem.getModifier(stack);
      return id == null ? "" : id.toString();
    });
  }

  @Override
  public void registerGuiHandlers(IGuiHandlerRegistration registration) {
    registration.addGenericGuiContainerHandler(MelterScreen.class, new GuiContainerTankHandler<>());
    registration.addGenericGuiContainerHandler(HeatingStructureScreen.class, new GuiContainerTankHandler<>());
  }

  @Override
  public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
    registration.addRecipeTransferHandler(new CraftingStationTransferInfo());
    registration.addRecipeTransferHandler(new TinkerStationTransferInfo<>(TConstructJEIConstants.MODIFIERS));
    registration.addRecipeTransferHandler(new TinkerStationTransferInfo<>(TConstructJEIConstants.TOOL_BUILDING));
  }

  /**
   * Removes a fluid from JEI
   * @param manager  Manager
   * @param fluid    Fluid to remove
   */
  private static void removeFluid(IIngredientManager manager, Fluid fluid) {
    manager.removeIngredientsAtRuntime(ForgeTypes.FLUID_STACK, Collections.singleton(new FluidStack(fluid, FluidType.BUCKET_VOLUME)));
  }

  /** Checks if the given tag exists */
  @SuppressWarnings("deprecation")
  private static boolean tagExists(String name) {
    Optional<Named<Item>> tag = BuiltInRegistries.ITEM.getTag(ItemTags.create(commonResource(name)));
    return tag.isPresent() && tag.get().size() > 0;
  }

  /** Removes any retextured variants that shouldn't show */
  private static void cleanupRetexturedBlock(Predicate<ItemStack> remover, boolean showAll, ItemLike item, TagKey<Item> tag) {
    if (showAll) {
      remover.test(new ItemStack(item));
    } else {
      RetexturedHelper.addTagVariants(remover, item, tag);
    }
  }

  @Override
  public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
    IIngredientManager manager = jeiRuntime.getIngredientManager();

    List<ItemStack> removeItems = new ArrayList<>();
    Consumer<ItemStack> removeItem = removeItems::add;
    List<ItemStack> addItems = new ArrayList<>();
    Consumer<ItemStack> addItem = addItems::add;
    // shown via the modifiers
    ModifierCrystalItem.addVariants(removeItem);
    // fluids can be clutter so remove them by default
    if (!Config.CLIENT.showFilledFluidTanks.get()) {
      CopperCanItem.addFilledVariants(removeItem);
      TankItem.addFilledVariants(removeItem);
      // add back lava and blazing blood filled tanks, since they are useful and not much clutter
      // easier to do this than to filter the list
      addItems.add(TankItem.fillTank(TinkerSmeltery.searedTank, TankType.FUEL_TANK, Fluids.LAVA));
      addItems.add(TankItem.fillTank(TinkerSmeltery.searedTank, TankType.FUEL_TANK, TinkerFluids.blazingBlood.get()));
      addItems.add(TankItem.fillTank(TinkerSmeltery.scorchedTank, TankType.FUEL_TANK, Fluids.LAVA));
      addItems.add(TankItem.fillTank(TinkerSmeltery.scorchedTank, TankType.FUEL_TANK, TinkerFluids.blazingBlood.get()));
    }
    // tool config filters to 1 material, easiest to just remove all then add back the 1
    String showOnlyTools = Config.CLIENT.showOnlyToolMaterial.get();
    if (!showOnlyTools.isEmpty()) {
      for (Holder<Item> item : BuiltInRegistries.ITEM.getTagOrEmpty(TinkerTags.Items.MODIFIABLE)) {
        if (item.get() instanceof IModifiable modifiable) {
          ToolBuildHandler.addVariants(removeItem, modifiable, "");
          ToolBuildHandler.addVariants(addItem, modifiable, showOnlyTools);
        }
      }
    }
    String showOnlyParts = Config.CLIENT.showOnlyPartMaterial.get();
    if (!showOnlyTools.isEmpty()) {
      for (Holder<Item> item : BuiltInRegistries.ITEM.getTagOrEmpty(TinkerTags.Items.TOOL_PARTS)) {
        if (item.get() instanceof IMaterialItem part) {
          part.addVariants(removeItem, "");
          part.addVariants(addItem, showOnlyParts);
        }
      }
    }
    // for smeltery and tables, if the relevant config is true clear the blank variant
    // if its false clear the special variants
    Predicate<ItemStack> cleanupItem = stack -> {
      removeItems.add(stack);
      return false;
    };
    // wooden
    boolean showTables = Config.CLIENT.showAllTableVariants.get();
    cleanupRetexturedBlock(cleanupItem, showTables, TinkerTables.craftingStation, ItemTags.LOGS);
    cleanupRetexturedBlock(cleanupItem, showTables, TinkerTables.partBuilder, ItemTags.PLANKS);
    cleanupRetexturedBlock(cleanupItem, showTables, TinkerTables.tinkerStation, ItemTags.PLANKS);
    cleanupRetexturedBlock(cleanupItem, showTables, TinkerTables.modifierWorktable, TinkerTags.Items.WORKSTATION_ROCK);
    // anvils
    boolean showAnvils = Config.CLIENT.showAllAnvilVariants.get();
    cleanupRetexturedBlock(cleanupItem, showAnvils, TinkerTables.tinkersAnvil, TinkerTags.Items.ANVIL_METAL);
    cleanupRetexturedBlock(cleanupItem, showAnvils, TinkerTables.scorchedAnvil, TinkerTags.Items.ANVIL_METAL);
    // smeltery
    boolean showSmeltery = Config.CLIENT.showAllSmelteryVariants.get();
    cleanupRetexturedBlock(cleanupItem, showSmeltery, TinkerSmeltery.smelteryController, TinkerTags.Items.SEARED_BLOCKS);
    cleanupRetexturedBlock(cleanupItem, showSmeltery, TinkerSmeltery.searedDrain, TinkerTags.Items.SEARED_BLOCKS);
    cleanupRetexturedBlock(cleanupItem, showSmeltery, TinkerSmeltery.searedDuct, TinkerTags.Items.SEARED_BLOCKS);
    cleanupRetexturedBlock(cleanupItem, showSmeltery, TinkerSmeltery.searedChute, TinkerTags.Items.SEARED_BLOCKS);
    cleanupRetexturedBlock(cleanupItem, showSmeltery, TinkerSmeltery.foundryController, TinkerTags.Items.SCORCHED_BLOCKS);
    cleanupRetexturedBlock(cleanupItem, showSmeltery, TinkerSmeltery.scorchedDrain, TinkerTags.Items.SCORCHED_BLOCKS);
    cleanupRetexturedBlock(cleanupItem, showSmeltery, TinkerSmeltery.scorchedDuct, TinkerTags.Items.SCORCHED_BLOCKS);
    cleanupRetexturedBlock(cleanupItem, showSmeltery, TinkerSmeltery.scorchedChute, TinkerTags.Items.SCORCHED_BLOCKS);

    if (!removeItems.isEmpty()) {
      manager.removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, removeItems);
    }
    if (!addItems.isEmpty()) {
      manager.addIngredientsAtRuntime(VanillaTypes.ITEM_STACK, addItems);
    }

    // fluid hiding, buckets are hidden via the creative tab logic
    // hide compat that is not present
    for (SmelteryCompat compat : SmelteryCompat.values()) {
      if (!tagExists("ingots/" + compat.getName())) {
        // if the alt tag exists then still show the fluid
        if (!compat.getAltTag().isEmpty()) {
          if (tagExists("ingots/" + compat.getAltTag())) {
            continue;
          }
        }
        removeFluid(manager, compat.getFluid().get());
      }
    }
    if (!ModList.get().isLoaded("ceramics")) {
      removeFluid(manager, TinkerFluids.moltenPorcelain.get());
    }

    // add potion fluids for each potion variant if requested
    if (Config.CLIENT.showPotionFluidInJEI.get()) {
      manager.addIngredientsAtRuntime(ForgeTypes.FLUID_STACK,
                                      BuiltInRegistries.POTION.holders().filter(holder -> {
                                        Potion potion = holder.get();
                                        return potion != Potions.EMPTY && potion != Potions.WATER;
                                      }).map(holder -> PotionFluidType.potionFluid(holder.key(), FluidType.BUCKET_VOLUME)).toList());
    }
    // remove variantless potion fluid
    removeFluid(manager, TinkerFluids.potion.get());

    modIdHelper = jeiRuntime.getJeiHelpers().getModIdHelper();
  }
}
