package io.github.kaivian.klibrary.inventory.impl;

import io.github.kaivian.klibrary.inventory.api.InventoryContext;
import io.github.kaivian.klibrary.inventory.api.InventoryProvider;
import io.github.kaivian.klibrary.inventory.api.InventoryView;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

/**
 * Default implementation of {@link InventoryView} that holds references to the
 * view's ID, Bukkit inventory, context, and provider.
 *
 * <p>This is an immutable data holder created by {@link InventoryProvider#createView(InventoryContext)}.
 * It delegates refresh operations to its associated provider.</p>
 *
 * <p><b>Usage:</b> Typically created inside an {@link InventoryProvider} implementation:</p>
 * <pre>{@code
 * @Override
 * public InventoryView createView(InventoryContext context) {
 *     Inventory inventory = Bukkit.createInventory(null, 54, title);
 *     return new InventoryViewImpl("my_menu", inventory, context, this);
 * }
 * }</pre>
 *
 * @see InventoryView
 * @see InventoryProvider
 */
public class InventoryViewImpl implements InventoryView {

    private final String id;
    private final Inventory inventory;
    private final InventoryContext context;
    private final InventoryProvider provider;

    /**
     * Constructs a new {@code InventoryViewImpl}.
     *
     * @param id        the unique identifier for this view (typically the provider's ID)
     * @param inventory the Bukkit inventory instance to display
     * @param context   the inventory context for this session
     * @param provider  the provider that created this view
     */
    public InventoryViewImpl(String id, Inventory inventory, InventoryContext context, InventoryProvider provider) {
        this.id = id;
        this.inventory = inventory;
        this.context = context;
        this.provider = provider;
    }

    /** {@inheritDoc} */
    @Override
    public @NotNull String getId() {
        return id;
    }

    /** {@inheritDoc} */
    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    /** {@inheritDoc} */
    @Override
    public @NotNull InventoryContext getContext() {
        return context;
    }

    /** {@inheritDoc} */
    @Override
    public @NotNull InventoryProvider getProvider() {
        return provider;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Delegates to {@link InventoryProvider#update(InventoryView)} on this view's provider.</p>
     */
    @Override
    public void refresh() {
        provider.update(this);
    }
}
