package net.ryancave282.tconstruct.tools.recipe.severing;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Blocks;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.ingredient.EntityIngredient;
import net.ryancave282.tconstruct.library.recipe.modifiers.severing.SeveringRecipe;
import net.ryancave282.tconstruct.tools.TinkerModifiers;

/** Beheading recipe to drop pumpkins only if equipped */
public class SnowGolemBeheadingRecipe extends SeveringRecipe {
  public SnowGolemBeheadingRecipe(ResourceLocation id) {
    super(id, EntityIngredient.of(EntityType.SNOW_GOLEM), ItemOutput.fromItem(Items.CARVED_PUMPKIN));
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerModifiers.snowGolemBeheadingSerializer.get();
  }

  @Override
  public ItemStack getOutput(Entity entity) {
    if (entity instanceof SnowGolem && !((SnowGolem)entity).hasPumpkin()) {
      return new ItemStack(Blocks.SNOW_BLOCK);
    }
    return getOutput().copy();
  }
}
