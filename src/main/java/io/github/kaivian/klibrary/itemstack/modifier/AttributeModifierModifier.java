package io.github.kaivian.klibrary.itemstack.modifier;

import com.google.common.collect.Multimap;
import io.github.kaivian.klibrary.itemstack.api.ItemModifier;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A modifier for manipulating attribute modifiers on an item.
 *
 * <p>Supports adding single attributes, setting bulk attributes, and removing
 * attributes by type or equipment slot.</p>
 */
public class AttributeModifierModifier implements ItemModifier {

    @FunctionalInterface
    private interface AttributeAction {
        void apply(@NotNull ItemMeta meta);
    }

    private final AttributeAction action;

    private AttributeModifierModifier(@NotNull AttributeAction action) {
        this.action = action;
    }

    /**
     * Creates a modifier that adds a single attribute modifier.
     *
     * @param attribute the attribute to modify
     * @param modifier  the modifier to add
     * @return a new modifier
     */
    public static @NotNull AttributeModifierModifier add(@NotNull Attribute attribute,
                                                          @NotNull AttributeModifier modifier) {
        return new AttributeModifierModifier(meta -> meta.addAttributeModifier(attribute, modifier));
    }

    /**
     * Creates a modifier that sets all attribute modifiers, replacing existing ones.
     *
     * @param modifiers the multimap of attributes and modifiers, or null to clear
     * @return a new modifier
     */
    public static @NotNull AttributeModifierModifier setAll(@Nullable Multimap<Attribute, AttributeModifier> modifiers) {
        return new AttributeModifierModifier(meta -> meta.setAttributeModifiers(modifiers));
    }

    /**
     * Creates a modifier that removes all modifiers for a given attribute.
     *
     * @param attribute the attribute to remove modifiers for
     * @return a new modifier
     */
    public static @NotNull AttributeModifierModifier remove(@NotNull Attribute attribute) {
        return new AttributeModifierModifier(meta -> meta.removeAttributeModifier(attribute));
    }

    /**
     * Creates a modifier that removes all modifiers for a given equipment slot.
     *
     * @param slot the equipment slot
     * @return a new modifier
     */
    public static @NotNull AttributeModifierModifier remove(@NotNull EquipmentSlot slot) {
        return new AttributeModifierModifier(meta -> meta.removeAttributeModifier(slot));
    }

    @Override
    public void apply(@NotNull ItemStack itemStack, @NotNull ItemMeta meta) {
        action.apply(meta);
    }
}
