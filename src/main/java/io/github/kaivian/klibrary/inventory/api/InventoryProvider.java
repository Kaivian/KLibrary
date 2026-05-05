package io.github.kaivian.klibrary.inventory.api;

import org.jetbrains.annotations.NotNull;

/**
 * Defines the blueprint for creating and managing inventory GUI screens.
 *
 * <p>An {@code InventoryProvider} is registered with the {@link InventoryManager}
 * and is responsible for:</p>
 * <ul>
 *   <li>Creating new {@link InventoryView} instances on demand</li>
 *   <li>Rendering and updating the contents of those views</li>
 *   <li>Handling inventory events (clicks, open, close) via {@link InventoryHandler}</li>
 * </ul>
 *
 * <h2>Registration</h2>
 * <pre>{@code
 * InventoryProvider provider = new MyCustomProvider("shop");
 * manager.registerProvider(provider);
 * }</pre>
 *
 * <h2>Programmatic Implementation</h2>
 * <pre>{@code
 * public class ShopProvider implements InventoryProvider {
 *     @Override
 *     public @NotNull String getId() { return "shop"; }
 *
 *     @Override
 *     public @NotNull InventoryView createView(@NotNull InventoryContext context) {
 *         Inventory inv = Bukkit.createInventory(null, 27, Component.text("Shop"));
 *         return new InventoryViewImpl("shop", inv, context, this);
 *     }
 *
 *     @Override
 *     public void update(@NotNull InventoryView view) {
 *         // Populate the inventory with items
 *     }
 *
 *     @Override
 *     public void onClick(InventoryClickEvent event, InventoryContext context) {
 *         event.setCancelled(true);
 *         // Handle click logic
 *     }
 * }
 * }</pre>
 *
 * @see InventoryManager
 * @see InventoryView
 * @see InventoryHandler
 */
public interface InventoryProvider extends InventoryHandler {

    /**
     * Returns the unique identifier for this provider.
     *
     * <p>This ID is used to register the provider with the {@link InventoryManager}
     * and to reference it when opening menus (e.g., {@code "main_menu"}, {@code "shop"}).</p>
     *
     * @return the provider ID; never {@code null}
     */
    @NotNull String getId();

    /**
     * Creates a new {@link InventoryView} for the given context.
     *
     * <p>Each call should return a fresh view instance. The view's Bukkit inventory
     * is created here, typically with a title parsed from MiniMessage and a size
     * determined by configuration.</p>
     *
     * @param context the inventory context containing player, placeholders, and metadata;
     *                must not be {@code null}
     * @return a newly created inventory view; never {@code null}
     */
    @NotNull InventoryView createView(@NotNull InventoryContext context);

    /**
     * Renders or updates the contents of the given view.
     *
     * <p>This method is called:</p>
     * <ul>
     *   <li>Once immediately after the view is created (initial render)</li>
     *   <li>Periodically by the global ticker if {@link #isAutoRefresh()} returns {@code true}</li>
     *   <li>Manually via {@link InventoryView#refresh()}</li>
     * </ul>
     *
     * <p>Implementations should populate the view's inventory with the appropriate
     * items based on the current context state.</p>
     *
     * @param view the inventory view to update; must not be {@code null}
     */
    void update(@NotNull InventoryView view);
    
    /**
     * Returns whether this provider's views should be automatically refreshed
     * by the {@link InventoryManager}'s global ticker.
     *
     * <p>When enabled, the manager will call {@link #update(InventoryView)} on all
     * active views from this provider approximately once per second (every 20 ticks).
     * This is useful for dynamic content like timers, live statistics, or animated elements.</p>
     *
     * <p>Default implementation returns {@code false}.</p>
     *
     * @return {@code true} if auto-refresh is enabled; {@code false} otherwise
     */
    default boolean isAutoRefresh() {
        return false;
    }
}
