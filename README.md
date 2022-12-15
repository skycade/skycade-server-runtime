# skycade-server-runtime
Skycade's official custom server runtime that is the building block of all custom games produced in-house

-------


**Guidelines**

A checkstyle.xml is required at all times.
Configure your IDE to use the checkstyle guidelines to format your code before any commits.

**Example Game Implementation**
```java
/**
 * Main class of the runtime.
 *
 * @author Jacob Cohen
 */
public class EmptyGameThatDoesNothing extends Game {

  /**
   * The constructor for the main class.
   */
  public EmptyGameThatDoesNothing() {
    this.init();
  }

  /**
   * The main method.
   *
   * @param args the arguments
   */
  public static void main(String[] args) {
    new EmptyGameThatDoesNothing();
  }

  @Override
  protected List<Command> commands() {
    return List.of(new TestCommand());
  }

  @Override
  protected List<EventNode<Event>> eventNodes() {
    return List.of(new GameEventHandler("test") {
      @Override
      protected void init() {
        this.on(PlayerLoginEvent.class, event -> {
          event.getPlayer().setRespawnPoint(new Pos(0, 20, 0));
        });
        this.on(PlayerChatEvent.class, event -> {
          event.setChatFormat(
              (e) -> Component.text(e.getPlayer().getUsername(), NamedTextColor.GRAY)
                  .append(Component.text(": ", NamedTextColor.DARK_GRAY))
                  .append(Component.text(e.getMessage())));
        });
      }
    }.node());
  }

  @Override
  protected List<InstanceContainer> instances() {
    return List.of(TestGameSpace.INSTANCE);
  }

  @Override
  protected InstanceContainer provideSpawningInstance(Player player) {
    return TestGameSpace.INSTANCE;
  }

  static class TestGameSpace extends GameSpace {
    public static final TestGameSpace INSTANCE = new TestGameSpace();

    public TestGameSpace() {
      super(UUID.randomUUID(), FullbrightDimension.INSTANCE);
    }

    @Override
    public void init() {
      setGenerator(unit -> unit.modifier().fillHeight(0, 20, Block.STONE));
      setTimeRate(0);

      EventNode<InstanceEvent> eventNode = eventNode();
      eventNode.addListener(AddEntityToInstanceEvent.class, event -> {
        final Entity entity = event.getEntity();
        if (entity instanceof Player player) {
          if (player.getInstance() == null) {
            MinecraftServer.getSchedulerManager().scheduleNextTick(() -> {
              // todo: implement onFirstSpawn
            });
          }
        }
      }).addListener(ItemDropEvent.class, event -> event.setCancelled(true));
    }
  }


  static final class TestCommand extends Command {

    public TestCommand() {
      super("test");

      setDefaultExecutor(this::usage);
    }

    /**
     * The default executor for the command.
     *
     * @param sender  the sender
     * @param context the context
     */
    private void usage(CommandSender sender, CommandContext context) {
      final Collection<Player> players = MinecraftServer.getConnectionManager().getOnlinePlayers();
      final int playerCount = players.size();
      sender.sendMessage(Component.text("Total players: " + playerCount));
      final int limit = 15;
      if (playerCount <= limit) {
        for (final Player player : players) {
          sender.sendMessage(Component.text(player.getUsername()));
        }
      } else {
        for (final Player player : players.stream().limit(limit).toList()) {
          sender.sendMessage(Component.text(player.getUsername()));
        }
        sender.sendMessage(Component.text("..."));
      }
    }
  }
}
```
