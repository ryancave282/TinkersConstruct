package net.ryancave282.tconstruct.library.tools.helper;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import slimeknights.mantle.data.predicate.block.BlockPredicate;
import net.ryancave282.tconstruct.fixture.MaterialItemFixture;
import net.ryancave282.tconstruct.fixture.MaterialStatsFixture;
import net.ryancave282.tconstruct.fixture.ToolDefinitionFixture;
import net.ryancave282.tconstruct.library.tools.definition.ToolDefinition;
import net.ryancave282.tconstruct.library.tools.definition.ToolDefinitionDataBuilder;
import net.ryancave282.tconstruct.library.tools.definition.module.build.MultiplyStatsModule;
import net.ryancave282.tconstruct.library.tools.definition.module.material.PartStatsModule;
import net.ryancave282.tconstruct.library.tools.definition.module.mining.IsEffectiveModule;
import net.ryancave282.tconstruct.library.tools.definition.module.mining.MiningSpeedToolHook;
import net.ryancave282.tconstruct.library.tools.item.ModifiableItem;
import net.ryancave282.tconstruct.library.tools.item.ToolItemTest;
import net.ryancave282.tconstruct.library.tools.nbt.MultiplierNBT;
import net.ryancave282.tconstruct.library.tools.stat.ToolStats;

import static org.assertj.core.api.Assertions.assertThat;

class ToolHarvestLogicTest extends ToolItemTest {
  private static ModifiableItem pickaxeTool;

  @BeforeAll
  synchronized static void beforeAllToolLogic() {
    MaterialItemFixture.init();
    if (pickaxeTool == null) {
      pickaxeTool = new ModifiableItem(new Item.Properties().stacksTo(1), ToolDefinitionFixture.getStandardToolDefinition());
      ForgeRegistries.ITEMS.register(new ResourceLocation("test", "pickaxe"), pickaxeTool);
    }
  }

  @Test
  void calcSpeed_dirt_notEffective() {
    ItemStack tool = buildTestTool(pickaxeTool);

    float speed = MiningSpeedToolHook.getDestroySpeed(tool, Blocks.DIRT.defaultBlockState());

    assertThat(speed).isEqualTo(1f);
  }

  @Test
  void calcSpeed_cobble_effective() {
    ItemStack tool = buildTestTool(pickaxeTool);

    float speed = MiningSpeedToolHook.getDestroySpeed(tool, Blocks.COBBLESTONE.defaultBlockState());

    assertThat(speed).isEqualTo(MaterialStatsFixture.MATERIAL_STATS_HEAD.miningSpeed());
  }

  @Test
  void calcSpeed_obsidian_notEnoughHarvestLevel() {
    ItemStack tool = buildTestTool(pickaxeTool);

    float speed = MiningSpeedToolHook.getDestroySpeed(tool, Blocks.OBSIDIAN.defaultBlockState());

    assertThat(speed).isEqualTo(1f);
  }

  @Test
  void calcSpeed_broken_slowButNotZero() {
    ItemStack tool = buildTestTool(pickaxeTool);
    breakTool(tool);

    float speed = MiningSpeedToolHook.getDestroySpeed(tool, Blocks.DIRT.defaultBlockState());

    assertThat(speed).isLessThan(1f);
    assertThat(speed).isGreaterThan(0f);
  }

  @Test
  void calcSpeed_effective_withMiningModifier() {
    float modifier = 2f;

    ToolDefinition definition = new ToolDefinition(new ResourceLocation("test", "mining_tool"));
    definition.setData(ToolDefinitionDataBuilder
                         .builder()
                         .module(new IsEffectiveModule(BlockPredicate.set(Blocks.COBBLESTONE), true))
                         .module(PartStatsModule.parts()
                                                .part(MaterialItemFixture.MATERIAL_ITEM_HEAD)
                                                .part(MaterialItemFixture.MATERIAL_ITEM_HANDLE)
                                                .part(MaterialItemFixture.MATERIAL_ITEM_EXTRA).build())
                         .module(new MultiplyStatsModule(MultiplierNBT.builder().set(ToolStats.MINING_SPEED, modifier).build()))
                         .build());

    ModifiableItem toolWithMiningModifier = new ModifiableItem(new Item.Properties(), definition);
    ForgeRegistries.ITEMS.register(new ResourceLocation("test", "tool_with_mining_modifier"), toolWithMiningModifier);
    ItemStack tool = buildTestTool(toolWithMiningModifier);

    // boosted by correct block
    float speed = MiningSpeedToolHook.getDestroySpeed(tool, Blocks.COBBLESTONE.defaultBlockState());
    assertThat(speed).isEqualTo(MaterialStatsFixture.MATERIAL_STATS_HEAD.miningSpeed() * modifier);

    // default speed
    speed = MiningSpeedToolHook.getDestroySpeed(tool, Blocks.STONE.defaultBlockState());
    assertThat(speed).isEqualTo(1.0f);
  }
}
