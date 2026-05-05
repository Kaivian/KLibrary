package io.github.kaivian.klibrary.itemstack.modifier;

import com.google.common.collect.Multimap;
import io.github.kaivian.klibrary.itemstack.api.ItemModifier;
import io.github.kaivian.klibrary.itemstack.api.NbtAdapter;
import io.github.kaivian.klibrary.itemstack.display.DisplayModifier;
import io.github.kaivian.klibrary.itemstack.modifier.meta.*;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Central factory for creating {@link ItemModifier} instances.
 *
 * <p>This class provides convenient static methods for constructing all built-in
 * modifiers. It is the recommended way to access the modifier API.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * ItemStackBuilders.of(Material.DIAMOND_SWORD)
 *     .with(ItemModifiers.display()
 *         .name("<gradient:#ff0000:#00ff00>Excalibur</gradient>")
 *         .lore("<gray>A legendary blade</gray>"))
 *     .with(ItemModifiers.enchant(Enchantment.SHARPNESS, 5))
 *     .with(ItemModifiers.unbreakable(true))
 *     .with(ItemModifiers.nbt(nbt -> nbt.set("myplugin:id", PersistentDataType.STRING, "excalibur")))
 *     .build();
 * }</pre>
 */
public final class ItemModifiers {

    // ─── Display ─────────────────────────────────────────────────────────

    /**
     * Creates a new {@link DisplayModifier} for setting item names and lore
     * using MiniMessage formatting and Adventure Components.
     *
     * @return a new display modifier
     */
    public static @NotNull DisplayModifier display() {
        return new DisplayModifier();
    }

    // ─── Enchantments ────────────────────────────────────────────────────

    /**
     * Creates a modifier that adds a single enchantment.
     *
     * @param enchantment the enchantment
     * @param level       the level
     * @return a new modifier
     */
    public static @NotNull ItemModifier enchant(@NotNull Enchantment enchantment, int level) {
        return EnchantmentModifier.of(enchantment, level);
    }

    /**
     * Creates a modifier that adds a single enchantment at level 1.
     *
     * @param enchantment the enchantment
     * @return a new modifier
     */
    public static @NotNull ItemModifier enchant(@NotNull Enchantment enchantment) {
        return EnchantmentModifier.of(enchantment, 1);
    }

    /**
     * Creates a modifier that adds a single enchantment, bypassing level restrictions.
     *
     * @param enchantment the enchantment
     * @param level       the level
     * @return a new modifier
     */
    public static @NotNull ItemModifier unsafeEnchant(@NotNull Enchantment enchantment, int level) {
        return EnchantmentModifier.unsafe(enchantment, level);
    }

    /**
     * Creates a modifier that adds multiple enchantments.
     *
     * @param enchantments the enchantments and their levels
     * @return a new modifier
     */
    public static @NotNull ItemModifier enchantAll(@NotNull Map<Enchantment, Integer> enchantments) {
        return EnchantmentModifier.ofAll(enchantments);
    }

    /**
     * Creates a modifier that clears all enchantments.
     *
     * @return a new modifier
     */
    public static @NotNull ItemModifier clearEnchants() {
        return EnchantmentModifier.clearAll();
    }

    // ─── Flags ───────────────────────────────────────────────────────────

    /**
     * Creates a modifier that adds item flags.
     *
     * @param flags the flags to add
     * @return a new modifier
     */
    public static @NotNull ItemModifier addFlags(@NotNull ItemFlag... flags) {
        return FlagModifier.add(flags);
    }

    /**
     * Creates a modifier that removes item flags.
     *
     * @param flags the flags to remove
     * @return a new modifier
     */
    public static @NotNull ItemModifier removeFlags(@NotNull ItemFlag... flags) {
        return FlagModifier.remove(flags);
    }

    /**
     * Creates a modifier that hides all item flags.
     *
     * @return a new modifier
     */
    public static @NotNull ItemModifier hideAllFlags() {
        return FlagModifier.hideAll();
    }

    // ─── NBT / Persistent Data ───────────────────────────────────────────

    /**
     * Creates a modifier that manipulates the item's persistent data container.
     *
     * @param action the action to perform on the NBT adapter
     * @return a new modifier
     */
    public static @NotNull ItemModifier nbt(@NotNull Consumer<NbtAdapter> action) {
        return new NbtModifier(action);
    }

    // ─── General Properties ──────────────────────────────────────────────

    /**
     * Creates a modifier that sets the unbreakable state.
     *
     * @param unbreakable true for unbreakable
     * @return a new modifier
     */
    public static @NotNull ItemModifier unbreakable(boolean unbreakable) {
        return GeneralModifier.unbreakable(unbreakable);
    }

    /**
     * Creates a modifier that sets the enchantment glint override.
     *
     * @param override true to force, false to suppress, null to reset
     * @return a new modifier
     */
    public static @NotNull ItemModifier glintOverride(@Nullable Boolean override) {
        return GeneralModifier.glintOverride(override);
    }

    /**
     * Creates a modifier that sets custom model data.
     *
     * @param data the custom model data, or null to remove
     * @return a new modifier
     */
    public static @NotNull ItemModifier customModelData(@Nullable Integer data) {
        return GeneralModifier.customModelData(data);
    }

