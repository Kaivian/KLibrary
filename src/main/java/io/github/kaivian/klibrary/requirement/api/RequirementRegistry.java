package io.github.kaivian.klibrary.requirement.api;

import java.util.Optional;
import java.util.Set;

/**
 * Registry for requirement types and their factories.
 *
 * <p>The {@code RequirementRegistry} provides a central place for registering
 * and looking up {@link RequirementFactory} instances by their type key.
 * Both built-in and custom requirement types are registered through this
 * interface.</p>
 *
 * <p>External plugins can register custom requirement types at runtime:</p>
 * <pre>{@code
 * RequirementRegistry registry = KLibrary.getRequirementRegistry();
 * registry.register("my_custom_check", (config, services) -> {
 *     return new MyCustomRequirement(config.getString("param", "default"));
 * });
 * }</pre>
 *
 * <p>Type keys are case-insensitive and normalized to lowercase internally.</p>
 *
 * @see RequirementFactory
 * @see Requirement
 */
public interface RequirementRegistry {

    /**
     * Registers a requirement factory under the given type key.
     *
     * <p>If a factory is already registered for the given key, it will be
     * replaced and a warning will be logged.</p>
     *
     * @param key     the type key (case-insensitive, e.g., "permission", "has_money")
     * @param factory the factory that creates requirement instances
     * @throws IllegalArgumentException if {@code key} or {@code factory} is {@code null}
     */
    void register(String key, RequirementFactory factory);

    /**
     * Retrieves the factory registered for the given type key.
     *
     * @param key the type key (case-insensitive)
     * @return an {@link Optional} containing the factory, or empty if not registered
     */
    Optional<RequirementFactory> getFactory(String key);

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
