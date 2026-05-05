package io.github.kaivian.klibrary.requirement.impl.requirements;

import io.github.kaivian.klibrary.context.ExecutionContext;
import io.github.kaivian.klibrary.requirement.api.RequirementResult;
import io.github.kaivian.klibrary.requirement.impl.AbstractRequirement;
import io.github.kaivian.klibrary.service.ServiceProvider;

/**
 * Requires the player to have an inventory history of a specific size (e.g. for Back buttons).
 */
public class InventoryHistoryRequirement extends AbstractRequirement {

    private final int minHistorySize;

    public InventoryHistoryRequirement(ServiceProvider services, int minHistorySize) {
        super(services);
        this.minHistorySize = minHistorySize;
    }

    @Override
    public RequirementResult evaluate(ExecutionContext context) {
        return requirePlayer(context).map(player -> {
            return services.getInventoryManager()
                    .map(manager -> {
                        if (manager.getStackSize(player) >= minHistorySize) {
                            return met();
                        }
                        return notMet("Not enough inventory history");
                    })
                    .orElse(notMet("Inventory manager not available"));
        }).orElse(notMet("No player in context for InventoryHistoryRequirement"));
    }
}
