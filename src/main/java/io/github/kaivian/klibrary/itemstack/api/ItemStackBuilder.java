package io.github.kaivian.klibrary.itemstack.api;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * A fluent builder for constructing {@link ItemStack} instances.
 *
 * <p>Modifiers are queued and applied sequentially when {@link #build()} is invoked.
 * The builder produces a new {@link ItemStack} on each {@code build()} call,
 * ensuring immutability of the resulting item.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * ItemStack item = ItemStackBuilders.of(Material.DIAMOND_SWORD)
 *     .amount(1)
 *     .with(ItemModifiers.display()
 *         .name("<gradient:#ff0000:#00ff00>Excalibur</gradient>")
 *         .lore("<gray>A legendary blade</gray>"))
 *     .with(ItemModifiers.enchant(Enchantment.SHARPNESS, 5))
 *     .with(ItemModifiers.unbreakable(true))
 *     .build();
 * }</pre>
 */
public interface ItemStackBuilder {

    /**
     * Sets the material type of the item being built.
     *
     * @param material the material type, must not be air
     * @return this builder instance for chaining
     * @throws IllegalArgumentException if the material is air
     */
    @NotNull ItemStackBuilder type(@NotNull Material material);

    /**
     * Sets the stack amount of the item being built.
     *
     * @param amount the stack amount (1–127)
     * @return this builder instance for chaining
     * @throws IllegalArgumentException if the amount is out of range
     */
    @NotNull ItemStackBuilder amount(int amount);

    /**
     * Queues a modifier to be applied when the item is built.
     * Modifiers are applied in the order they are added.
     *
     * @param modifier the modifier to apply
     * @return this builder instance for chaining
     */
    @NotNull ItemStackBuilder with(@NotNull ItemModifier modifier);

    /**
     * Builds and returns a new {@link ItemStack} with all queued modifiers applied.
     * Each call produces a fresh instance.
     *
     * @return the constructed ItemStack
     */
    @NotNull ItemStack build();
}
