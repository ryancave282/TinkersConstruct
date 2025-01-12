package net.ryancave282.tconstruct.library.recipe.melting;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.mantle.recipe.helper.FluidOutput;
import slimeknights.mantle.registration.object.FluidObject;
import net.ryancave282.tconstruct.library.materials.definition.MaterialVariantId;

import java.util.function.Consumer;

import static net.ryancave282.tconstruct.library.recipe.melting.IMeltingRecipe.getTemperature;

/**
 * Builder for a recipe to melt a dynamic part material item
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MaterialMeltingRecipeBuilder extends AbstractRecipeBuilder<MaterialMeltingRecipeBuilder> {
  private final MaterialVariantId inputId;
  private final int temperature;
  private final FluidOutput result;

  /** Creates a recipe using the fluids temperature */
  public static MaterialMeltingRecipeBuilder material(MaterialVariantId materialId, int temperature, FluidOutput result) {
    if (temperature < 0) {
      throw new IllegalArgumentException("Invalid temperature " + temperature + ", must be 0 or greater");
    }
    return new MaterialMeltingRecipeBuilder(materialId, temperature, result);
  }

  /** Creates a recipe using the fluids temperature */
  public static MaterialMeltingRecipeBuilder material(MaterialVariantId materialId, int temperature, FluidStack result) {
    return material(materialId, temperature, FluidOutput.fromStack(result));
  }

  /** Creates a recipe using the fluids temperature */
  public static MaterialMeltingRecipeBuilder material(MaterialVariantId materialId, FluidObject<?> fluid, int amount) {
    return material(materialId, getTemperature(fluid), fluid.result(amount));
  }

  /** Creates a recipe using the fluids temperature */
  public static MaterialMeltingRecipeBuilder material(MaterialVariantId materialId, FluidStack result) {
    return material(materialId, getTemperature(result), result);
  }

  /** Creates a recipe using the fluids temperature */
  public static MaterialMeltingRecipeBuilder material(MaterialVariantId materialId, Fluid result, int amount) {
    return material(materialId, new FluidStack(result, amount));
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer) {
    save(consumer, inputId.getId());
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
    ResourceLocation advancementID = this.buildOptionalAdvancement(id, "melting");
    consumer.accept(new LoadableFinishedRecipe<>(new MaterialMeltingRecipe(id, inputId, temperature, result), MaterialMeltingRecipe.LOADER, advancementID));
  }
}
