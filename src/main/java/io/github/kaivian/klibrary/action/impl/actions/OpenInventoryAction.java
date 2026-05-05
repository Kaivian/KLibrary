package io.github.kaivian.klibrary.action.impl.actions;

import io.github.kaivian.klibrary.action.api.ActionResult;
import io.github.kaivian.klibrary.action.impl.AbstractAction;
import io.github.kaivian.klibrary.context.ExecutionContext;
import io.github.kaivian.klibrary.inventory.api.InventoryContext;
import io.github.kaivian.klibrary.service.ServiceProvider;

/**
 * Opens a specified GUI inventory for the player.
 */
public class OpenInventoryAction extends AbstractAction {

    private final String inventoryId;

    public OpenInventoryAction(ServiceProvider services, String inventoryId) {
        super(services);
        this.inventoryId = inventoryId;
    }

    @Override
    public ActionResult execute(ExecutionContext context) {
        return requirePlayer(context).map(player -> {
            services.getInventoryManager().ifPresent(manager -> {
                // Pass a new inventory context
                manager.push(player, inventoryId, InventoryContext.from(context).build());
            });
            return success();
        }).orElse(failure("No player in context for OpenInventoryAction"));
    }
}
