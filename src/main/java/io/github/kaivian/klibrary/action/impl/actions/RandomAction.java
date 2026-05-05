package io.github.kaivian.klibrary.action.impl.actions;

import io.github.kaivian.klibrary.action.api.Action;
import io.github.kaivian.klibrary.action.api.ActionResult;
import io.github.kaivian.klibrary.action.impl.AbstractAction;
import io.github.kaivian.klibrary.context.ExecutionContext;
import io.github.kaivian.klibrary.service.ServiceProvider;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Picks and executes a random action from a list of child actions.
 *
 * <p><b>YAML configuration:</b></p>
 * <pre>{@code
 * type: random
 * actions:
 *   - type: message
 *     message: "<green>You won a diamond!"
 *   - type: message
 *     message: "<yellow>Better luck next time."
 *   - type: give_money
 *     amount: 50
 * }</pre>
 */
public class RandomAction extends AbstractAction {

    private final List<Action> childActions;

    /**
     * Constructs a new {@code RandomAction}.
     *
     * @param services     the service provider
     * @param childActions the pool of actions to randomly pick from
     */
    public RandomAction(ServiceProvider services, List<Action> childActions) {
        super(services);
        this.childActions = List.copyOf(childActions);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Selects one random action from the child pool and executes it.
     * Returns the result of the chosen action.</p>
     */
    @Override
    public ActionResult execute(ExecutionContext context) {
        if (childActions.isEmpty()) {
            return skipped("No child actions available for RandomAction");
        }

        int index = ThreadLocalRandom.current().nextInt(childActions.size());
        return childActions.get(index).execute(context);
    }

    /** @return the child action pool */
    public List<Action> getChildActions() { return childActions; }
}
