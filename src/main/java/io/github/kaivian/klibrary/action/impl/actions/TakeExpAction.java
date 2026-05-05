package io.github.kaivian.klibrary.action.impl.actions;

import io.github.kaivian.klibrary.action.api.ActionResult;
import io.github.kaivian.klibrary.action.impl.AbstractAction;
import io.github.kaivian.klibrary.context.ExecutionContext;
import io.github.kaivian.klibrary.service.ServiceProvider;

/**
 * Removes experience points from the target player.
 *
 * <p>The player's total experience will not go below zero.</p>
 *
 * <p><b>YAML configuration:</b></p>
 * <pre>{@code
 * type: take_exp
 * amount: 50
 * }</pre>
 */
public class TakeExpAction extends AbstractAction {

    private final int amount;

    /**
     * Constructs a new {@code TakeExpAction}.
     *
     * @param services the service provider
     * @param amount   the amount of experience to remove (must be non-negative)
     */
    public TakeExpAction(ServiceProvider services, int amount) {
        super(services);
        if (amount < 0) {
            throw new IllegalArgumentException("Experience amount cannot be negative");
        }
        this.amount = amount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActionResult execute(ExecutionContext context) {
        return requirePlayer(context).map(player -> {
            int newExp = Math.max(0, player.getTotalExperience() - amount);
            player.setTotalExperience(0);
            player.setLevel(0);
            player.setExp(0);
            player.giveExp(newExp);
            return success();
        }).orElse(failure("No player in context for TakeExpAction"));
    }

    /**
     * Returns the experience amount.
     *
     * @return the amount of experience to remove
     */
    public int getAmount() {
        return amount;
    }
}
