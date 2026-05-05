package io.github.kaivian.klibrary;

import io.github.kaivian.klibrary.action.api.ActionRegistry;
import io.github.kaivian.klibrary.action.impl.SimpleActionRegistry;
import io.github.kaivian.klibrary.action.impl.actions.*;
import io.github.kaivian.klibrary.action.impl.config.ActionConfigDeserializer;
import io.github.kaivian.klibrary.requirement.api.RequirementRegistry;
import io.github.kaivian.klibrary.requirement.impl.SimpleRequirementRegistry;
import io.github.kaivian.klibrary.requirement.impl.config.RequirementConfigDeserializer;
import io.github.kaivian.klibrary.requirement.impl.requirements.*;
import io.github.kaivian.klibrary.service.ServiceProvider;
import io.github.kaivian.klibrary.inventory.api.InventoryManager;
import io.github.kaivian.klibrary.inventory.impl.InventoryManagerImpl;
import io.github.kaivian.klibrary.inventory.impl.config.InventoryConfigDeserializer;
import io.github.kaivian.klibrary.command.KLibraryCommand;
import io.github.kaivian.klibrary.config.BukkitConfigNode;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Main plugin class for KLibrary.
 *
 * <p>KLibrary provides a modular, extensible action and requirement engine
 * for Paper plugins. It initializes the service layer, registers all built-in
 * actions and requirements, and exposes public API accessors for external
 * plugin integration.</p>
 *
 * <p><b>External plugin usage:</b></p>
 * <pre>{@code
 * KLibrary lib = KLibrary.getInstance();
 * lib.getActionRegistry().register("my_action", myFactory);
 * lib.getServiceProvider().getEconomyService().deposit(player, 100);
 * }</pre>
 */
public final class KLibrary extends JavaPlugin {

    private static KLibrary instance;

