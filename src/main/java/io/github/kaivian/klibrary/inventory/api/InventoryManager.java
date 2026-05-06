package io.github.kaivian.klibrary.inventory.api;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Central manager for player inventory GUI sessions with stack-based navigation.
 *
 * <p>The {@code InventoryManager} maintains a per-player stack of {@link InventoryView}s,
 * enabling hierarchical menu navigation (push/pop pattern). It also manages a registry of
 * {@link InventoryProvider}s that define how inventories are created and rendered.</p>
 *
 * <h2>Navigation Model</h2>
 * <p>Each player has an independent view stack. Opening a new menu pushes it onto
 * the stack; going "back" pops the current view and reopens the previous one.</p>
 * <ul>
 *   <li>{@link #push} — Opens a new view on top of the stack</li>
 *   <li>{@link #pop} — Removes the current view and returns to the previous</li>
 *   <li>{@link #replace} — Swaps the current view without growing the stack</li>
 *   <li>{@link #clear} — Clears the entire stack and closes the inventory</li>
 * </ul>
 *
 * <h2>Provider Registry</h2>
 * <p>Providers are registered with unique string IDs and are used to create views
 * on demand. Providers can be registered programmatically or loaded from configuration.</p>
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * InventoryManager manager = KLibrary.getInstance().getServiceProvider()
 *         .getInventoryManager().orElseThrow();
 *
 * // Register a custom provider
 * manager.registerProvider(myCustomProvider);
 *
 * // Open a menu for a player
 * InventoryContext context = InventoryContext.builder()
 *         .player(player)
 *         .plugin(plugin)
 *         .build();
 * manager.push(player, "main_menu", context);
 *
 * // Navigate back
 * manager.pop(player);
 * }</pre>
 *
 * @see InventoryView
 * @see InventoryProvider
 * @see InventoryContext
 */
public interface InventoryManager {

    /**
     * Pushes a new inventory view onto the player's navigation stack and opens it.
     *
     * <p>The view is created by the registered {@link InventoryProvider} matching
     * the given provider ID. If no provider is found, this method does nothing.</p>
     *
     * @param player     the player to open the inventory for; must not be {@code null}
     * @param providerId the ID of the registered inventory provider; must not be {@code null}
     * @param context    the context for this inventory session; must not be {@code null}
     */
    void push(@NotNull Player player, @NotNull String providerId, @NotNull InventoryContext context);

    /**
     * Pops the current inventory view from the player's stack and opens the previous one.
     *
     * <p>If the stack becomes empty after popping, the player's inventory is closed.
     * If the stack is already empty, this method does nothing.</p>
     *
     * @param player the player whose current view should be popped; must not be {@code null}
     */
    void pop(@NotNull Player player);

    /**
     * Replaces the current inventory view with a new one.
     * 
     * <p>This method replaces the current GUI visually but still pushes it into the stack.</p>
     *
     * @param player     the player whose current view should be replaced; must not be {@code null}
     * @param providerId the ID of the registered inventory provider; must not be {@code null}
     * @param context    the context for the new inventory session; must not be {@code null}
     */
    void openReplace(@NotNull Player player, @NotNull String providerId, @NotNull InventoryContext context);

    /**
     * Updates the current inventory view with a new context without changing the stack depth.
     * This is useful for pagination.
     *
     * @param player  the player whose current view should be updated; must not be {@code null}
     * @param context the new context for this inventory session; must not be {@code null}
     */
    void update(@NotNull Player player, @NotNull InventoryContext context);

    /**
     * Clears the player's entire inventory stack and closes any open inventory.
     *
     * <p>All tracked views for the player are removed from the active view registry.</p>
     *
     * @param player the player whose stack should be cleared; must not be {@code null}
     */
    void clear(@NotNull Player player);

    /**
     * Registers an inventory provider, making it available for use with
     * {@link #push} and {@link #replace}.
     *
     * <p>If a provider with the same ID is already registered, it will be replaced.</p>
     *
     * @param provider the provider to register; must not be {@code null}
     */
    void registerProvider(@NotNull InventoryProvider provider);

    /**
     * Unregisters an inventory provider by its ID.
     *
     * <p>Any currently open views created by this provider will continue to function
     * but no new views can be created from it.</p>
     *
     * @param providerId the ID of the provider to unregister; must not be {@code null}
     */
    void unregisterProvider(@NotNull String providerId);

    /**
     * Retrieves a registered provider by its unique ID.
     *
     * @param providerId the provider ID to look up; must not be {@code null}
     * @return an {@link Optional} containing the provider if found, or empty if not registered
     */
    @NotNull Optional<InventoryProvider> getProvider(@NotNull String providerId);
    
    /**
     * Removes all registered providers from the registry.
     *
     * <p>This is typically called during plugin reload to prepare for re-registration.</p>
     */
    void clearProviders();

    /**
     * Shuts down the manager, cancelling the global update task, closing all
     * active inventories, and clearing all internal state.
     *
     * <p>This should be called during plugin disable to ensure clean shutdown.</p>
     */
    void shutdown();
    
    /**
     * Gets the ID of the currently active (topmost) view for the player.
     *
     * @param player the player to query; must not be {@code null}
     * @return an {@link Optional} containing the active view's provider ID,
     *         or empty if the player has no open managed inventory
     */
    @NotNull Optional<String> getActiveViewId(@NotNull Player player);
    
    /**
     * Gets the depth of the inventory navigation stack for the player.
     *
     * <p>A value of 0 means no managed inventory is open. A value of 1 means
     * one view is open (no back history). Values greater than 1 indicate
     * the player has navigated through multiple menus.</p>
     *
     * @param player the player to query; must not be {@code null}
     * @return the number of views in the player's stack (0 if none)
     */
    int getStackSize(@NotNull Player player);

    /**
     * Checks if the player has a previous view in their navigation stack.
     *
     * @param player the player to query; must not be {@code null}
     * @return {@code true} if there is a previous view (stack size > 1)
     */
    boolean hasPrevious(@NotNull Player player);
}
