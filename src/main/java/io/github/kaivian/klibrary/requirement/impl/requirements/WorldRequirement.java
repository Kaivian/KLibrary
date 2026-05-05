package io.github.kaivian.klibrary.requirement.impl.requirements;

import io.github.kaivian.klibrary.context.ExecutionContext;
import io.github.kaivian.klibrary.requirement.api.RequirementResult;
import io.github.kaivian.klibrary.requirement.impl.AbstractRequirement;
import io.github.kaivian.klibrary.service.ServiceProvider;

import java.util.List;
import java.util.Locale;

/**
 * Checks whether a player is in one of the specified worlds.
 *
 * <p><b>YAML:</b></p>
 * <pre>{@code
 * type: world
 * worlds:
 *   - "world"
 *   - "world_nether"
 * }</pre>
 */
public class WorldRequirement extends AbstractRequirement {

    private final List<String> worlds;

    /**
     * Constructs a new {@code WorldRequirement}.
     *
     * @param services the service provider
     * @param worlds   the allowed world names (case-insensitive)
     */
    public WorldRequirement(ServiceProvider services, List<String> worlds) {
        super(services);
        this.worlds = worlds.stream().map(w -> w.toLowerCase(Locale.ROOT)).toList();
    }

    /** {@inheritDoc} */
    @Override
    public RequirementResult evaluate(ExecutionContext context) {
        return context.getPlayer().map(player -> {
            String currentWorld = player.getWorld().getName().toLowerCase(Locale.ROOT);
            return worlds.contains(currentWorld)
                    ? met()
                    : notMet("Player is in world '" + player.getWorld().getName()
                    + "' but required: " + worlds);
        }).orElse(notMet("No player in context"));
    }

    /** @return the allowed world names */
    public List<String> getWorlds() { return worlds; }
}
