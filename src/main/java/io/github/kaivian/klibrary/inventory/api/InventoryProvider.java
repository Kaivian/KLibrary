package io.github.kaivian.klibrary.inventory.api;

import org.jetbrains.annotations.NotNull;

/**
 * Defines how an inventory is initialized and how its contents are refreshed.
 */
public interface InventoryProvider extends InventoryHandler {

    /**
     * Returns the unique ID of this provider (e.g., "main_menu").
     * @return the provider ID
     */
    @NotNull String getId();

    /**
     * Creates a new InventoryView for the given context.
     * @param context the inventory context
     * @return a new InventoryView instance
     */
    @NotNull InventoryView createView(@NotNull InventoryContext context);

    /**
     * Called to render or update the contents of the view.
     * @param view the inventory view to update
     */
    void update(@NotNull InventoryView view);
    
    /**
     * Returns true if this provider's views should be automatically refreshed by the global ticker.
     * @return true if auto-refresh is enabled
     */
    default boolean isAutoRefresh() {
        return false;
    }
}
