package net.ryancave282.tconstruct.library.events.teleport;

import lombok.Getter;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import net.ryancave282.tconstruct.library.modifiers.ModifierEntry;
import net.ryancave282.tconstruct.library.tools.nbt.IToolStackView;

/** Event fired when an entity teleports using the ender sling modifier */
@Cancelable
public class SlingModifierTeleportEvent extends EntityTeleportEvent {
  @Getter
  private final IToolStackView tool;
  @Getter
  private final ModifierEntry entry;
  public SlingModifierTeleportEvent(Entity entity, double targetX, double targetY, double targetZ, IToolStackView tool, ModifierEntry entry) {
    super(entity, targetX, targetY, targetZ);
    this.tool = tool;
    this.entry = entry;
  }
}
