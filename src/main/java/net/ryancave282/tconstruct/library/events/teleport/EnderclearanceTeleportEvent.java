package net.ryancave282.tconstruct.library.events.teleport;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import net.ryancave282.tconstruct.tools.modules.armor.EnderclearanceModule;
import net.ryancave282.tconstruct.library.utils.TeleportHelper.ITeleportEventFactory;

/** Event fired when an entity teleports via {@link EnderclearanceModule} */
@Cancelable
public class EnderclearanceTeleportEvent extends EntityTeleportEvent {
  public static final ITeleportEventFactory TELEPORT_FACTORY = EnderclearanceTeleportEvent::new;

  public EnderclearanceTeleportEvent(Entity entity, double targetX, double targetY, double targetZ) {
    super(entity, targetX, targetY, targetZ);
  }
}
