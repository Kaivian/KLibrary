package io.github.kaivian.klibrary.inventory.button;

import io.github.kaivian.klibrary.inventory.api.InventoryContext;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a clickable element in a GUI inventory.
 *
 * <p>A {@code Button} is the fundamental building block of inventory GUIs. Each button
 * defines three behaviors:</p>
 * <ul>
 *   <li><b>Visual representation</b> — The {@link ItemStack} displayed in the inventory slot</li>
 *   <li><b>Click handling</b> — The logic executed when a player clicks the button</li>
 *   <li><b>Visibility</b> — Whether the button should appear in a given context</li>
 * </ul>
 *
 * <p>Buttons are associated with specific inventory slots by the
 * {@link io.github.kaivian.klibrary.inventory.api.InventoryProvider} and are
 * rendered/evaluated during inventory updates.</p>
 *
 * <h2>Built-in Implementation</h2>
 * <p>The {@link BaseButton} class provides a full implementation with support for
 * left/right/any click actions and view requirements. For simple cases, use
 * the builder:</p>
 * <pre>{@code
 * Button button = BaseButton.builder()
 *     .template(new ItemStack(Material.DIAMOND))
 *     .leftClick(List.of(myAction))
 *     .viewRequirements(List.of(myRequirement))
 *     .build();
 * }</pre>
 *
 * <h2>Custom Implementation</h2>
 * <p>For advanced behavior (e.g., animated icons, dynamic content), implement
 * this interface directly.</p>
 *
 * @see BaseButton
 * @see io.github.kaivian.klibrary.inventory.api.InventoryProvider
 */
public interface Button {

    /**
     * Returns the {@link ItemStack} that represents this button in the inventory
     * for the given context.
     *
     * <p>This method is called during each inventory update/render cycle. Implementations
     * may return different items based on the context (e.g., player-specific data,
     * current page, or placeholder values).</p>
     *
     * <p>The returned item should be a clone or new instance to avoid shared
     * mutable state between renders.</p>
     *
     * @param context the current inventory context
     * @return the item stack to display in this button's slot; never {@code null}
     */
    @NotNull ItemStack getItem(@NotNull InventoryContext context);

    /**
     * Handles a player click on this button.
     *
     * <p>This method is called after the {@link io.github.kaivian.klibrary.inventory.api.InventoryHandler}
     * has determined that the click targets this button's slot and the button is visible.
     * The click event has already been cancelled by this point.</p>
     *
     * @param event   the raw Bukkit click event (already cancelled)
     * @param context the current inventory context
     */
    void onClick(@NotNull InventoryClickEvent event, @NotNull InventoryContext context);

    /**
     * Determines whether this button should be visible in the given context.
     *
     * <p>When this returns {@code false}, the button's slot will be empty (or show
     * a fallback item, depending on the provider implementation). Visibility is
     * typically gated by {@link io.github.kaivian.klibrary.requirement.api.Requirement}s.</p>
     *
     * @param context the current inventory context
     * @return {@code true} if the button should be displayed; {@code false} to hide it
     */
    boolean isVisible(@NotNull InventoryContext context);
}
