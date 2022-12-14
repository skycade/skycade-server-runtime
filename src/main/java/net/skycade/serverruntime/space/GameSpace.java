package net.skycade.serverruntime.space;

import java.nio.file.Path;
import java.util.UUID;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.instance.AddEntityToInstanceEvent;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.instance.AnvilLoader;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.skycade.serverruntime.space.dimension.FullbrightDimension;

/**
 * A game space.
 *
 * @author Jacob Cohen
 */
public final class GameSpace extends InstanceContainer {

  public static final GameSpace DEFAULT = new GameSpace();

  /**
   * The constructor for the game space instance.
   */
  public GameSpace() {
    super(UUID.randomUUID(), FullbrightDimension.INSTANCE);

    // setChunkLoader(new AnvilLoader(Path.of("gamespace")));
    setTimeRate(0);

    EventNode<InstanceEvent> eventNode = eventNode();
    eventNode.addListener(AddEntityToInstanceEvent.class, event -> {
      final Entity entity = event.getEntity();
      if (entity instanceof Player player) {
        if (player.getInstance() == null) {
          onFirstSpawn(player);
        }
      }
    }).addListener(ItemDropEvent.class, event -> event.setCancelled(true));
  }

  private void onFirstSpawn(Player player) {
    // todo: implement
    // clear the inventory
    player.getInventory().clear();
  }
}
