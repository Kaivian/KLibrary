package io.github.kaivian.klibrary.requirement.impl.requirements;

import io.github.kaivian.klibrary.context.ExecutionContext;
import io.github.kaivian.klibrary.requirement.api.RequirementResult;
import io.github.kaivian.klibrary.requirement.impl.AbstractRequirement;
import io.github.kaivian.klibrary.service.ServiceProvider;

/**
 * Checks whether a player has at least a specified amount of experience points.
 *
 * <p><b>YAML:</b></p>
 * <pre>{@code
 * type: has_exp
 * amount: 500
 * }</pre>
 */
public class HasExpRequirement extends AbstractRequirement {

    private final int amount;

    /**
     * Constructs a new {@code HasExpRequirement}.
     *
     * @param services the service provider
     * @param amount   the minimum experience required
     */
    public HasExpRequirement(ServiceProvider services, int amount) {
        super(services);
        this.amount = amount;
    }

    /** {@inheritDoc} */
    @Override
    public RequirementResult evaluate(ExecutionContext context) {
        return context.getPlayer().map(player ->
                player.getTotalExperience() >= amount
                        ? met()
                        : notMet("Insufficient experience: " + player.getTotalExperience() + "/" + amount)
        ).orElse(notMet("No player in context"));
    }

    /** @return the required experience amount */
    public int getAmount() { return amount; }
}
