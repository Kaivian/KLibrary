package io.github.kaivian.klibrary.service;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Service for managing player permissions with a tiered fallback strategy.
 *
 * <p>This service attempts to use the best available permission provider
 * in the following priority order:</p>
 * <ol>
 *   <li><b>LuckPerms</b> — Direct API, most powerful and feature-rich</li>
 *   <li><b>Vault Permission</b> — Standard abstraction layer</li>
 *   <li><b>Bukkit PermissionAttachment</b> — Basic built-in fallback</li>
 * </ol>
 *
 * <p>The active provider is determined lazily on first use and cached for
 * subsequent operations. All methods are fail-safe and will log warnings
 * instead of throwing exceptions when providers are unavailable.</p>
 *
 * <p>LuckPerms API classes are loaded in isolation via a separate inner class
 * to prevent {@link NoClassDefFoundError} when LuckPerms is not installed.</p>
 *
 * <p><b>Example:</b></p>
 * <pre>{@code
 * PermissionService perms = new PermissionService(dependencyService, plugin);
 * perms.addPermission(player, "example.vip");
 * boolean hasVip = perms.hasPermission(player, "example.vip");
 * }</pre>
 */
public class PermissionService {

    private static final Logger LOGGER = Logger.getLogger(PermissionService.class.getName());

    /**
     * Enum representing the available permission providers.
     */
    public enum Provider {
        /** LuckPerms direct API */
        LUCKPERMS,
        /** Vault permission abstraction */
        VAULT,
        /** Bukkit built-in PermissionAttachment */
        BUKKIT
    }

    private final PluginDependencyService dependencyService;
    private final Plugin plugin;

    private Provider activeProvider;
    private Permission vaultPermission;
    private final Map<UUID, PermissionAttachment> bukkitAttachments = new ConcurrentHashMap<>();
    private boolean initialized = false;

    /**
     * Constructs a new {@code PermissionService}.
     *
     * @param dependencyService the plugin dependency service for checking providers
     * @param plugin            the owning plugin (needed for Bukkit PermissionAttachments)
     */
    public PermissionService(PluginDependencyService dependencyService, Plugin plugin) {
        this.dependencyService = dependencyService;
        this.plugin = plugin;
    }

    /**
     * Lazily initializes the permission provider using the tiered fallback strategy.
     */
    private void ensureInitialized() {
        if (initialized) return;
        initialized = true;

        // Priority 1: LuckPerms
        if (dependencyService.hasLuckPerms()) {
            try {
                LuckPermsHelper.init();
                activeProvider = Provider.LUCKPERMS;
                LOGGER.info("Permission service using LuckPerms provider.");
                return;
            } catch (Exception e) {
                LOGGER.warning("LuckPerms detected but API unavailable: " + e.getMessage());
            }
        }

        // Priority 2: Vault
        if (dependencyService.hasVault()) {
            try {
                RegisteredServiceProvider<Permission> rsp =
                        Bukkit.getServicesManager().getRegistration(Permission.class);
                if (rsp != null) {
                    vaultPermission = rsp.getProvider();
                    activeProvider = Provider.VAULT;
                    LOGGER.info("Permission service using Vault provider.");
                    return;
                }
            } catch (Exception e) {
                LOGGER.warning("Vault detected but permission provider unavailable: " + e.getMessage());
            }
        }

        // Priority 3: Bukkit fallback
        activeProvider = Provider.BUKKIT;
        LOGGER.info("Permission service using Bukkit PermissionAttachment fallback.");
    }

    /**
     * Returns the currently active permission provider.
     *
     * @return the active {@link Provider}
     */
    public Provider getActiveProvider() {
        ensureInitialized();
        return activeProvider;
    }

    /**
     * Checks whether a player has the specified permission.
     *
     * @param player     the player to check
     * @param permission the permission node
     * @return {@code true} if the player has the permission
     */
    public boolean hasPermission(Player player, String permission) {
        ensureInitialized();

        try {
            return switch (activeProvider) {
                case LUCKPERMS -> LuckPermsHelper.hasPermission(player, permission);
                case VAULT -> vaultPermission.playerHas(player, permission);
                case BUKKIT -> player.hasPermission(permission);
            };
        } catch (Exception e) {
            LOGGER.warning("Permission check failed for " + player.getName()
                    + " (" + permission + "): " + e.getMessage());
            return false;
        }
    }

