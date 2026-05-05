package io.github.kaivian.klibrary.service;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * Service for playing sounds to players with safe key resolution.
 *
 * <p>This service provides utility methods for playing sounds to individual
 * players, all online players (broadcast), or all players in a specific world.
 * Sound keys are resolved safely — invalid keys result in a warning log
 * rather than an exception.</p>
 *
 * <p><b>Example:</b></p>
 * <pre>{@code
 * SoundService soundService = new SoundService();
 * soundService.playSound(player, "ENTITY_EXPERIENCE_ORB_PICKUP", 1.0f, 1.5f);
 * soundService.broadcastSound("BLOCK_NOTE_BLOCK_PLING", 1.0f, 1.0f);
 * }</pre>
 */
public class SoundService {

    private static final Logger LOGGER = Logger.getLogger(SoundService.class.getName());

    /**
     * Resolves a sound key string to a Bukkit {@link Sound} enum value.
     *
     * @param soundKey the sound key (case-insensitive)
     * @return an {@link Optional} containing the resolved sound, or empty if invalid
     */
    @SuppressWarnings("deprecation")
    public Optional<Sound> resolveSound(String soundKey) {
        if (soundKey == null || soundKey.isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(Sound.valueOf(soundKey.toUpperCase()));
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Invalid sound key: " + soundKey);
            return Optional.empty();
        }
    }

    /**
     * Plays a sound to a specific player.
     *
     * @param player   the player to play the sound for
     * @param soundKey the sound key (case-insensitive)
     * @param volume   the volume level (1.0 is normal)
     * @param pitch    the pitch level (1.0 is normal)
     * @return {@code true} if the sound was played successfully
     */
    public boolean playSound(Player player, String soundKey, float volume, float pitch) {
        return resolveSound(soundKey).map(sound -> {
            player.playSound(player.getLocation(), sound, volume, pitch);
            return true;
        }).orElse(false);
    }

    /**
     * Plays a sound to a specific player using a {@link Sound} enum value.
     *
     * @param player the player to play the sound for
     * @param sound  the sound to play
     * @param volume the volume level
     * @param pitch  the pitch level
     */
    public void playSound(Player player, Sound sound, float volume, float pitch) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    /**
     * Broadcasts a sound to all online players.
     *
     * @param soundKey the sound key (case-insensitive)
     * @param volume   the volume level
     * @param pitch    the pitch level
     * @return {@code true} if the sound was played successfully
     */
    public boolean broadcastSound(String soundKey, float volume, float pitch) {
        return resolveSound(soundKey).map(sound -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.playSound(player.getLocation(), sound, volume, pitch);
            }
            return true;
        }).orElse(false);
    }

    /**
     * Broadcasts a sound to all players in the same world as the target player.
     *
     * @param target   the reference player (determines the world)
     * @param soundKey the sound key (case-insensitive)
     * @param volume   the volume level
     * @param pitch    the pitch level
     * @return {@code true} if the sound was played successfully
     */
    public boolean broadcastWorldSound(Player target, String soundKey, float volume, float pitch) {
        return resolveSound(soundKey).map(sound -> {
            for (Player player : target.getWorld().getPlayers()) {
                player.playSound(player.getLocation(), sound, volume, pitch);
            }
            return true;
        }).orElse(false);
    }
}
