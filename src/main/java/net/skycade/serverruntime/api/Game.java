package net.skycade.serverruntime.api;

import java.util.List;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.instance.InstanceContainer;

/**
 * Represents a game.
 * <p>
 * A game is a collection of game spaces, which are collections of worlds. A game can have multiple
 * game spaces, but it must have at least one. A game space can have multiple worlds, but it must have
 * at least one.
 * <p>
 * A game has to initialize the server runtime, and it has to register all of its commands and events
 * through the methods {@link #commands()} which returns a list of commands, and {@link #events()} which
 * returns a list of events and their corresponding consumers for listening.
 *
 * @author Jacob Cohen
 */
public abstract class Game {

  /**
   * The server runtime instance.
   */
  private ServerRuntime runtime;

  /**
   * The main method that initializes the server runtime.
   */
  protected void init() {
    // initialize the server runtime
    if (this.runtime != null) {
      throw new IllegalStateException("Server runtime already initialized.");
    }

    // initialize the server runtime
    this.runtime = new ServerRuntime();
    this.runtime.initializeRuntime();

    // register the game spaces (instances)
    this.registerGameSpaces();

    // register commands
    this.registerCommands();
    // register events
    this.registerEvents();

    // start the server
    this.runtime.startServer();

    // call the on server start method
    this.onCompleteStartup();
  }

  /**
   * When the server has completely started.
   */
  public abstract void onCompleteStartup();

  /**
   * Register the game spaces (instances).
   */
  protected void registerGameSpaces() {
    // register game spaces
    this.instances().forEach(instance -> {
      MinecraftServer.getInstanceManager().registerInstance(instance);
    });
  }

  /**
   * Registers commands.
   */
  private void registerCommands() {
    ServerRuntime.LOGGER.info("Registering commands.");
    commands().forEach(this.runtime.commandManager()::register);
  }

  /**
   * Registers events.
   */
  private void registerEvents() {
    ServerRuntime.LOGGER.info("Registering events.");
    eventNodes().forEach(this.runtime.eventHandler()::addChild);
    this.runtime.eventHandler().addListener(PlayerLoginEvent.class, (event) -> {
      event.setSpawningInstance(this.provideSpawningInstance(event.getPlayer()));
    });
  }

  /**
   * Constructs and returns a list of commands to register.
   *
   * @return a list of commands to register
   */
  protected abstract List<Command> commands();

  /**
   * Constructs and returns a list of events to register.
   *
   * @return a list of events to register
   */
  protected abstract List<EventNode<Event>> eventNodes();

  /**
   * Provides instances to register.
   *
   * @return a list of instances to register
   */
  protected abstract List<InstanceContainer> instances();

  /**
   * Provides the spawning instance based on the player.
   *
   * @param player the player
   * @return the spawning instance
   */
  protected abstract InstanceContainer provideSpawningInstance(Player player);

  /**
   * Gets the server runtime instance.
   *
   * @return the server runtime instance
   */
  public ServerRuntime runtime() {
    return this.runtime;
  }
}
