package io.github.kaivian.klibrary.itemstack.api;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a single, composable transformation that can be applied to an {@link ItemStack}.
 *
 * <p>Modifiers follow the Single Responsibility Principle — each modifier handles
 * exactly one concern (display, enchantments, flags, NBT, etc.).</p>
 *
 * <p>Implementations should be stateless where possible to allow safe reuse across
 * multiple builder pipelines.</p>
 *
 * @see ItemStackBuilder#with(ItemModifier)
 */
@FunctionalInterface
public interface ItemModifier {

    /**
     * Applies this modification to the given item's metadata.
     *
     * <p>The {@code itemStack} parameter is provided for type-specific checks
     * (e.g., material validation), but all metadata changes should target the
     * {@code meta} parameter. The meta is committed to the item automatically
     * by the builder.</p>
     *
     * @param itemStack the item stack being built (for read-only context)
     * @param meta      the mutable metadata to modify
     */
    void apply(@NotNull ItemStack itemStack, @NotNull ItemMeta meta);
}
