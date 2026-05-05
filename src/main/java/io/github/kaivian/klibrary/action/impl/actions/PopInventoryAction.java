package io.github.kaivian.klibrary.action.impl.actions;

import io.github.kaivian.klibrary.action.api.ActionResult;
import io.github.kaivian.klibrary.action.impl.AbstractAction;
import io.github.kaivian.klibrary.context.ExecutionContext;
import io.github.kaivian.klibrary.service.ServiceProvider;

/**
 * Pops the current inventory view from the player's stack, returning to the previous one.
 */
public class PopInventoryAction extends AbstractAction {

    public PopInventoryAction(ServiceProvider services) {
        super(services);
    }

    @Override
    public ActionResult execute(ExecutionContext context) {
        return requirePlayer(context).map(player -> {
            services.getInventoryManager().ifPresent(manager -> manager.pop(player));
            return success();
        }).orElse(failure("No player in context for PopInventoryAction"));
    }
}
