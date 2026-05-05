package io.github.kaivian.klibrary.util;

import org.bukkit.NamespacedKey;

/**
 * Utility for parsing namespaced key strings into Bukkit {@link NamespacedKey} instances.
 *
 * <p>Supports both bare keys (e.g., {@code "stone"}) which default to the
 * {@code minecraft} namespace, and fully-qualified keys (e.g., {@code "myplugin:custom_item"}).</p>
 *
 * <p><b>Example:</b></p>
 * <pre>{@code
 * NamespacedKey key1 = NamespaceKey.from("stone");              // minecraft:stone
 * NamespacedKey key2 = NamespaceKey.from("myplugin:my_sword");  // myplugin:my_sword
 * }</pre>
 */
public final class NamespaceKey {

    private NamespaceKey() {
        // Utility class — no instantiation
    }

    /**
     * Parses a namespaced key string into a {@link NamespacedKey}.
     *
     * <p>If the value does not contain a colon ({@code :}), the namespace
     * defaults to {@code minecraft}. Otherwise, the string is split on the
     * first colon into namespace and key components.</p>
     *
     * @param value the namespaced key string (e.g., {@code "stone"} or {@code "myplugin:item"})
     * @return the parsed {@link NamespacedKey}
     * @throws IllegalArgumentException if {@code value} is {@code null} or blank
     */
    @SuppressWarnings("deprecation")
    public static NamespacedKey from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Namespaced key value must not be null or blank");
        }

        String namespace;
        String key;
        int colonIndex = value.indexOf(':');
        if (colonIndex == -1) {
            namespace = NamespacedKey.MINECRAFT;
            key = value;
        } else {
            namespace = value.substring(0, colonIndex);
            key = value.substring(colonIndex + 1);
        }
        return new NamespacedKey(namespace, key);
    }
}
