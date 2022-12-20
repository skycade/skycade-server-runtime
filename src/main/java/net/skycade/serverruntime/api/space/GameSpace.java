package net.skycade.serverruntime.api.space;

import java.util.Objects;
import java.util.UUID;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.world.DimensionType;
import org.jetbrains.annotations.NotNull;

public abstract class GameSpace extends InstanceContainer {

  private final EventNode<PlayerEvent> instanceBoundPlayerEventNode;

  /**
   * The constructor for the game space instance.
   *
   * @param uniqueId      the unique id of the game space
   * @param dimensionType the dimension type of the game space
   */
  public GameSpace(@NotNull UUID uniqueId, @NotNull DimensionType dimensionType) {
    super(uniqueId, dimensionType);

    this.instanceBoundPlayerEventNode =
        EventNode.type("game-space-" + this.getUniqueId(), EventFilter.PLAYER,
            (event, player) -> player.getInstance() != null &&
                Objects.requireNonNull(player.getInstance()).getUniqueId()
                    .equals(this.getUniqueId()));

    MinecraftServer.getGlobalEventHandler().addChild(this.instanceBoundPlayerEventNode);

    this.init();
  }

  /**
   * Registers the chunk initializers/generators for the game space, as well as events,
   * time, and more.
   */
  public abstract void init();

  /**
   * Gets the event node for the game space.
   *
   * @return the event node for the game space
   */
  public @NotNull EventNode<PlayerEvent> instanceBoundPlayerEventNode() {
    return this.instanceBoundPlayerEventNode;
  }
}
