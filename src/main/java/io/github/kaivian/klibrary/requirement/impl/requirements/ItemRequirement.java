package io.github.kaivian.klibrary.requirement.impl.requirements;

import io.github.kaivian.klibrary.context.ExecutionContext;
import io.github.kaivian.klibrary.requirement.api.RequirementResult;
import io.github.kaivian.klibrary.requirement.impl.AbstractRequirement;
import io.github.kaivian.klibrary.service.ServiceProvider;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

/**
 * Checks whether a player has a specific item in their inventory.
 *
 * <p><b>YAML:</b></p>
 * <pre>{@code
 * type: item
 * material: DIAMOND
 * amount: 5    # optional, defaults to 1
 * }</pre>
 */
public class ItemRequirement extends AbstractRequirement {

    private final Material material;
    private final int amount;

    /**
     * Constructs a new {@code ItemRequirement}.
     *
     * @param services the service provider
     * @param material the required material
     * @param amount   the minimum amount required
     */
    public ItemRequirement(ServiceProvider services, Material material, int amount) {
        super(services);
        this.material = material;
        this.amount = Math.max(1, amount);
    }

    /**
     * Constructs a new {@code ItemRequirement} from a material name string.
     *
     * @param services     the service provider
     * @param materialName the material name (case-insensitive)
     * @param amount       the minimum amount required
     * @throws IllegalArgumentException if the material name is invalid
     */
    public ItemRequirement(ServiceProvider services, String materialName, int amount) {
        this(services, parseMaterial(materialName), amount);
    }

    /** {@inheritDoc} */
    @Override
    public RequirementResult evaluate(ExecutionContext context) {
        return context.getPlayer().map(player -> {
            int count = 0;
            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && item.getType() == material) {
                    count += item.getAmount();
                }
            }
            return count >= amount
                    ? met()
                    : notMet("Insufficient items: " + count + "/" + amount + " " + material.name());
        }).orElse(notMet("No player in context"));
    }

    /**
     * Parses a material name to a {@link Material} enum value.
     *
     * @param name the material name
     * @return the parsed material
     * @throws IllegalArgumentException if invalid
     */
    private static Material parseMaterial(String name) {
        Material mat = Material.matchMaterial(name.toUpperCase(Locale.ROOT));
        if (mat == null) {
            throw new IllegalArgumentException("Invalid material: " + name);
        }
        return mat;
    }

    /** @return the required material */
    public Material getMaterial() { return material; }

    /** @return the required amount */
    public int getAmount() { return amount; }
}
