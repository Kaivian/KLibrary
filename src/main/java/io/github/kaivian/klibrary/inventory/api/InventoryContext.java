package io.github.kaivian.klibrary.inventory.api;

import io.github.kaivian.klibrary.context.ExecutionContext;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Optional;

/**
 * Context object for inventory GUI operations, wrapping an {@link ExecutionContext}
 * with GUI-specific state such as pagination.
 *
 * <p>The {@code InventoryContext} serves as the bridge between the action/requirement
 * engine's {@link ExecutionContext} and the inventory system. It provides convenient
 * access to player, plugin, and placeholder data while adding inventory-specific
 * features like page tracking.</p>
 *
 * <p>Instances are created via the {@link Builder}:</p>
 * <pre>{@code
 * InventoryContext context = InventoryContext.builder()
 *     .player(player)
 *     .plugin(myPlugin)
 *     .page(0)
 *     .placeholder("currency", "Coins")
 *     .metadata("category", "weapons")
 *     .build();
 * }</pre>
 *
 * <p>Alternatively, an existing {@link ExecutionContext} can be wrapped:</p>
 * <pre>{@code
 * InventoryContext context = InventoryContext.from(executionContext)
 *     .page(2)
 *     .build();
 * }</pre>
 *
 * <p>The context is <b>immutable</b> after construction.</p>
 *
 * @see ExecutionContext
 * @see InventoryManager
 * @see InventoryView
 */
public final class InventoryContext {

    private final ExecutionContext executionContext;
    private final int page;

    private InventoryContext(Builder builder) {
        this.executionContext = builder.executionContextBuilder.build();
        this.page = builder.page;
    }

    /**
     * Returns the underlying {@link ExecutionContext} for this inventory session.
     *
     * <p>The execution context provides access to placeholders, metadata, and
     * the command sender — all of which are shared with the action/requirement engine.</p>
     *
     * @return the execution context; never {@code null}
     */
    public ExecutionContext getExecutionContext() {
        return executionContext;
    }

    /**
     * Returns the player associated with this inventory session.
     *
     * @return an {@link Optional} containing the player, or empty if not set
     */
    public Optional<Player> getPlayer() {
        return executionContext.getPlayer();
    }

    /**
     * Returns the plugin associated with this inventory session.
     *
     * @return an {@link Optional} containing the plugin, or empty if not set
     */
    public Optional<Plugin> getPlugin() {
        return executionContext.getPlugin();
    }

    /**
     * Returns the current page index for paginated inventories.
     *
     * <p>Page indices are zero-based. A value of 0 indicates the first page.</p>
     *
     * @return the current page index (default: 0)
     */
    public int getPage() {
        return page;
    }

    /**
     * Creates a new {@link Builder} for constructing an {@code InventoryContext}.
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates a new {@link Builder} pre-populated with data from an existing
     * {@link ExecutionContext}.
     *
     * <p>This copies the player, sender, plugin, placeholders, and metadata
     * from the execution context into the builder.</p>
     *
     * @param context the execution context to copy from; must not be {@code null}
     * @return a new builder instance pre-populated with the context data
     */
    public static Builder from(ExecutionContext context) {
        return new Builder().executionContext(context);
    }

    /**
     * Builder for constructing {@link InventoryContext} instances.
     *
     * <p>All setters return {@code this} for method chaining. The builder
     * internally manages an {@link ExecutionContext.Builder} for the shared context data.</p>
     */
    public static final class Builder {
        private final ExecutionContext.Builder executionContextBuilder;
        private int page = 0;

        private Builder() {
            this.executionContextBuilder = ExecutionContext.builder();
        }

        /**
         * Copies all data from an existing {@link ExecutionContext} into this builder.
         *
         * @param context the execution context to copy from
         * @return this builder
         */
        public Builder executionContext(ExecutionContext context) {
            context.getPlayer().ifPresent(this.executionContextBuilder::player);
            this.executionContextBuilder.sender(context.getSender());
            context.getPlugin().ifPresent(this.executionContextBuilder::plugin);
            this.executionContextBuilder.placeholders(context.getPlaceholders());
            this.executionContextBuilder.metadata(context.getMetadata());
            return this;
        }

        /**
         * Sets the player for this inventory context.
         *
         * <p>Also sets the player as the command sender in the underlying
         * execution context.</p>
         *
         * @param player the player
         * @return this builder
         */
        public Builder player(Player player) {
            this.executionContextBuilder.player(player);
            this.executionContextBuilder.sender(player);
            return this;
        }

        /**
         * Sets the plugin for this inventory context.
         *
         * @param plugin the plugin instance
         * @return this builder
         */
        public Builder plugin(Plugin plugin) {
            this.executionContextBuilder.plugin(plugin);
            return this;
        }

        /**
         * Sets the page index for paginated inventories.
         *
         * @param page the zero-based page index
         * @return this builder
         */
        public Builder page(int page) {
            this.page = page;
            return this;
        }

        /**
         * Adds a custom placeholder to the inventory context.
         *
         * @param key   the placeholder key (without braces)
         * @param value the placeholder value
         * @return this builder
         */
        public Builder placeholder(String key, String value) {
            this.executionContextBuilder.placeholder(key, value);
            return this;
        }

        /**
         * Adds a metadata entry to the inventory context.
         *
         * @param key   the metadata key
         * @param value the metadata value
         * @return this builder
         */
        public Builder metadata(String key, Object value) {
            this.executionContextBuilder.metadata(key, value);
            return this;
        }

        /**
         * Builds and returns the {@link InventoryContext}.
         *
         * @return the constructed inventory context
         */
        public InventoryContext build() {
            return new InventoryContext(this);
        }
    }
}
