package net.skycade.serverruntime.config;

import java.util.List;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

/**
 * Basic runtime configuration.
 *
 * @author Jacob Cohen
 */
@ConfigSerializable
public final class BasicRuntimeConfiguration {

  /**
   * The server section.
   */
  private Server server;

  /**
   * The messages section.
   */
  private Messages messages;

  /**
   * Returns the server section.
   *
   * @return the server section
   */
  public Server server() {
    return server;
  }

  /**
   * Returns the messages section.
   *
   * @return the messages section
   */
  public Messages messages() {
    return messages;
  }

  @ConfigSerializable
  public static class Server {

    /**
     * The host to bind to.
     */
    private String host;

    /**
     * The port to bind to.
     */
    private int port;

    /**
     * The MOTD to display.
     */
    private List<String> motd;

    /**
     * Governs whether the server should be in proxy mode.
     */
    private boolean proxyEnabled;

    /**
     * The proxy secret.
     */
    private String proxySecret;

    public String host() {
      return host;
    }

    public int port() {
      return port;
    }

    public List<String> motd() {
      return motd;
    }

    public boolean proxyEnabled() {
      return proxyEnabled;
    }

    public String proxySecret() {
      return proxySecret;
    }
  }

  @ConfigSerializable
  public static class Messages {

    /**
     * The message to display when a player enters a command that doesn't exist.
     */
    private String commandNotFound;

    public String commandNotFound() {
      return commandNotFound;
    }
  }
}
