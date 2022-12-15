package net.skycade.serverruntime.api.space;

import java.util.UUID;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.world.DimensionType;
import org.jetbrains.annotations.NotNull;

public abstract class GameSpace extends InstanceContainer {

  /**
   * The constructor for the game space instance.
   *
   * @param uniqueId      the unique id of the game space
   * @param dimensionType the dimension type of the game space
   */
  public GameSpace(@NotNull UUID uniqueId, @NotNull DimensionType dimensionType) {
    super(uniqueId, dimensionType);

    this.init();
  }

  /**
   * Registers the chunk initializers/generators for the game space, as well as events,
   * time, and more.
   */
  public abstract void init();
}
