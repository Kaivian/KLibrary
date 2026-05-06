package io.github.kaivian.klibrary.inventory.button.impl;

import io.github.kaivian.klibrary.KLibrary;
import io.github.kaivian.klibrary.inventory.api.InventoryContext;
import io.github.kaivian.klibrary.inventory.button.Button;
import io.github.kaivian.klibrary.itemstack.builder.ItemStackBuilders;
import io.github.kaivian.klibrary.itemstack.modifier.ItemModifiers;
import io.github.kaivian.klibrary.lang.LanguageManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CloseButton implements Button {

    @Override
    public @NotNull ItemStack getItem(@NotNull InventoryContext context) {
        Player player = context.getPlayer().orElse(null);
        if (player == null) return new ItemStack(Material.BARRIER);

        LanguageManager lang = KLibrary.getInstance().getServiceProvider().getLanguageManager().orElse(null);
        Component name = lang != null ? lang.get(player, "gui.button.close.name") : Component.text("Close");

        return ItemStackBuilders.of(Material.BARRIER)
                .with(ItemModifiers.display().name(name))
                .build();
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event, @NotNull InventoryContext context) {
        event.getWhoClicked().closeInventory();
    }

    @Override
    public boolean isVisible(@NotNull InventoryContext context) {
        return true;
    }
}
