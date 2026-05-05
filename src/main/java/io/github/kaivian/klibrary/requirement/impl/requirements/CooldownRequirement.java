package io.github.kaivian.klibrary.requirement.impl.requirements;

import io.github.kaivian.klibrary.context.ExecutionContext;
import io.github.kaivian.klibrary.requirement.api.RequirementResult;
import io.github.kaivian.klibrary.requirement.impl.AbstractRequirement;
import io.github.kaivian.klibrary.service.ServiceProvider;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enforces a per-player cooldown before the requirement can pass again.
 *
 * <p>The cooldown is tracked in-memory per player UUID. When a player
 * first passes this requirement, a timestamp is recorded. Subsequent
 * evaluations within the cooldown period will fail.</p>
 *
 * <p><b>YAML:</b></p>
 * <pre>{@code
 * type: cooldown
 * seconds: 60      # cooldown duration in seconds
 * key: "daily_reward"  # optional unique key for this cooldown
 * }</pre>
 */
public class CooldownRequirement extends AbstractRequirement {

    private static final Map<String, Map<UUID, Long>> COOLDOWN_STORE = new ConcurrentHashMap<>();

    private final long cooldownMillis;
    private final String cooldownKey;

    /**
     * Constructs a new {@code CooldownRequirement}.
     *
     * @param services      the service provider
     * @param cooldownSeconds the cooldown duration in seconds
     * @param cooldownKey   a unique identifier for this cooldown (used for tracking)
     */
    public CooldownRequirement(ServiceProvider services, long cooldownSeconds, String cooldownKey) {
        super(services);
        this.cooldownMillis = cooldownSeconds * 1000L;
        this.cooldownKey = cooldownKey != null ? cooldownKey : "default";
    }

    /** {@inheritDoc} */
    @Override
    public RequirementResult evaluate(ExecutionContext context) {
        return context.getPlayer().map(player -> {
            UUID uuid = player.getUniqueId();
            Map<UUID, Long> store = COOLDOWN_STORE.computeIfAbsent(cooldownKey,
                    k -> new ConcurrentHashMap<>());

            Long lastUse = store.get(uuid);
            long now = System.currentTimeMillis();

            if (lastUse != null && (now - lastUse) < cooldownMillis) {
                long remaining = (cooldownMillis - (now - lastUse)) / 1000;
                return notMet("Cooldown active: " + remaining + "s remaining");
            }

            // Mark the cooldown
            store.put(uuid, now);
            return met();
        }).orElse(notMet("No player in context"));
    }

    /**
     * Clears cooldown data for a specific player across all keys.
     *
     * @param playerUuid the player UUID to clear
     */
    public static void clearCooldowns(UUID playerUuid) {
        COOLDOWN_STORE.values().forEach(store -> store.remove(playerUuid));
    }

    /**
     * Clears all cooldown data.
     */
    public static void clearAllCooldowns() {
        COOLDOWN_STORE.clear();
    }

    /** @return the cooldown duration in milliseconds */
    public long getCooldownMillis() { return cooldownMillis; }

    /** @return the cooldown key */
    public String getCooldownKey() { return cooldownKey; }
}
