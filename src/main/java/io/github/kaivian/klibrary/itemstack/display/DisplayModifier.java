package io.github.kaivian.klibrary.itemstack.display;

import io.github.kaivian.klibrary.itemstack.api.ItemModifier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A composable modifier for setting the display name and lore of an item
 * using the Adventure Component API and MiniMessage formatting.
 *
 * <p>Supports both raw {@link Component} objects and MiniMessage-formatted strings,
 * including gradients, bold, italic, hover events, and all MiniMessage tags.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * ItemModifiers.display()
 *     .name("<gradient:#ff0000:#00ff00>Fire Blade</gradient>")
 *     .lore(
 *         "<gray>A legendary weapon</gray>",
 *         "",
 *         "<yellow><bold>Unbreakable</bold></yellow>"
 *     )
 * }</pre>
 *
 * <p>This modifier is fully decoupled from core item logic and can be reused
 * across different plugins without modification.</p>
 */
public class DisplayModifier implements ItemModifier {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private @Nullable Component displayName;
    private final List<Component> loreLines = new ArrayList<>();
    private boolean clearLore = false;

    /**
     * Sets the display name using a MiniMessage-formatted string.
     *
     * <p>Supports all MiniMessage tags including gradients, colors, decorations, etc.</p>
     *
     * @param miniMessageText the MiniMessage text (e.g. {@code "<red>Fire Sword</red>"})
     * @return this modifier for chaining
     */
    public @NotNull DisplayModifier name(@NotNull String miniMessageText) {
        this.displayName = MINI_MESSAGE.deserialize(miniMessageText);
        return this;
    }

    /**
     * Sets the display name using a raw Adventure {@link Component}.
     *
     * @param component the component to use as the display name
     * @return this modifier for chaining
     */
    public @NotNull DisplayModifier name(@NotNull Component component) {
        this.displayName = component;
        return this;
    }

    /**
     * Clears the display name (resets to default).
     *
     * @return this modifier for chaining
     */
    public @NotNull DisplayModifier clearName() {
        this.displayName = null;
        return this;
    }

    /**
     * Sets the lore using MiniMessage-formatted strings, replacing any existing lore.
     *
     * @param lines the lore lines (each parsed as MiniMessage)
     * @return this modifier for chaining
     */
    public @NotNull DisplayModifier lore(@NotNull String... lines) {
        this.clearLore = true;
        this.loreLines.clear();
        Arrays.stream(lines)
                .map(line -> line.isEmpty() ? Component.empty() : MINI_MESSAGE.deserialize(line))
                .forEach(this.loreLines::add);
        return this;
    }

    /**
     * Sets the lore using raw Adventure {@link Component}s, replacing any existing lore.
     *
     * @param lines the lore components
     * @return this modifier for chaining
     */
    public @NotNull DisplayModifier lore(@NotNull Component... lines) {
        this.clearLore = true;
        this.loreLines.clear();
        this.loreLines.addAll(Arrays.asList(lines));
        return this;
    }

    /**
     * Sets the lore using a list of raw Adventure {@link Component}s, replacing any existing lore.
     *
     * @param lines the list of lore components
     * @return this modifier for chaining
     */
    public @NotNull DisplayModifier lore(@NotNull List<Component> lines) {
        this.clearLore = true;
        this.loreLines.clear();
        this.loreLines.addAll(lines);
        return this;
    }

    /**
     * Appends a single MiniMessage-formatted line to the existing lore.
     *
     * @param miniMessageText the lore line to append
     * @return this modifier for chaining
     */
    public @NotNull DisplayModifier addLore(@NotNull String miniMessageText) {
        this.loreLines.add(
                miniMessageText.isEmpty() ? Component.empty() : MINI_MESSAGE.deserialize(miniMessageText)
        );
        return this;
    }

    /**
     * Appends a single Adventure {@link Component} to the existing lore.
     *
     * @param component the lore component to append
     * @return this modifier for chaining
     */
    public @NotNull DisplayModifier addLore(@NotNull Component component) {
        this.loreLines.add(component);
        return this;
    }

    /**
     * Clears all lore from the item.
     *
     * @return this modifier for chaining
     */
    public @NotNull DisplayModifier clearLore() {
        this.clearLore = true;
        this.loreLines.clear();
        return this;
    }

    @Override
    public void apply(@NotNull ItemStack itemStack, @NotNull ItemMeta meta) {
        if (displayName != null) {
            meta.displayName(displayName);
        }

        if (clearLore) {
            meta.lore(loreLines.isEmpty() ? null : List.copyOf(loreLines));
        } else if (!loreLines.isEmpty()) {
            List<Component> existing = meta.lore();
            List<Component> merged = existing != null ? new ArrayList<>(existing) : new ArrayList<>();
            merged.addAll(loreLines);
            meta.lore(merged);
        }
    }
}
