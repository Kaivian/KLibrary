package io.github.kaivian.klibrary.action.impl;

import io.github.kaivian.klibrary.action.api.ActionFactory;
import io.github.kaivian.klibrary.action.api.ActionRegistry;

import java.util.*;
import java.util.logging.Logger;

/**
 * Default implementation of the {@link ActionRegistry}.
 *
 * <p>Stores action factories in a case-insensitive map (all keys normalized
 * to lowercase). Thread-safe for concurrent registration and lookup.</p>
 *
 * <p>This is the standard registry used by KLibrary. External plugins
 * interact with it through the {@link ActionRegistry} interface.</p>
 */
public class SimpleActionRegistry implements ActionRegistry {

    private static final Logger LOGGER = Logger.getLogger(SimpleActionRegistry.class.getName());

    private final Map<String, ActionFactory> factories = new LinkedHashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void register(String key, ActionFactory factory) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Action key must not be null or blank");
        }
        if (factory == null) {
            throw new IllegalArgumentException("ActionFactory must not be null");
        }

        String normalized = key.toLowerCase(Locale.ROOT);
        if (factories.containsKey(normalized)) {
            LOGGER.warning("Overwriting existing action factory for key: " + normalized);
        }

        factories.put(normalized, factory);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<ActionFactory> getFactory(String key) {
        if (key == null) return Optional.empty();
        return Optional.ofNullable(factories.get(key.toLowerCase(Locale.ROOT)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getRegisteredKeys() {
        return Collections.unmodifiableSet(factories.keySet());
    }
}
