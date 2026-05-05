package io.github.kaivian.klibrary.requirement.impl.requirements;

import io.github.kaivian.klibrary.context.ExecutionContext;
import io.github.kaivian.klibrary.requirement.api.RequirementResult;
import io.github.kaivian.klibrary.requirement.impl.AbstractRequirement;
import io.github.kaivian.klibrary.service.ServiceProvider;

/**
 * Checks whether a player has at least a specified amount of money via Vault.
 *
 * <p>If Vault is not installed, the requirement automatically passes
 * (fail-open) to avoid blocking actions due to missing optional dependencies.</p>
 *
 * <p><b>YAML:</b></p>
 * <pre>{@code
 * type: has_money
 * amount: 500.0
 * }</pre>
 */
public class HasMoneyRequirement extends AbstractRequirement {

    private final double amount;

    /**
     * Constructs a new {@code HasMoneyRequirement}.
     *
     * @param services the service provider
     * @param amount   the minimum balance required
     */
    public HasMoneyRequirement(ServiceProvider services, double amount) {
        super(services);
        this.amount = amount;
    }

    /** {@inheritDoc} */
    @Override
    public RequirementResult evaluate(ExecutionContext context) {
        if (!services.getEconomyService().isAvailable()) {
            return met(); // Fail-open when Vault is missing
        }
        return context.getPlayer().map(player ->
                services.getEconomyService().has(player, amount)
                        ? met()
                        : notMet("Insufficient balance: need " + amount)
        ).orElse(notMet("No player in context"));
    }

    /** @return the required amount */
    public double getAmount() { return amount; }
}
