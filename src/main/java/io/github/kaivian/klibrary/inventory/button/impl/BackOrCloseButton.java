package io.github.kaivian.klibrary.inventory.button.impl;

import io.github.kaivian.klibrary.KLibrary;
import io.github.kaivian.klibrary.inventory.api.InventoryContext;
import io.github.kaivian.klibrary.inventory.api.InventoryManager;
import io.github.kaivian.klibrary.inventory.button.Button;
import io.github.kaivian.klibrary.itemstack.builder.ItemStackBuilders;
import io.github.kaivian.klibrary.itemstack.modifier.ItemModifiers;
import io.github.kaivian.klibrary.lang.LanguageManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class BackOrCloseButton implements Button {

    @Override
    public @NotNull ItemStack getItem(@NotNull InventoryContext context) {
        Player player = context.getPlayer().orElse(null);
        if (player == null) return new ItemStack(Material.BARRIER);

        InventoryManager manager = KLibrary.getInstance().getServiceProvider().getInventoryManager().orElse(null);
        boolean hasPrevious = manager != null && manager.hasPrevious(player);
        
        Material material = hasPrevious ? Material.ARROW : Material.BARRIER;
        
        LanguageManager lang = KLibrary.getInstance().getServiceProvider().getLanguageManager().orElse(null);
        Component name = Component.text(hasPrevious ? "Go Back" : "Close");
        List<Component> lore = Collections.emptyList();
        
        if (lang != null) {
            if (hasPrevious) {
                name = lang.get(player, "gui.button.back_or_close.name_back");
                lore = lang.getList(player.locale(), "gui.button.back_or_close.lore", 
                        Placeholder.unparsed("page", String.valueOf(context.getPage() + 1)));
            } else {
                name = lang.get(player, "gui.button.back_or_close.name_close");
            }
        }

        return ItemStackBuilders.of(material)
                .with(ItemModifiers.display()
                        .name(name)
                        .lore(lore))
                .build();
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event, @NotNull InventoryContext context) {
        Player player = (Player) event.getWhoClicked();
        InventoryManager manager = KLibrary.getInstance().getServiceProvider().getInventoryManager().orElse(null);
        if (manager != null) {
            if (manager.hasPrevious(player)) {
                manager.pop(player);
            } else {
                player.closeInventory();
            }
        } else {
            player.closeInventory();
        }
    }

    @Override
    public boolean isVisible(@NotNull InventoryContext context) {
        return true;
    }
}
