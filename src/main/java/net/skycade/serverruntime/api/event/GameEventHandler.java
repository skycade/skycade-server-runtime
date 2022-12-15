package net.skycade.serverruntime.api.event;

import java.util.Arrays;
import java.util.function.Consumer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;

public abstract class GameEventHandler {

  /**
   * The node for events.
   * <p>
   * This is the node that all events are registered to.
   * It may contain other nodes, but it is the main node.
   */
  private final EventNode<Event> eventNode;

  /**
   * Creates a new game event handler.
   * <p>
   * This constructor initializes the event node.
   * It is recommended to call this constructor in the constructor of the game.
   *
   * @param name the event node name
   */
  public GameEventHandler(String name) {
    this.eventNode = EventNode.all(name);
    this.init();
  }

  /**
   * Called when the game handler is initialized.
   * <p>
   * The {@link #on(Class, Consumer)} method should be called in this method a number of times, or
   * children should be added using the {@link #append(EventNode[])}} method.
   */
  protected abstract void init();

  /**
   * Registers an event listener.
   *
   * @param eventClass the event class
   * @param callback   the callback
   */
  protected <T extends Event> void on(Class<T> eventClass, Consumer<T> callback) {
    this.eventNode.addListener(eventClass, callback);
  }

  /**
   * Appends children to the event node.
   *
   * @param children the children to append
   */
  protected void append(EventNode<?>... children) {
    Arrays.stream(children).forEach(this.eventNode::addChild);
  }

  /**
   * Appends another game event handler to this one.
   *
   * @param handler the handler to append
   */
  protected void append(GameEventHandler handler) {
    this.eventNode.addChild(handler.node());
  }

  /**
   * Gets the event node.
   *
   * @return the event node
   */
  public EventNode<Event> node() {
    return eventNode;
  }
}
