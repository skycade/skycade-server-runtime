package net.skycade.serverruntime;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.util.Collection;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.adventure.audience.Audiences;
import net.minestom.server.command.CommandManager;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.extras.velocity.VelocityProxy;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.timer.TaskSchedule;
import net.skycade.serverruntime.config.BasicRuntimeConfiguration;
import net.skycade.serverruntime.config.RuntimeConfigurationLoader;
import net.skycade.serverruntime.message.Messenger;
import net.skycade.serverruntime.space.GameSpace;

/**
 * Main class of the runtime.
 *
 * @author Jacob Cohen
 */
public class Main {

  /**
   * The main method.
   *
   * @param args the arguments
   */
  public static void main(String[] args) {

    // load the configuration
    @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
    BasicRuntimeConfiguration configuration =
        new RuntimeConfigurationLoader<>(Path.of("runtime.conf"),
            BasicRuntimeConfiguration.class).config();

    // initialize the server
    @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
    MinecraftServer minecraftServer = MinecraftServer.init();

    // register commands
    CommandManager manager = MinecraftServer.getCommandManager();
    manager.setUnknownCommandCallback((sender, c) -> Messenger.warn(sender, "Command not found."));
    // todo: register here with {@link CommandManager#register(Command)}

    // register events
    GlobalEventHandler handler = MinecraftServer.getGlobalEventHandler();

    // when a player logins
    handler.addListener(PlayerLoginEvent.class, event -> {
      final Player player = event.getPlayer();
      event.setSpawningInstance(GameSpace.DEFAULT);
      player.setRespawnPoint(new Pos(0, 50, 0));

      Audiences.all().filterAudience(audience -> audience instanceof Player && audience != player)
          .sendMessage(Component.text(player.getUsername() + " entered", NamedTextColor.GREEN));
    });

    // when a player spawns
    handler.addListener(PlayerSpawnEvent.class, event -> {
      if (!event.isFirstSpawn()) {
        return;
      }
      final Player player = event.getPlayer();
      Messenger.info(player, "You joined the game.");
      player.setGameMode(GameMode.ADVENTURE);
      player.playSound(Sound.sound(SoundEvent.ENTITY_PLAYER_LEVELUP, Sound.Source.MASTER, 1f, 1f));
      player.setEnableRespawnScreen(false);
    });

    // when a player leaves
    handler.addListener(PlayerDisconnectEvent.class, event -> Audiences.all().sendMessage(
        Component.text(event.getPlayer().getUsername() + "  exited", NamedTextColor.RED)));

    // when a player chats
    handler.addListener(PlayerChatEvent.class, chatEvent -> {
      chatEvent.setChatFormat(
          (event) -> Component.text(event.getEntity().getUsername(), NamedTextColor.DARK_GRAY)
              .append(Component.text(") ", NamedTextColor.DARK_GRAY)
                  .append(Component.text(event.getMessage(), NamedTextColor.WHITE))));
    });

    // header/footer
    MinecraftServer.getSchedulerManager().scheduleTask(() -> {
      Collection<Player> players = MinecraftServer.getConnectionManager().getOnlinePlayers();
      if (players.isEmpty()) {
        return;
      }

      Component header = Component.text("Game Server - ", NamedTextColor.DARK_GRAY)
          .append(Component.text(players.size(), NamedTextColor.GRAY))
          .append(Component.text(" here", NamedTextColor.GRAY));
      Component footer = Component.text("play.skycade.net", NamedTextColor.WHITE);

      players.forEach(player -> player.sendPlayerListHeaderAndFooter(header, footer));
    }, TaskSchedule.tick(20), TaskSchedule.tick(20));

    if (configuration.proxyEnabled()) {
      VelocityProxy.enable(configuration.proxySecret());
    } else {
      MojangAuth.init();
    }

    try {
      minecraftServer.start(new InetSocketAddress(InetAddress.getByName(configuration.host()),
          configuration.port()));
    } catch (UnknownHostException e) {
      throw new RuntimeException(e);
    }
    System.out.println("Server startup done.");
  }
}