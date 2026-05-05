package io.github.kaivian.klibrary.itemstack.modifier;

import io.github.kaivian.klibrary.itemstack.api.ItemModifier;
import io.github.kaivian.klibrary.util.NamespaceKey;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.damage.DamageType;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A general-purpose modifier for common item properties that don't warrant
 * their own dedicated modifier class.
 *
 * <p>Handles: unbreakable, glint override, custom model data, max stack size,
 * rarity, hide tooltip, tooltip style, item model, glider, damage resistance,
 * enchantable value.</p>
 */
public class GeneralModifier implements ItemModifier {

    @FunctionalInterface
    private interface MetaAction {
        void apply(@NotNull ItemMeta meta);
    }

    private final MetaAction action;

    private GeneralModifier(@NotNull MetaAction action) {
        this.action = action;
    }

    /**
     * Sets the unbreakable state of the item.
     *
     * @param unbreakable true to make unbreakable
     * @return a new modifier
     */
    public static @NotNull GeneralModifier unbreakable(boolean unbreakable) {
        return new GeneralModifier(meta -> meta.setUnbreakable(unbreakable));
    }

    /**
     * Sets or removes the enchantment glint override.
     *
     * @param override true to force glint, false to suppress, null to reset
     * @return a new modifier
     */
    public static @NotNull GeneralModifier glintOverride(@Nullable Boolean override) {
        return new GeneralModifier(meta -> meta.setEnchantmentGlintOverride(override));
    }

    /**
     * Sets or removes the custom model data.
     *
     * @param data the custom model data, or null to remove
     * @return a new modifier
     */
    public static @NotNull GeneralModifier customModelData(@Nullable Integer data) {
        return new GeneralModifier(meta -> meta.setCustomModelData(data));
    }

    /**
     * Sets or removes the maximum stack size.
     *
     * @param maxStackSize the max stack size (1–99), or null to reset
     * @return a new modifier
     */
    public static @NotNull GeneralModifier maxStackSize(@Nullable Integer maxStackSize) {
        return new GeneralModifier(meta -> meta.setMaxStackSize(maxStackSize));
    }

    /**
     * Sets or removes the item rarity.
     *
     * @param rarity the rarity, or null to reset
     * @return a new modifier
     */
    public static @NotNull GeneralModifier rarity(@Nullable ItemRarity rarity) {
        return new GeneralModifier(meta -> meta.setRarity(rarity));
    }

    /**
     * Sets whether the item's tooltip is hidden.
     *
     * @param hidden true to hide the tooltip
     * @return a new modifier
     */
    public static @NotNull GeneralModifier hideTooltip(boolean hidden) {
        return new GeneralModifier(meta -> meta.setHideTooltip(hidden));
    }

    /**
     * Sets the tooltip style using a string key.
     *
     * @param key the style key (e.g. {@code "minecraft:custom_style"}), or null to remove
     * @return a new modifier
     */
    public static @NotNull GeneralModifier tooltipStyle(@Nullable String key) {
        return new GeneralModifier(meta ->
                meta.setTooltipStyle(key != null ? NamespaceKey.from(key) : null));
    }

    /**
     * Sets the tooltip style using a {@link NamespacedKey}.
     *
     * @param key the style key, or null to remove
     * @return a new modifier
     */
    public static @NotNull GeneralModifier tooltipStyle(@Nullable NamespacedKey key) {
        return new GeneralModifier(meta -> meta.setTooltipStyle(key));
    }

    /**
     * Sets the item model using a string key.
     *
     * @param key the model key, or null to remove
     * @return a new modifier
     */
    public static @NotNull GeneralModifier itemModel(@Nullable String key) {
        return new GeneralModifier(meta ->
                meta.setItemModel(key != null ? NamespaceKey.from(key) : null));
    }

    /**
     * Sets the item model using a {@link NamespacedKey}.
     *
     * @param key the model key, or null to remove
     * @return a new modifier
     */
    public static @NotNull GeneralModifier itemModel(@Nullable NamespacedKey key) {
        return new GeneralModifier(meta -> meta.setItemModel(key));
    }

    /**
     * Sets the glider property of the item.
     *
     * @param glider true to make this item a glider
     * @return a new modifier
     */
    public static @NotNull GeneralModifier glider(boolean glider) {
        return new GeneralModifier(meta -> meta.setGlider(glider));
    }

    /**
     * Sets the damage resistance tag for the item.
     *
     * @param tag the damage type tag, or null to remove
     * @return a new modifier
     */
    public static @NotNull GeneralModifier damageResistant(@Nullable Tag<DamageType> tag) {
        return new GeneralModifier(meta -> meta.setDamageResistant(tag));
    }

    /**
     * Sets or removes the enchantable value.
     *
     * @param enchantable the enchantable value, or null to remove
     * @return a new modifier
     */
    public static @NotNull GeneralModifier enchantable(@Nullable Integer enchantable) {
        return new GeneralModifier(meta -> meta.setEnchantable(enchantable));
    }

    /**
     * Sets the use remainder item.
     *
     * @param remainder the item that remains after use, or null to remove
     * @return a new modifier
     */
    public static @NotNull GeneralModifier useRemainder(@Nullable ItemStack remainder) {
        return new GeneralModifier(meta -> meta.setUseRemainder(remainder));
    }

    @Override
    public void apply(@NotNull ItemStack itemStack, @NotNull ItemMeta meta) {
        action.apply(meta);
    }
}
