package io.github.kaivian.klibrary.service;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

/**
 * Service for safely detecting optional plugin dependencies at runtime.
 *
 * <p>This service provides a centralized, fail-safe mechanism for checking
 * whether optional plugins (Vault, PlaceholderAPI, LuckPerms, etc.) are
 * present and enabled on the server. All checks use Bukkit's plugin manager
 * and never throw exceptions.</p>
 *
 * <p><b>Usage:</b></p>
 * <pre>{@code
 * PluginDependencyService deps = new PluginDependencyService();
 * if (deps.hasVault()) {
 *     // safe to use economy/permission features
 * }
 * }</pre>
 */
public class PluginDependencyService {

    private static final Logger LOGGER = Logger.getLogger(PluginDependencyService.class.getName());

    /**
     * Checks whether Vault is installed and enabled.
     *
     * @return {@code true} if Vault is available
     */
    public boolean hasVault() {
        return isPluginEnabled("Vault");
    }

    /**
     * Checks whether PlaceholderAPI is installed and enabled.
     *
     * @return {@code true} if PlaceholderAPI is available
     */
    public boolean hasPlaceholderAPI() {
        return isPluginEnabled("PlaceholderAPI");
    }

    /**
     * Checks whether LuckPerms is installed and enabled.
     *
     * @return {@code true} if LuckPerms is available
     */
    public boolean hasLuckPerms() {
        return isPluginEnabled("LuckPerms");
    }

    /**
     * Checks whether a plugin with the given name is installed and enabled.
     *
     * <p>This method is safe to call at any time and will never throw exceptions.
     * If the server's plugin manager is not yet initialized, this returns {@code false}.</p>
     *
     * @param pluginName the name of the plugin to check
     * @return {@code true} if the plugin is present and enabled
     */
    public boolean isPluginEnabled(String pluginName) {
        try {
            Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
            return plugin != null && plugin.isEnabled();
        } catch (Exception e) {
            LOGGER.warning("Failed to check plugin dependency '" + pluginName + "': " + e.getMessage());
            return false;
        }
    }

    /**
     * Gets the plugin instance for the given name, if present and enabled.
     *
     * @param pluginName the name of the plugin
     * @return the plugin instance, or {@code null} if not available
     */
    public Plugin getPlugin(String pluginName) {
        try {
            Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
            return (plugin != null && plugin.isEnabled()) ? plugin : null;
        } catch (Exception e) {
            return null;
        }
    }
}
