package io.github.kaivian.klibrary.itemstack.implement;

import io.github.kaivian.klibrary.itemstack.api.ItemModifier;
import io.github.kaivian.klibrary.itemstack.api.ItemStackBuilder;
import io.github.kaivian.klibrary.itemstack.util.ItemPreconditions;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of {@link ItemStackBuilder} using the Paper API.
 *
 * <p>Uses {@link ItemStack#editMeta} for efficient metadata modification.
 * Modifiers are accumulated and applied sequentially at build time.</p>
 *
 * <p>This class is package-private to {@code impl} and should not be exposed
 * directly. Use {@link io.github.kaivian.klibrary.itemstack.builder.ItemStackBuilders}
 * as the public entry point.</p>
 */
public class PaperItemStackBuilder implements ItemStackBuilder {

    private Material material;
    private int amount = 1;
    private final List<ItemModifier> modifiers = new ArrayList<>();

    /**
     * Creates a new builder for the given material.
     *
     * @param material the material type
     */
    public PaperItemStackBuilder(@NotNull Material material) {
        ItemPreconditions.requireNonAir(material);
        this.material = material;
    }

    /**
     * Creates a new builder from an existing {@link ItemStack}.
     * The item is cloned to prevent mutation of the original.
     *
     * @param itemStack the item stack to copy from
     */
    public PaperItemStackBuilder(@NotNull ItemStack itemStack) {
        ItemPreconditions.requireNonAir(itemStack.getType());
        this.material = itemStack.getType();
        this.amount = itemStack.getAmount();
    }

    @Override
    public @NotNull ItemStackBuilder type(@NotNull Material material) {
        ItemPreconditions.requireNonAir(material);
        this.material = material;
        return this;
    }

    @Override
    public @NotNull ItemStackBuilder amount(int amount) {
        ItemPreconditions.requireValidAmount(amount);
        this.amount = amount;
        return this;
    }

    @Override
    public @NotNull ItemStackBuilder with(@NotNull ItemModifier modifier) {
        this.modifiers.add(modifier);
        return this;
    }

    @Override
    public @NotNull ItemStack build() {
        ItemStack itemStack = new ItemStack(material, amount);
        itemStack.editMeta(meta -> {
            for (ItemModifier modifier : modifiers) {
                modifier.apply(itemStack, meta);
            }
        });
        return itemStack;
    }
}
