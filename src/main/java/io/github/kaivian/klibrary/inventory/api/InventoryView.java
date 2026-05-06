package io.github.kaivian.klibrary.inventory.api;

import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a single instance of a GUI screen that is displayed to a player.
 *
 * <p>An {@code InventoryView} is the runtime representation of a menu. It wraps
 * the underlying Bukkit {@link Inventory}, holds the {@link InventoryContext} for
 * the session, and links back to the {@link InventoryProvider} that created it.</p>
 *
 * <p>Views are created by {@link InventoryProvider#createView(InventoryContext)} and
 * managed by the {@link InventoryManager}'s per-player navigation stack. Each view
 * has a unique ID (typically matching the provider's ID) used for stack lookups.</p>
 *
 * <h2>Lifecycle</h2>
 * <ol>
 *   <li>Created by the provider via {@link InventoryProvider#createView(InventoryContext)}</li>
 *   <li>Pushed onto the player's stack by the {@link InventoryManager}</li>
 *   <li>Initially rendered via {@link InventoryProvider#update(InventoryView)}</li>
 *   <li>Optionally auto-refreshed by the global ticker if {@link InventoryProvider#isAutoRefresh()} is {@code true}</li>
 *   <li>Removed from the stack when popped, replaced, or cleared</li>
 * </ol>
 *
 * @see InventoryProvider
 * @see InventoryManager
 * @see InventoryContext
 */
public interface InventoryView {

    /**
     * Returns the unique identifier of this view.
     *
     * <p>This typically matches the ID of the {@link InventoryProvider} that created it
     * (e.g., {@code "main_menu"}, {@code "shop"}).</p>
     *
     * @return the view ID; never {@code null}
     */
    @NotNull String getId();

    /**
     * Returns the underlying Bukkit {@link Inventory} instance associated with this view.
     *
     * <p>This is the actual inventory displayed to the player. Modifying its contents
     * directly will be reflected in the player's GUI.</p>
     *
     * @return the Bukkit inventory; never {@code null}
     */
    @NotNull Inventory getInventory();

    /**
     * Returns the context associated with this view.
     *
     * <p>The context holds player information, placeholders, metadata, and pagination
     * state for this inventory session.</p>
     *
     * @return the inventory context; never {@code null}
     */
    @NotNull InventoryContext getContext();

    /**
     * Returns the provider that created and manages this view.
     *
     * <p>The provider defines how the view's contents are rendered and how
     * click events are handled.</p>
     *
     * @return the inventory provider; never {@code null}
     */
    @NotNull InventoryProvider getProvider();

    /**
     * Refreshes this view by delegating to its provider's
     * {@link InventoryProvider#update(InventoryView)} method.
     *
     * <p>This can be called manually to force a content update outside of
     * the automatic refresh cycle.</p>
     */
    void refresh();

    /**
     * Sets a view-specific button at the given slot.
     *
     * <p>Buttons set on the view level take precedence over buttons
     * defined in the provider's configuration. This allows for dynamic
     * injection of buttons like pagination or close buttons.</p>
     *
     * @param slot   the inventory slot index (0-based)
     * @param button the button to set; must not be {@code null}
     */
    void setButton(int slot, @NotNull io.github.kaivian.klibrary.inventory.button.Button button);

    /**
     * Retrieves a view-specific button at the given slot, if any.
     *
     * @param slot the inventory slot index (0-based)
     * @return the button at the slot, or {@code null} if none is set on the view
     */
    @org.jetbrains.annotations.Nullable io.github.kaivian.klibrary.inventory.button.Button getButton(int slot);
}
