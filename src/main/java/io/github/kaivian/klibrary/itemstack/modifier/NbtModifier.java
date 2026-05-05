package io.github.kaivian.klibrary.itemstack.modifier;

import io.github.kaivian.klibrary.itemstack.api.ItemModifier;
import io.github.kaivian.klibrary.itemstack.api.NbtAdapter;
import io.github.kaivian.klibrary.itemstack.implement.PaperNbtAdapter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * A modifier that allows writing custom NBT data to an item's
 * {@link org.bukkit.persistence.PersistentDataContainer} via the {@link NbtAdapter} abstraction.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * ItemModifiers.nbt(nbt -> {
 *     nbt.set("myplugin:weapon_id", PersistentDataType.STRING, "excalibur");
 *     nbt.set("myplugin:rarity", PersistentDataType.INTEGER, 5);
 * })
 * }</pre>
 */
public class NbtModifier implements ItemModifier {

    private final Consumer<NbtAdapter> action;

    /**
     * Creates a new NBT modifier with the given action.
     *
     * @param action the action to perform on the NBT adapter
     */
    public NbtModifier(@NotNull Consumer<NbtAdapter> action) {
        this.action = action;
    }

    @Override
    public void apply(@NotNull ItemStack itemStack, @NotNull ItemMeta meta) {
        NbtAdapter adapter = new PaperNbtAdapter(meta.getPersistentDataContainer());
        action.accept(adapter);
    }
}
