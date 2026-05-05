package io.github.kaivian.klibrary.inventory.api;

import io.github.kaivian.klibrary.context.ExecutionContext;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.Optional;

/**
 * Context for inventory operations, wrapping an ExecutionContext and holding GUI-specific state.
 */
public final class InventoryContext {

    private final ExecutionContext executionContext;
    private final int page;

    private InventoryContext(Builder builder) {
        this.executionContext = builder.executionContextBuilder.build();
        this.page = builder.page;
    }

    public ExecutionContext getExecutionContext() {
        return executionContext;
    }

    public Optional<Player> getPlayer() {
        return executionContext.getPlayer();
    }

    public Optional<Plugin> getPlugin() {
        return executionContext.getPlugin();
    }

    public int getPage() {
        return page;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder from(ExecutionContext context) {
        return new Builder().executionContext(context);
    }

    public static final class Builder {
        private final ExecutionContext.Builder executionContextBuilder;
        private int page = 0;

        private Builder() {
            this.executionContextBuilder = ExecutionContext.builder();
        }

        public Builder executionContext(ExecutionContext context) {
            context.getPlayer().ifPresent(this.executionContextBuilder::player);
            this.executionContextBuilder.sender(context.getSender());
            context.getPlugin().ifPresent(this.executionContextBuilder::plugin);
            this.executionContextBuilder.placeholders(context.getPlaceholders());
            this.executionContextBuilder.metadata(context.getMetadata());
            return this;
        }

        public Builder player(Player player) {
            this.executionContextBuilder.player(player);
            this.executionContextBuilder.sender(player);
            return this;
        }

        public Builder plugin(Plugin plugin) {
            this.executionContextBuilder.plugin(plugin);
            return this;
        }

        public Builder page(int page) {
            this.page = page;
            return this;
        }

        public Builder placeholder(String key, String value) {
            this.executionContextBuilder.placeholder(key, value);
            return this;
        }

        public Builder metadata(String key, Object value) {
            this.executionContextBuilder.metadata(key, value);
            return this;
        }

        public InventoryContext build() {
            return new InventoryContext(this);
        }
    }
}
