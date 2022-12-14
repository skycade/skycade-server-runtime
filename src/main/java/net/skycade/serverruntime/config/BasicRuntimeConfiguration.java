package net.skycade.serverruntime.config;

import java.util.List;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

/**
 * Basic runtime configuration.
 *
 * @author Jacob Cohen
 */
@ConfigSerializable
public class BasicRuntimeConfiguration {
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
