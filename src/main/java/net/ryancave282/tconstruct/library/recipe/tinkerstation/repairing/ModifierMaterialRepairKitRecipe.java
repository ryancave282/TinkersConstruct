package net.ryancave282.tconstruct.library.recipe.tinkerstation.repairing;

import com.mojang.datafixers.util.Pair;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import net.ryancave282.tconstruct.common.TinkerTags;
import net.ryancave282.tconstruct.library.materials.definition.MaterialId;
import net.ryancave282.tconstruct.library.materials.stats.MaterialStatsId;
import net.ryancave282.tconstruct.library.modifiers.ModifierId;
import net.ryancave282.tconstruct.library.tools.definition.module.material.MaterialRepairModule;
import net.ryancave282.tconstruct.library.tools.helper.ModifierUtil;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;
import net.ryancave282.tconstruct.library.tools.nbt.ToolStack;
import net.ryancave282.tconstruct.library.tools.part.IMaterialItem;
import net.ryancave282.tconstruct.tables.recipe.CraftingTableRepairKitRecipe;
import net.ryancave282.tconstruct.tools.TinkerModifiers;

/** Recipe for using a repair kit in a crafting station for a specialized tool */
public class ModifierMaterialRepairKitRecipe extends CraftingTableRepairKitRecipe implements IModifierMaterialRepairRecipe {
  public static final RecordLoadable<ModifierMaterialRepairKitRecipe> LOADER = RecordLoadable.create(ContextKey.ID.requiredField(), MODIFIER_FIELD, REPAIR_MATERIAL_FIELD, STAT_TYPE_FIELD, ModifierMaterialRepairKitRecipe::new);

  /** Tool that can be repaired with this recipe */
  @Getter
  private final ModifierId modifier;
  /** ID of material used in repairing */
  @Getter
  private final MaterialId repairMaterial;
  /** Stat type used for repairing, null means it will be fetched as the first available stat type */
  @Getter
  private final MaterialStatsId statType;
  public ModifierMaterialRepairKitRecipe(ResourceLocation id, ModifierId modifier, MaterialId repairMaterial, MaterialStatsId statType) {
    super(id);
    this.modifier = modifier;
    this.repairMaterial = repairMaterial;
    this.statType = statType;
  }

  @Override
  protected boolean toolMatches(ItemStack stack) {
    return stack.is(TinkerTags.Items.DURABILITY) && ModifierUtil.getModifierLevel(stack, modifier) > 0;
  }

  @Override
  public boolean matches(CraftingContainer inv, Level worldIn) {
    Pair<ToolStack, ItemStack> inputs = getRelevantInputs(inv);
    return inputs != null && repairMaterial.equals(IMaterialItem.getMaterialFromStack(inputs.getSecond()).getId());
  }

  @Override
  protected float getRepairAmount(IToolStackView tool, ItemStack repairStack) {
    return MaterialRepairModule.getDurability(tool.getDefinition().getId(), repairMaterial.getId(), statType) * tool.getModifierLevel(modifier);
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerModifiers.craftingModifierMaterialRepair.get();
  }
}
