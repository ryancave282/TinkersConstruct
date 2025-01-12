package net.ryancave282.tconstruct.tools.client;

import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import slimeknights.mantle.util.ItemLayerPixels;
import net.ryancave282.tconstruct.library.client.modifiers.IUnbakedModifierModel;
import net.ryancave282.tconstruct.library.client.modifiers.NormalModifierModel;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;
import net.ryancave282.tconstruct.tools.modifiers.slotless.OverslimeModifier;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Modifier model that turns invisible when out of overslime
 */
public class OverslimeModifierModel extends NormalModifierModel {
  /** Constant unbaked model instance, as they are all the same */
  public static final IUnbakedModifierModel UNBAKED_INSTANCE = (smallGetter, largeGetter) -> {
    Material smallTexture = smallGetter.apply("");
    Material largeTexture = largeGetter.apply("");
    if (smallTexture != null || largeTexture != null) {
      return new OverslimeModifierModel(smallTexture, largeTexture);
    }
    return null;
  };

  public OverslimeModifierModel(@Nullable Material smallTexture, @Nullable Material largeTexture) {
    super(smallTexture, largeTexture);
  }

  @Nullable
  @Override
  public Object getCacheKey(IToolStackView tool, ModifierEntry entry) {
    if (entry.getModifier() instanceof OverslimeModifier overslime && overslime.getShield(tool) == 0) {
      return null;
    }
    return super.getCacheKey(tool, entry);
  }

  @Override
  public void addQuads(IToolStackView tool, ModifierEntry entry, Function<Material,TextureAtlasSprite> spriteGetter, Transformation transforms, boolean isLarge, int startTintIndex, Consumer<Collection<BakedQuad>> quadConsumer, @Nullable ItemLayerPixels pixels) {
    if (!(entry.getModifier() instanceof OverslimeModifier overslime) || overslime.getShield(tool) != 0) {
      super.addQuads(tool, entry, spriteGetter, transforms, isLarge, startTintIndex, quadConsumer, pixels);
    }
  }
}
