# Advanced GUI Update Patterns

As your menus grow in complexity, indiscriminately wiping and rebuilding an entire 54-slot inventory on every state change can become a performance bottleneck. Professional GUI integration requires partial refresh strategies and strict conditional rendering logic.

## Conditional Updates & State-Based Rendering

Because the `InventoryProvider#update()` method acts as a top-down rendering pipeline, you can use the `InventoryContext` to execute fast-fail logic.

### 1. Avoid Full Rebuilds
If your GUI consists of a heavy outer border (glass panes) and a small dynamic center area, do not rebuild the border on every single `update()` tick. 

```java
@Override
public void update(@NotNull InventoryView view) {
    Inventory inv = view.getInventory();
    
    // Check metadata to see if this is the initial render
    boolean isFirstRender = (boolean) view.getContext().getExecutionContext().getMetadata().getOrDefault("initialized", false) == false;
    
    if (isFirstRender) {
        // Build heavy static elements once
        buildHeavyBorder(inv);
        
        // Mutate context to mark as initialized for future frames
        // (Note: Requires a state patch, or mutating a custom session object)
    }
    
    // Only update the dynamic slots every frame!
    updateDynamicCenter(inv, view.getContext());
}
```

### 2. Update Only Specific Slots
If a player clicks a button that toggles a specific setting, you do not need to call `manager.update()` (which redraws the whole GUI). You can achieve a targeted, single-slot update by writing a specialized method inside your provider and calling it directly from the button click event.

```java
public class SettingsProvider implements InventoryProvider {
    
    // ... createView implementation ...
    
    // Specialized targeted updater
    public void refreshToggleSlot(InventoryView view, boolean isEnabled) {
        ItemStack toggleItem = new ItemStack(isEnabled ? Material.LIME_DYE : Material.GRAY_DYE);
        view.getInventory().setItem(22, toggleItem);
    }
}

// Inside the Button logic:
Button toggleButton = BaseButton.builder()
    .template(new ItemStack(Material.DYE))
    .anyClick(List.of(ctx -> {
        // 1. Calculate new state
        boolean newState = !oldState;
        
        // 2. Fetch the active view from the context
        InventoryView activeView = getActiveViewSomehow(); 
        
        // 3. Directly target the slot without invoking a global manager.update() loop
        if (activeView.getProvider() instanceof SettingsProvider provider) {
            provider.refreshToggleSlot(activeView, newState);
        }
        
        return ActionResult.success();
    }))
    .build();
```
*(Note: Because of KLibrary's strict unidirectional flow, circumventing `manager.update()` should be reserved for high-frequency toggle buttons where extreme optimization is necessary).*

## Event Throttling

If a player spam-clicks a "Buy" button, sending 10 `manager.update()` calls per second may lag the client and server. Implement a simple rate-limiter inside your buttons:

```java
public boolean canClick(Player player) {
    long lastClick = lastClickMap.getOrDefault(player.getUniqueId(), 0L);
    if (System.currentTimeMillis() - lastClick < 200) { // 200ms cooldown
        return false;
    }
    lastClickMap.put(player.getUniqueId(), System.currentTimeMillis());
    return true;
}
```

## Summary of Optimization Strategies
1. **Cache your Buttons**: Do not instantiate `new NextButton()` inside a for-loop. Create them once statically or globally within the Provider.
2. **Batch Context Updates**: If multiple state variables change simultaneously, build the `InventoryContext` once and call `manager.update()` once.
3. **Use the View Layer**: Place static UI controls (Close, Back, Pagination) on the `InventoryView` layer. This completely removes them from the computational overhead of your dynamic item sorting algorithms inside the provider layer.
