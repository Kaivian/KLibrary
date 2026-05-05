package io.github.kaivian.klibrary.requirement.impl.config;

import io.github.kaivian.klibrary.action.api.Action;
import io.github.kaivian.klibrary.action.impl.config.ActionConfigDeserializer;
import io.github.kaivian.klibrary.config.ConfigNode;
import io.github.kaivian.klibrary.requirement.api.Requirement;
import io.github.kaivian.klibrary.requirement.api.RequirementFactory;
import io.github.kaivian.klibrary.requirement.api.RequirementRegistry;
import io.github.kaivian.klibrary.requirement.impl.AbstractRequirement;
import io.github.kaivian.klibrary.requirement.impl.RequirementGroup;
import io.github.kaivian.klibrary.service.ServiceProvider;

import java.util.*;
import java.util.logging.Logger;

/**
 * Deserializes {@link Requirement} instances from configuration nodes.
 *
 * <p>This deserializer reads requirement definitions from a {@link ConfigNode}
 * and uses the {@link RequirementRegistry} to look up the appropriate factory
 * for each requirement type. It also supports requirement groups with AND/OR logic
 * and accept/deny action handlers.</p>
 *
 * <p><b>Expected YAML structure:</b></p>
 * <pre>{@code
 * requirements:
 *   - type: permission
 *     permission: "example.vip"
 *     deny-actions:
 *       - type: message
 *         message: "<red>You need VIP!"
 *   - type: has_money
 *     amount: 100
 *     accept-actions:
 *       - type: message
 *         message: "<green>Payment accepted!"
 * }</pre>
 *
 * <p><b>Group mode:</b></p>
 * <pre>{@code
 * requirements:
 *   mode: OR
 *   checks:
 *     - type: permission
 *       permission: "example.bypass"
 *     - type: has_money
 *       amount: 500
 * }</pre>
 *
 * @see RequirementRegistry
 * @see ConfigNode
 */
public class RequirementConfigDeserializer {

    private static final Logger LOGGER = Logger.getLogger(RequirementConfigDeserializer.class.getName());

    private final RequirementRegistry registry;
    private final ServiceProvider services;
    private final ActionConfigDeserializer actionDeserializer;

    /**
     * Constructs a new {@code RequirementConfigDeserializer}.
     *
     * @param registry           the requirement registry for factory lookups
     * @param services           the service provider
     * @param actionDeserializer the action deserializer (for accept/deny action handlers)
     */
    public RequirementConfigDeserializer(RequirementRegistry registry,
                                         ServiceProvider services,
                                         ActionConfigDeserializer actionDeserializer) {
        this.registry = registry;
        this.services = services;
        this.actionDeserializer = actionDeserializer;
    }

    /**
     * Deserializes a list of requirements from a configuration node.
     *
     * @param parentNode the parent configuration node
     * @param key        the key containing the list of requirement definitions
     * @return a list of deserialized requirements
     */
    public List<Requirement> deserializeList(ConfigNode parentNode, String key) {
        List<ConfigNode> nodes = parentNode.getNodeList(key);
        if (nodes.isEmpty()) {
            return Collections.emptyList();
        }

        List<Requirement> requirements = new ArrayList<>(nodes.size());
        for (ConfigNode node : nodes) {
            deserializeSingle(node).ifPresent(requirements::add);
        }
        return Collections.unmodifiableList(requirements);
    }

    /**
     * Deserializes a requirement group from a configuration node.
     *
     * <p>If the node contains a {@code mode} field, it creates a
     * {@link RequirementGroup} wrapping all child requirements.</p>
     *
     * @param parentNode the parent configuration node
     * @param key        the key containing the requirement group definition
     * @return a requirement (possibly a group), or empty if invalid
     */
    public Optional<Requirement> deserializeGroup(ConfigNode parentNode, String key) {
        Optional<ConfigNode> groupNode = parentNode.getNode(key);
        if (groupNode.isEmpty()) {
            // Try as a list directly
            List<Requirement> list = deserializeList(parentNode, key);
            if (list.isEmpty()) return Optional.empty();
            return Optional.of(new RequirementGroup(list, RequirementGroup.GroupMode.AND));
        }

        ConfigNode node = groupNode.get();
        String modeStr = node.getString("mode", "AND").toUpperCase(Locale.ROOT);
        RequirementGroup.GroupMode mode;
        try {
            mode = RequirementGroup.GroupMode.valueOf(modeStr);
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Invalid requirement group mode: " + modeStr + ", defaulting to AND");
            mode = RequirementGroup.GroupMode.AND;
        }

        List<Requirement> checks = new ArrayList<>();
        for (ConfigNode check : node.getNodeList("checks")) {
            deserializeSingle(check).ifPresent(checks::add);
        }

        if (checks.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new RequirementGroup(checks, mode));
    }

    /**
     * Deserializes a single requirement from a configuration node.
     *
     * @param node the requirement definition node
     * @return an {@link Optional} containing the requirement, or empty if invalid
     */
    public Optional<Requirement> deserializeSingle(ConfigNode node) {
        Optional<String> typeOpt = node.getString("type");
        if (typeOpt.isEmpty()) {
            LOGGER.warning("Requirement definition missing 'type' field. Skipping.");
            return Optional.empty();
        }

        String type = typeOpt.get();
        Optional<RequirementFactory> factoryOpt = registry.getFactory(type);
        if (factoryOpt.isEmpty()) {
            LOGGER.warning("Unknown requirement type: '" + type + "'. Skipping.");
            return Optional.empty();
        }

        try {
            Requirement requirement = factoryOpt.get().create(node, services);

            // Wire up accept/deny action handlers if the requirement supports them
            if (requirement instanceof AbstractRequirement abstractReq) {
                List<Action> denyActions = actionDeserializer.deserializeList(node, "deny-actions");
                List<Action> acceptActions = actionDeserializer.deserializeList(node, "accept-actions");
                abstractReq.setDenyActions(denyActions);
                abstractReq.setAcceptActions(acceptActions);
            }

            return Optional.of(requirement);
        } catch (Exception e) {
            LOGGER.warning("Failed to create requirement of type '" + type + "': " + e.getMessage());
            return Optional.empty();
        }
    }
}
