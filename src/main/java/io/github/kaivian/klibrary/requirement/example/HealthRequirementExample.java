package io.github.kaivian.klibrary.requirement.example;

import io.github.kaivian.klibrary.context.ExecutionContext;
import io.github.kaivian.klibrary.requirement.api.Requirement;
import io.github.kaivian.klibrary.requirement.api.RequirementFactory;
import io.github.kaivian.klibrary.requirement.api.RequirementRegistry;
import io.github.kaivian.klibrary.requirement.api.RequirementResult;
import io.github.kaivian.klibrary.requirement.impl.AbstractRequirement;
import io.github.kaivian.klibrary.service.ServiceProvider;
import org.bukkit.entity.Player;

/**
 * Example demonstrating how to create, register, and use a custom {@link Requirement}.
 *
 * <p>This example creates a "health" requirement that checks whether a player's
 * health is at or above a configured threshold. It demonstrates:</p>
 * <ol>
 *   <li>Extending {@link AbstractRequirement} for built-in helpers ({@code met()}, {@code notMet()}, etc.)</li>
 *   <li>Implementing the {@link Requirement#evaluate(ExecutionContext)} method</li>
 *   <li>Creating a {@link RequirementFactory} for config deserialization</li>
 *   <li>Registering the requirement with the {@link RequirementRegistry}</li>
 * </ol>
 *
 * <h2>YAML Configuration</h2>
 * <pre>{@code
 * requirements:
 *   - type: health
 *     min: 10.0
 *     deny-actions:
 *       - type: message
 *         message: "<red>You need at least 10 health!"
 * }</pre>
 *
 * <h2>Registration (in your plugin's onEnable)</h2>
 * <pre>{@code
 * RequirementRegistry registry = klibrary.getRequirementRegistry();
 * registry.register("health", HealthRequirementExample.factory());
 * }</pre>
 *
 * <h2>Programmatic Usage</h2>
 * <pre>{@code
 * Requirement healthReq = new HealthRequirementExample(services, 10.0);
 * RequirementResult result = healthReq.evaluate(context);
 * if (result.isMet()) {
 *     // Player has enough health
 * } else {
 *     // result.getReason() contains "Health too low: 5.0 < 10.0"
 * }
 * }</pre>
 *
 * <p><b>Note:</b> This is an illustrative example and is NOT intended for production use.</p>
 */
public class HealthRequirementExample extends AbstractRequirement {

    private final double minHealth;

    /**
     * Constructs a new HealthRequirementExample.
     *
     * @param services  the service provider
     * @param minHealth the minimum health threshold (in half-hearts)
     */
    public HealthRequirementExample(ServiceProvider services, double minHealth) {
        super(services);
        this.minHealth = minHealth;
    }

    /**
     * Evaluates whether the player's current health meets the minimum threshold.
     *
     * @param context the execution context containing the target player
     * @return {@code met()} if health ≥ threshold; {@code notMet(reason)} otherwise
     */
    @Override
    public RequirementResult evaluate(ExecutionContext context) {
        return requirePlayer(context).map(player -> {
            if (player.getHealth() >= minHealth) {
                return met();
            }
            return notMet("Health too low: " + player.getHealth() + " < " + minHealth);
        }).orElse(notMet("No player in context for HealthRequirement"));
    }

    // ─── Factory for Config Deserialization ───────────────────────────────

    /**
     * Creates a {@link RequirementFactory} that can deserialize this requirement
     * from configuration.
     *
     * <p>Expected config keys:</p>
     * <ul>
     *   <li>{@code min} — Minimum health threshold (default: 20.0 = full health)</li>
     * </ul>
     *
     * @return the factory for "health" requirement type
     */
    public static RequirementFactory factory() {
        return (config, services) -> {
            double min = config.getDouble("min", 20.0);
            return new HealthRequirementExample(services, min);
        };
    }
}
