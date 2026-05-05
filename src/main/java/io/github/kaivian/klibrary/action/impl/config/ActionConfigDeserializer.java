package io.github.kaivian.klibrary.action.impl.config;

import io.github.kaivian.klibrary.action.api.Action;
import io.github.kaivian.klibrary.action.api.ActionFactory;
import io.github.kaivian.klibrary.action.api.ActionRegistry;
import io.github.kaivian.klibrary.config.ConfigNode;
import io.github.kaivian.klibrary.service.ServiceProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Deserializes {@link Action} instances from configuration nodes.
 *
 * <p>This deserializer reads a list of action definitions from a
 * {@link ConfigNode} and uses the {@link ActionRegistry} to look up
 * the appropriate factory for each action type.</p>
 *
 * <p><b>Expected YAML structure:</b></p>
 * <pre>{@code
 * actions:
 *   - type: message
 *     message: "<green>Hello!"
 *   - type: give_money
 *     amount: 100
 *   - type: sound
 *     sound: ENTITY_EXPERIENCE_ORB_PICKUP
 *     volume: 1.0
 *     pitch: 1.5
 * }</pre>
 *
 * <p><b>JSON equivalent:</b></p>
 * <pre>{@code
 * {
 *   "actions": [
 *     { "type": "message", "message": "<green>Hello!" },
 *     { "type": "give_money", "amount": 100 }
 *   ]
 * }
 * }</pre>
 *
 * @see ActionRegistry
 * @see ConfigNode
 */
public class ActionConfigDeserializer {

    private static final Logger LOGGER = Logger.getLogger(ActionConfigDeserializer.class.getName());

    private final ActionRegistry registry;
    private final ServiceProvider services;

    /**
     * Constructs a new {@code ActionConfigDeserializer}.
     *
     * @param registry the action registry for factory lookups
     * @param services the service provider for action construction
     */
    public ActionConfigDeserializer(ActionRegistry registry, ServiceProvider services) {
        this.registry = registry;
        this.services = services;
    }

    /**
     * Deserializes a list of actions from a configuration node.
     *
     * <p>Reads child nodes under the given key, each of which must contain
     * a {@code type} field matching a registered action factory.</p>
     *
     * @param parentNode the parent configuration node
     * @param key        the key containing the list of action definitions
     * @return a list of deserialized actions; invalid entries are skipped
     */
    public List<Action> deserializeList(ConfigNode parentNode, String key) {
        List<ConfigNode> nodes = parentNode.getNodeList(key);
        if (nodes.isEmpty()) {
            return Collections.emptyList();
        }

        List<Action> actions = new ArrayList<>(nodes.size());
        for (ConfigNode node : nodes) {
            deserializeSingle(node).ifPresent(actions::add);
        }
        return Collections.unmodifiableList(actions);
    }

    /**
     * Deserializes a list of actions from a list of configuration nodes.
     *
     * @param nodes the action definition nodes
     * @return a list of deserialized actions
     */
    public List<Action> deserializeList(List<ConfigNode> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return Collections.emptyList();
        }

        List<Action> actions = new ArrayList<>(nodes.size());
        for (ConfigNode node : nodes) {
            deserializeSingle(node).ifPresent(actions::add);
        }
        return Collections.unmodifiableList(actions);
    }

    /**
     * Deserializes a single action from a configuration node.
     *
     * @param node the action definition node
     * @return an {@link Optional} containing the action, or empty if invalid
     */
    public Optional<Action> deserializeSingle(ConfigNode node) {
        Optional<String> typeOpt = node.getString("type");
        if (typeOpt.isEmpty()) {
            LOGGER.warning("Action definition missing 'type' field. Skipping.");
            return Optional.empty();
        }

        String type = typeOpt.get();
        Optional<ActionFactory> factoryOpt = registry.getFactory(type);
        if (factoryOpt.isEmpty()) {
            LOGGER.warning("Unknown action type: '" + type + "'. Skipping.");
            return Optional.empty();
        }

        try {
            Action action = factoryOpt.get().create(node, services);
            return Optional.of(action);
        } catch (Exception e) {
            LOGGER.warning("Failed to create action of type '" + type + "': " + e.getMessage());
            return Optional.empty();
        }
    }
}
