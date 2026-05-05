package io.github.kaivian.klibrary.requirement.impl.requirements;

import io.github.kaivian.klibrary.context.ExecutionContext;
import io.github.kaivian.klibrary.inventory.api.InventoryView;
import io.github.kaivian.klibrary.requirement.api.RequirementResult;
import io.github.kaivian.klibrary.requirement.impl.AbstractRequirement;
import io.github.kaivian.klibrary.service.ServiceProvider;
import org.bukkit.inventory.Inventory;

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
        return requirePlayer(context).map(player -> {
            Inventory topInventory = player.getOpenInventory().getTopInventory();
            if (topInventory == null) {
                return notMet("No inventory open");
            }
            
            return services.getInventoryManager()
                    .map(manager -> {
                        // We check active view by matching the ID or we can just rely on the open inventory title/size
                        // But since we can't easily fetch active views by inventory object without exposing it from manager,
                        // we can fetch the provider and check if title and size match.
                        // Or a better way: pass manager down or keep it accessible.
                        // Actually, wait, the simplest way is to check the title or menu type if we don't expose activeViews.
                        // Let's assume we expose a way to get the current provider ID from the manager.
                        Optional<String> activeId = manager.getActiveViewId(player);
                        if (activeId.isPresent() && activeId.get().equals(inventoryId)) {
                            return met();
                        }
                        return notMet("Required inventory not open");
                    })
                    .orElse(notMet("Inventory manager not available"));
        }).orElse(notMet("No player in context for InventoryOpenRequirement"));
    }
}
