package io.github.kaivian.klibrary.itemstack.modifier.meta;

import io.github.kaivian.klibrary.itemstack.api.ItemModifier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A modifier for setting item durability/damage values.
 *
 * <p>Only applies if the item's meta is an instance of {@link Damageable}.</p>
 */
public class DamageModifier implements ItemModifier {

    private final @Nullable Integer damage;
    private final @Nullable Integer maxDamage;

    private DamageModifier(@Nullable Integer damage, @Nullable Integer maxDamage) {
        this.damage = damage;
        this.maxDamage = maxDamage;
    }

    /**
     * Creates a modifier that sets the damage value on the item.
     *
     * @param damage the damage value
     * @return a new modifier
     */
    public static @NotNull DamageModifier damage(int damage) {
        return new DamageModifier(damage, null);
    }

    /**
     * Creates a modifier that sets the maximum damage value on the item.
     *
     * @param maxDamage the max damage value
     * @return a new modifier
     */
    public static @NotNull DamageModifier maxDamage(int maxDamage) {
        return new DamageModifier(null, maxDamage);
    }

    /**
     * Creates a modifier that sets both damage and max damage.
     *
     * @param damage    the current damage
     * @param maxDamage the max damage
     * @return a new modifier
     */
    public static @NotNull DamageModifier of(int damage, int maxDamage) {
        return new DamageModifier(damage, maxDamage);
    }

    @Override
    public void apply(@NotNull ItemStack itemStack, @NotNull ItemMeta meta) {
        if (meta instanceof Damageable damageable) {
            if (damage != null) {
                damageable.setDamage(damage);
            }
            if (maxDamage != null) {
                damageable.setMaxDamage(maxDamage);
            }
        }
    }
}
