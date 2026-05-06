# Inventory System

KLibrary replaces raw Bukkit inventory handling with a modern, modular, and dynamic stack-based GUI framework.

## Creating a Basic GUI

A GUI is represented by an `InventoryProvider` and registered with the `InventoryManager`.

```java
import io.github.kaivian.klibrary.inventory.api.InventoryProvider;
import io.github.kaivian.klibrary.inventory.api.InventoryView;
import io.github.kaivian.klibrary.inventory.api.InventoryContext;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MyProvider implements InventoryProvider {
    @Override
    public @NotNull String getId() {
        return "my_menu";
    }

    @Override
    public @NotNull InventoryView createView(@NotNull InventoryContext context) {
        return InventoryView.builder()
                .title("<gradient:blue:aqua>My Awesome Menu</gradient>")
                .size(27) // 3 rows
                .build();
    }
    
    @Override
    public void update(@NotNull InventoryView view) {
        // Optional: Update inventory items here
    }
}
```

To open this GUI for a player, you must register the provider and push it to the player's view stack:
```java
import io.github.kaivian.klibrary.KLibrary;
import io.github.kaivian.klibrary.inventory.api.InventoryManager;
import io.github.kaivian.klibrary.inventory.api.InventoryContext;

// Get the InventoryManager instance
InventoryManager manager = KLibrary.getInstance().getServiceProvider()
        .getInventoryManager().orElseThrow();

// Register the provider (usually done once on startup)
manager.registerProvider(new MyProvider());

// Create a context and open the menu for the player
InventoryContext context = InventoryContext.builder()
        .player(player)
        .build();

manager.push(player, "my_menu", context);
```

## The Button System

Rather than handling raw `InventoryClickEvent`s, KLibrary provides a highly abstracted `Button` concept. Buttons are essentially `ItemStack`s combined with interaction logic. The `BaseButton` class is the standard implementation.

```java
import io.github.kaivian.klibrary.inventory.button.BaseButton;
import io.github.kaivian.klibrary.inventory.api.Button;
import io.github.kaivian.klibrary.action.api.ActionResult;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import java.util.List;

// Inside your provider's update method:
public void update(InventoryView view) {
    ItemStack item = new ItemStack(Material.DIAMOND);
    
    Button diamondButton = BaseButton.builder()
            .template(item)
            .anyClick(List.of(ctx -> {
                ctx.getPlayer().ifPresent(p -> p.sendMessage("You clicked the diamond!"));
                return ActionResult.success();
            }))
            .build();

    view.setButton(4, diamondButton); // Center slot
}
```

## Navigation Stack (Back System)

KLibrary stores the hierarchy of opened menus using a stack-based navigation model. You can dynamically go "back" to the previous menu by popping the current view.

```java
// Inside an update method, creating a back button:
Button backButton = BaseButton.builder()
    .template(new ItemStack(Material.BARRIER)) // Or your custom back icon
    .anyClick(List.of(ctx -> {
        // Access the InventoryManager and pop the current view to go back
        KLibrary.getInstance().getServiceProvider().getInventoryManager().ifPresent(manager -> {
            ctx.getPlayer().ifPresent(manager::pop);
        });
        return ActionResult.success();
    }))
    .build();

view.setButton(26, backButton); // Bottom right slot
```

## Dynamic Updates

To update an inventory continuously (e.g., creating animations or updating values), override the `isAutoRefresh()` and `update()` methods inside your provider:

```java
@Override
public boolean isAutoRefresh() {
    return true; // Enables automatic refreshing by the global ticker
}

@Override
public void update(@NotNull InventoryView view) {
    // This runs automatically based on the InventoryManager's tick rate.
    // Modify the view's current items or buttons here.
}
```
