package io.github.kaivian.klibrary.itemstack.util;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

/**
 * Minimal validation utilities for the itemstack package.
 *
 * <p>These are internal guards — not meant for public API consumption.</p>
 */
public final class ItemPreconditions {

    /**
     * Ensures the given material is not air.
     *
     * @param material the material to check
     * @throws IllegalArgumentException if the material is air
     */
    public static void requireNonAir(@NotNull Material material) {
        if (material.isAir()) {
            throw new IllegalArgumentException("Material must not be air: " + material);
        }
    }

    /**
     * Ensures the given amount is within the valid range (1–127).
     *
     * @param amount the amount to check
     * @throws IllegalArgumentException if the amount is out of range
     */
    public static void requireValidAmount(int amount) {
        if (amount < 1 || amount > 127) {
            throw new IllegalArgumentException("Amount must be between 1 and 127, got: " + amount);
        }
    }
}
