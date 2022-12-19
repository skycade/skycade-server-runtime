package net.skycade.serverruntime.api.event;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.trait.InstanceEvent;
import net.skycade.serverruntime.api.space.GameSpace;

public abstract class GameEventHandler extends EventHandler<InstanceEvent> {

  /**
   * The game space(s) that this event handler is for.
   */
  private final List<GameSpace> forGameSpaces;

  /**
   * Creates a new game event handler.
   * <p>
   * This constructor initializes the event node.
   * It is recommended to call this constructor in the constructor of the game.
   *
   * @param name the event node name
   */
  public GameEventHandler(String name) {
    this(name, new GameSpace[0]);
  }

  /**
   * Creates a new game event handler.
   * <p>
   * This constructor initializes the event node.
   * It is recommended to call this constructor in the constructor of the game.
   *
   * @param name          the event node name
   * @param forGameSpaces the game space(s) that this event handler is for
   */
  public GameEventHandler(String name, GameSpace... forGameSpaces) {
    super(EventNode.type(name, EventFilter.INSTANCE,
        (event, instance) -> Arrays.stream(forGameSpaces)
            .anyMatch(gameSpace -> gameSpace.getUniqueId().equals(instance.getUniqueId()))));
    this.forGameSpaces = Arrays.asList(forGameSpaces);
  }

  /**
   * Called when the game handler is initialized.
   * <p>
   * The {@link #on(Class, Consumer)} method should be called in this method a number of times, or
   * children should be added using the {@link #append(EventNode[])}} method.
   */
  protected abstract void init();

  /**
   * Gets the game space(s) that this event handler is for.
   *
   * @return the game space(s) that this event handler is for
   */
  public List<GameSpace> forGameSpaces() {
    return forGameSpaces;
  }
}
