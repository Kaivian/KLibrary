package io.github.kaivian.klibrary.itemstack.modifier.meta;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import io.github.kaivian.klibrary.itemstack.api.ItemModifier;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * A modifier for configuring player skull items.
 *
 * <p>Supports setting the owning player or a custom Base64-encoded skin texture
 * using Paper's native {@link PlayerProfile} and {@link ProfileProperty} API.</p>
 *
 * <p>Only applies if the item's meta is an instance of {@link SkullMeta}.</p>
 */
public class SkullModifier implements ItemModifier {

    private @Nullable OfflinePlayer owner;
    private @Nullable String base64Texture;

    /**
     * Sets the skull owner to a specific player.
     *
     * @param player the player whose head to display
     * @return this modifier for chaining
     */
    public @NotNull SkullModifier owner(@NotNull OfflinePlayer player) {
        this.owner = player;
        this.base64Texture = null;
        return this;
    }

    /**
     * Sets a custom skull texture using a Base64-encoded texture string.
     *
     * <p>The Base64 string is the standard Minecraft texture blob, e.g. from
     * <a href="https://minecraft-heads.com">minecraft-heads.com</a>.</p>
     *
     * @param base64 the Base64-encoded texture value
     * @return this modifier for chaining
     */
    public @NotNull SkullModifier texture(@NotNull String base64) {
        this.base64Texture = base64;
        this.owner = null;
        return this;
    }

    @Override
    public void apply(@NotNull ItemStack itemStack, @NotNull ItemMeta meta) {
        if (!(meta instanceof SkullMeta skullMeta)) {
            return;
        }

        if (owner != null) {
            skullMeta.setOwningPlayer(owner);
        } else if (base64Texture != null) {
            PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
            profile.setProperty(new ProfileProperty("textures", base64Texture));
            skullMeta.setPlayerProfile(profile);
        }
    }
}
