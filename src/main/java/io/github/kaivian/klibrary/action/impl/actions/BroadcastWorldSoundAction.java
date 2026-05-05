package io.github.kaivian.klibrary.action.impl.actions;

import io.github.kaivian.klibrary.action.api.ActionResult;
import io.github.kaivian.klibrary.action.impl.AbstractAction;
import io.github.kaivian.klibrary.context.ExecutionContext;
import io.github.kaivian.klibrary.service.ServiceProvider;

/**
 * Broadcasts a sound to all players in the same world as the target player.
 *
 * <p><b>YAML configuration:</b></p>
 * <pre>{@code
 * type: broadcast_world_sound
 * sound: ENTITY_ENDER_DRAGON_GROWL
 * volume: 1.0
 * pitch: 0.5
 * }</pre>
 */
public class BroadcastWorldSoundAction extends AbstractAction {

    private final String soundKey;
    private final float volume;
    private final float pitch;

    /**
     * Constructs a new {@code BroadcastWorldSoundAction}.
     *
     * @param services the service provider
     * @param soundKey the sound key (case-insensitive)
     * @param volume   the volume level
     * @param pitch    the pitch level
     */
    public BroadcastWorldSoundAction(ServiceProvider services, String soundKey, float volume, float pitch) {
        super(services);
        this.soundKey = soundKey;
        this.volume = volume;
        this.pitch = pitch;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActionResult execute(ExecutionContext context) {
        return requirePlayer(context).map(player -> {
            boolean played = services.getSoundService().broadcastWorldSound(player, soundKey, volume, pitch);
            return played ? success() : failure("Invalid sound: " + soundKey);
        }).orElse(failure("No player in context for BroadcastWorldSoundAction"));
    }

    /** @return the sound key */
    public String getSoundKey() { return soundKey; }

    /** @return the volume level */
    public float getVolume() { return volume; }

    /** @return the pitch level */
    public float getPitch() { return pitch; }
}
