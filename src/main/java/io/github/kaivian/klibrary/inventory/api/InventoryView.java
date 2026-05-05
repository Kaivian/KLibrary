package io.github.kaivian.klibrary.inventory.api;

import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a GUI screen holding the Bukkit Inventory instance.
 */
public interface InventoryView {

    /**
     * Returns the unique ID of this inventory view (typically the menu ID from config).
     * @return the inventory ID
     */
    @NotNull String getId();

    /**
     * Returns the Bukkit inventory associated with this view.
     * @return the Bukkit inventory
     */
    @NotNull Inventory getInventory();

    /**
     * Returns the context of this inventory view.
     * @return the inventory context
     */
    @NotNull InventoryContext getContext();

    /**
     * Returns the provider that created and manages this view.
     * @return the inventory provider
     */
    @NotNull InventoryProvider getProvider();

    /**
     * Refreshes the inventory view.
     */
    void refresh();
}
