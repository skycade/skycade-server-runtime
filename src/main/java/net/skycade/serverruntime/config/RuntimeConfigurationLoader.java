package net.skycade.serverruntime.config;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import net.skycade.serverruntime.utils.ResourceUtils;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

/**
 * Runtime configuration loader.
 *
 * @author Jacob Cohen
 */
public class RuntimeConfigurationLoader<T> {

  private final Path path;

  private CommentedConfigurationNode root;

  private final Class<T> configClass;

  public RuntimeConfigurationLoader(Path path, Class<T> clazz) {
    this.path = path;
    this.configClass = clazz;
  }

  private CommentedConfigurationNode load() {
    // if the file doesn't exist, create it
    if (!path.toFile().exists()) {
      try {
        ResourceUtils.extractResource(path.getFileName().toString());
      } catch (IOException | URISyntaxException e) {
        e.printStackTrace();
      }
    }

    HoconConfigurationLoader loader = HoconConfigurationLoader.builder().path(path).build();

    CommentedConfigurationNode root;
    try {
      root = loader.load();
    } catch (IOException e) {
      throw new RuntimeException("Failed to load configuration file", e);
    }

    this.root = root;

    return root;
  }

  /**
   * Returns the configuration object, represented by the class passed in the constructor.
   *
   * @return the configuration object
   */
  public T config() {
    if (this.root == null) {
      this.root = load();
    }

    try {
      return this.root.get(configClass);
    } catch (SerializationException e) {
      throw new RuntimeException(e);
    }
  }
}
