package io.github.kaivian.klibrary.service;

import org.bukkit.plugin.Plugin;

import java.util.Objects;

/**
 * Central service locator providing access to all KLibrary services.
 *
 * <p>The {@code ServiceProvider} is initialized once during plugin startup
 * and serves as the single point of access for all service instances. It is
 * passed to action/requirement factories and made available through the
 * {@link io.github.kaivian.klibrary.context.ExecutionContext}.</p>
 *
 * <p><b>Initialization:</b></p>
 * <pre>{@code
 * ServiceProvider services = new ServiceProvider(plugin);
 * // Services are now accessible:
 * services.getMessageService().send(player, "<green>Hello!");
 * services.getEconomyService().deposit(player, 100.0);
 * }</pre>
 *
 * <p>External plugins using KLibrary as a dependency can obtain the
 * {@code ServiceProvider} via {@code KLibrary.getServiceProvider()} and
 * use it to access all services without direct coupling.</p>
 *
 * @see PluginDependencyService
 * @see MessageService
 * @see EconomyService
 * @see PermissionService
 * @see SoundService
 */
public class ServiceProvider {

    private final Plugin plugin;
    private final PluginDependencyService dependencyService;
    private final MessageService messageService;
    private final EconomyService economyService;
    private final PermissionService permissionService;
    private final SoundService soundService;
    private io.github.kaivian.klibrary.inventory.api.InventoryManager inventoryManager;
    private io.github.kaivian.klibrary.lang.LanguageManager languageManager;

    /**
     * Constructs a new {@code ServiceProvider} and initializes all services.
     *
     * @param plugin the owning plugin instance; must not be {@code null}
     * @throws NullPointerException if {@code plugin} is {@code null}
     */
    public ServiceProvider(Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "Plugin must not be null");
        this.dependencyService = new PluginDependencyService();
        this.messageService = new MessageService(dependencyService);
        this.economyService = new EconomyService(dependencyService);
        this.permissionService = new PermissionService(dependencyService, plugin);
        this.soundService = new SoundService();
    }

    /**
     * Returns the owning plugin.
     *
     * @return the plugin instance
     */
    public Plugin getPlugin() {
        return plugin;
    }

    /**
     * Returns the plugin dependency detection service.
     *
     * @return the {@link PluginDependencyService}
     */
    public PluginDependencyService getDependencyService() {
        return dependencyService;
    }

    /**
     * Returns the message service for MiniMessage + PlaceholderAPI integration.
     *
     * @return the {@link MessageService}
     */
    public MessageService getMessageService() {
        return messageService;
    }

    /**
     * Returns the economy service for Vault integration.
     *
     * @return the {@link EconomyService}
     */
    public EconomyService getEconomyService() {
        return economyService;
    }

    /**
     * Returns the permission service (LuckPerms → Vault → Bukkit fallback).
     *
     * @return the {@link PermissionService}
     */
    public PermissionService getPermissionService() {
        return permissionService;
    }

    /**
     * Returns the sound utility service.
     *
     * @return the {@link SoundService}
     */
    public SoundService getSoundService() {
        return soundService;
    }

    /**
     * Returns the inventory manager.
     *
     * @return an {@link java.util.Optional} containing the inventory manager, if initialized
     */
    public java.util.Optional<io.github.kaivian.klibrary.inventory.api.InventoryManager> getInventoryManager() {
        return java.util.Optional.ofNullable(inventoryManager);
    }
    
    /**
     * Sets the inventory manager.
     */
    public void setInventoryManager(io.github.kaivian.klibrary.inventory.api.InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
    }

    /**
     * Returns the language manager.
     *
     * @return an {@link java.util.Optional} containing the language manager, if initialized
     */
    public java.util.Optional<io.github.kaivian.klibrary.lang.LanguageManager> getLanguageManager() {
        return java.util.Optional.ofNullable(languageManager);
    }

    /**
     * Sets the language manager.
     */
    public void setLanguageManager(io.github.kaivian.klibrary.lang.LanguageManager languageManager) {
        this.languageManager = languageManager;
    }
}
