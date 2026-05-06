# Updating GUI Dynamically (Live Refresh System)

KLibrary provides specific API methods tailored for different types of UI modifications. Understanding the exact difference between updating an existing view and replacing a view completely is essential for responsive menu design.

## `update()` vs `openReplace()`

### `manager.update(player, context)`
The `update()` method forces the current `InventoryProvider` to re-execute its rendering logic (`update(view)`). 
- **Effect**: It acts as a **re-render of the exact same view**. 
- **Stack Behavior**: It does **not** push a new frame to the back stack.
- **Use Case**: This is strictly used when changing the internal state of a layout (e.g., pagination, toggling a filter on/off, updating a live counter).

### `manager.openReplace(player, providerId, context)`
The `openReplace()` method terminates the current GUI view and immediately instantiates an entirely **new view transition** without clearing the player's back history.
- **Effect**: Replaces the active GUI container physically.
- **Stack Behavior**: Pops the current view frame and pushes the new one. Thus, clicking "Back" in the new view skips the intermediate menu.
- **Use Case**: Moving a player sideways across unrelated menus, like switching from a "Weapons Shop" to an "Armor Shop", where you don't want them getting trapped hitting "Back" twenty times.

## How Context Affects Rendering
Because providers are entirely state-driven, dynamic updates only work if the underlying `InventoryContext` contains the relevant state data. When performing a dynamic update, you should construct an updated context using metadata.

## Example 1: Dynamic Shop Updates (Using `update()`)
In this example, we have a menu that allows the player to toggle a display filter (e.g., "Show Swords" vs "Show Bows").

```java
import io.github.kaivian.klibrary.inventory.api.InventoryContext;
import io.github.kaivian.klibrary.inventory.api.InventoryManager;
import io.github.kaivian.klibrary.inventory.api.InventoryView;
import io.github.kaivian.klibrary.inventory.button.BaseButton;
import io.github.kaivian.klibrary.inventory.button.Button;
import io.github.kaivian.klibrary.action.api.ActionResult;

// Inside your Provider's update method:
@Override
public void update(@NotNull InventoryView view) {
    InventoryContext ctx = view.getContext();
    
    // Read state from context metadata (defaults to "swords")
    String activeFilter = (String) ctx.getExecutionContext().getMetadata().getOrDefault("filter", "swords");
    
    // Create a button that dynamically updates the state
    Button toggleFilterButton = BaseButton.builder()
        .template(new ItemStack(activeFilter.equals("swords") ? Material.IRON_SWORD : Material.BOW))
        .anyClick(List.of(actionCtx -> {
            String newFilter = activeFilter.equals("swords") ? "bows" : "swords";
            
            // Build cloned context with new state
            InventoryContext newCtx = InventoryContext.from(ctx.getExecutionContext())
                .metadata("filter", newFilter)
                .build();
                
            // Trigger a live re-render
            manager.update(actionCtx.getPlayer().get(), newCtx);
            
            return ActionResult.success();
        }))
        .build();
        
    view.setButton(8, toggleFilterButton);
}
```

## Example 2: Live Scoreboard GUI (Global Ticker)
To create GUIs that refresh continuously based on external server state (e.g., a live player count or minigame scoreboard), override the `isAutoRefresh()` method. 

When auto-refresh is enabled, the `InventoryManager` will call your provider's `update(view)` method roughly every 20 ticks (1 second).

```java
public class LiveStatsProvider implements InventoryProvider {
    
    @Override
    public boolean isAutoRefresh() {
        // Enables automatic 1-second interval re-rendering
        return true; 
    }

    @Override
    public void update(@NotNull InventoryView view) {
        int online = Bukkit.getOnlinePlayers().size();
        
        ItemStack statsItem = ItemStackBuilders.of(Material.PLAYER_HEAD)
            .with(ItemModifiers.display()
                .name("<green>Live Players: " + online))
            .build();
            
        view.getInventory().setItem(13, statsItem);
    }
}
```

## Best Practices
- **Do not recreate static items**: If you are using auto-refresh, avoid doing expensive item recalculations on every tick if the data hasn't changed.
- **`update()` implies visual continuity**: Because `update()` does not invoke `player.openInventory()` underneath, it prevents the mouse cursor from resetting back to the center of the screen, creating a much smoother UX.
