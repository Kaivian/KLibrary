package io.github.kaivian.klibrary.requirement.impl.requirements;

import io.github.kaivian.klibrary.context.ExecutionContext;
import io.github.kaivian.klibrary.requirement.api.RequirementResult;
import io.github.kaivian.klibrary.requirement.impl.AbstractRequirement;
import io.github.kaivian.klibrary.service.ServiceProvider;

import java.util.Optional;

/**
 * Requires the player to currently have a specific inventory open.
 */
public class InventoryOpenRequirement extends AbstractRequirement {

    private final String inventoryId;

    public InventoryOpenRequirement(ServiceProvider services, String inventoryId) {
        super(services);
        this.inventoryId = inventoryId;
    }

    @Override
    public RequirementResult evaluate(ExecutionContext context) {
        return requirePlayer(context).map(player ->
            services.getInventoryManager()
                    .map(manager -> {
                        Optional<String> activeId = manager.getActiveViewId(player);
                        if (activeId.isPresent() && activeId.get().equals(inventoryId)) {
                            return met();
                        }
                        return notMet("Required inventory not open");
                    })
                    .orElse(notMet("Inventory manager not available"))
        ).orElse(notMet("No player in context for InventoryOpenRequirement"));
    }
}
