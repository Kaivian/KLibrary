package io.github.kaivian.klibrary.inventory.impl;

import io.github.kaivian.klibrary.action.api.Action;
import io.github.kaivian.klibrary.inventory.api.InventoryContext;
import io.github.kaivian.klibrary.inventory.api.InventoryProvider;
import io.github.kaivian.klibrary.inventory.api.InventoryView;
import io.github.kaivian.klibrary.inventory.button.Button;
import io.github.kaivian.klibrary.requirement.api.Requirement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Config-driven implementation of {@link InventoryProvider} that creates inventory
 * GUIs from configuration data.
 *
 * <p>This provider is the primary mechanism for defining GUIs in YAML or JSON
 * configuration. It supports:</p>
 * <ul>
 *   <li>MiniMessage-formatted titles with placeholder resolution</li>
 *   <li>Configurable inventory size (multiples of 9)</li>
 *   <li>Button-to-slot mapping with visibility requirements</li>
 *   <li>Open requirements and open actions</li>
 *   <li>Optional auto-refresh via the global ticker</li>
 * </ul>
 *
 * <h2>Configuration Example</h2>
 * <pre>{@code
 * my_menu:
 *   title: "<gradient:#ff0000:#00ff00>My Menu</gradient>"
 *   size: 27
 *   auto-refresh: false
 *   open-actions:
 *     - type: sound
 *       sound: BLOCK_NOTE_BLOCK_PLING
 *   items:
 *     info_item:
 *       slot: 13
 *       material: BOOK
 *       name: "<yellow>Information"
 *       left-click-actions:
 *         - type: message
 *           message: "<green>Hello!"
 * }</pre>
 *
 * @see InventoryProvider
 * @see io.github.kaivian.klibrary.inventory.impl.config.InventoryConfigDeserializer
 */
public class ConfigurableInventoryProvider implements InventoryProvider {

    private final String id;
    private final String title;
    private final int size;
    private final boolean autoRefresh;
    
    private final List<Requirement> openRequirements;
    private final List<Action> openActions;
    
    /** Slot index → Button mapping for this provider. */
    private final Map<Integer, Button> buttons = new ConcurrentHashMap<>();

    /**
     * Constructs a new {@code ConfigurableInventoryProvider}.
     *
     * @param id               the unique provider ID
     * @param title            the MiniMessage-formatted inventory title (supports placeholders)
     * @param size             the inventory size (must be a multiple of 9, max 54)
     * @param autoRefresh      whether views from this provider should auto-refresh
     * @param openRequirements requirements that must be met before the inventory opens
     * @param openActions      actions to execute when the inventory opens
     */
    public ConfigurableInventoryProvider(String id, String title, int size, boolean autoRefresh, 
                                         List<Requirement> openRequirements, List<Action> openActions) {
        this.id = id;
        this.title = title;
        this.size = size;
        this.autoRefresh = autoRefresh;
        this.openRequirements = openRequirements;
        this.openActions = openActions;
    }
    
    /**
     * Registers a button at the specified slot index.
     *
     * <p>If a button already exists at the given slot, it will be replaced.</p>
     *
     * @param slot   the slot index (0-based)
     * @param button the button to place at that slot
     */
    public void addButton(int slot, Button button) {
        buttons.put(slot, button);
    }

    /** {@inheritDoc} */
    @Override
    public @NotNull String getId() {
        return id;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Parses the title with custom placeholder resolution, then creates
     * a new Bukkit inventory with the configured size.</p>
     */
    @Override
    public @NotNull InventoryView createView(@NotNull InventoryContext context) {
        // Resolve custom placeholders in the title
        String parsedTitle = title;
        for (Map.Entry<String, String> entry : context.getExecutionContext().getPlaceholders().entrySet()) {
            parsedTitle = parsedTitle.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        
        Component componentTitle = MiniMessage.miniMessage().deserialize(parsedTitle);
        Inventory inventory = Bukkit.createInventory(null, size, componentTitle);
        return new InventoryViewImpl(id, inventory, context, this);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Iterates all slots and renders visible buttons. Empty slots or
     * slots with hidden buttons are cleared.</p>
     */
    @Override
    public void update(@NotNull InventoryView view) {
        Inventory inventory = view.getInventory();
        InventoryContext context = view.getContext();
        
        for (int i = 0; i < size; i++) {
            Button button = buttons.get(i);
            if (button != null && button.isVisible(context)) {
                inventory.setItem(i, button.getItem(context));
            } else {
                inventory.setItem(i, null);
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>Only processes clicks on the top inventory. Delegates to the
     * clicked button if one is present and visible.</p>
     */
    @Override
    public void onClick(InventoryClickEvent event, InventoryContext context) {
        event.setCancelled(true);
        
        if (event.getClickedInventory() == null || !event.getClickedInventory().equals(event.getView().getTopInventory())) {
            return;
        }

        int slot = event.getSlot();
        Button button = buttons.get(slot);
        if (button != null && button.isVisible(context)) {
            button.onClick(event, context);
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>Executes all configured open actions when the inventory is opened.</p>
     */
    @Override
    public void onOpen(InventoryOpenEvent event, InventoryContext context) {
        if (openActions != null) {
            for (Action action : openActions) {
                action.execute(context.getExecutionContext());
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onClose(InventoryCloseEvent event, InventoryContext context) {
        // No-op by default; subclasses may override for custom close behavior
    }

    /** {@inheritDoc} */
    @Override
    public boolean isAutoRefresh() {
        return autoRefresh;
    }
    
    /**
     * Returns the requirements that must be met before this inventory can be opened.
     *
     * <p>These are evaluated by external code (e.g., the {@code OpenInventoryAction})
     * before calling {@link InventoryManager#push}.</p>
     *
     * @return the open requirements; never {@code null}
     */
    public List<Requirement> getOpenRequirements() {
        return openRequirements;
    }
}
