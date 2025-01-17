package net.ryancave282.tconstruct.library.client.book.sectiontransformer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import slimeknights.mantle.client.book.data.content.PageContent;
import net.ryancave282.tconstruct.TConstruct;
import net.ryancave282.tconstruct.library.client.book.content.ContentModifier;
import net.ryancave282.tconstruct.library.modifiers.Modifier;
import net.ryancave282.tconstruct.library.modifiers.ModifierManager;

import java.util.Iterator;

/** Injects modifiers into a section based on a tag */
public class ModifierTagInjectorTransformer extends AbstractTagInjectingTransformer<Modifier> {
  public static final ModifierTagInjectorTransformer INSTANCE = new ModifierTagInjectorTransformer();

  private ModifierTagInjectorTransformer() {
    super(ModifierManager.REGISTRY_KEY, TConstruct.getResource("load_modifiers"), ContentModifier.ID);
  }

  @Override
  protected Iterator<Modifier> getTagEntries(TagKey<Modifier> tag) {
    return ModifierManager.getTagValues(tag).iterator();
  }

  @Override
  protected ResourceLocation getId(Modifier modifier) {
    return modifier.getId();
  }

  @Override
  protected PageContent createFallback(Modifier modifier) {
    return new ContentModifier(modifier);
  }
}
