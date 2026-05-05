package io.github.kaivian.klibrary.inventory.button;

import io.github.kaivian.klibrary.action.api.Action;
import io.github.kaivian.klibrary.inventory.api.InventoryContext;
import io.github.kaivian.klibrary.requirement.api.Requirement;
import io.github.kaivian.klibrary.requirement.api.RequirementResult;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * Standard implementation of {@link Button} that combines an {@link ItemStack} template
 * with configurable click actions and view requirements.
 *
 * <p>This class is the primary button implementation used throughout KLibrary's inventory
 * system. It supports:</p>
 * <ul>
 *   <li><b>Separate click handlers</b> — Different action lists for left-click,
 *       right-click, and any-click events</li>
 *   <li><b>View requirements</b> — The button is hidden when any requirement is not met</li>
 *   <li><b>Template cloning</b> — The item template is cloned on each render to prevent
 *       shared mutable state</li>
 * </ul>
 *
 * <h2>Builder Pattern</h2>
 * <pre>{@code
 * Button shopButton = BaseButton.builder()
 *     .template(ItemStackBuilders.of(Material.EMERALD)
 *         .with(ItemModifiers.display().name("<green>Buy Sword"))
 *         .build())
 *     .leftClick(List.of(buySwordAction))
 *     .rightClick(List.of(previewAction))
 *     .viewRequirements(List.of(hasMoneyRequirement))
 *     .build();
 * }</pre>
 *
 * <h2>Click Handling</h2>
 * <p>When a player clicks a {@code BaseButton}:</p>
 * <ol>
 *   <li>The click type is determined (left, right, other)</li>
 *   <li>The corresponding action list is executed (left-click or right-click)</li>
 *   <li>The any-click action list is always executed afterwards</li>
 * </ol>
 *
 * @see Button
 * @see Builder
 */
public class BaseButton implements Button {

    private final ItemStack template;
    private final List<Action> leftClickActions;
    private final List<Action> rightClickActions;
    private final List<Action> anyClickActions;
    private final List<Requirement> viewRequirements;

    /**
     * Constructs a new {@code BaseButton} with the given template, actions, and requirements.
     *
     * <p>Prefer using the {@link #builder()} for a more readable construction API.</p>
     *
     * @param template          the item template to display; must not be {@code null}
     * @param leftClickActions  actions to execute on left-click; must not be {@code null}
     * @param rightClickActions actions to execute on right-click; must not be {@code null}
     * @param anyClickActions   actions to execute on any click type; must not be {@code null}
     * @param viewRequirements  requirements that must be met for the button to be visible;
     *                          must not be {@code null}
     */
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

    /**
     * {@inheritDoc}
     *
     * <p>Returns a clone of the item template. The original template is never mutated.</p>
     */
    @Override
    public @NotNull ItemStack getItem(@NotNull InventoryContext context) {
        // Clone the template to avoid modifying the base button
        return template.clone();
    }

    /**
     * {@inheritDoc}
     *
     * <p>Executes the click-type-specific actions first (left or right), then
     * always executes the any-click actions.</p>
     */
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

    /**
     * {@inheritDoc}
     *
     * <p>Evaluates all view requirements. The button is visible only if every
     * requirement is met. An empty requirement list means the button is always visible.</p>
     */
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

    /**
     * Executes a list of actions within the given inventory context.
     *
     * @param actions the actions to execute
     * @param context the inventory context providing the execution context
     */
    private void executeActions(List<Action> actions, InventoryContext context) {
        if (actions == null || actions.isEmpty()) return;
        for (Action action : actions) {
            action.execute(context.getExecutionContext());
        }
    }

    /**
     * Creates a new {@link Builder} for constructing a {@code BaseButton}.
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for constructing {@link BaseButton} instances.
     *
     * <p>The only required field is {@link #template(ItemStack)}. All action
     * and requirement lists default to empty.</p>
     */
    public static class Builder {
        private ItemStack template;
        private List<Action> leftClickActions = Collections.emptyList();
        private List<Action> rightClickActions = Collections.emptyList();
        private List<Action> anyClickActions = Collections.emptyList();
        private List<Requirement> viewRequirements = Collections.emptyList();

        private Builder() {}

        /**
         * Sets the item template for the button.
         *
         * @param template the item stack to display; must not be {@code null}
         * @return this builder
         */
        public Builder template(ItemStack template) {
            this.template = template;
            return this;
        }

        /**
         * Sets the actions to execute on left-click.
         *
         * @param actions the left-click actions
         * @return this builder
         */
        public Builder leftClick(List<Action> actions) {
            this.leftClickActions = actions;
            return this;
        }

        /**
         * Sets the actions to execute on right-click.
         *
         * @param actions the right-click actions
         * @return this builder
         */
        public Builder rightClick(List<Action> actions) {
            this.rightClickActions = actions;
            return this;
        }

        /**
         * Sets the actions to execute on any click type (always runs after
         * type-specific actions).
         *
         * @param actions the any-click actions
         * @return this builder
         */
        public Builder anyClick(List<Action> actions) {
            this.anyClickActions = actions;
            return this;
        }

        /**
         * Sets the requirements that must be met for the button to be visible.
         *
         * @param requirements the visibility requirements
         * @return this builder
         */
        public Builder viewRequirements(List<Requirement> requirements) {
            this.viewRequirements = requirements;
            return this;
        }

        /**
         * Builds the {@link BaseButton}.
         *
         * @return the constructed button
         * @throws IllegalStateException if no template has been set
         */
        public BaseButton build() {
            if (template == null) {
                throw new IllegalStateException("Template ItemStack cannot be null");
            }
            return new BaseButton(template, leftClickActions, rightClickActions, anyClickActions, viewRequirements);
        }
    }
}
