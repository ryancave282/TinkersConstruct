package net.ryancave282.tconstruct.plugin;

import blusunrize.immersiveengineering.api.tool.ChemthrowerHandler;
import blusunrize.immersiveengineering.api.tool.ChemthrowerHandler.ChemthrowerEffect_Potion;
import blusunrize.immersiveengineering.api.tool.ChemthrowerHandler.ChemthrowerEffect_RandomTeleport;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.ryancave282.tconstruct.fluids.TinkerFluids;

/** Event handlers to run when Immersive Engineering is present */
public class ImmersiveEngineeringPlugin {
  @SubscribeEvent
  public void commonSetup(FMLCommonSetupEvent event) {
    ChemthrowerHandler.registerFlammable(TinkerFluids.blazingBlood.getTag());
    registerChemEffect(TinkerFluids.earthSlime.getTag(), MobEffects.MOVEMENT_SLOWDOWN, 140);
    registerChemEffect(TinkerFluids.skySlime.getTag(), MobEffects.JUMP, 200);
    registerChemEffect(TinkerFluids.enderSlime.getTag(), MobEffects.LEVITATION, 100);
    registerChemEffect(TinkerFluids.venom.getTag(), MobEffects.POISON, 300);
    registerChemEffect(TinkerFluids.magma.getTag(), MobEffects.FIRE_RESISTANCE, 200);
    registerChemEffect(TinkerFluids.liquidSoul.getTag(), MobEffects.BLINDNESS, 100);
    ChemthrowerHandler.registerEffect(TinkerFluids.moltenEnder.getTag(), new ChemthrowerEffect_RandomTeleport(null, 0, 1));
    registerChemEffect(TinkerFluids.moltenUranium.getTag(), MobEffects.POISON, 200);
  }

  /** Shorthand to register a chemical potion effect */
  private static void registerChemEffect(TagKey<Fluid> tag, MobEffect effect, int duration) {
    ChemthrowerHandler.registerEffect(tag, new ChemthrowerEffect_Potion(null, 0, effect, duration, 0));
  }
}
