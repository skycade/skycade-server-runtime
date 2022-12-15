package net.skycade.serverruntime.utils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import net.skycade.serverruntime.api.ServerRuntime;

/**
 * Utility class for extracting resources from the jar.
 *
 * @author Jacob Cohen
 */
public final class ResourceUtils {

  /**
   * Extracts a resource from the jar file.
   *
   * @param source the source
   * @throws URISyntaxException if the URI is invalid
   * @throws IOException        if an I/O error occurs
   */
  public static void extractResource(String source) throws URISyntaxException, IOException {
    final URI uri = Objects.requireNonNull(ResourceUtils.class.getResource("/" + source)).toURI();
    FileSystem fileSystem = null;

    // log the resource being extracted
    ServerRuntime.LOGGER.info("Extracting resource: " + source);

    // create a new filesystem if it's a jar file
    if (uri.toString().startsWith("jar:")) {
      fileSystem = FileSystems.newFileSystem(uri, Map.of("create", "true"));
    }

    try {
      final Path resourcesPath = Paths.get(uri);
      final Path targetPath = Path.of(source);

      if (Files.exists(targetPath)) {
        try (Stream<Path> pathStream = Files.walk(targetPath)) {
          pathStream.sorted(Comparator.reverseOrder()).forEach(path -> {
            try {
              Files.delete(path);
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          });
        }
      }
      Files.walkFileTree(resourcesPath, new SimpleFileVisitor<>() {
        @Override
        public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs)
            throws IOException {
          Path currentTarget = targetPath.resolve(resourcesPath.relativize(dir).toString());
          Files.createDirectories(currentTarget);
          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs)
            throws IOException {
          final Path to = targetPath.resolve(resourcesPath.relativize(file).toString());
          Files.copy(file, to, StandardCopyOption.REPLACE_EXISTING);
          return FileVisitResult.CONTINUE;
        }
      });
    } finally {
      if (fileSystem != null) {
        fileSystem.close();
      }
    }
  }
}
