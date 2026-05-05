package io.github.kaivian.klibrary.itemstack.modifier.meta;

import io.github.kaivian.klibrary.itemstack.api.ItemModifier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A modifier for applying armor trims to armor items.
 *
 * <p>Only applies if the item's meta is an instance of {@link ArmorMeta}.</p>
 */
public class ArmorModifier implements ItemModifier {

    private final @Nullable ArmorTrim trim;

    private ArmorModifier(@Nullable ArmorTrim trim) {
        this.trim = trim;
    }

    /**
     * Creates a modifier that sets an armor trim.
     *
     * @param trim the armor trim to apply
     * @return a new modifier
     */
    public static @NotNull ArmorModifier trim(@NotNull ArmorTrim trim) {
        return new ArmorModifier(trim);
    }

    /**
     * Creates a modifier that removes the armor trim.
     *
     * @return a new modifier
     */
    public static @NotNull ArmorModifier clearTrim() {
        return new ArmorModifier(null);
    }

    @Override
    public void apply(@NotNull ItemStack itemStack, @NotNull ItemMeta meta) {
        if (meta instanceof ArmorMeta armorMeta) {
            armorMeta.setTrim(trim);
        }
    }
}
