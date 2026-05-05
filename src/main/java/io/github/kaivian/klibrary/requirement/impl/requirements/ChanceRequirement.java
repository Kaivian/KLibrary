package io.github.kaivian.klibrary.requirement.impl.requirements;

import io.github.kaivian.klibrary.context.ExecutionContext;
import io.github.kaivian.klibrary.requirement.api.RequirementResult;
import io.github.kaivian.klibrary.requirement.impl.AbstractRequirement;
import io.github.kaivian.klibrary.service.ServiceProvider;

import java.util.concurrent.ThreadLocalRandom;

/**
 * A probabilistic requirement that passes based on a random chance.
 *
 * <p>The chance is a value between 0.0 (never passes) and 1.0 (always passes).</p>
 *
 * <p><b>YAML:</b></p>
 * <pre>{@code
 * type: chance
 * chance: 0.5     # 50% chance of passing
 * }</pre>
 */
public class ChanceRequirement extends AbstractRequirement {

    private final double chance;

    /**
     * Constructs a new {@code ChanceRequirement}.
     *
     * @param services the service provider
     * @param chance   the probability of passing (0.0 to 1.0)
     */
    public ChanceRequirement(ServiceProvider services, double chance) {
        super(services);
        this.chance = Math.clamp(chance, 0.0, 1.0);
    }

    /** {@inheritDoc} */
    @Override
    public RequirementResult evaluate(ExecutionContext context) {
        double roll = ThreadLocalRandom.current().nextDouble();
        return roll < chance
                ? met()
                : notMet("Chance check failed: rolled " + String.format("%.2f", roll)
                + " (needed < " + String.format("%.2f", chance) + ")");
    }

    /** @return the chance value (0.0-1.0) */
    public double getChance() { return chance; }
}
