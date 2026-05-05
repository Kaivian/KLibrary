package io.github.kaivian.klibrary.inventory.api;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Manages player inventory sessions and stack-based navigation.
 */
public interface InventoryManager {

    /**
     * Pushes a new inventory view onto the player's stack and opens it.
     * @param player the player
     * @param providerId the ID of the inventory provider to open
     * @param context the context to use (can be built with extra metadata)
     */
    void push(@NotNull Player player, @NotNull String providerId, @NotNull InventoryContext context);

    /**
     * Pops the current inventory view from the stack and opens the previous one.
     * If the stack is empty, it closes the inventory.
     * @param player the player
     */
    void pop(@NotNull Player player);

    /**
     * Replaces the current inventory view with a new one without growing the stack.
     * @param player the player
     * @param providerId the ID of the inventory provider to open
     * @param context the context to use
     */
    void replace(@NotNull Player player, @NotNull String providerId, @NotNull InventoryContext context);

    /**
     * Clears the player's inventory stack and closes the inventory.
     * @param player the player
     */
    void clear(@NotNull Player player);

    /**
     * Registers an inventory provider.
     * @param provider the provider to register
     */
    void registerProvider(@NotNull InventoryProvider provider);

    /**
     * Unregisters an inventory provider.
     * @param providerId the ID of the provider to unregister
     */
    void unregisterProvider(@NotNull String providerId);

    /**
     * Gets a registered provider by its ID.
     * @param providerId the provider ID
     * @return an optional containing the provider if found
     */
    @NotNull Optional<InventoryProvider> getProvider(@NotNull String providerId);
    
    /**
     * Clears all registered providers.
     */
    void clearProviders();

    /**
     * Shuts down the manager, clearing all stacks and closing active inventories.
     */
    void shutdown();
    
    /**
     * Gets the ID of the currently active view for the player.
     * @param player the player
     * @return an optional containing the active view ID
     */
    @NotNull Optional<String> getActiveViewId(@NotNull Player player);
    
    /**
     * Gets the size of the inventory stack for the player.
     * @param player the player
     * @return the stack size
     */
    int getStackSize(@NotNull Player player);
}
