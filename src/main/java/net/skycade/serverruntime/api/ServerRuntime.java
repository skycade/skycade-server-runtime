package net.skycade.serverruntime.api;

import java.net.InetSocketAddress;
import java.nio.file.Path;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.extras.velocity.VelocityProxy;
import net.minestom.server.network.ConnectionManager;
import net.skycade.java.api.SkycadeJavaApi;
import net.skycade.java.api.SkycadeJavaBootstrapper;
import net.skycade.java.api.config.ConfigurationLoader;
import net.skycade.serverruntime.api.message.Messenger;
import net.skycade.serverruntime.config.BasicRuntimeConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The server runtime.
 * <p>
 * This class is the main class of the runtime. It is responsible for initializing the runtime and
 * starting the server.
 *
 * @author Jacob Cohen
 */
public final class ServerRuntime {
  /**
   * The logger for the server runtime.
   */
  public static final Logger LOGGER = LoggerFactory.getLogger(ServerRuntime.class);

  /**
   * The minecraft server instance.
   */
  private MinecraftServer minecraftServer;

  /**
   * Basic runtime configuration that is required for the runtime to run.
   */
  private BasicRuntimeConfiguration runtimeConfiguration;

  /*
   * The Skycade Java API.
   */
  private SkycadeJavaApi skycadeJavaApi;

  /**
   * The main method that initializes the runtime code so the server can run.
   */
  public void initializeRuntime() {
    // initialize the server
    if (this.minecraftServer != null) {
      throw new IllegalStateException("Server already initialized.");
    }

    this.skycadeJavaApi = new SkycadeJavaBootstrapper().skycadeJavaApi();

    // load the configuration
    this.loadConfiguration();

    // initialize the server
    this.minecraftServer = MinecraftServer.init();

    // set the unknown command handler
    this.setUnknownCommandHandler();

    // initialize the server here with configuration variables
    if (runtimeConfiguration.server().proxyEnabled()) {
      VelocityProxy.enable(runtimeConfiguration.server().proxySecret());
    } else {
      MojangAuth.init();
    }
  }

  /**
   * Sets the unknown command handler.
   */
  private void setUnknownCommandHandler() {
    // get the message from the configuration
    String message = this.runtimeConfiguration().messages().commandNotFound();

    // set the unknown command handler
    this.commandManager().setUnknownCommandCallback((sender, command) -> {
      Messenger.miniMessage(sender, message);
    });
  }


  /**
   * Starts the server.
   */
  public void startServer() {
    InetSocketAddress address = new InetSocketAddress(this.runtimeConfiguration().server().host(),
        this.runtimeConfiguration().server().port());

    // start the server
    this.minecraftServer.start(address);
  }

  /**
   * Returns the command manager.
   *
   * @return the command manager
   */
  public CommandManager commandManager() {
    return MinecraftServer.getCommandManager();
  }

  /**
   * Returns the event handler.
   *
   * @return the event handler
   */
  public GlobalEventHandler eventHandler() {
    return MinecraftServer.getGlobalEventHandler();
  }

  /**
   * Returns the connection manager.
   *
   * @return the connection manager
   */
  public ConnectionManager connectionManager() {
    return MinecraftServer.getConnectionManager();
  }

  /**
   * Gets and loads the runtime configuration.
   */
  private void loadConfiguration() {
    this.runtimeConfiguration =
        new ConfigurationLoader<>(Path.of("runtime.conf"), BasicRuntimeConfiguration.class,
            this.getClass()).config();
  }

  /**
   * Gets the minecraft server instance.
   *
   * @return the minecraft server instance
   */
  public MinecraftServer minecraftServer() {
    return minecraftServer;
  }

  /**
   * Gets the runtime configuration.
   *
   * @return the runtime configuration
   */
  public BasicRuntimeConfiguration runtimeConfiguration() {
    return runtimeConfiguration;
  }

  /**
   * Gets the Skycade Java API bootstrapper.
   *
   * @return the Skycade Java API bootstrapper
   */
  public SkycadeJavaApi skycadeJavaApi() {
    return this.skycadeJavaApi;
  }
}
