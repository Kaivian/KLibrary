package io.github.kaivian.klibrary.inventory.api;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

/**
 * Handles Bukkit inventory events for a managed GUI screen.
 *
 * <p>This interface defines the event callbacks that an {@link InventoryProvider}
 * receives when players interact with its managed inventories. The
 * {@link InventoryManager} routes Bukkit events to the appropriate provider
 * based on the active view registry.</p>
 *
 * <p>By default, {@link #onOpen} and {@link #onClose} are no-ops, allowing
 * implementations to only override the events they care about. The
 * {@link #onClick} handler is the most commonly implemented, as it handles
 * button interactions and item slot logic.</p>
 *
 * <h2>Event Flow</h2>
 * <ol>
 *   <li>Player interacts with an inventory managed by this handler</li>
 *   <li>{@link InventoryManager} looks up the active view for that inventory</li>
 *   <li>The corresponding handler method is invoked with the event and context</li>
 * </ol>
 *
 * @see InventoryProvider
 * @see InventoryManager
 */
public interface InventoryHandler {

    /**
     * Called when a player clicks inside the managed inventory.
     *
     * <p>Implementations should typically cancel the event to prevent item theft
     * and then process the click based on the clicked slot and click type.</p>
     *
     * @param event   the Bukkit click event
     * @param context the inventory context for this session
     */
    void onClick(InventoryClickEvent event, InventoryContext context);

    /**
     * Called when the managed inventory is opened for a player.
     *
     * <p>This can be used to execute open actions (e.g., playing sounds,
     * sending messages). Default implementation does nothing.</p>
     *
     * @param event   the Bukkit open event
     * @param context the inventory context for this session
     */
    default void onOpen(InventoryOpenEvent event, InventoryContext context) {}

    /**
     * Called when the managed inventory is closed by a player.
     *
     * <p>This can be used to perform cleanup, save state, or execute
     * close actions. Default implementation does nothing.</p>
     *
     * <p><b>Note:</b> This is called before the {@link InventoryManager}'s
     * internal close handling (stack cleanup). Do not call
     * {@link InventoryManager#clear} or {@link InventoryManager#pop} from
     * this handler, as the manager handles stack management automatically.</p>
     *
     * @param event   the Bukkit close event
     * @param context the inventory context for this session
     */
    default void onClose(InventoryCloseEvent event, InventoryContext context) {}
}
