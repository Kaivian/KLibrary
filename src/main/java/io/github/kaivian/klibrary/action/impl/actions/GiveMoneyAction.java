package io.github.kaivian.klibrary.action.impl.actions;

import io.github.kaivian.klibrary.action.api.ActionResult;
import io.github.kaivian.klibrary.action.impl.AbstractAction;
import io.github.kaivian.klibrary.context.ExecutionContext;
import io.github.kaivian.klibrary.service.ServiceProvider;

/**
 * Deposits money into the target player's account via Vault.
 *
 * <p>If Vault is not installed, the action is skipped with a warning.</p>
 *
 * <p><b>YAML configuration:</b></p>
 * <pre>{@code
 * type: give_money
 * amount: 100.0
 * }</pre>
 */
public class GiveMoneyAction extends AbstractAction {

    private final double amount;

    /**
     * Constructs a new {@code GiveMoneyAction}.
     *
     * @param services the service provider
     * @param amount   the amount to deposit (must be non-negative)
     */
    public GiveMoneyAction(ServiceProvider services, double amount) {
        super(services);
        if (amount < 0) {
            throw new IllegalArgumentException("Money amount cannot be negative");
        }
        this.amount = amount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActionResult execute(ExecutionContext context) {
        if (!services.getEconomyService().isAvailable()) {
            return skipped("Vault not found, skipping money deposit");
        }
        return requirePlayer(context).map(player -> {
            boolean success = services.getEconomyService().deposit(player, amount);
            return success ? success() : failure("Failed to deposit money");
        }).orElse(failure("No player in context for GiveMoneyAction"));
    }

    /** @return the deposit amount */
    public double getAmount() { return amount; }
}
