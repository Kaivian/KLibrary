package io.github.kaivian.klibrary.inventory.impl;

import io.github.kaivian.klibrary.KLibrary;
import io.github.kaivian.klibrary.inventory.api.InventoryContext;
import io.github.kaivian.klibrary.inventory.api.InventoryManager;
import io.github.kaivian.klibrary.inventory.api.InventoryProvider;
import io.github.kaivian.klibrary.inventory.api.InventoryView;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of the InventoryManager with stack-based navigation and global ticking.
 */
public class InventoryManagerImpl implements InventoryManager, Listener {

    private final KLibrary plugin;
    private final Map<String, InventoryProvider> providers = new ConcurrentHashMap<>();
    
    // Stack of views per player
    private final Map<UUID, Deque<InventoryView>> playerStacks = new ConcurrentHashMap<>();
    
    // Fast lookup for currently open inventory views to route events
    private final Map<Inventory, InventoryView> activeViews = new ConcurrentHashMap<>();
    
    private final BukkitTask updateTask;

    public InventoryManagerImpl(KLibrary plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);

        // Global ticker for auto-refreshing GUI elements
        this.updateTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (InventoryView view : activeViews.values()) {
                if (view.getProvider().isAutoRefresh()) {
                    view.getProvider().update(view);
                }
            }
        }, 20L, 20L); // Update every second (20 ticks) - could be configurable
    }

    @Override
    public void push(@NotNull Player player, @NotNull String providerId, @NotNull InventoryContext context) {
        providers.computeIfPresent(providerId, (id, provider) -> {
            InventoryView view = provider.createView(context);
            Deque<InventoryView> stack = playerStacks.computeIfAbsent(player.getUniqueId(), k -> new ArrayDeque<>());
            
            // Push the new view
            stack.push(view);
            openView(player, view);
            return provider;
        });
    }

    @Override
    public void pop(@NotNull Player player) {
        Deque<InventoryView> stack = playerStacks.get(player.getUniqueId());
        if (stack != null && !stack.isEmpty()) {
            // Remove current view
            InventoryView current = stack.pop();
            activeViews.remove(current.getInventory());
            
            if (!stack.isEmpty()) {
                // Reopen the previous view
                InventoryView previous = stack.peek();
                openView(player, previous);
            } else {
                // Stack empty, close inventory
                player.closeInventory();
            }
        }
    }

    @Override
    public void replace(@NotNull Player player, @NotNull String providerId, @NotNull InventoryContext context) {
        providers.computeIfPresent(providerId, (id, provider) -> {
            Deque<InventoryView> stack = playerStacks.computeIfAbsent(player.getUniqueId(), k -> new ArrayDeque<>());
            
            // Remove current if exists without closing
            if (!stack.isEmpty()) {
                InventoryView current = stack.pop();
                activeViews.remove(current.getInventory());
            }
            
            InventoryView newView = provider.createView(context);
            stack.push(newView);
            openView(player, newView);
            return provider;
        });
    }

    @Override
    public void clear(@NotNull Player player) {
        Deque<InventoryView> stack = playerStacks.remove(player.getUniqueId());
        if (stack != null) {
            for (InventoryView view : stack) {
                activeViews.remove(view.getInventory());
            }
        }
        player.closeInventory();
    }

    private void openView(Player player, InventoryView view) {
        // Register view to active views
        activeViews.put(view.getInventory(), view);
        // Call provider update initially
        view.getProvider().update(view);
        // Open
        Bukkit.getScheduler().runTask(plugin, () -> player.openInventory(view.getInventory()));
    }

    @Override
    public void registerProvider(@NotNull InventoryProvider provider) {
        providers.put(provider.getId(), provider);
    }

    @Override
    public void unregisterProvider(@NotNull String providerId) {
        providers.remove(providerId);
    }

    @Override
    public @NotNull Optional<InventoryProvider> getProvider(@NotNull String providerId) {
        return Optional.ofNullable(providers.get(providerId));
    }

    @Override
    public void clearProviders() {
        providers.clear();
    }

    @Override
    public void shutdown() {
        if (updateTask != null) {
            updateTask.cancel();
        }
        for (UUID uuid : new ArrayList<>(playerStacks.keySet())) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.closeInventory();
            }
        }
        playerStacks.clear();
        activeViews.clear();
        providers.clear();
    }

    @Override
    public @NotNull Optional<String> getActiveViewId(@NotNull Player player) {
        Deque<InventoryView> stack = playerStacks.get(player.getUniqueId());
        if (stack != null && !stack.isEmpty()) {
            return Optional.of(stack.peek().getId());
        }
        return Optional.empty();
    }

    @Override
    public int getStackSize(@NotNull Player player) {
        Deque<InventoryView> stack = playerStacks.get(player.getUniqueId());
        return stack != null ? stack.size() : 0;
    }

    // --- Event Handling ---

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryView view = activeViews.get(event.getInventory());
        if (view != null) {
            // Default cancel
            event.setCancelled(true);
            
            // Only process top inventory clicks for buttons, or allow custom handling
            view.getProvider().onClick(event, view.getContext());
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        InventoryView view = activeViews.get(event.getInventory());
        if (view != null) {
            view.getProvider().onOpen(event, view.getContext());
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        InventoryView view = activeViews.get(inventory);
        
        if (view != null) {
            view.getProvider().onClose(event, view.getContext());
            
            Player player = (Player) event.getPlayer();
            Deque<InventoryView> stack = playerStacks.get(player.getUniqueId());
            
            // If the player closed it manually (not via push/replace), we need to clear stack
            // We use a slight delay to see if a new inventory opens immediately
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                InventoryView currentView = activeViews.get(player.getOpenInventory().getTopInventory());
                if (currentView == null) {
                    // Player fully closed their inventory
                    clear(player);
                }
            }, 1L);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        clear(event.getPlayer());
    }
}
