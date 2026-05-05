package io.github.kaivian.klibrary.action.api;

import java.util.Optional;
import java.util.Set;

/**
 * Registry for action types and their factories.
 *
 * <p>The {@code ActionRegistry} provides a central place for registering and
 * looking up {@link ActionFactory} instances by their type key. Both built-in
 * and custom action types are registered through this interface.</p>
 *
 * <p>External plugins can register custom action types at runtime:</p>
 * <pre>{@code
 * ActionRegistry registry = KLibrary.getActionRegistry();
 * registry.register("my_custom_action", (config, services) -> {
 *     return new MyCustomAction(config.getString("param", "default"));
 * });
 * }</pre>
 *
 * <p>Type keys are case-insensitive and normalized to lowercase internally.</p>
 *
 * @see ActionFactory
 * @see Action
 */
public interface ActionRegistry {

    /**
     * Registers an action factory under the given type key.
     *
     * <p>If a factory is already registered for the given key, it will be
     * replaced and a warning will be logged.</p>
     *
     * @param key     the type key (case-insensitive, e.g., "message", "give_money")
     * @param factory the factory that creates action instances
     * @throws IllegalArgumentException if {@code key} or {@code factory} is {@code null}
     */
    void register(String key, ActionFactory factory);

    /**
     * Retrieves the factory registered for the given type key.
     *
     * @param key the type key (case-insensitive)
     * @return an {@link Optional} containing the factory, or empty if not registered
     */
    Optional<ActionFactory> getFactory(String key);

    /**
     * Returns all registered type keys.
     *
     * @return an unmodifiable set of registered keys (all lowercase)
     */
    Set<String> getRegisteredKeys();

    /**
     * Checks whether a factory is registered for the given type key.
     *
     * @param key the type key (case-insensitive)
     * @return {@code true} if a factory is registered
     */
    default boolean isRegistered(String key) {
        return getFactory(key).isPresent();
    }
}
