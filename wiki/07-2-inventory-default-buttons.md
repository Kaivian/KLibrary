# Default Buttons Setup System

The KLibrary GUI framework uses a layered button system designed to decouple static interaction logic (like closing menus or turning pages) from dynamic content rendering.

## What are Default Buttons?
Default buttons are pre-built, localized, and reusable `Button` implementations that dispatch predefined actions when clicked. Because buttons in KLibrary act as **action dispatchers** rather than data stores, using built-in buttons eliminates boilerplate logic across different menus.

KLibrary provides the following built-in buttons in `io.github.kaivian.klibrary.inventory.button.impl`:
- **`CloseButton`**: Instantly closes the inventory for the player.
- **`BackButton`**: Safely pops the navigation stack, returning the player to their previous menu.
- **`BackOrCloseButton`**: Intelligently checks the manager's back stack. It acts as a Back button if history exists, or a Close button if the stack is empty.
- **`NavigationButton.Next` / `Previous`**: Automatically increments or decrements the current page and handles pagination updates.

## View-Level Priority System
KLibrary rendering occurs in a strict hierarchy:
1. **View-Level Buttons**: Highest priority. Added directly to the `InventoryView` instance via `view.setButton()`.
2. **Provider-Level Rendering**: Lowest priority. Rendered globally by the `InventoryProvider` inside the `update()` loop.

By injecting UI controls (like Back or Next buttons) directly into the `InventoryView` during the provider's `update()` cycle, you guarantee that these controls **cannot be overwritten** by dynamic content generators (such as listing items from a database).

### `setButton(slot, Button)` Usage
To securely place a default UI control, use the `setButton` method on the `InventoryView`.

```java
import io.github.kaivian.klibrary.inventory.button.impl.BackOrCloseButton;
import io.github.kaivian.klibrary.inventory.button.impl.NavigationButton;

@Override
public void update(@NotNull InventoryView view) {
    // 1. Establish the highest-priority View layer
    view.setButton(45, new NavigationButton.Previous());
    view.setButton(49, new BackOrCloseButton());
    view.setButton(53, new NavigationButton.Next());
    
    // 2. Render dynamic content (Provider layer)
    Inventory inv = view.getInventory();
    List<ItemStack> myDynamicItems = fetchItems();
    
    for (int i = 0; i < myDynamicItems.size(); i++) {
        // Even if we accidentally iterate over slots 45, 49, or 53,
        // the click handler will intercept and prioritize the View-level buttons set above!
        inv.setItem(i, myDynamicItems.get(i));
    }
}
```

## Why Set Default Buttons in `update()`?
While you technically could inject view-level buttons once during `createView()`, setting them in the `update()` cycle is the safest architectural practice. 

Since `update()` acts as a complete re-render pipeline, redefining your view-level buttons here ensures that their visibility states (e.g., hiding the `Previous` button on page 0) are strictly recalculated on every frame based on the active `InventoryContext`.

## Best Practices
- **Never hardcode close logic**: Always use `CloseButton` instead of manually intercepting clicks and calling `player.closeInventory()`.
- **Prefer `BackOrCloseButton` over `BackButton`**: For general UI design, a combined `BackOrCloseButton` prevents players from getting stuck if they directly opened a sub-menu without navigating from a main menu first.
- **Isolate Action Dispatchers**: Treat buttons exclusively as action dispatchers. The `Button` logic should never calculate *what* to display—that is the responsibility of the Provider reading from the Context.
