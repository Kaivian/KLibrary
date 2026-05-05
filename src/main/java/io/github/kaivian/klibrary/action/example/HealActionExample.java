package io.github.kaivian.klibrary.action.example;

import io.github.kaivian.klibrary.action.api.Action;
import io.github.kaivian.klibrary.action.api.ActionFactory;
import io.github.kaivian.klibrary.action.api.ActionRegistry;
import io.github.kaivian.klibrary.action.api.ActionResult;
import io.github.kaivian.klibrary.action.impl.AbstractAction;
import io.github.kaivian.klibrary.action.impl.ActionPipeline;
import io.github.kaivian.klibrary.context.ExecutionContext;
import io.github.kaivian.klibrary.service.ServiceProvider;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Example demonstrating how to create, register, and use a custom {@link Action}.
 *
 * <p>This example creates a "heal" action that restores a player's health and plays
 * a sound effect. It shows the full lifecycle of a custom action:</p>
 * <ol>
 *   <li>Extending {@link AbstractAction} for built-in service access</li>
 *   <li>Implementing the {@link Action#execute(ExecutionContext)} method</li>
 *   <li>Creating an {@link ActionFactory} for config deserialization</li>
 *   <li>Registering the action with the {@link ActionRegistry}</li>
 *   <li>Executing actions via {@link ActionPipeline}</li>
 * </ol>
 *
 * <h2>YAML Configuration</h2>
 * <pre>{@code
 * actions:
 *   - type: heal
 *     amount: 10.0
 * }</pre>
 *
 * <h2>Registration (in your plugin's onEnable)</h2>
 * <pre>{@code
 * ActionRegistry registry = klibrary.getActionRegistry();
 * registry.register("heal", HealAction.factory());
 * }</pre>
 *
 * <h2>Programmatic Usage</h2>
 * <pre>{@code
 * Action heal = new HealAction(services, 10.0);
 * heal.execute(context);
 *
 * // Or via pipeline:
 * ActionPipeline pipeline = ActionPipeline.of(List.of(heal, otherAction));
 * pipeline.execute(context);
 * }</pre>
 *
 * <p><b>Note:</b> This is an illustrative example and is NOT intended for production use.</p>
 */
public class HealActionExample extends AbstractAction {

    private final double amount;

    /**
     * Constructs a new HealAction.
     *
     * @param services the service provider for accessing shared services
     * @param amount   the amount of health to restore (in half-hearts)
     */
    public HealActionExample(ServiceProvider services, double amount) {
        super(services);
        this.amount = amount;
    }

    /**
     * Executes the heal action — restores health and plays a sound.
     *
     * @param context the execution context containing the target player
     * @return {@link ActionResult#success()} if healed, or failure if no player
     */
    @Override
    public ActionResult execute(ExecutionContext context) {
        return requirePlayer(context).map(player -> {
            // Calculate new health, clamping to max
            double newHealth = Math.min(
                    player.getHealth() + amount,
                    player.getMaxHealth()
            );
            player.setHealth(newHealth);

            // Play a healing sound
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);

            return success();
        }).orElse(failure("No player in context for HealAction"));
    }

    // ─── Factory for Config Deserialization ───────────────────────────────

    /**
     * Creates an {@link ActionFactory} that can deserialize this action from configuration.
     *
     * <p>Expected config keys:</p>
     * <ul>
     *   <li>{@code amount} — The health to restore (default: 20.0)</li>
     * </ul>
     *
     * @return the factory for "heal" action type
     */
    public static ActionFactory factory() {
        return (config, services) -> {
            double amount = config.getDouble("amount", 20.0);
            return new HealActionExample(services, amount);
        };
    }
}
