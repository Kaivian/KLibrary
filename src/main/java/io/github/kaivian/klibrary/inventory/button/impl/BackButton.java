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

public class BackButton implements Button {

    @Override
    public @NotNull ItemStack getItem(@NotNull InventoryContext context) {
        Player player = context.getPlayer().orElse(null);
        if (player == null) return new ItemStack(Material.ARROW);

        LanguageManager lang = KLibrary.getInstance().getServiceProvider().getLanguageManager().orElse(null);
        
        Component name = Component.text("Go Back");
        List<Component> lore = Collections.emptyList();
        
        if (lang != null) {
            name = lang.get(player, "gui.button.back.name");
            lore = lang.getList(player.locale(), "gui.button.back.lore", 
                    Placeholder.unparsed("page", String.valueOf(context.getPage() + 1)));
        }

        return ItemStackBuilders.of(Material.ARROW)
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
            manager.pop(player);
        }
    }

    @Override
    public boolean isVisible(@NotNull InventoryContext context) {
        Player player = context.getPlayer().orElse(null);
        if (player == null) return false;
        
        InventoryManager manager = KLibrary.getInstance().getServiceProvider().getInventoryManager().orElse(null);
        return manager != null && manager.hasPrevious(player);
    }
}
