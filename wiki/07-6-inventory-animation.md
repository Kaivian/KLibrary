# GUI Animation System (Transitions & Effects)

Minecraft inventory menus do not support native CSS-like animations or smooth UI transitions. However, by leveraging KLibrary's state-driven `update()` pipeline, you can easily simulate engaging animations such as slot-by-slot reveals, spinning icons, or fade-in loading screens.

## Concept: Staged Update Cycles

In KLibrary, "Animation" is simply a sequence of timed render updates. Instead of writing complex `BukkitRunnable` blocks that forcibly alter raw item stacks, you modify the `InventoryContext` state over time and call `manager.update()` on a schedule.

Because `manager.update()` seamlessly repopulates the active view without triggering Bukkit's disruptive `close/open` packet (which resets the mouse cursor), these rapid re-renders look completely smooth to the player.

## Example: Fade-In Loading Screen

Imagine a GUI that takes 2 seconds to fetch database records. Instead of freezing the player, you can show a loading animation and then trigger a final update.

```java
public class AnimatedLoadingProvider implements InventoryProvider {
    
    @Override
    public boolean isAutoRefresh() {
        return true; // Tick every 1 second
    }

    @Override
    public void update(@NotNull InventoryView view) {
        InventoryContext ctx = view.getContext();
        
        // Use context metadata to track how many seconds have passed
        int ticksPassed = (int) ctx.getExecutionContext().getMetadata().getOrDefault("loading_ticks", 0);
        
        if (ticksPassed >= 3) {
            // Once loading completes, load the actual content
            renderFinalContent(view);
        } else {
            // Render the loading animation frames
            renderLoadingFrame(view, ticksPassed);
            
            // Increment the counter state safely
            InventoryContext newCtx = InventoryContext.from(ctx.getExecutionContext())
                .metadata("loading_ticks", ticksPassed + 1)
                .build();
            
            // DO NOT call manager.update() here, because isAutoRefresh() 
            // is already automatically calling this update method natively!
            // However, we MUST update the view's backing context wrapper if we are 
            // tracking state mutations that auto-refresh needs. 
            // (Typically, animations are better executed via scheduled Bukkit tasks)
        }
    }
}
```

## Example: Slot-by-Slot Reveal

For high-impact visual flair, you can reveal items sequentially (e.g., placing one item per tick) using Bukkit's scheduler.

```java
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public void playRevealAnimation(Plugin plugin, InventoryManager manager, Player player, InventoryContext baseContext) {
    
    // We want to reveal 10 items, one every 2 ticks
    for (int i = 0; i <= 10; i++) {
        final int revealCount = i;
        
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            
            // Build a new context state specifying how many slots are "unlocked"
            InventoryContext animatedContext = InventoryContext.from(baseContext.getExecutionContext())
                .metadata("reveal_limit", revealCount)
                .build();
                
            // Trigger a silent re-render!
            manager.update(player, animatedContext);
            
        }, i * 2L); // 0, 2, 4, 6... ticks
    }
}

// In your Provider:
@Override
public void update(InventoryView view) {
    int revealLimit = (int) view.getContext().getExecutionContext().getMetadata().getOrDefault("reveal_limit", 0);
    
    for (int i = 0; i < revealLimit; i++) {
        view.getInventory().setItem(i, getCoolItem(i));
    }
}
```

## Page Transition Animations

When a user clicks "Next Page", rather than instantly swapping the items, you can play a "swipe" animation:
1. Clear the inventory.
2. Schedule a sequence of state updates over 5 ticks that slide the new items in from the right edge.
3. Because the `Provider` reads the "slide offset" from the Context, the rendering math isolates cleanly from the scheduling loop.

## Best Practices
- **Never hold thread locks**: Minecraft servers run on a single main thread. Do not try to animate GUIs with `Thread.sleep()`. Always use `Bukkit.getScheduler()`.
- **Cancel tasks on close**: If the player closes the inventory halfway through a 3-second reveal animation, ensure your Bukkit tasks cancel gracefully or check `player.getOpenInventory()` before firing `manager.update()` to prevent null pointers or ghost inventories popping up.
