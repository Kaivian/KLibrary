package io.github.kaivian.klibrary.inventory.button;

import io.github.kaivian.klibrary.inventory.api.InventoryContext;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a clickable element in a GUI.
 */
public interface Button {

    /**
     * Gets the item representation of this button for the given context.
     * @param context the inventory context
     * @return the item stack to display
     */
    @NotNull ItemStack getItem(@NotNull InventoryContext context);

    /**
     * Handles a click on this button.
     * @param event the raw click event
     * @param context the inventory context
     */
    void onClick(@NotNull InventoryClickEvent event, @NotNull InventoryContext context);

    /**
     * Checks if this button should be visible in the given context.
     * @param context the inventory context
     * @return true if visible, false otherwise
     */
    boolean isVisible(@NotNull InventoryContext context);
}