    /**
     * Creates a modifier that sets the max stack size.
     *
     * @param maxStackSize the max size (1–99), or null to reset
     * @return a new modifier
     */
    public static @NotNull ItemModifier maxStackSize(@Nullable Integer maxStackSize) {
        return GeneralModifier.maxStackSize(maxStackSize);
    }

    /**
     * Creates a modifier that sets the item rarity.
     *
     * @param rarity the rarity, or null to reset
     * @return a new modifier
     */
    public static @NotNull ItemModifier rarity(@Nullable ItemRarity rarity) {
        return GeneralModifier.rarity(rarity);
    }

    /**
     * Creates a modifier that hides or shows the tooltip.
     *
     * @param hidden true to hide
     * @return a new modifier
     */
    public static @NotNull ItemModifier hideTooltip(boolean hidden) {
        return GeneralModifier.hideTooltip(hidden);
    }

    /**
     * Creates a modifier that sets the tooltip style.
     *
     * @param key the style key, or null to remove
     * @return a new modifier
     */
    public static @NotNull ItemModifier tooltipStyle(@Nullable String key) {
        return GeneralModifier.tooltipStyle(key);
    }

    /**
     * Creates a modifier that sets the item model.
     *
     * @param key the model key, or null to remove
     * @return a new modifier
     */
    public static @NotNull ItemModifier itemModel(@Nullable String key) {
        return GeneralModifier.itemModel(key);
    }

    /**
     * Creates a modifier that sets the item model.
     *
     * @param key the model key, or null to remove
     * @return a new modifier
     */
    public static @NotNull ItemModifier itemModel(@Nullable NamespacedKey key) {
        return GeneralModifier.itemModel(key);
    }

    /**
     * Creates a modifier that sets the glider property.
     *
     * @param glider true to enable
     * @return a new modifier
     */
    public static @NotNull ItemModifier glider(boolean glider) {
        return GeneralModifier.glider(glider);
    }

    /**
     * Creates a modifier that sets damage resistance.
     *
     * @param tag the damage type tag, or null to remove
     * @return a new modifier
     */
    public static @NotNull ItemModifier damageResistant(@Nullable Tag<DamageType> tag) {
        return GeneralModifier.damageResistant(tag);
    }

    /**
     * Creates a modifier that sets the enchantable value.
     *
     * @param enchantable the value, or null to remove
     * @return a new modifier
     */
    public static @NotNull ItemModifier enchantable(@Nullable Integer enchantable) {
        return GeneralModifier.enchantable(enchantable);
    }

    /**
     * Creates a modifier that sets the use remainder.
     *
     * @param remainder the remainder item, or null to remove
     * @return a new modifier
     */
    public static @NotNull ItemModifier useRemainder(@Nullable ItemStack remainder) {
        return GeneralModifier.useRemainder(remainder);
    }

    // ─── Attributes ──────────────────────────────────────────────────────

    /**
     * Creates a modifier that adds a single attribute modifier.
     *
     * @param attribute the attribute
     * @param modifier  the attribute modifier
     * @return a new item modifier
     */
    public static @NotNull ItemModifier addAttribute(@NotNull Attribute attribute,
                                                      @NotNull AttributeModifier modifier) {
        return AttributeModifierModifier.add(attribute, modifier);
    }

    /**
     * Creates a modifier that sets all attribute modifiers.
     *
     * @param modifiers the attribute modifiers, or null to clear
     * @return a new item modifier
     */
    public static @NotNull ItemModifier setAttributes(@Nullable Multimap<Attribute, AttributeModifier> modifiers) {
        return AttributeModifierModifier.setAll(modifiers);
    }

    /**
     * Creates a modifier that removes all modifiers for a given attribute.
     *
     * @param attribute the attribute
     * @return a new item modifier
     */
    public static @NotNull ItemModifier removeAttribute(@NotNull Attribute attribute) {
        return AttributeModifierModifier.remove(attribute);
    }

    // ─── Meta-Specific Modifiers ─────────────────────────────────────────

    /**
     * Creates a modifier that applies an armor trim.
     *
     * @param trim the trim to apply
     * @return a new modifier
     */
    public static @NotNull ItemModifier armorTrim(@NotNull ArmorTrim trim) {
        return ArmorModifier.trim(trim);
    }

    /**
     * Creates a new potion modifier for configuring potion items.
     *
     * @return a new potion modifier
     */
    public static @NotNull PotionModifier potion() {
        return new PotionModifier();
    }

    /**
     * Creates a new skull modifier for configuring player head items.
     *
     * @return a new skull modifier
     */
    public static @NotNull SkullModifier skull() {
        return new SkullModifier();
    }

    /**
     * Creates a modifier that sets damage on a damageable item.
     *
     * @param damage the damage value
     * @return a new modifier
     */
    public static @NotNull ItemModifier damage(int damage) {
        return DamageModifier.damage(damage);
    }

    /**
     * Creates a modifier that dyes leather armor.
     *
     * @param color the color
     * @return a new modifier
     */
    public static @NotNull ItemModifier leatherColor(@NotNull Color color) {
        return LeatherArmorModifier.color(color);
    }

    // ─── Raw / Lambda Modifier ───────────────────────────────────────────

    /**
     * Creates a custom inline modifier using a lambda expression.
     * Useful for one-off modifications that don't warrant their own class.
     *
     * @param modifier the modification lambda
     * @return the modifier
     */
    public static @NotNull ItemModifier custom(@NotNull ItemModifier modifier) {
        return modifier;
    }
}