    /**
     * Grants a permission to a player.
     *
     * @param player     the player to grant the permission to
     * @param permission the permission node to add
     * @return {@code true} if the operation succeeded
     */
    public boolean addPermission(Player player, String permission) {
        ensureInitialized();

        try {
            return switch (activeProvider) {
                case LUCKPERMS -> LuckPermsHelper.addPermission(player, permission);
                case VAULT -> {
                    vaultPermission.playerAdd(player, permission);
                    yield true;
                }
                case BUKKIT -> {
                    PermissionAttachment attachment = getOrCreateAttachment(player);
                    attachment.setPermission(permission, true);
                    yield true;
                }
            };
        } catch (Exception e) {
            LOGGER.warning("Failed to add permission '" + permission + "' to "
                    + player.getName() + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Removes a permission from a player.
     *
     * @param player     the player to remove the permission from
     * @param permission the permission node to remove
     * @return {@code true} if the operation succeeded
     */
    public boolean removePermission(Player player, String permission) {
        ensureInitialized();

        try {
            return switch (activeProvider) {
                case LUCKPERMS -> LuckPermsHelper.removePermission(player, permission);
                case VAULT -> {
                    vaultPermission.playerRemove(player, permission);
                    yield true;
                }
                case BUKKIT -> {
                    PermissionAttachment attachment = bukkitAttachments.get(player.getUniqueId());
                    if (attachment != null) {
                        attachment.unsetPermission(permission);
                    }
                    yield true;
                }
            };
        } catch (Exception e) {
            LOGGER.warning("Failed to remove permission '" + permission + "' from "
                    + player.getName() + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Gets or creates a Bukkit {@link PermissionAttachment} for the given player.
     *
     * @param player the player
     * @return the permission attachment
     */
    private PermissionAttachment getOrCreateAttachment(Player player) {
        return bukkitAttachments.computeIfAbsent(player.getUniqueId(),
                uuid -> player.addAttachment(plugin));
    }

    /**
     * Cleans up the Bukkit permission attachment for a player.
     *
     * <p>Should be called when a player disconnects to prevent memory leaks.</p>
     *
     * @param player the player whose attachment should be cleaned up
     */
    public void cleanup(Player player) {
        PermissionAttachment attachment = bukkitAttachments.remove(player.getUniqueId());
        if (attachment != null) {
            try {
                player.removeAttachment(attachment);
            } catch (Exception ignored) {
                // Player may already be disconnected
            }
        }
    }

    /**
     * Isolated helper class for LuckPerms API operations.
     *
     * <p>This class is only loaded by the JVM when its methods are first
     * invoked, ensuring that LuckPerms classes are never resolved unless
     * LuckPerms is actually present on the server. This prevents
     * {@link NoClassDefFoundError} in environments without LuckPerms.</p>
     */
    private static final class LuckPermsHelper {

        private static net.luckperms.api.LuckPerms luckPerms;

        /**
         * Initializes the LuckPerms API handle.
         */
        static void init() {
            luckPerms = net.luckperms.api.LuckPermsProvider.get();
        }

        /**
         * Checks a permission via LuckPerms cached data.
         */
        static boolean hasPermission(Player player, String permission) {
            net.luckperms.api.model.user.User user =
                    luckPerms.getUserManager().getUser(player.getUniqueId());
            return user != null && user.getCachedData()
                    .getPermissionData()
                    .checkPermission(permission)
                    .asBoolean();
        }

        /**
         * Adds a permission node via LuckPerms.
         */
        static boolean addPermission(Player player, String permission) {
            net.luckperms.api.model.user.User user =
                    luckPerms.getUserManager().getUser(player.getUniqueId());
            if (user == null) return false;
            user.data().add(net.luckperms.api.node.Node.builder(permission).build());
            luckPerms.getUserManager().saveUser(user);
            return true;
        }

        /**
         * Removes a permission node via LuckPerms.
         */
        static boolean removePermission(Player player, String permission) {
            net.luckperms.api.model.user.User user =
                    luckPerms.getUserManager().getUser(player.getUniqueId());
            if (user == null) return false;
            user.data().remove(net.luckperms.api.node.Node.builder(permission).build());
            luckPerms.getUserManager().saveUser(user);
            return true;
        }
    }
}
