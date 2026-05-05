package io.github.kaivian.klibrary.itemstack.implement;

import io.github.kaivian.klibrary.itemstack.api.NbtAdapter;
import io.github.kaivian.klibrary.util.NamespaceKey;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Paper-specific implementation of {@link NbtAdapter} backed by
 * Bukkit's {@link PersistentDataContainer}.
 *
 * <p>This class is internal and should not be referenced directly by consumers.
 * Access it through the modifier pipeline via {@code ItemModifiers.nbt(...)}.</p>
 */
public class PaperNbtAdapter implements NbtAdapter {

    private final PersistentDataContainer container;

    /**
     * Creates a new adapter wrapping the given container.
     *
     * @param container the persistent data container to wrap
     */
    public PaperNbtAdapter(@NotNull PersistentDataContainer container) {
        this.container = Objects.requireNonNull(container, "container must not be null");
    }

    @Override
    public <T, Z> void set(@NotNull String key, @NotNull PersistentDataType<T, Z> type, @NotNull Z value) {
        set(NamespaceKey.from(key), type, value);
    }

    @Override
    public <T, Z> void set(@NotNull NamespacedKey key, @NotNull PersistentDataType<T, Z> type, @NotNull Z value) {
        container.set(key, type, value);
    }

    @Override
    public <T, Z> @Nullable Z get(@NotNull String key, @NotNull PersistentDataType<T, Z> type) {
        return get(NamespaceKey.from(key), type);
    }

    @Override
    public <T, Z> @Nullable Z get(@NotNull NamespacedKey key, @NotNull PersistentDataType<T, Z> type) {
        return container.get(key, type);
    }

    @Override
    public boolean has(@NotNull String key) {
        return has(NamespaceKey.from(key));
    }

    @Override
    public boolean has(@NotNull NamespacedKey key) {
        return container.has(key);
    }

    @Override
    public void remove(@NotNull String key) {
        remove(NamespaceKey.from(key));
    }

    @Override
    public void remove(@NotNull NamespacedKey key) {
        container.remove(key);
    }

    @Override
    public @NotNull PersistentDataContainer getContainer() {
        return container;
    }
}
