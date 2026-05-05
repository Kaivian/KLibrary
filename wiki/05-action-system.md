# Action System

The Action System provides a streamlined way to execute defined behaviors for players dynamically.

## Built-in Actions

KLibrary typically provides out-of-the-box standard actions:

- `[message] <text>`: Sends a MiniMessage parsed text to the player.
- `[console] <command>`: Executes a command as the console.
- `[player] <command>`: Forces the player to execute a command.
- `[sound] <sound>;<volume>;<pitch>`: Plays a sound to the player.
- `[close]`: Closes the player's active inventory.

## Using Actions via the API

You can execute actions programmatically:

```java
List<String> rawActions = Arrays.asList(
    "[message] <green>Action executed!",
    "[sound] BLOCK_NOTE_BLOCK_PLING;1.0;2.0"
);

List<Action> actions = actionFactory.deserializeActions(rawActions);

for (Action action : actions) {
    action.execute(player);
}
```

## Creating Custom Actions

Creating a new Action involves extending the base `Action` abstraction.

### 1. Define the Action Class

```java
import io.github.kaivian.klibrary.action.api.Action;
import org.bukkit.entity.Player;

public class HealAction extends Action {
    private final double amount;

    public HealAction(String rawData) {
        // rawData is the string after the tag: e.g. "10.0" from "[heal] 10.0"
        try {
            this.amount = Double.parseDouble(rawData.trim());
        } catch (NumberFormatException e) {
            this.amount = 20.0; // Fallback
        }
    }

    @Override
    public void execute(Player player) {
        double newHealth = Math.min(20.0, player.getHealth() + amount);
        player.setHealth(newHealth);
    }
}
```

### 2. Register the Action

Register your custom action using the `ActionFactory` you instantiated on plugin start.

```java
// Registering the "[heal]" tag to map to HealAction
actionFactory.register("heal", HealAction.class);
```

Now, anywhere in your configuration, `[heal] 5.0` will resolve to your custom class and heal the player when executed!
