package io.github.kaivian.klibrary.inventory.api;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

/**
 * Handles Bukkit inventory events for a specific GUI.
 */
public interface InventoryHandler {

    /**
     * Called when a player clicks inside the inventory.
     * @param event The click event
     * @param context The inventory context
     */
    void onClick(InventoryClickEvent event, InventoryContext context);

    /**
     * Called when the inventory is opened.
     * @param event The open event
     * @param context The inventory context
     */
    default void onOpen(InventoryOpenEvent event, InventoryContext context) {}

    /**
     * Called when the inventory is closed.
     * @param event The close event
     * @param context The inventory context
     */
    default void onClose(InventoryCloseEvent event, InventoryContext context) {}
}