    private ServiceProvider serviceProvider;
    private ActionRegistry actionRegistry;
    private RequirementRegistry requirementRegistry;
    private ActionConfigDeserializer actionConfigDeserializer;
    private RequirementConfigDeserializer requirementConfigDeserializer;
    private InventoryConfigDeserializer inventoryConfigDeserializer;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onEnable() {
        instance = this;
        new Metrics(this, 26198);

        // Initialize service layer
        serviceProvider = new ServiceProvider(this);

        // Initialize registries
        actionRegistry = new SimpleActionRegistry();
        requirementRegistry = new SimpleRequirementRegistry();

        // Register built-in actions
        registerBuiltInActions();

        // Register built-in requirements
        registerBuiltInRequirements();

        // Initialize config deserializers
        actionConfigDeserializer = new ActionConfigDeserializer(actionRegistry, serviceProvider);
        requirementConfigDeserializer = new RequirementConfigDeserializer(
                requirementRegistry, serviceProvider, actionConfigDeserializer);
        inventoryConfigDeserializer = new InventoryConfigDeserializer(
                actionConfigDeserializer, requirementConfigDeserializer);

        // Initialize inventory manager
        InventoryManager inventoryManager = new InventoryManagerImpl(this);
        serviceProvider.setInventoryManager(inventoryManager);

        // Register new actions that depend on InventoryManager
        actionRegistry.register("open_inventory", (config, svc) ->
                new OpenInventoryAction(svc, config.getString("menu").orElse("")));
        actionRegistry.register("pop_inventory", (config, svc) ->
                new PopInventoryAction(svc));

        // Register new requirements that depend on InventoryManager
        requirementRegistry.register("inventory_open", (config, svc) ->
                new InventoryOpenRequirement(svc, config.getString("menu").orElse("")));
        requirementRegistry.register("inventory_history", (config, svc) ->
                new InventoryHistoryRequirement(svc, config.getInt("size").orElse(1)));

        // Load menus
        loadMenus();

        // Register commands
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            new KLibraryCommand(this).register(event.registrar());
        });

        getLogger().info("KLibrary action/requirement engine initialized.");
        getLogger().info("Registered " + actionRegistry.getRegisteredKeys().size() + " action types.");
        getLogger().info("Registered " + requirementRegistry.getRegisteredKeys().size() + " requirement types.");
    }

    /**
     * Reloads configuration and menus.
     */
    public void reload() {
        serviceProvider.getInventoryManager().ifPresent(manager -> {
            manager.shutdown(); // Close open inventories
            manager.clearProviders();
        });
        loadMenus();
    }

    private void loadMenus() {
        serviceProvider.getInventoryManager().ifPresent(manager -> {
            File menuFolder = new File(getDataFolder(), "menu");
            if (!menuFolder.exists()) {
                menuFolder.mkdirs();
                // Optionally save a default menu here
            }

            File[] files = menuFolder.listFiles((dir, name) -> name.endsWith(".yml"));
            if (files != null) {
                for (File file : files) {
                    try {
                        org.bukkit.configuration.file.YamlConfiguration yaml = 
                                org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(file);
                        inventoryConfigDeserializer.deserialize(new BukkitConfigNode(yaml))
                                .ifPresent(manager::registerProvider);
                    } catch (Exception e) {
                        getLogger().warning("Failed to load menu " + file.getName() + ": " + e.getMessage());
                    }
                }
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDisable() {
        if (serviceProvider != null) {
            serviceProvider.getInventoryManager().ifPresent(InventoryManager::shutdown);
        }
        instance = null;
    }

    /**
     * Registers all built-in action factories.
     */
    private void registerBuiltInActions() {
        // Messaging
        actionRegistry.register("message", (config, svc) ->
                new MessageAction(svc, config.getString("message", "")));
        actionRegistry.register("broadcast", (config, svc) ->
                new BroadcastAction(svc, config.getString("message", "")));
        actionRegistry.register("broadcast_world", (config, svc) ->
                new BroadcastWorldAction(svc, config.getString("message", "")));
        actionRegistry.register("actionbar", (config, svc) ->
                new ActionBarAction(svc, config.getString("message", "")));
        actionRegistry.register("title", (config, svc) ->
                new TitleAction(svc,
                        config.getString("title", ""),
                        config.getString("subtitle", ""),
                        config.getInt("fade_in", 10),
                        config.getInt("stay", 70),
                        config.getInt("fade_out", 20)));

        // Sound
        actionRegistry.register("sound", (config, svc) ->
                new SoundAction(svc,
                        config.getString("sound", ""),
                        config.getFloat("volume", 1.0f),
                        config.getFloat("pitch", 1.0f)));
        actionRegistry.register("broadcast_sound", (config, svc) ->
                new BroadcastSoundAction(svc,
                        config.getString("sound", ""),
                        config.getFloat("volume", 1.0f),
                        config.getFloat("pitch", 1.0f)));
        actionRegistry.register("broadcast_world_sound", (config, svc) ->
                new BroadcastWorldSoundAction(svc,
                        config.getString("sound", ""),
                        config.getFloat("volume", 1.0f),
                        config.getFloat("pitch", 1.0f)));

        // Commands
        actionRegistry.register("chat", (config, svc) ->
                new ChatAction(svc, config.getString("message", "")));
        actionRegistry.register("console_command", (config, svc) ->
                new ConsoleCommandAction(svc, config.getString("command", "")));
        actionRegistry.register("player_command", (config, svc) ->
                new PlayerCommandAction(svc, config.getString("command", "")));
        actionRegistry.register("command", (config, svc) -> {
            String executor = config.getString("executor", "CONSOLE").toUpperCase();
            return new CommandAction(svc,
                    config.getString("command", ""),
                    CommandAction.Executor.valueOf(executor));
        });

        // Economy
        actionRegistry.register("give_money", (config, svc) ->
                new GiveMoneyAction(svc, config.getDouble("amount", 0.0)));
        actionRegistry.register("take_money", (config, svc) ->
                new TakeMoneyAction(svc, config.getDouble("amount", 0.0)));

        // Experience
        actionRegistry.register("give_exp", (config, svc) ->
                new GiveExpAction(svc, config.getInt("amount", 0)));
        actionRegistry.register("take_exp", (config, svc) ->
                new TakeExpAction(svc, config.getInt("amount", 0)));

        // Permissions
        actionRegistry.register("give_permission", (config, svc) ->
                new GivePermissionAction(svc, config.getString("permission", "")));
        actionRegistry.register("take_permission", (config, svc) ->
                new TakePermissionAction(svc, config.getString("permission", "")));

        // Teleport
        actionRegistry.register("teleport", (config, svc) ->
                new TeleportAction(svc,
                        config.getString("world").orElse(null),
                        config.getDouble("x", 0.0),
                        config.getDouble("y", 64.0),
                        config.getDouble("z", 0.0),
                        config.getFloat("yaw").orElse(null),
                        config.getFloat("pitch").orElse(null)));

        // Composite actions
        actionRegistry.register("delay", (config, svc) -> {
            ActionConfigDeserializer deserializer = new ActionConfigDeserializer(actionRegistry, svc);
            return new DelayAction(svc,
                    config.getInt("ticks", 20),
                    deserializer.deserializeList(config, "actions"));
        });
        actionRegistry.register("random", (config, svc) -> {
            ActionConfigDeserializer deserializer = new ActionConfigDeserializer(actionRegistry, svc);
            return new RandomAction(svc, deserializer.deserializeList(config, "actions"));
        });
        actionRegistry.register("conditional", (config, svc) -> {
            ActionConfigDeserializer aDeserializer = new ActionConfigDeserializer(actionRegistry, svc);
            RequirementConfigDeserializer rDeserializer = new RequirementConfigDeserializer(
                    requirementRegistry, svc, aDeserializer);
            return new ConditionalAction(svc,
                    rDeserializer.deserializeList(config, "requirements"),
                    aDeserializer.deserializeList(config, "then"),
                    aDeserializer.deserializeList(config, "else"));
        });
    }

    /**
     * Registers all built-in requirement factories.
     */
    private void registerBuiltInRequirements() {
        requirementRegistry.register("permission", (config, svc) ->
                new HasPermissionRequirement(svc, config.getString("permission", "")));
        requirementRegistry.register("has_exp", (config, svc) ->
                new HasExpRequirement(svc, config.getInt("amount", 0)));
        requirementRegistry.register("has_level", (config, svc) ->
                new HasLevelRequirement(svc, config.getInt("level", 0)));
        requirementRegistry.register("has_money", (config, svc) ->
                new HasMoneyRequirement(svc, config.getDouble("amount", 0.0)));
        requirementRegistry.register("world", (config, svc) ->
                new WorldRequirement(svc, config.getStringList("worlds")));
        requirementRegistry.register("gamemode", (config, svc) ->
                new GamemodeRequirement(svc, config.getString("gamemode", "SURVIVAL")));
        requirementRegistry.register("item", (config, svc) ->
                new ItemRequirement(svc,
                        config.getString("material", "STONE"),
                        config.getInt("amount", 1)));
        requirementRegistry.register("cooldown", (config, svc) ->
                new CooldownRequirement(svc,
                        config.getInt("seconds", 60),
                        config.getString("key").orElse(null)));
        requirementRegistry.register("chance", (config, svc) ->
                new ChanceRequirement(svc, config.getDouble("chance", 0.5)));
        requirementRegistry.register("placeholder", (config, svc) ->
                new PlaceholderRequirement(svc,
                        config.getString("placeholder", ""),
                        PlaceholderRequirement.Operator.fromSymbol(config.getString("operator", "=")),
                        config.getString("value", "")));
    }

    // ===== Public API =====

    /**
     * Returns the KLibrary plugin instance.
     *
     * @return the singleton instance
     * @throws IllegalStateException if KLibrary is not enabled
     */
    public static KLibrary getInstance() {
        if (instance == null) {
            throw new IllegalStateException("KLibrary is not enabled");
        }
        return instance;
    }

    /**
     * Returns the central service provider.
     *
     * @return the {@link ServiceProvider}
     */
    public ServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    /**
     * Returns the action registry for registering and looking up action types.
     *
     * @return the {@link ActionRegistry}
     */
    public ActionRegistry getActionRegistry() {
        return actionRegistry;
    }

    /**
     * Returns the requirement registry for registering and looking up requirement types.
     *
     * @return the {@link RequirementRegistry}
     */
    public RequirementRegistry getRequirementRegistry() {
        return requirementRegistry;
    }

    /**
     * Returns the action configuration deserializer.
     *
     * @return the {@link ActionConfigDeserializer}
     */
    public ActionConfigDeserializer getActionConfigDeserializer() {
        return actionConfigDeserializer;
    }

    /**
     * Returns the requirement configuration deserializer.
     *
     * @return the {@link RequirementConfigDeserializer}
     */
    public RequirementConfigDeserializer getRequirementConfigDeserializer() {
        return requirementConfigDeserializer;
    }
}
