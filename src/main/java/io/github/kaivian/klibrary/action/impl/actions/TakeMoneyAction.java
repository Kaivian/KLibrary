package io.github.kaivian.klibrary.action.impl.actions;

import io.github.kaivian.klibrary.action.api.ActionResult;
import io.github.kaivian.klibrary.action.impl.AbstractAction;
import io.github.kaivian.klibrary.context.ExecutionContext;
import io.github.kaivian.klibrary.service.ServiceProvider;

/**
 * Withdraws money from the target player's account via Vault.
 *
 * <p>If Vault is not installed, the action is skipped with a warning.</p>
 *
 * <p><b>YAML configuration:</b></p>
 * <pre>{@code
 * type: take_money
 * amount: 50.0
 * }</pre>
 */
public class TakeMoneyAction extends AbstractAction {

    private final double amount;

    /**
     * Constructs a new {@code TakeMoneyAction}.
     *
     * @param services the service provider
     * @param amount   the amount to withdraw (must be non-negative)
     */
    public TakeMoneyAction(ServiceProvider services, double amount) {
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
            return skipped("Vault not found, skipping money withdrawal");
        }
        return requirePlayer(context).map(player -> {
            boolean success = services.getEconomyService().withdraw(player, amount);
            return success ? success() : failure("Failed to withdraw money (insufficient funds?)");
        }).orElse(failure("No player in context for TakeMoneyAction"));
    }

    /** @return the withdrawal amount */
    public double getAmount() { return amount; }
}
