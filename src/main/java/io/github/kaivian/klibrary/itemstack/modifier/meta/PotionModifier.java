package io.github.kaivian.klibrary.itemstack.modifier.meta;

import io.github.kaivian.klibrary.itemstack.api.ItemModifier;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A modifier for configuring potion items (potions, splash potions, lingering potions, tipped arrows).
 *
 * <p>Only applies if the item's meta is an instance of {@link PotionMeta}.</p>
 */
public class PotionModifier implements ItemModifier {

    private @Nullable PotionType basePotionType;
    private @Nullable Color color;
    private final List<PotionEffect> customEffects = new ArrayList<>();
    private boolean overwrite = true;

    /**
     * Sets the base potion type.
     *
     * @param type the base type
     * @return this modifier for chaining
     */
    public @NotNull PotionModifier basePotionType(@NotNull PotionType type) {
        this.basePotionType = type;
        return this;
    }

    /**
     * Sets the potion color.
     *
     * @param color the color
     * @return this modifier for chaining
     */
    public @NotNull PotionModifier color(@NotNull Color color) {
        this.color = color;
        return this;
    }

    /**
     * Adds a custom potion effect.
     *
     * @param effect the effect to add
     * @return this modifier for chaining
     */
    public @NotNull PotionModifier addEffect(@NotNull PotionEffect effect) {
        this.customEffects.add(effect);
        return this;
    }

    /**
     * Sets whether added effects should overwrite existing effects of the same type.
     *
     * @param overwrite true to overwrite
     * @return this modifier for chaining
     */
    public @NotNull PotionModifier overwrite(boolean overwrite) {
        this.overwrite = overwrite;
        return this;
    }

    @Override
    public void apply(@NotNull ItemStack itemStack, @NotNull ItemMeta meta) {
        if (meta instanceof PotionMeta potionMeta) {
            if (basePotionType != null) {
                potionMeta.setBasePotionType(basePotionType);
            }
            if (color != null) {
                potionMeta.setColor(color);
            }
            for (PotionEffect effect : customEffects) {
                potionMeta.addCustomEffect(effect, overwrite);
            }
        }
    }
}
