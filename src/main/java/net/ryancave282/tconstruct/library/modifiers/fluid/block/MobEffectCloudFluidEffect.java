package net.ryancave282.tconstruct.library.modifiers.fluid.block;

import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import net.ryancave282.tconstruct.library.modifiers.fluid.EffectLevel;
import net.ryancave282.tconstruct.library.modifiers.fluid.FluidEffect;
import net.ryancave282.tconstruct.library.modifiers.fluid.FluidEffectContext;
import net.ryancave282.tconstruct.library.modifiers.fluid.FluidMobEffect;

import java.util.List;

/**
 * Effect to create a lingering cloud at the hit block
 * @see FluidMobEffect.Builder
 */
public record MobEffectCloudFluidEffect(List<FluidMobEffect> effects) implements FluidEffect<FluidEffectContext.Block> {
  public static final RecordLoadable<MobEffectCloudFluidEffect> LOADER = RecordLoadable.create(
    FluidMobEffect.LOADABLE.list(1).requiredField("effects", e -> e.effects),
    MobEffectCloudFluidEffect::new);

  @Override
  public RecordLoadable<MobEffectCloudFluidEffect> getLoader() {
    return LOADER;
  }

  /** Makes a cloud for the given context and size */
  public static AreaEffectCloud makeCloud(FluidEffectContext.Block context) {
    Vec3 location = context.getHitResult().getLocation();
    AreaEffectCloud cloud = new AreaEffectCloud(context.getLevel(), location.x(), location.y(), location.z());
    cloud.setOwner(context.getEntity());
    cloud.setRadius(1);
    cloud.setRadiusOnUse(-0.5f);
    cloud.setWaitTime(10);
    cloud.setRadiusPerTick(-cloud.getRadius() / cloud.getDuration());
    return cloud;
  }

  @Override
  public float apply(FluidStack fluid, EffectLevel level, FluidEffectContext.Block context, FluidAction action) {
    if (context.isOffsetReplaceable()) {
      float scale = level.value();
      if (action.execute()) {
        AreaEffectCloud cloud = makeCloud(context);
        boolean hasEffects = false;
        for (FluidMobEffect effect : effects) {
          int time = (int)(effect.time() * scale);
          if (time > 10) {
            cloud.addEffect(effect.effectWithTime(time));
            hasEffects = true;
          }
        }
        if (hasEffects) {
          context.getLevel().addFreshEntity(cloud);
        } else {
          cloud.discard();
          return 0;
        }
      }
      return scale;
    }
    return 0;
  }
}
