# Inventory System

KLibrary replaces raw Bukkit inventory handling with a modern, modular, and dynamic stack-based GUI framework.

## Creating a Basic GUI

A GUI is represented by an `InventoryProvider`.

```java
import io.github.kaivian.klibrary.inventory.api.InventoryProvider;
import io.github.kaivian.klibrary.inventory.api.InventoryView;
import org.bukkit.entity.Player;

public class MyProvider implements InventoryProvider {
    @Override
    public InventoryView build(Player player) {
        return InventoryView.builder()
                .title("<gradient:blue:aqua>My Awesome Menu</gradient>")
                .size(27) // 3 rows
                .build();
    }
}
```

To open this GUI for a player:
```java
// Requires access to the central InventoryManager instance
inventoryManager.open(player, new MyProvider());
```

## The Button System

Rather than handling raw `InventoryClickEvent`s, KLibrary provides a highly abstracted `Button` concept. Buttons are essentially `ItemStack`s combined with interaction logic.

```java
import io.github.kaivian.klibrary.inventory.api.Button;
import io.github.kaivian.klibrary.inventory.api.InventoryView;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MyProvider implements InventoryProvider {
    @Override
    public InventoryView build(Player player) {
        InventoryView view = InventoryView.builder()
                .title("Interactive Menu")
                .size(9)
                .build();

        ItemStack item = new ItemStack(Material.DIAMOND);
        Button diamondButton = Button.builder(item)
                .onClick((event, context) -> {
                    player.sendMessage("You clicked the diamond!");
                })
                .build();

        view.setButton(4, diamondButton); // Center slot
        return view;
    }
}
```

## Navigation Stack (Back System)

KLibrary stores the hierarchy of opened menus using an `InventoryContext`. You can dynamically go "back" to the previous menu.

```java
// Inside an onClick handler:
Button backButton = Button.builder(new ItemStack(Material.BARRIER))
    .onClick((event, context) -> {
        context.navigateBack(); // Opens the previous provider seamlessly
    })
    .build();
```

## Dynamic Updates

To update an inventory continuously (e.g., creating animations or updating values), override the `update()` method inside your provider:

```java
@Override
public void update(Player player, InventoryContext context) {
    // This runs automatically based on the InventoryManager's tick rate.
    // Modify the context's current View or buttons here.
}
```
