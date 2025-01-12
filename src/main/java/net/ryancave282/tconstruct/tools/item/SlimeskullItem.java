package net.ryancave282.tconstruct.tools.item;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.ryancave282.tconstruct.library.client.armor.ArmorModelManager.ArmorModelDispatcher;
import net.ryancave282.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import net.ryancave282.tconstruct.library.tools.helper.ArmorUtil;
import net.ryancave282.tconstruct.library.tools.item.armor.ModifiableArmorItem;
import net.ryancave282.tconstruct.tools.client.SlimeskullArmorModel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

/** This item is mainly to return the proper model for a slimeskull */
public class SlimeskullItem extends ModifiableArmorItem {
  private final ResourceLocation name;
  public SlimeskullItem(ModifiableArmorMaterial material, Properties properties) {
    super(material, ArmorItem.Type.HELMET, properties);
    this.name = material.getId();
  }

  @Nullable
  @Override
  public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
    return ArmorUtil.getDummyArmorTexture(slot);
  }

  @Override
  public void initializeClient(Consumer<IClientItemExtensions> consumer) {
    consumer.accept(new ArmorModelDispatcher() {
      @Override
      protected ResourceLocation getName() {
        return name;
      }

      @Nonnull
      @Override
      public Model getGenericArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
        return SlimeskullArmorModel.INSTANCE.setup(living, stack, original, getModel(stack));
      }
    });
  }
}
