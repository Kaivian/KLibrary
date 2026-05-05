package io.github.kaivian.klibrary.itemstack.modifier;

import io.github.kaivian.klibrary.itemstack.api.ItemModifier;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * A modifier for adding or removing {@link ItemFlag}s on an item.
 */
public class FlagModifier implements ItemModifier {

    private final Set<ItemFlag> flagsToAdd;
    private final Set<ItemFlag> flagsToRemove;
    private final boolean hideAll;

    private FlagModifier(Set<ItemFlag> flagsToAdd, Set<ItemFlag> flagsToRemove, boolean hideAll) {
        this.flagsToAdd = flagsToAdd;
        this.flagsToRemove = flagsToRemove;
        this.hideAll = hideAll;
    }

    /**
     * Creates a modifier that adds the specified flags.
     *
     * @param flags the flags to add
     * @return a new modifier
     */
    public static @NotNull FlagModifier add(@NotNull ItemFlag... flags) {
        return new FlagModifier(Set.of(flags), Set.of(), false);
    }

    /**
     * Creates a modifier that removes the specified flags.
     *
     * @param flags the flags to remove
     * @return a new modifier
     */
    public static @NotNull FlagModifier remove(@NotNull ItemFlag... flags) {
        return new FlagModifier(Set.of(), Set.of(flags), false);
    }

    /**
     * Creates a modifier that hides all item flags.
     *
     * @return a new modifier
     */
    public static @NotNull FlagModifier hideAll() {
        return new FlagModifier(Set.of(), Set.of(), true);
    }

    @Override
    public void apply(@NotNull ItemStack itemStack, @NotNull ItemMeta meta) {
        if (hideAll) {
            meta.addItemFlags(ItemFlag.values());
            return;
        }
        if (!flagsToAdd.isEmpty()) {
            meta.addItemFlags(flagsToAdd.toArray(ItemFlag[]::new));
        }
        if (!flagsToRemove.isEmpty()) {
            meta.removeItemFlags(flagsToRemove.toArray(ItemFlag[]::new));
        }
    }
}
