package io.github.kaivian.klibrary.inventory.impl.config;

import io.github.kaivian.klibrary.action.api.Action;
import io.github.kaivian.klibrary.action.impl.config.ActionConfigDeserializer;
import io.github.kaivian.klibrary.config.ConfigNode;
import io.github.kaivian.klibrary.inventory.api.InventoryProvider;
import io.github.kaivian.klibrary.inventory.button.BaseButton;
import io.github.kaivian.klibrary.inventory.button.Button;
import io.github.kaivian.klibrary.inventory.impl.ConfigurableInventoryProvider;
import io.github.kaivian.klibrary.itemstack.builder.ItemStackBuilders;
import io.github.kaivian.klibrary.itemstack.modifier.ItemModifiers;
import io.github.kaivian.klibrary.requirement.api.Requirement;
import io.github.kaivian.klibrary.requirement.impl.config.RequirementConfigDeserializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Deserializes {@link InventoryProvider} instances from configuration nodes.
 */
public class InventoryConfigDeserializer {

    private static final Logger LOGGER = Logger.getLogger(InventoryConfigDeserializer.class.getName());

    private final ActionConfigDeserializer actionDeserializer;
    private final RequirementConfigDeserializer requirementDeserializer;

    public InventoryConfigDeserializer(ActionConfigDeserializer actionDeserializer, RequirementConfigDeserializer requirementDeserializer) {
        this.actionDeserializer = actionDeserializer;
        this.requirementDeserializer = requirementDeserializer;
    }

    public Optional<InventoryProvider> deserialize(ConfigNode node) {
        String id = node.getString("menu-id").orElse("unknown_menu");
        String title = node.getString("menu-title").orElse("GUI");
        int size = node.getInt("size").orElse(node.getInt("menu-type").orElse(54)); // Assuming size directly or map menu-type
        boolean autoRefresh = node.getBoolean("auto-refresh").orElse(false);

        List<Requirement> openRequirements = requirementDeserializer.deserializeList(node, "open-requirement");
        List<Action> openActions = actionDeserializer.deserializeList(node, "open-action");

        ConfigurableInventoryProvider provider = new ConfigurableInventoryProvider(
                id, title, size, autoRefresh, openRequirements, openActions
        );

        Optional<ConfigNode> itemsNodeOpt = node.getNode("items");
        if (itemsNodeOpt.isPresent()) {
            ConfigNode itemsNode = itemsNodeOpt.get();
            for (String key : itemsNode.getKeys()) {
                Optional<ConfigNode> itemNodeOpt = itemsNode.getNode(key);
                if (itemNodeOpt.isEmpty()) continue;
                ConfigNode itemNode = itemNodeOpt.get();

                try {
                    Button button = deserializeButton(itemNode, key);
                    
                    // Parse slots
                    List<Integer> slots = new ArrayList<>();
                    itemNode.getInt("slot").ifPresent(slots::add);
                    slots.addAll(itemNode.getIntList("slots"));
                    slots.addAll(itemNode.getIntList("slot")); // Sometimes users use 'slot' as list

                    for (int slot : slots) {
                        provider.addButton(slot, button);
                    }
                } catch (Exception e) {
                    LOGGER.warning("Failed to load button '" + key + "' in menu '" + id + "': " + e.getMessage());
                }
            }
        }

        return Optional.of(provider);
    }

    private Button deserializeButton(ConfigNode node, String id) {
        String materialStr = node.getString("material").orElse("STONE");
        Material material;
        try {
            material = Material.valueOf(materialStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Invalid material '" + materialStr + "' for button '" + id + "'. Using STONE.");
            material = Material.STONE;
        }

        var builder = ItemStackBuilders.of(material);
        var display = ItemModifiers.display();

        node.getString("display-name").ifPresent(display::name);
        
        List<String> lore = node.getStringList("lore");
        if (!lore.isEmpty()) {
            display.lore(lore.toArray(new String[0]));
        } else {
            // Check if it's a single string
            node.getString("lore").ifPresent(l -> display.lore(l));
        }
        
        // Hide tooltip if completely empty name and lore (legacy feature)
        if (node.getString("display-name").isEmpty() && lore.isEmpty() && node.getString("lore").isEmpty()) {
            builder.with(ItemModifiers.hideTooltip(true));
        } else {
            builder.with(display);
        }

        List<String> flagsStr = node.getStringList("item-flags");
        if (!flagsStr.isEmpty()) {
            List<ItemFlag> flags = new ArrayList<>();
            for (String flag : flagsStr) {
                try {
                    flags.add(ItemFlag.valueOf(flag.toUpperCase()));
                } catch (IllegalArgumentException ignored) {}
            }
            if (!flags.isEmpty()) {
                builder.with(ItemModifiers.addFlags(flags.toArray(new ItemFlag[0])));
            }
        }

        ItemStack template = builder.build();

        List<Action> leftClick = actionDeserializer.deserializeList(node, "left-click-action");
        List<Action> rightClick = actionDeserializer.deserializeList(node, "right-click-action");
        List<Action> anyClick = actionDeserializer.deserializeList(node, "click-action");
        List<Requirement> viewRequirements = requirementDeserializer.deserializeList(node, "requirements");

        return BaseButton.builder()
                .template(template)
                .leftClick(leftClick)
                .rightClick(rightClick)
                .anyClick(anyClick)
                .viewRequirements(viewRequirements)
                .build();
    }
}
