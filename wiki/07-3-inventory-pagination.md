# Pagination System (Next / Previous Page GUI)

Pagination in KLibrary is built strictly upon the **state-driven** design model. Instead of tearing down and recreating entire inventories, pagination simply modifies the active `InventoryContext` and triggers a safe re-render cycle.

## When to Use Pagination
Pagination should **only** be used when you are displaying different data within the **exact same layout**. 

- **Valid Use Case**: Scrolling through a 100-item shop list.
- **Invalid Use Case**: Clicking an item in a menu to open a completely different confirmation dialog layout. (Use `manager.push()` or `manager.openReplace()` for transitions instead).

## How Pagination Works

The backbone of the pagination system relies on two factors:
1. `context.getPage()`: Stores the current zero-based page index (0 is page 1).
2. `manager.update()`: Submits the new state back into the provider's `update()` pipeline.

### The Pagination Flow
1. **Player Click**: A player clicks the `NextPageButton`.
2. **Action Dispatch**: The button logic reads the current `context.getPage()`, increments it (`page++`), builds a cloned context, and calls `manager.update(player, newContext)`.
3. **State Replacement**: The manager seamlessly updates the active `InventoryView` with the new state. It does **not** push a new frame onto the navigation stack.
4. **Rerender**: The provider's `update(view)` method executes again. Because the new context has a different page number, the provider slices different data out of its datasets.

## Using Built-in Navigation Buttons

KLibrary makes pagination completely boilerplate-free by using the `NavigationButton` implementations. These natively read the page boundaries and hook into `manager.update()`.

To enable pagination, simply inject `NavigationButton.Previous()` and `NavigationButton.Next()` into your view.

## Full Example: Item List GUI

Here is a comprehensive example demonstrating how to read the context and render a paginated list of items.

```java
import io.github.kaivian.klibrary.inventory.api.InventoryContext;
import io.github.kaivian.klibrary.inventory.api.InventoryProvider;
import io.github.kaivian.klibrary.inventory.api.InventoryView;
import io.github.kaivian.klibrary.inventory.button.impl.NavigationButton;
import io.github.kaivian.klibrary.inventory.impl.InventoryViewImpl;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PaginatedShopProvider implements InventoryProvider {

    // Hypothetical data source
    private final List<ItemStack> shopItems = fetchAllShopItems(); 
    private final int ITEMS_PER_PAGE = 45; // Slots 0-44

    @Override
    public @NotNull String getId() {
        return "shop_list";
    }

    @Override
    public @NotNull InventoryView createView(@NotNull InventoryContext context) {
        Inventory inv = Bukkit.createInventory(null, 54, Component.text("Shop"));
        return new InventoryViewImpl(getId(), inv, context, this);
    }

    @Override
    public void update(@NotNull InventoryView view) {
        Inventory inv = view.getInventory();
        InventoryContext context = view.getContext();
        
        // 1. Establish the view-layer UI controls
        view.setButton(45, new NavigationButton.Previous());
        view.setButton(53, new NavigationButton.Next());
        
        // 2. Clear old dynamic content
        for (int i = 0; i < ITEMS_PER_PAGE; i++) {
            inv.setItem(i, null);
        }

        // 3. Render items based on context state
        int page = context.getPage();
        int startIndex = page * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, shopItems.size());
        
        // Safety bounds check
        if (startIndex >= shopItems.size() && page > 0) return; 

        int slot = 0;
        for (int i = startIndex; i < endIndex; i++) {
            // Because view-level buttons override provider items, 
            // the NavigationButtons on slots 45 & 53 are completely safe.
            inv.setItem(slot++, shopItems.get(i));
        }
    }
}
```

## Best Practices
- **Do not mix up `push` and `update`**: Always remember that `manager.update()` guarantees the `BackButton` history isn't artificially flooded with dozens of pages.
- **Trust the View-Level override**: Do not complicate your rendering loop with `if (slot == 45) continue;`. Set the `NavigationButton` on the view and let the manager's hierarchy system naturally prioritize the UI controls over the generic items.
