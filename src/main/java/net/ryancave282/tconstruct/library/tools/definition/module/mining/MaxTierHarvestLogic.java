package net.ryancave282.tconstruct.library.tools.definition.module.mining;

import net.minecraft.world.item.Tier;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import net.ryancave282.tconstruct.library.json.TinkerLoadables;
import net.ryancave282.tconstruct.library.module.HookProvider;
import net.ryancave282.tconstruct.library.module.ModuleHook;
import net.ryancave282.tconstruct.library.tools.definition.module.ToolHooks;
import net.ryancave282.tconstruct.library.tools.definition.module.ToolModule;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;
import net.ryancave282.tconstruct.library.utils.HarvestTiers;

import java.util.List;

/**
 * Module that limits the tier to the given max
 * TODO 1.20: rename to MaxTierModule
 */
public record MaxTierHarvestLogic(Tier tier) implements MiningTierToolHook, ToolModule {
  public static final RecordLoadable<MaxTierHarvestLogic> LOADER = RecordLoadable.create(TinkerLoadables.TIER.requiredField("tier", MaxTierHarvestLogic::tier), MaxTierHarvestLogic::new);
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<MaxTierHarvestLogic>defaultHooks(ToolHooks.MINING_TIER);

  @Override
  public RecordLoadable<MaxTierHarvestLogic> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public Tier modifyTier(IToolStackView tool, Tier tier) {
    return HarvestTiers.min(this.tier, tier);
  }
}
