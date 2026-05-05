package io.github.kaivian.klibrary.itemstack.builder;

import io.github.kaivian.klibrary.itemstack.api.ItemStackBuilder;
import io.github.kaivian.klibrary.itemstack.implement.PaperItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Static entry point for creating {@link ItemStackBuilder} instances.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * ItemStack item = ItemStackBuilders.of(Material.DIAMOND_SWORD)
 *     .with(ItemModifiers.display().name("<red>Fire Sword</red>"))
 *     .with(ItemModifiers.enchant(Enchantment.SHARPNESS, 5))
 *     .build();
 * }</pre>
 */
public final class ItemStackBuilders {

    /**
     * Creates a new builder for the specified material.
     *
     * @param material the material type
     * @return a new builder instance
     */
    public static @NotNull ItemStackBuilder of(@NotNull Material material) {
        return new PaperItemStackBuilder(material);
    }

    /**
     * Creates a new builder initialized from an existing {@link ItemStack}.
     * The original item is not mutated.
     *
     * @param itemStack the item to copy from
     * @return a new builder instance
     */
    public static @NotNull ItemStackBuilder from(@NotNull ItemStack itemStack) {
        return new PaperItemStackBuilder(itemStack);
    }
}
