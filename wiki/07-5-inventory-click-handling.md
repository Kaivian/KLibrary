# GUI Click Handling System

KLibrary replaces manual raw `InventoryClickEvent` handling with an object-oriented Button architecture. The system automatically secures inventories, prevents unintended item theft, and maps interactions strictly to the displayed `Button` elements.

## Execution Flow

When a player interacts with an open KLibrary GUI, the following deterministic event sequence occurs:

1. **Player Click**: The raw Bukkit event is fired.
2. **Event Interception**: The `InventoryManager` intercepts the event and universally cancels it (`event.setCancelled(true)`), preventing the physical movement of items.
3. **Target Resolution**: The manager evaluates the exact clicked slot and searches the prioritized layers for an associated `Button`.
4. **Action Dispatch**: If a button exists and is currently visible, `button.onClick(event, context)` is invoked.
5. **State Modification (Optional)**: The button's click logic may choose to alter the contextual state and call `manager.update(...)`.
6. **Rerender**: The provider redraws the items corresponding to the new state.

## Click Handling Priority System

KLibrary implements layered priority rendering. This protects critical UI controls (like Back or Next buttons) from being accidentally hijacked by dynamically loaded data grids.

The click resolution hierarchy is:
1. **View-Level Buttons**: The manager immediately checks if the active `InventoryView` has a button registered at the clicked slot (via `view.getButton(slot)`). If found, the event is consumed here.
2. **Provider-Level Handling**: If no view-level button exists, the click falls back to the `InventoryProvider#onClick` method, which typically maps to the generic content items built during the provider's `update()` loop.

## Example Button Implementation

To build custom interactivity without boilerplate, use the `BaseButton` builder. By passing logic to `anyClick`, `leftClick`, or `rightClick`, you bind discrete actions that have full access to the current `InventoryContext`.

```java
import io.github.kaivian.klibrary.inventory.api.InventoryContext;
import io.github.kaivian.klibrary.inventory.button.BaseButton;
import io.github.kaivian.klibrary.inventory.button.Button;
import io.github.kaivian.klibrary.action.api.ActionResult;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import java.util.List;

public Button createInteractiveButton() {
    return BaseButton.builder()
        .template(new ItemStack(Material.GOLD_BLOCK))
        .anyClick(List.of(actionContext -> {
            
            // 1. Read current state
            int goldCount = (int) actionContext.getMetadata().getOrDefault("gold_collected", 0);
            
            // 2. Build modified context with updated state
            InventoryContext newContext = InventoryContext.from(actionContext)
                .metadata("gold_collected", goldCount + 1)
                .build();
                
            // 3. Dispatch the state update to trigger a re-render
            manager.update(actionContext.getPlayer().get(), newContext);
            
            // 4. Return success action result
            return ActionResult.success();
        }))
        .build();
}
```

## Advanced Logic: Implementing the `Button` Interface

For exceptionally complex interactions that require direct access to the raw Bukkit click event properties (such as Shift-Clicks or checking the exact cursor item), you can implement the `Button` interface directly instead of using `BaseButton`.

```java
public class AdvancedTradeButton implements Button {
    
    @Override
    public @NotNull ItemStack getItem(@NotNull InventoryContext context) {
        return new ItemStack(Material.EMERALD);
    }
    
    @Override
    public void onClick(@NotNull InventoryClickEvent event, @NotNull InventoryContext context) {
        if (event.isShiftClick()) {
            // Bulk trade logic
        } else if (event.isRightClick()) {
            // Half trade logic
        }
    }
    
    @Override
    public boolean isVisible(@NotNull InventoryContext context) {
        return true;
    }
}
```

## Best Practices
- **Do not cancel events manually**: The `InventoryManager` handles baseline cancellation universally. You only need to focus on your business logic.
- **Never mutate the GUI directly in the click handler**: e.g., avoid `event.getClickedInventory().setItem(...)`. Always modify the `InventoryContext` state and let `update()` recalculate the visual output.
