package net.ryancave282.tconstruct.library.tools.definition.module.aoe;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import net.ryancave282.tconstruct.library.tools.definition.module.aoe.BoxAOEIterator.RectangleIterator;
import net.ryancave282.tconstruct.library.tools.definition.module.aoe.IBoxExpansion.ExpansionDirections;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;
import net.ryancave282.tconstruct.tools.TinkerModifiers;

import java.util.Collections;
import java.util.function.Predicate;

/**
 * AOE harvest logic that mines blocks in a circle
 * @param diameter  Diameter of the circle, starting from 1
 * @param is3D      If true, calculates AOE blocks in 3D instead of 2D
 */
public record CircleAOEIterator(int diameter, boolean is3D) implements AreaOfEffectIterator.Loadable {
  public static final RecordLoadable<CircleAOEIterator> LOADER = RecordLoadable.create(
    IntLoadable.FROM_ONE.defaultField("diameter", 1, true, CircleAOEIterator::diameter),
    BooleanLoadable.INSTANCE.defaultField("3D", false, CircleAOEIterator::is3D),
    CircleAOEIterator::new);

  @Override
  public RecordLoadable<CircleAOEIterator> getLoader() {
    return LOADER;
  }

  @Override
  public Iterable<BlockPos> getBlocks(IToolStackView tool, ItemStack stack, Player player, BlockState state, Level world, BlockPos origin, Direction sideHit, AOEMatchType matchType) {
    // expanded gives an extra width every odd level, and an extra height every even level
    int expanded = tool.getModifierLevel(TinkerModifiers.expanded.getId());
    return calculate(tool, stack, world, player, origin, sideHit, diameter + expanded, is3D, matchType);
  }

  /**
   *
   * @param tool       Tool used for harvest
   * @param stack      Item stack used for harvest (for vanilla hooks)
   * @param world      World containing the block
   * @param player     Player harvesting
   * @param origin     Center of harvest
   * @param sideHit    Block side hit
   * @param diameter   Circle diameter
   * @param matchType  Type of harvest being performed
   * @return  List of block positions
   */
  public static Iterable<BlockPos> calculate(IToolStackView tool, ItemStack stack, Level world, Player player, BlockPos origin, Direction sideHit, int diameter, boolean is3D, AOEMatchType matchType) {
    // skip if no work
    if (diameter == 1) {
      return Collections.emptyList();
    }

    // math works out that we can leave this an integer and get the radius working still
    int radiusSq = diameter * diameter / 4;
    Predicate<BlockPos> posPredicate = AreaOfEffectIterator.defaultBlockPredicate(tool, stack, world, origin, matchType);
    ExpansionDirections directions = IBoxExpansion.SIDE_HIT.getDirections(player, sideHit);
    // max needs to be an odd number
    return () -> new CircleIterator(origin, directions.width(), directions.height(), directions.traverseDown(), directions.depth(), radiusSq, diameter / 2, is3D, posPredicate);
  }

  /** Iterator used for getting the blocks, secret is a circle is a rectangle */
  private static class CircleIterator extends RectangleIterator {
    /* Diameter of the area to mine, circular */
    private final int radiusSq;
    private CircleIterator(BlockPos origin, Direction widthDir, Direction heightDir, boolean traverseDown, Direction depthDir, int radiusSq, int extra, boolean is3D, Predicate<BlockPos> posPredicate) {
      super(origin, widthDir, extra, heightDir, extra, traverseDown, depthDir, is3D ? extra : 0, posPredicate);
      this.radiusSq = radiusSq;
    }

    /** Gets the squared distance between the origin and the mutable position */
    private int distanceSq() {
      // built in method returns a double, thats overkill
      int dx = origin.getX() - mutablePos.getX();
      int dy = origin.getY() - mutablePos.getY();
      int dz = origin.getZ() - mutablePos.getZ();
      return dx*dx + dy*dy + dz*dz;
    }

    @Override
    protected BlockPos computeNext() {
      // ensure the position did not get changed by the consumer last time
      mutablePos.set(lastX, lastY, lastZ);
      // as long as we have another position, try using it
      while (incrementPosition()) {
        // skip over the origin
        // ensure it matches the predicate
        if (!mutablePos.equals(origin) && distanceSq() <= radiusSq && posPredicate.test(mutablePos)) {
          // store position in case the consumer changes it
          lastX = mutablePos.getX();
          lastY = mutablePos.getY();
          lastZ = mutablePos.getZ();
          return mutablePos;
        }
      }
      return endOfData();
    }
  }
}
