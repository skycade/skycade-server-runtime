package net.skycade.serverruntime.message;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import net.minestom.server.MinecraftServer;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.timer.TaskSchedule;

/**
 * A class that contains methods for sending messages to players.
 *
 * @author Jacob Cohen
 */
public final class Messenger {
  public static final TextColor INFO_COLOR = TextColor.fromHexString("#9fb6cd");
  public static final TextColor ORANGE_COLOR = TextColor.fromHexString("#e49b0f");

  public static void info(Audience audience, String message) {
    info(audience, Component.text(message));
  }

  public static void info(Audience audience, Component message) {
    audience.sendMessage(
        Component.text("! ", INFO_COLOR).append(message.color(NamedTextColor.GRAY)));
  }

  public static void warn(Audience audience, String message) {
    warn(audience, Component.text(message));
  }

  public static void warn(Audience audience, Component message) {
    audience.sendMessage(
        Component.text("! ", ORANGE_COLOR).append(message.color(NamedTextColor.GRAY)));
  }

  /**
   * Sends a countdown to the specified audience.
   *
   * @param audience the audience to send the countdown to.
   * @param from     the number to start the countdown from.
   * @return a completable future that completes when the countdown is over.
   */
  public static CompletableFuture<Void> countdown(Audience audience, int from) {
    final CompletableFuture<Void> completableFuture = new CompletableFuture<>();
    final AtomicInteger countdown = new AtomicInteger(from);
    MinecraftServer.getSchedulerManager().submitTask(() -> {
      final int count = countdown.getAndDecrement();
      if (count <= 0) {
        completableFuture.complete(null);
        return TaskSchedule.stop();
      }

      audience.showTitle(
          Title.title(Component.text(count, NamedTextColor.GREEN), Component.empty()));
      audience.playSound(Sound.sound(SoundEvent.BLOCK_NOTE_BLOCK_PLING, Sound.Source.BLOCK, 1, 1),
          Sound.Emitter.self());

      return TaskSchedule.seconds(1);
    });

    return completableFuture;
  }
}
