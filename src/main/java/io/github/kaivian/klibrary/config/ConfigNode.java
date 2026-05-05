package io.github.kaivian.klibrary.config;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * A format-agnostic abstraction over configuration entries.
 *
 * <p>This interface decouples the action/requirement engine from any specific
 * configuration format (YAML, JSON, database, etc.). Implementations wrap
 * a concrete data source and expose a uniform API for reading values.</p>
 *
 * <p>Built-in implementations include {@link BukkitConfigNode} for Bukkit's
 * {@code ConfigurationSection} (YAML) and {@link JsonConfigNode} for Gson
 * {@code JsonObject}.</p>
 *
 * @see BukkitConfigNode
 * @see JsonConfigNode
 */
public interface ConfigNode {

    /**
     * Retrieves a string value at the given key.
     *
     * @param key the configuration key
     * @return an {@link Optional} containing the string value, or empty if absent
     */
    Optional<String> getString(String key);

    /**
     * Retrieves a string value at the given key, returning a default if absent.
     *
     * @param key          the configuration key
     * @param defaultValue the fallback value
     * @return the string value, or {@code defaultValue} if absent
     */
    default String getString(String key, String defaultValue) {
        return getString(key).orElse(defaultValue);
    }

    /**
     * Retrieves an integer value at the given key.
     *
     * @param key the configuration key
     * @return an {@link Optional} containing the integer value, or empty if absent or not a number
     */
    Optional<Integer> getInt(String key);

    /**
     * Retrieves an integer value at the given key, returning a default if absent.
     *
     * @param key          the configuration key
     * @param defaultValue the fallback value
     * @return the integer value, or {@code defaultValue} if absent
     */
    default int getInt(String key, int defaultValue) {
        return getInt(key).orElse(defaultValue);
    }

    /**
     * Retrieves a double value at the given key.
     *
     * @param key the configuration key
     * @return an {@link Optional} containing the double value, or empty if absent or not a number
     */
    Optional<Double> getDouble(String key);

    /**
     * Retrieves a double value at the given key, returning a default if absent.
     *
     * @param key          the configuration key
     * @param defaultValue the fallback value
     * @return the double value, or {@code defaultValue} if absent
     */
    default double getDouble(String key, double defaultValue) {
        return getDouble(key).orElse(defaultValue);
    }

    /**
     * Retrieves a boolean value at the given key.
     *
     * @param key the configuration key
     * @return an {@link Optional} containing the boolean value, or empty if absent
     */
    Optional<Boolean> getBoolean(String key);

    /**
     * Retrieves a boolean value at the given key, returning a default if absent.
     *
     * @param key          the configuration key
     * @param defaultValue the fallback value
     * @return the boolean value, or {@code defaultValue} if absent
     */
    default boolean getBoolean(String key, boolean defaultValue) {
        return getBoolean(key).orElse(defaultValue);
    }

    /**
     * Retrieves a nested configuration node at the given key.
     *
     * @param key the configuration key
     * @return an {@link Optional} containing the child {@link ConfigNode}, or empty if absent
     */
    Optional<ConfigNode> getNode(String key);

    /**
     * Retrieves a list of child configuration nodes at the given key.
     *
     * <p>This is used for configuration entries that contain a list of objects,
     * such as a list of action definitions or requirement definitions.</p>
     *
     * @param key the configuration key
     * @return a list of child {@link ConfigNode}s, or an empty list if absent
     */
    List<ConfigNode> getNodeList(String key);

    /**
     * Retrieves a list of string values at the given key.
     *
     * @param key the configuration key
     * @return a list of strings, or an empty list if absent
     */
    List<String> getStringList(String key);

    /**
     * Returns all top-level keys in this configuration node.
     *
     * @return a set of key names
     */
    Set<String> getKeys();

    /**
     * Checks whether a given key exists in this configuration node.
     *
     * @param key the configuration key
     * @return {@code true} if the key is present, {@code false} otherwise
     */
    boolean has(String key);

    /**
     * Retrieves a float value at the given key.
     *
     * @param key the configuration key
     * @return an {@link Optional} containing the float value, or empty if absent
     */
    default Optional<Float> getFloat(String key) {
        return getDouble(key).map(Double::floatValue);
    }

    /**
     * Retrieves a float value at the given key, returning a default if absent.
     *
     * @param key          the configuration key
     * @param defaultValue the fallback value
     * @return the float value, or {@code defaultValue} if absent
     */
    default float getFloat(String key, float defaultValue) {
        return getFloat(key).orElse(defaultValue);
    }

    /**
     * Retrieves a long value at the given key.
     *
     * @param key the configuration key
     * @return an {@link Optional} containing the long value, or empty if absent
     */
    default Optional<Long> getLong(String key) {
        return getInt(key).map(Integer::longValue);
    }

    /**
     * Retrieves a long value at the given key, returning a default if absent.
     *
     * @param key          the configuration key
     * @param defaultValue the fallback value
     * @return the long value, or {@code defaultValue} if absent
     */
    default long getLong(String key, long defaultValue) {
        return getLong(key).orElse(defaultValue);
    }
}
