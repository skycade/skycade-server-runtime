package net.skycade.serverruntime.space.dimension;

import net.minestom.server.MinecraftServer;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.DimensionType;

/**
 * A dimension that has fullbright.
 *
 * @author Jacob Cohen
 */
public class FullbrightDimension {
  public static final DimensionType INSTANCE =
      DimensionType.builder(NamespaceID.from("minestom:full_bright")).ambientLight(2.0f).build();

  static {
    MinecraftServer.getDimensionTypeManager().addDimension(INSTANCE);
  }
}