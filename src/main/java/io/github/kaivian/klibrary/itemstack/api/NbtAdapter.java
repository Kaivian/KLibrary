package io.github.kaivian.klibrary.itemstack.api;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An abstraction over Bukkit's {@link PersistentDataContainer} for reading and writing
 * custom NBT data on items.
 *
 * <p>This adapter ensures that modifier code never directly depends on the underlying
 * PDC implementation, enabling easier testing and future-proofing.</p>
 *
 * <p>String-based keys are automatically resolved to {@link NamespacedKey} instances.
 * Keys without a namespace prefix default to {@code minecraft:}.</p>
 */
public interface NbtAdapter {

    /**
     * Sets a value in the data container using a string key.
     *
     * @param key   the key (e.g. {@code "myplugin:custom_id"})
     * @param type  the persistent data type
     * @param value the value to store
     * @param <T>   the primary object type of the data type
     * @param <Z>   the retrieved object type of the data type
     */
    <T, Z> void set(@NotNull String key, @NotNull PersistentDataType<T, Z> type, @NotNull Z value);

    /**
     * Sets a value in the data container.
     *
     * @param key   the namespaced key
     * @param type  the persistent data type
     * @param value the value to store
     * @param <T>   the primary object type of the data type
     * @param <Z>   the retrieved object type of the data type
     */
    <T, Z> void set(@NotNull NamespacedKey key, @NotNull PersistentDataType<T, Z> type, @NotNull Z value);

    /**
     * Retrieves a value from the data container using a string key.
     *
     * @param key  the key
     * @param type the persistent data type
     * @param <T>  the primary object type of the data type
     * @param <Z>  the retrieved object type of the data type
     * @return the value, or {@code null} if not present
     */
    <T, Z> @Nullable Z get(@NotNull String key, @NotNull PersistentDataType<T, Z> type);

    /**
     * Retrieves a value from the data container.
     *
     * @param key  the namespaced key
     * @param type the persistent data type
     * @param <T>  the primary object type of the data type
     * @param <Z>  the retrieved object type of the data type
     * @return the value, or {@code null} if not present
     */
    <T, Z> @Nullable Z get(@NotNull NamespacedKey key, @NotNull PersistentDataType<T, Z> type);

    /**
     * Checks if the data container contains a value for the given string key.
     *
     * @param key the key
     * @return {@code true} if a value exists
     */
    boolean has(@NotNull String key);

    /**
     * Checks if the data container contains a value for the given namespaced key.
     *
     * @param key the namespaced key
     * @return {@code true} if a value exists
     */
    boolean has(@NotNull NamespacedKey key);

    /**
     * Removes a value from the data container using a string key.
     *
     * @param key the key
     */
    void remove(@NotNull String key);

    /**
     * Removes a value from the data container.
     *
     * @param key the namespaced key
     */
    void remove(@NotNull NamespacedKey key);

    /**
     * Returns the underlying {@link PersistentDataContainer} for advanced use cases.
     *
     * @return the raw container
     */
    @NotNull PersistentDataContainer getContainer();
}
