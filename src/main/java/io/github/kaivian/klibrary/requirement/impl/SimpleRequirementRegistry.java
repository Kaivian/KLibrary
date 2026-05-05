package io.github.kaivian.klibrary.requirement.impl;

import io.github.kaivian.klibrary.requirement.api.RequirementFactory;
import io.github.kaivian.klibrary.requirement.api.RequirementRegistry;

import java.util.*;
import java.util.logging.Logger;

/**
 * Default implementation of the {@link RequirementRegistry}.
 *
 * <p>Stores requirement factories in a case-insensitive map (all keys
 * normalized to lowercase).</p>
 */
public class SimpleRequirementRegistry implements RequirementRegistry {

    private static final Logger LOGGER = Logger.getLogger(SimpleRequirementRegistry.class.getName());

    private final Map<String, RequirementFactory> factories = new LinkedHashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void register(String key, RequirementFactory factory) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Requirement key must not be null or blank");
        }
        if (factory == null) {
            throw new IllegalArgumentException("RequirementFactory must not be null");
        }

        String normalized = key.toLowerCase(Locale.ROOT);
        if (factories.containsKey(normalized)) {
            LOGGER.warning("Overwriting existing requirement factory for key: " + normalized);
        }

        factories.put(normalized, factory);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<RequirementFactory> getFactory(String key) {
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
