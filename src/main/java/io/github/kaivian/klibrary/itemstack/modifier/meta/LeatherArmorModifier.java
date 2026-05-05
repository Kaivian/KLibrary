package io.github.kaivian.klibrary.itemstack.modifier.meta;

import io.github.kaivian.klibrary.itemstack.api.ItemModifier;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A modifier for dyeing leather armor.
 *
 * <p>Only applies if the item's meta is an instance of {@link LeatherArmorMeta}.</p>
 */
public class LeatherArmorModifier implements ItemModifier {

    private final @Nullable Color color;

    private LeatherArmorModifier(@Nullable Color color) {
        this.color = color;
    }

    /**
     * Creates a modifier that sets the armor color.
     *
     * @param color the dye color
     * @return a new modifier
     */
    public static @NotNull LeatherArmorModifier color(@NotNull Color color) {
        return new LeatherArmorModifier(color);
    }

    /**
     * Creates a modifier that resets the armor color to default.
     *
     * @return a new modifier
     */
    public static @NotNull LeatherArmorModifier clearColor() {
        return new LeatherArmorModifier(null);
    }

    @Override
    public void apply(@NotNull ItemStack itemStack, @NotNull ItemMeta meta) {
        if (meta instanceof LeatherArmorMeta leatherMeta) {
            leatherMeta.setColor(color);
        }
    }
}
