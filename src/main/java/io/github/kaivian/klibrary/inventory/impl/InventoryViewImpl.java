package io.github.kaivian.klibrary.inventory.impl;

import io.github.kaivian.klibrary.inventory.api.InventoryContext;
import io.github.kaivian.klibrary.inventory.api.InventoryProvider;
import io.github.kaivian.klibrary.inventory.api.InventoryView;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class InventoryViewImpl implements InventoryView {

    private final String id;
    private final Inventory inventory;
    private final InventoryContext context;
    private final InventoryProvider provider;

    public InventoryViewImpl(String id, Inventory inventory, InventoryContext context, InventoryProvider provider) {
        this.id = id;
        this.inventory = inventory;
        this.context = context;
        this.provider = provider;
    }

    @Override
    public @NotNull String getId() {
        return id;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    @Override
    public @NotNull InventoryContext getContext() {
        return context;
    }

    @Override
    public @NotNull InventoryProvider getProvider() {
        return provider;
    }

    @Override
    public void refresh() {
        provider.update(this);
    }
}
