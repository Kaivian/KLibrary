package io.github.kaivian.klibrary.requirement.impl.requirements;

import io.github.kaivian.klibrary.context.ExecutionContext;
import io.github.kaivian.klibrary.requirement.api.RequirementResult;
import io.github.kaivian.klibrary.requirement.impl.AbstractRequirement;
import io.github.kaivian.klibrary.service.ServiceProvider;

/**
 * Checks whether a player has at least a specified level.
 *
 * <p><b>YAML:</b></p>
 * <pre>{@code
 * type: has_level
 * level: 30
 * }</pre>
 */
public class HasLevelRequirement extends AbstractRequirement {

    private final int level;

    /**
     * Constructs a new {@code HasLevelRequirement}.
     *
     * @param services the service provider
     * @param level    the minimum level required
     */
    public HasLevelRequirement(ServiceProvider services, int level) {
        super(services);
        this.level = level;
    }

    /** {@inheritDoc} */
    @Override
    public RequirementResult evaluate(ExecutionContext context) {
        return context.getPlayer().map(player ->
                player.getLevel() >= level
                        ? met()
                        : notMet("Insufficient level: " + player.getLevel() + "/" + level)
        ).orElse(notMet("No player in context"));
    }

    /** @return the required level */
    public int getLevel() { return level; }
}
