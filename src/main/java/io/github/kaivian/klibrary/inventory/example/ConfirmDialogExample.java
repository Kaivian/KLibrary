package io.github.kaivian.klibrary.inventory.example;

import io.github.kaivian.klibrary.inventory.api.InventoryContext;
import io.github.kaivian.klibrary.inventory.api.InventoryManager;
import io.github.kaivian.klibrary.inventory.api.InventoryProvider;
import io.github.kaivian.klibrary.inventory.api.InventoryView;
import io.github.kaivian.klibrary.inventory.button.BaseButton;
import io.github.kaivian.klibrary.inventory.button.Button;
import io.github.kaivian.klibrary.inventory.impl.InventoryViewImpl;
import io.github.kaivian.klibrary.itemstack.builder.ItemStackBuilders;
import io.github.kaivian.klibrary.itemstack.modifier.ItemModifiers;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Example demonstrating how to create a programmatic GUI inventory using
 * KLibrary's inventory system.
 *
 * <p>This example creates a "confirm dialog" menu with two buttons (Confirm and Cancel)
 * and shows the full lifecycle of programmatic inventory usage:</p>
 * <ol>
 *   <li>Implementing {@link InventoryProvider} with custom layout</li>
 *   <li>Creating buttons with click actions using {@link BaseButton}</li>
 *   <li>Building items with the {@link ItemStackBuilders} API</li>
 *   <li>Registering and opening the menu via {@link InventoryManager}</li>
 * </ol>
 *
 * <h2>Registration (in your plugin's onEnable)</h2>
 * <pre>{@code
 * InventoryManager manager = klibrary.getServiceProvider()
 *     .getInventoryManager().orElseThrow();
 * manager.registerProvider(new ConfirmDialogExample());
 * }</pre>
 *
 * <h2>Opening the Menu</h2>
 * <pre>{@code
 * InventoryContext context = InventoryContext.builder()
 *     .player(player)
 *     .plugin(myPlugin)
 *     .metadata("action", "delete_home")
 *     .build();
 * manager.push(player, "confirm_dialog", context);
 * }</pre>
 *
 * <p><b>Note:</b> This is an illustrative example and is NOT intended for production use.</p>
 */
public class ConfirmDialogExample implements InventoryProvider {

    /** The confirm button — green wool with a checkmark name. */
    private final Button confirmButton;

    /** The cancel button — red wool with an X name. */
    private final Button cancelButton;

    /**
     * Constructs the confirm dialog with pre-built buttons.
     *
     * <p>In a real implementation, the click actions would be provided
     * via configuration or dependency injection rather than inline lambdas.</p>
     */
    public ConfirmDialogExample() {
        // ─── Confirm Button ──────────────────────────────────────────
        ItemStack confirmItem = ItemStackBuilders.of(Material.LIME_WOOL)
                .with(ItemModifiers.display()
                        .name("<green><bold>✔ Confirm</bold></green>")
                        .lore("<gray>Click to confirm this action"))
                .build();

        this.confirmButton = BaseButton.builder()
                .template(confirmItem)
                .leftClick(List.of(context -> {
                    context.getPlayer().ifPresent(player ->
                            player.sendMessage(Component.text("Action confirmed!")));
                    return io.github.kaivian.klibrary.action.api.ActionResult.success();
                }))
                .build();

        // ─── Cancel Button ───────────────────────────────────────────
        ItemStack cancelItem = ItemStackBuilders.of(Material.RED_WOOL)
                .with(ItemModifiers.display()
                        .name("<red><bold>✘ Cancel</bold></red>")
                        .lore("<gray>Click to cancel"))
                .build();

        this.cancelButton = BaseButton.builder()
                .template(cancelItem)
                .leftClick(List.of(context -> {
                    context.getPlayer().ifPresent(Player::closeInventory);
                    return io.github.kaivian.klibrary.action.api.ActionResult.success();
                }))
                .build();
    }

    /** {@inheritDoc} */
    @Override
    public @NotNull String getId() {
        return "confirm_dialog";
    }

    /**
     * {@inheritDoc}
     *
     * <p>Creates a 27-slot (3-row) inventory titled "Confirm?".</p>
     */
    @Override
    public @NotNull InventoryView createView(@NotNull InventoryContext context) {
        Inventory inventory = Bukkit.createInventory(null, 27,
                Component.text("Confirm?"));
        return new InventoryViewImpl(getId(), inventory, context, this);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Places the confirm button at slot 11 (left side) and the cancel button
     * at slot 15 (right side) for a balanced, centered layout.</p>
     */
    @Override
    public void update(@NotNull InventoryView view) {
        Inventory inv = view.getInventory();
        InventoryContext ctx = view.getContext();

        inv.clear();
        inv.setItem(11, confirmButton.getItem(ctx));  // Left-center
        inv.setItem(15, cancelButton.getItem(ctx));    // Right-center
    }

    /**
     * {@inheritDoc}
     *
     * <p>Delegates clicks to the confirm (slot 11) or cancel (slot 15) buttons.</p>
     */
    @Override
    public void onClick(InventoryClickEvent event, InventoryContext context) {
        event.setCancelled(true);

        int slot = event.getSlot();
        if (slot == 11) {
            confirmButton.onClick(event, context);
        } else if (slot == 15) {
            cancelButton.onClick(event, context);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onOpen(InventoryOpenEvent event, InventoryContext context) {
        // Optional: play a sound when the dialog opens
    }

    /** {@inheritDoc} */
    @Override
    public void onClose(InventoryCloseEvent event, InventoryContext context) {
        // Optional: perform cleanup on close
    }
}
