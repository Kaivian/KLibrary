package io.github.kaivian.klibrary.inventory.button;

import io.github.kaivian.klibrary.action.api.Action;
import io.github.kaivian.klibrary.inventory.api.InventoryContext;
import io.github.kaivian.klibrary.requirement.api.Requirement;
import io.github.kaivian.klibrary.requirement.api.RequirementResult;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * Base implementation of a button that holds an ItemStack template, actions, and requirements.
 */
public class BaseButton implements Button {

    private final ItemStack template;
    private final List<Action> leftClickActions;
    private final List<Action> rightClickActions;
    private final List<Action> anyClickActions;
    private final List<Requirement> viewRequirements;

    public BaseButton(@NotNull ItemStack template, 
                      @NotNull List<Action> leftClickActions, 
                      @NotNull List<Action> rightClickActions, 
                      @NotNull List<Action> anyClickActions, 
                      @NotNull List<Requirement> viewRequirements) {
        this.template = template;
        this.leftClickActions = leftClickActions;
        this.rightClickActions = rightClickActions;
        this.anyClickActions = anyClickActions;
        this.viewRequirements = viewRequirements;
    }

    @Override
    public @NotNull ItemStack getItem(@NotNull InventoryContext context) {
        // Clone the template to avoid modifying the base button
        ItemStack item = template.clone();
        
        // Placeholder parsing could be done here if the name/lore contains placeholders
        // We assume MiniMessage is used and placeholders from context are applied.
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            // Note: True dynamic placeholder replacement on items requires parsing strings again.
            // For optimal performance, if PlaceholderAPI is used, we'd replace here.
            // We'll leave it as the base template for now, but external providers can extend this.
        }
        
        return item;
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event, @NotNull InventoryContext context) {
        ClickType click = event.getClick();

        if (click == ClickType.LEFT) {
            executeActions(leftClickActions, context);
        } else if (click == ClickType.RIGHT) {
            executeActions(rightClickActions, context);
        }
        
        executeActions(anyClickActions, context);
    }

    @Override
    public boolean isVisible(@NotNull InventoryContext context) {
        if (viewRequirements.isEmpty()) {
            return true;
        }
        for (Requirement req : viewRequirements) {
            RequirementResult result = req.evaluate(context.getExecutionContext());
            if (!result.isMet()) {
                return false;
            }
        }
        return true;
    }

    private void executeActions(List<Action> actions, InventoryContext context) {
        if (actions == null || actions.isEmpty()) return;
        for (Action action : actions) {
            action.execute(context.getExecutionContext());
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ItemStack template;
        private List<Action> leftClickActions = Collections.emptyList();
        private List<Action> rightClickActions = Collections.emptyList();
        private List<Action> anyClickActions = Collections.emptyList();
        private List<Requirement> viewRequirements = Collections.emptyList();

        private Builder() {}

        public Builder template(ItemStack template) {
            this.template = template;
            return this;
        }

        public Builder leftClick(List<Action> actions) {
            this.leftClickActions = actions;
            return this;
        }

        public Builder rightClick(List<Action> actions) {
            this.rightClickActions = actions;
            return this;
        }

        public Builder anyClick(List<Action> actions) {
            this.anyClickActions = actions;
            return this;
        }

        public Builder viewRequirements(List<Requirement> requirements) {
            this.viewRequirements = requirements;
            return this;
        }

        public BaseButton build() {
            if (template == null) {
                throw new IllegalStateException("Template ItemStack cannot be null");
            }
            return new BaseButton(template, leftClickActions, rightClickActions, anyClickActions, viewRequirements);
        }
    }
}
