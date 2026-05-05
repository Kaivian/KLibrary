package io.github.kaivian.klibrary.action.impl.actions;

import io.github.kaivian.klibrary.action.api.ActionResult;
import io.github.kaivian.klibrary.action.impl.AbstractAction;
import io.github.kaivian.klibrary.context.ExecutionContext;
import io.github.kaivian.klibrary.service.ServiceProvider;

/**
 * Gives experience points to the target player.
 *
 * <p><b>YAML configuration:</b></p>
 * <pre>{@code
 * type: give_exp
 * amount: 100
 * }</pre>
 */
public class GiveExpAction extends AbstractAction {

    private final int amount;

    /**
     * Constructs a new {@code GiveExpAction}.
     *
     * @param services the service provider
     * @param amount   the amount of experience to give (must be non-negative)
     */
    public GiveExpAction(ServiceProvider services, int amount) {
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
            player.giveExp(amount);
            return success();
        }).orElse(failure("No player in context for GiveExpAction"));
    }

    /**
     * Returns the experience amount.
     *
     * @return the amount of experience
     */
    public int getAmount() {
        return amount;
    }
}
