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

public abstract class NavigationButton implements Button {

    private final boolean isNext;

    public NavigationButton(boolean isNext) {
        this.isNext = isNext;
    }

    @Override
    public @NotNull ItemStack getItem(@NotNull InventoryContext context) {
        Player player = context.getPlayer().orElse(null);
        if (player == null) return new ItemStack(Material.ARROW);

        LanguageManager lang = KLibrary.getInstance().getServiceProvider().getLanguageManager().orElse(null);
        
        String direction = isNext ? "next" : "previous";
        int targetPage = context.getPage() + (isNext ? 1 : -1);
        
        Component name = Component.text(isNext ? "Next Page" : "Previous Page");
        List<Component> lore = Collections.emptyList();
        
        if (lang != null) {
            name = lang.get(player, "gui.button.navigation." + direction + ".name");
            lore = lang.getList(player.locale(), "gui.button.navigation." + direction + ".lore", 
                    Placeholder.unparsed("page", String.valueOf(targetPage + 1)));
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
            int targetPage = context.getPage() + (isNext ? 1 : -1);
            if (targetPage < 0) return; // Cannot go to negative page
            
            InventoryContext newContext = InventoryContext.from(context.getExecutionContext())
                    .page(targetPage)
                    .build();
            
            // Pagination uses manager.update
            manager.update(player, newContext);
        }
    }

    public static class Next extends NavigationButton {
        public Next() {
            super(true);
        }

        @Override
        public boolean isVisible(@NotNull InventoryContext context) {
            // Can be overridden to hide if there is no next page
            return true; 
        }
    }

    public static class Previous extends NavigationButton {
        public Previous() {
            super(false);
        }

        @Override
        public boolean isVisible(@NotNull InventoryContext context) {
            return context.getPage() > 0;
        }
    }
}
