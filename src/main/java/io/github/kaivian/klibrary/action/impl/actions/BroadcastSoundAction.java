package io.github.kaivian.klibrary.action.impl.actions;

import io.github.kaivian.klibrary.action.api.ActionResult;
import io.github.kaivian.klibrary.action.impl.AbstractAction;
import io.github.kaivian.klibrary.context.ExecutionContext;
import io.github.kaivian.klibrary.service.ServiceProvider;

/**
 * Broadcasts a sound to all online players.
 *
 * <p><b>YAML configuration:</b></p>
 * <pre>{@code
 * type: broadcast_sound
 * sound: BLOCK_NOTE_BLOCK_PLING
 * volume: 1.0
 * pitch: 1.0
 * }</pre>
 */
public class BroadcastSoundAction extends AbstractAction {

    private final String soundKey;
    private final float volume;
    private final float pitch;

    /**
     * Constructs a new {@code BroadcastSoundAction}.
     *
     * @param services the service provider
     * @param soundKey the sound key (case-insensitive)
     * @param volume   the volume level
     * @param pitch    the pitch level
     */
    public BroadcastSoundAction(ServiceProvider services, String soundKey, float volume, float pitch) {
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
        boolean played = services.getSoundService().broadcastSound(soundKey, volume, pitch);
        return played ? success() : failure("Invalid sound: " + soundKey);
    }

    /** @return the sound key */
    public String getSoundKey() { return soundKey; }

    /** @return the volume level */
    public float getVolume() { return volume; }

    /** @return the pitch level */
    public float getPitch() { return pitch; }
}
