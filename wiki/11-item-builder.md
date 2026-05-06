# ItemBuilder System

KLibrary provides a fluent, immutable `ItemStackBuilder` API to simplify the creation and modification of `ItemStack`s. It replaces repetitive Bukkit boilerplate with a clean builder pattern that natively supports modern components (like Adventure for display names and lore).

## Creating an ItemStack

You can start a builder either from a `Material` or by cloning an existing `ItemStack` using `ItemStackBuilders`.

```java
import io.github.kaivian.klibrary.itemstack.builder.ItemStackBuilders;
import io.github.kaivian.klibrary.itemstack.modifier.ItemModifiers;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

ItemStack item = ItemStackBuilders.of(Material.DIAMOND_SWORD)
    .amount(1)
    .with(ItemModifiers.display()
        .name("<gradient:#ff0000:#00ff00>Excalibur</gradient>")
        .lore("<gray>A legendary blade</gray>", "<white>Forged in the ancient times.</white>"))
    .with(ItemModifiers.enchant(Enchantment.SHARPNESS, 5))
    .with(ItemModifiers.unbreakable(true))
    .build();
```

### Modifying an Existing Item

To modify an existing item without altering the original (since the builder creates a new instance on `.build()`), use `ItemStackBuilders.from()`:

```java
ItemStack modifiedItem = ItemStackBuilders.from(existingItem)
    .with(ItemModifiers.display().name("<red>New Name</red>"))
    .build();
```

## Item Modifiers

The power of the `ItemStackBuilder` comes from its `ItemModifier`s. Modifiers are queued and applied sequentially when `build()` is invoked.

Common modifiers provided by `ItemModifiers` include:
- `ItemModifiers.display()` - For modifying the item's display name, lore, and custom model data using MiniMessage.
- `ItemModifiers.enchant(Enchantment, level)` - For adding enchantments.
- `ItemModifiers.unbreakable(boolean)` - For making the item unbreakable.
- `ItemModifiers.customModelData(int)` - For setting custom model data directly.

Using this system ensures thread-safety (items are cloned appropriately) and maintains a clean, declarative approach to item creation.
