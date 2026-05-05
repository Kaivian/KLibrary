package io.github.kaivian.klibrary.inventory.impl;

import io.github.kaivian.klibrary.KLibrary;
import io.github.kaivian.klibrary.action.api.Action;
import io.github.kaivian.klibrary.inventory.api.InventoryContext;
import io.github.kaivian.klibrary.inventory.api.InventoryManager;
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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigurableInventoryProvider implements InventoryProvider {

    private final String id;
    private final String title;
    private final int size;
    private final boolean autoRefresh;
    
    private final List<Requirement> openRequirements;
    private final List<Action> openActions;
    
    // Slot -> Button
    private final Map<Integer, Button> buttons = new ConcurrentHashMap<>();

    public ConfigurableInventoryProvider(String id, String title, int size, boolean autoRefresh, 
                                         List<Requirement> openRequirements, List<Action> openActions) {
        this.id = id;
        this.title = title;
        this.size = size;
        this.autoRefresh = autoRefresh;
        this.openRequirements = openRequirements;
        this.openActions = openActions;
    }
    
    public void addButton(int slot, Button button) {
        buttons.put(slot, button);
    }

    @Override
    public @NotNull String getId() {
        return id;
    }

    @Override
    public @NotNull InventoryView createView(@NotNull InventoryContext context) {
        // Here we could parse placeholders in the title
        String parsedTitle = title;
        for (Map.Entry<String, String> entry : context.getExecutionContext().getPlaceholders().entrySet()) {
            parsedTitle = parsedTitle.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        
        Component componentTitle = MiniMessage.miniMessage().deserialize(parsedTitle);
        Inventory inventory = Bukkit.createInventory(null, size, componentTitle);
        return new InventoryViewImpl(id, inventory, context, this);
    }

    @Override
    public void update(@NotNull InventoryView view) {
        Inventory inventory = view.getInventory();
        InventoryContext context = view.getContext();
        
        // Setup Back Button if necessary (Hybrid Approach: check if history requirement is met, auto-inject at default slot if not manually overridden)
        // Wait, for back button to work automatically, we need the InventoryManager instance. 
        // A better approach is to handle it during config load or dynamically insert a BackButton in empty slots.
        
        for (int i = 0; i < size; i++) {
            Button button = buttons.get(i);
            if (button != null && button.isVisible(context)) {
                inventory.setItem(i, button.getItem(context));
            } else {
                inventory.setItem(i, null);
            }
        }
    }

    @Override
    public void onClick(InventoryClickEvent event, InventoryContext context) {
        // Prevent interactions by default
        event.setCancelled(true);
        
        // Only top inventory
        if (event.getClickedInventory() == null || !event.getClickedInventory().equals(event.getView().getTopInventory())) {
            return;
        }

        int slot = event.getSlot();
        Button button = buttons.get(slot);
        if (button != null && button.isVisible(context)) {
            button.onClick(event, context);
        }
    }

    @Override
    public void onOpen(InventoryOpenEvent event, InventoryContext context) {
        if (openActions != null) {
            for (Action action : openActions) {
                action.execute(context.getExecutionContext());
            }
        }
    }

    @Override
    public void onClose(InventoryCloseEvent event, InventoryContext context) {
        // Optional logic
    }

    @Override
    public boolean isAutoRefresh() {
        return autoRefresh;
    }
    
    public List<Requirement> getOpenRequirements() {
        return openRequirements;
    }
}
