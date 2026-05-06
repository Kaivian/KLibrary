# Getting Started: Creating and Opening a GUI

The KLibrary Inventory System replaces standard Bukkit GUI handling with a highly scalable, state-driven framework. By isolating the logic into structured layers, creating complex and interactive menus becomes predictable and boilerplate-free.

## Architectural Rules
Before building your first GUI, it is essential to understand the core responsibilities of each layer:
- **`InventoryContext`**: Holds runtime state only (e.g., active player, current page, filters). It has zero rendering logic.
- **`InventoryProvider`**: Holds rendering logic. It translates abstract state from the Context into physical `ItemStack`s on the screen.
- **`InventoryView`**: The runtime container that wraps the physical Bukkit inventory instance.
- **`InventoryManager`**: The lifecycle controller. Handles pushing views, managing the back stack, and dispatching interactions.

## Creating a Basic GUI Provider

To build a GUI, implement the `InventoryProvider` interface. The provider acts as a blueprint. It tells the manager how to construct the initial `InventoryView` and how to populate it with items when the `update()` cycle runs.

```java
import io.github.kaivian.klibrary.inventory.api.InventoryContext;
import io.github.kaivian.klibrary.inventory.api.InventoryProvider;
import io.github.kaivian.klibrary.inventory.api.InventoryView;
import io.github.kaivian.klibrary.inventory.impl.InventoryViewImpl;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MainMenuProvider implements InventoryProvider {

    @Override
    public @NotNull String getId() {
        return "main_menu";
    }

    @Override
    public @NotNull InventoryView createView(@NotNull InventoryContext context) {
        // Create the raw Bukkit inventory instance
        Inventory inventory = Bukkit.createInventory(null, 27, Component.text("Main Menu"));
        
        // Wrap it in the runtime container (InventoryView)
        return new InventoryViewImpl(getId(), inventory, context, this);
    }

    @Override
    public void update(@NotNull InventoryView view) {
        // This is the rendering layer. Items are calculated and placed here based on context.
        Inventory inv = view.getInventory();
        inv.clear(); // Always clear previous items before re-rendering
        
        inv.setItem(13, new ItemStack(Material.DIAMOND));
    }
    
    @Override
    public void onClick(org.bukkit.event.inventory.InventoryClickEvent event, InventoryContext context) {
        event.setCancelled(true); // Always cancel clicks for secure GUIs
        // Provider-level click handling
    }
}
```

## Registering the GUI

Providers are singletons. You register them once during plugin startup into the `InventoryManager`.

```java
import io.github.kaivian.klibrary.KLibrary;
import io.github.kaivian.klibrary.inventory.api.InventoryManager;

public void onEnable() {
    InventoryManager manager = KLibrary.getInstance()
            .getServiceProvider()
            .getInventoryManager()
            .orElseThrow();
            
    manager.registerProvider(new MainMenuProvider());
}
```

## Opening the GUI for a Player

To open the menu for a player, you must construct an `InventoryContext` and ask the `InventoryManager` to push the view to the player's active navigation stack.

```java
import io.github.kaivian.klibrary.inventory.api.InventoryContext;

public void openMenu(Player player) {
    InventoryManager manager = KLibrary.getInstance().getServiceProvider().getInventoryManager().orElseThrow();
    
    // 1. Build the immutable runtime state
    InventoryContext context = InventoryContext.builder()
            .player(player)
            .build();
            
    // 2. Push the provider ID to the manager
    manager.push(player, "main_menu", context);
}
```

### Understanding the `open()` Flow
When `manager.push(...)` is called:
1. The manager looks up the provider using the `viewId` (`"main_menu"`).
2. The manager invokes `provider.createView(context)` to instantiate the runtime container.
3. The view is immediately pushed onto the player's internal GUI stack.
4. The manager calls `provider.update(view)` to trigger the first rendering pass.
5. Finally, the manager opens the physical Bukkit inventory for the player.

## Best Practices
- **Do not store mutable state in the Provider**: `InventoryProvider` is a singleton. Any player-specific or session-specific state (like current page or selected category) must be stored inside the `InventoryContext`.
- **View IDs**: Use namespaced, descriptive strings for `getId()` (e.g., `shop_main`, `shop_category_weapons`).
