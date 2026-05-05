package io.github.kaivian.klibrary.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * A {@link ConfigNode} implementation backed by Gson's {@link JsonObject}.
 *
 * <p>This adapter allows the action/requirement engine to consume JSON
 * configuration data using the same format-agnostic API. Gson is bundled
 * with Paper, so no additional dependencies are required.</p>
 *
 * <p><b>Example usage:</b></p>
 * <pre>{@code
 * JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
 * ConfigNode node = new JsonConfigNode(json);
 * String type = node.getString("type", "message");
 * }</pre>
 *
 * @see ConfigNode
 */
public class JsonConfigNode implements ConfigNode {

    private final JsonObject json;

    /**
     * Constructs a new {@code JsonConfigNode} wrapping the given JSON object.
     *
     * @param json the Gson JSON object to wrap; must not be {@code null}
     * @throws IllegalArgumentException if {@code json} is {@code null}
     */
    public JsonConfigNode(JsonObject json) {
        if (json == null) {
            throw new IllegalArgumentException("JsonObject must not be null");
        }
        this.json = json;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<String> getString(String key) {
        JsonElement el = json.get(key);
        if (el == null || !el.isJsonPrimitive()) {
            return Optional.empty();
        }
        return Optional.of(el.getAsString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Integer> getInt(String key) {
        JsonElement el = json.get(key);
        if (el == null || !el.isJsonPrimitive()) {
            return Optional.empty();
        }
        try {
            return Optional.of(el.getAsInt());
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Double> getDouble(String key) {
        JsonElement el = json.get(key);
        if (el == null || !el.isJsonPrimitive()) {
            return Optional.empty();
        }
        try {
            return Optional.of(el.getAsDouble());
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Boolean> getBoolean(String key) {
        JsonElement el = json.get(key);
        if (el == null || !el.isJsonPrimitive()) {
            return Optional.empty();
        }
        JsonPrimitive primitive = el.getAsJsonPrimitive();
        if (primitive.isBoolean()) {
            return Optional.of(primitive.getAsBoolean());
        }
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<ConfigNode> getNode(String key) {
        JsonElement el = json.get(key);
        if (el == null || !el.isJsonObject()) {
            return Optional.empty();
        }
        return Optional.of(new JsonConfigNode(el.getAsJsonObject()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ConfigNode> getNodeList(String key) {
        JsonElement el = json.get(key);
        if (el == null || !el.isJsonArray()) {
            return Collections.emptyList();
        }
        JsonArray array = el.getAsJsonArray();
        return StreamSupport.stream(array.spliterator(), false)
                .filter(JsonElement::isJsonObject)
                .map(item -> (ConfigNode) new JsonConfigNode(item.getAsJsonObject()))
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getStringList(String key) {
        JsonElement el = json.get(key);
        if (el == null || !el.isJsonArray()) {
            return Collections.emptyList();
        }
        JsonArray array = el.getAsJsonArray();
        return StreamSupport.stream(array.spliterator(), false)
                .filter(JsonElement::isJsonPrimitive)
                .map(JsonElement::getAsString)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getKeys() {
        return json.keySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean has(String key) {
        return json.has(key);
    }

    /**
     * Returns the underlying Gson {@link JsonObject}.
     *
     * <p>This escape hatch is provided for cases where direct JSON
     * manipulation is needed.</p>
     *
     * @return the backing JSON object
     */
    public JsonObject getJsonObject() {
        return json;
    }
}
