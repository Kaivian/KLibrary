package io.github.kaivian.klibrary.itemstack.modifier;

import io.github.kaivian.klibrary.itemstack.api.ItemModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * A modifier for adding, removing, or clearing enchantments on an item.
 *
 * <p>Supports both single and bulk enchantment operations, with optional
 * level restriction bypass.</p>
 */
public class EnchantmentModifier implements ItemModifier {

    private final Map<Enchantment, Integer> enchantments;
    private final boolean ignoreLevelRestriction;
    private final boolean clearExisting;

    private EnchantmentModifier(Map<Enchantment, Integer> enchantments,
                                boolean ignoreLevelRestriction,
                                boolean clearExisting) {
        this.enchantments = enchantments;
        this.ignoreLevelRestriction = ignoreLevelRestriction;
        this.clearExisting = clearExisting;
    }

    /**
     * Creates a modifier that adds a single enchantment.
     *
     * @param enchantment the enchantment to add
     * @param level       the enchantment level
     * @return a new modifier
     */
    public static @NotNull EnchantmentModifier of(@NotNull Enchantment enchantment, int level) {
        return new EnchantmentModifier(Map.of(enchantment, level), false, false);
    }

    /**
     * Creates a modifier that adds a single enchantment, bypassing level restrictions.
     *
     * @param enchantment the enchantment to add
     * @param level       the enchantment level
     * @return a new modifier
     */
    public static @NotNull EnchantmentModifier unsafe(@NotNull Enchantment enchantment, int level) {
        return new EnchantmentModifier(Map.of(enchantment, level), true, false);
    }

    /**
     * Creates a modifier that adds multiple enchantments.
     *
     * @param enchantments the enchantments and their levels
     * @return a new modifier
     */
    public static @NotNull EnchantmentModifier ofAll(@NotNull Map<Enchantment, Integer> enchantments) {
        return new EnchantmentModifier(Map.copyOf(enchantments), false, false);
    }

    /**
     * Creates a modifier that removes all enchantments from the item.
     *
     * @return a new modifier
     */
    public static @NotNull EnchantmentModifier clearAll() {
        return new EnchantmentModifier(Map.of(), false, true);
    }

    @Override
    public void apply(@NotNull ItemStack itemStack, @NotNull ItemMeta meta) {
        if (clearExisting) {
            meta.removeEnchantments();
        }
        enchantments.forEach((enchantment, level) -> {
            if (level <= 0) {
                meta.removeEnchant(enchantment);
            } else {
                meta.addEnchant(enchantment, level, ignoreLevelRestriction);
            }
        });
    }
}
