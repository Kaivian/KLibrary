# Requirement System

Requirements dictate the conditions under which an event or action should be allowed to process.

## Built-in Requirements

KLibrary usually ships with several basic requirements:

- `permission`: Checks if the player has a specific permission node.
- `money`: Integrates with Vault to check if the player has enough balance.
- `inventory-open`: Checks if a player currently has an active KLibrary inventory session.

## Logical Grouping (AND / OR)

KLibrary's requirement engine natively supports grouping logic.

When defining requirements via the config, you can specify `type: AND` or `type: OR` and supply nested lists.

```yaml
requirements:
  can-open-crate:
    type: "AND"
    requirements:
      has-key:
        type: "item"
        material: "TRIPWIRE_HOOK"
        amount: 1
      is-vip:
        type: "permission"
        permission: "server.vip"
        deny-actions:
          - "[message] <red>Only VIPs can open this crate!"
```

If the entire group evaluates to `false`, the group's `deny-actions` will be executed.

## Custom Requirements

Creating custom requirements is identical to the Action System workflow.

### 1. Extend the Requirement Abstraction

```java
import io.github.kaivian.klibrary.requirement.api.Requirement;
import io.github.kaivian.klibrary.requirement.api.RequirementResult;
import io.github.kaivian.klibrary.action.api.Action;
import org.bukkit.entity.Player;
import java.util.List;

public class LevelRequirement extends Requirement {
    private final int minLevel;

    public LevelRequirement(int minLevel, List<Action> denyActions) {
        super(denyActions); // Pass deny actions to the superclass
        this.minLevel = minLevel;
    }

    @Override
    public RequirementResult evaluate(Player player) {
        if (player.getLevel() >= minLevel) {
            return RequirementResult.SUCCESS;
        } else {
            return RequirementResult.failure(this); // Triggers execution of denyActions
        }
    }
}
```

### 2. Register the Requirement

```java
// Register via your factory instance
requirementFactory.register("level", node -> {
    // Deserialize arguments from the ConfigNode
    int level = node.getInt("level", 1);
    List<Action> denyActions = node.getActionList("deny-actions");
    return new LevelRequirement(level, denyActions);
});
```
