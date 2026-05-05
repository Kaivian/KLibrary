package io.github.kaivian.klibrary.config;

import org.bukkit.configuration.ConfigurationSection;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A {@link ConfigNode} implementation backed by Bukkit's {@link ConfigurationSection}.
 *
 * <p>This adapter wraps Bukkit's native YAML configuration API, providing a
 * format-agnostic view of YAML configuration data. This is the default
 * implementation used when loading configuration from {@code plugin.yml}
 * or any Bukkit {@code FileConfiguration}.</p>
 *
 * <p><b>Example usage:</b></p>
 * <pre>{@code
 * ConfigurationSection section = config.getConfigurationSection("actions");
 * ConfigNode node = new BukkitConfigNode(section);
 * String type = node.getString("type", "message");
 * }</pre>
 *
 * @see ConfigNode
 */
public class BukkitConfigNode implements ConfigNode {

    private final ConfigurationSection section;

    /**
     * Constructs a new {@code BukkitConfigNode} wrapping the given section.
     *
     * @param section the Bukkit configuration section to wrap; must not be {@code null}
     * @throws IllegalArgumentException if {@code section} is {@code null}
     */
    public BukkitConfigNode(ConfigurationSection section) {
        if (section == null) {
            throw new IllegalArgumentException("ConfigurationSection must not be null");
        }
        this.section = section;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<String> getString(String key) {
        return section.contains(key) ? Optional.ofNullable(section.getString(key)) : Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Integer> getInt(String key) {
        return section.contains(key) ? Optional.of(section.getInt(key)) : Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Double> getDouble(String key) {
        return section.contains(key) ? Optional.of(section.getDouble(key)) : Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Boolean> getBoolean(String key) {
        return section.contains(key) ? Optional.of(section.getBoolean(key)) : Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<ConfigNode> getNode(String key) {
        ConfigurationSection child = section.getConfigurationSection(key);
        return child != null ? Optional.of(new BukkitConfigNode(child)) : Optional.empty();
    }

    /**
     * {@inheritDoc}
     *
     * <p>For Bukkit configurations, list-of-objects is represented as either
     * a list of {@link java.util.Map}s (from {@code getMapList()}) or as
     * numbered subsections. This implementation supports both patterns.</p>
     */
    @Override
    public List<ConfigNode> getNodeList(String key) {
        // Try map list first (common YAML pattern for lists of objects)
        List<Map<?, ?>> mapList = section.getMapList(key);
        if (!mapList.isEmpty()) {
            return mapList.stream()
                    .map(map -> {
                        // Create a temporary section from the map
                        ConfigurationSection temp = section.createSection("__temp_" + UUID.randomUUID());
                        map.forEach((k, v) -> temp.set(String.valueOf(k), v));
                        BukkitConfigNode node = new BukkitConfigNode(temp);
                        section.set(temp.getCurrentPath(), null); // cleanup
                        return (ConfigNode) node;
                    })
                    .collect(Collectors.toList());
        }

        // Fall back to numbered subsections
        ConfigurationSection listSection = section.getConfigurationSection(key);
        if (listSection == null) {
            return Collections.emptyList();
        }

        return listSection.getKeys(false).stream()
                .map(listSection::getConfigurationSection)
                .filter(Objects::nonNull)
                .map(sub -> (ConfigNode) new BukkitConfigNode(sub))
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getStringList(String key) {
        return section.contains(key) ? section.getStringList(key) : Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getKeys() {
        return section.getKeys(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean has(String key) {
        return section.contains(key);
    }

    /**
     * Returns the underlying Bukkit {@link ConfigurationSection}.
     *
     * <p>This escape hatch is provided for cases where direct access to the
     * Bukkit API is needed, but its use should be minimized to maintain
     * format-agnostic code.</p>
     *
     * @return the backing configuration section
     */
    public ConfigurationSection getSection() {
        return section;
    }
}
