# Advanced Usage

This section covers technical tips and advanced practices for maximizing performance and flexibility with KLibrary.

## Asynchronous Execution

Bukkit requires most API calls (e.g., setting blocks, altering inventories, changing health) to happen on the **main thread**. However, database fetches or heavy requirement calculations should happen asynchronously.

### Async Requirements

If you write a requirement that queries a database, do not evaluate it directly in an event listener.

1. Fetch data asynchronously using `Bukkit.getScheduler().runTaskAsynchronously`.
2. Cache the result.
3. Validate the cached result via the `RequirementResult.evaluate()` sync loop.

### Async Inventory Updates

While `InventoryProvider#update()` ticks on the main thread to allow safe GUI item mutation, you can perform logic fetches on another thread and apply the state update via thread-safe variables before the next tick.

## Extending the Configuration Deserializer

KLibrary uses `ConfigNode` heavily. If you use a format completely foreign to Bukkit's `YamlConfiguration` (e.g., TOML, JSON, or HOCON via Spongepowered Configurate), you only need to write a new implementation of the `ConfigNode` interface wrapping your format.

All deserialization code across Actions, Requirements, and Inventories will instantly support your new file format.

## Performance Tips

1. **Cache Action Parsings**: Compiling MiniMessage strings into Components is CPU heavy. If your action sends a static text string, parse the `Component` once inside the Action's constructor, rather than parsing it every time `execute(player)` is called.
2. **Registry Singleton**: `ActionFactory` and `RequirementFactory` should be singletons per plugin instance. Do not instantiate them repeatedly.
3. **Tick Limits**: Be mindful of your `InventoryProvider#update()` execution times. If you have 50 players opening dynamic inventories, heavy logic inside `update()` will cause server lag. Limit complex redraw operations.
