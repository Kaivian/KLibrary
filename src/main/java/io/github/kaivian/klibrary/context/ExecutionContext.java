package io.github.kaivian.klibrary.context;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Shared execution context passed to all actions and requirements.
 *
 * <p>The {@code ExecutionContext} encapsulates all contextual information needed
 * during action execution or requirement evaluation. It provides access to the
 * target player, the command sender, custom placeholders, arbitrary metadata,
 * and the owning plugin (needed for scheduling, etc.).</p>
 *
 * <p>Instances are created via the {@link Builder}:</p>
 * <pre>{@code
 * ExecutionContext context = ExecutionContext.builder()
 *     .player(player)
 *     .sender(player)
 *     .plugin(myPlugin)
 *     .placeholder("reward", "100")
 *     .metadata("source", "daily_quest")
 *     .build();
 * }</pre>
 *
 * <p>The context is <b>immutable</b> after construction. Placeholder and metadata
 * maps are wrapped as unmodifiable views.</p>
 */
public final class ExecutionContext {

    private final Player player;
    private final CommandSender sender;
    private final Plugin plugin;
    private final Map<String, String> placeholders;
    private final Map<String, Object> metadata;

    private ExecutionContext(Builder builder) {
        this.player = builder.player;
        this.sender = builder.sender != null ? builder.sender : builder.player;
        this.plugin = builder.plugin;
        this.placeholders = Collections.unmodifiableMap(new HashMap<>(builder.placeholders));
        this.metadata = Collections.unmodifiableMap(new HashMap<>(builder.metadata));
    }

    /**
     * Returns the target player for this execution, if present.
     *
     * <p>May be empty for console-only contexts (e.g., broadcast actions
     * triggered from the console without a player target).</p>
     *
     * @return an {@link Optional} containing the player, or empty
     */
    public Optional<Player> getPlayer() {
        return Optional.ofNullable(player);
    }

    /**
     * Returns the command sender for this execution.
     *
     * <p>If no explicit sender was set, this defaults to the player.
     * Will be {@code null} only if neither player nor sender was provided.</p>
     *
     * @return the command sender, or {@code null} if none was set
     */
    public CommandSender getSender() {
        return sender;
    }

    /**
     * Returns the owning plugin for this execution.
     *
     * <p>This is used by actions that need to schedule tasks (e.g., {@code DelayAction})
     * or access plugin-specific resources.</p>
     *
     * @return an {@link Optional} containing the plugin, or empty if not set
     */
    public Optional<Plugin> getPlugin() {
        return Optional.ofNullable(plugin);
    }

    /**
     * Returns the custom placeholder map for this execution.
     *
     * <p>These placeholders are applied <b>in addition to</b> PlaceholderAPI
     * placeholders (if available). Custom placeholders use the format
     * {@code {key}} and are resolved before MiniMessage parsing.</p>
     *
     * @return an unmodifiable map of placeholder key-value pairs
     */
    public Map<String, String> getPlaceholders() {
        return placeholders;
    }

    /**
     * Returns the metadata map for this execution.
     *
     * <p>Metadata provides an extensible way to pass arbitrary data through
     * the execution pipeline. External plugins can use this to attach
     * custom state to action/requirement evaluations.</p>
     *
     * @return an unmodifiable map of metadata key-value pairs
     */
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    /**
     * Retrieves a metadata value by key, cast to the expected type.
     *
     * @param key  the metadata key
     * @param type the expected class type
     * @param <T>  the type parameter
     * @return an {@link Optional} containing the typed value, or empty if absent or wrong type
     */
    public <T> Optional<T> getMetadata(String key, Class<T> type) {
        Object value = metadata.get(key);
        if (type.isInstance(value)) {
            return Optional.of(type.cast(value));
        }
        return Optional.empty();
    }

    /**
     * Creates a new {@link Builder} for constructing an {@code ExecutionContext}.
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for constructing {@link ExecutionContext} instances.
     *
     * <p>All setters return {@code this} for method chaining.</p>
     */
    public static final class Builder {

        private Player player;
        private CommandSender sender;
        private Plugin plugin;
        private final Map<String, String> placeholders = new HashMap<>();
        private final Map<String, Object> metadata = new HashMap<>();

        private Builder() {
        }

        /**
         * Sets the target player for this context.
         *
         * @param player the target player
         * @return this builder
         */
        public Builder player(Player player) {
            this.player = player;
            return this;
        }

        /**
         * Sets the command sender for this context.
         *
         * @param sender the command sender
         * @return this builder
         */
        public Builder sender(CommandSender sender) {
            this.sender = sender;
            return this;
        }

        /**
         * Sets the owning plugin for this context.
         *
         * @param plugin the plugin instance
         * @return this builder
         */
        public Builder plugin(Plugin plugin) {
            this.plugin = plugin;
            return this;
        }

        /**
         * Adds a single custom placeholder.
         *
         * @param key   the placeholder key (without braces)
         * @param value the placeholder value
         * @return this builder
         */
        public Builder placeholder(String key, String value) {
            this.placeholders.put(key, value);
            return this;
        }

        /**
         * Adds multiple custom placeholders.
         *
         * @param placeholders a map of placeholder key-value pairs
         * @return this builder
         */
        public Builder placeholders(Map<String, String> placeholders) {
            this.placeholders.putAll(placeholders);
            return this;
        }

        /**
         * Adds a single metadata entry.
         *
         * @param key   the metadata key
         * @param value the metadata value
         * @return this builder
         */
        public Builder metadata(String key, Object value) {
            this.metadata.put(key, value);
            return this;
        }

        /**
         * Adds multiple metadata entries.
         *
         * @param metadata a map of metadata key-value pairs
         * @return this builder
         */
        public Builder metadata(Map<String, Object> metadata) {
            this.metadata.putAll(metadata);
            return this;
        }

        /**
         * Builds and returns the {@link ExecutionContext}.
         *
         * @return the constructed context
         */
        public ExecutionContext build() {
            return new ExecutionContext(this);
        }
    }
}
