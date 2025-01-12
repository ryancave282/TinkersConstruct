package net.ryancave282.tconstruct.library.recipe.melting;

import lombok.Getter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.mantle.recipe.helper.FluidOutput;
import net.ryancave282.tconstruct.library.materials.definition.MaterialId;
import net.ryancave282.tconstruct.library.materials.definition.MaterialVariant;
import net.ryancave282.tconstruct.library.materials.definition.MaterialVariantId;
import net.ryancave282.tconstruct.library.recipe.casting.material.MaterialCastingLookup;
import net.ryancave282.tconstruct.library.recipe.ingredient.MaterialIngredient;
import net.ryancave282.tconstruct.smeltery.TinkerSmeltery;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Recipe to melt all castable tool parts of a given material
 */
public class MaterialMeltingRecipe implements IMeltingRecipe, IMultiRecipe<MeltingRecipe> {
  public static final RecordLoadable<MaterialMeltingRecipe> LOADER = RecordLoadable.create(
    ContextKey.ID.requiredField(),
    MaterialVariantId.LOADABLE.requiredField("input", r -> r.input.getVariant()),
    IntLoadable.FROM_ONE.requiredField("temperature", r -> r.temperature),
    FluidOutput.Loadable.REQUIRED.requiredField("result", r -> r.result),
    MaterialMeltingRecipe::new);

  @Getter
  private final ResourceLocation id;
  private final MaterialVariant input;
  private final int temperature;
  private final FluidOutput result;

  public MaterialMeltingRecipe(ResourceLocation id, MaterialVariantId input, int temperature, FluidOutput result) {
    this.id = id;
    this.input = MaterialVariant.of(input);
    this.temperature = temperature;
    this.result = result;
  }

  @Override
  public boolean matches(IMeltingContainer inv, Level worldIn) {
    if (input.isUnknown()) {
      return false;
    }
    ItemStack stack = inv.getStack();
    if (stack.isEmpty() || MaterialCastingLookup.getItemCost(stack.getItem()) == 0) {
      return false;
    }
    return input.matchesVariant(stack);
  }

  @Override
  public int getTemperature(IMeltingContainer inv) {
    return temperature;
  }

  @Override
  public int getTime(IMeltingContainer inv) {
    int cost = MaterialCastingLookup.getItemCost(inv.getStack().getItem());
    return IMeltingRecipe.calcTimeForAmount(temperature, result.getAmount() * cost);
  }

  @Override
  public FluidStack getOutput(IMeltingContainer inv) {
    int cost = MaterialCastingLookup.getItemCost(inv.getStack().getItem());
    return new FluidStack(result.get(), result.getAmount() * cost);
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerSmeltery.materialMeltingSerializer.get();
  }


  /* JEI display */
  private List<MeltingRecipe> multiRecipes = null;

  @Override
  public List<MeltingRecipe> getRecipes(RegistryAccess access) {
    if (multiRecipes == null) {
      if (input.get().isHidden()) {
        multiRecipes = Collections.emptyList();
      } else {
        // 1 recipe for each part
        MaterialId inputId = input.getId();
        multiRecipes = MaterialCastingLookup
          .getAllItemCosts().stream()
          .filter(entry -> entry.getKey().canUseMaterial(inputId))
          .map(entry -> {
            FluidOutput output = this.result;
            if (entry.getIntValue() != 1) {
              output = FluidOutput.fromStack(new FluidStack(output.get(), output.getAmount() * entry.getIntValue()));
            }
            return new MeltingRecipe(id, "", MaterialIngredient.of(entry.getKey(), inputId), output, temperature,
                                     IMeltingRecipe.calcTimeForAmount(temperature, output.getAmount()), Collections.emptyList(), false);
          }).collect(Collectors.toList());
      }
    }
    return multiRecipes;
  }
}
