package io.github.kaivian.klibrary.requirement.impl.requirements;

import io.github.kaivian.klibrary.context.ExecutionContext;
import io.github.kaivian.klibrary.requirement.api.RequirementResult;
import io.github.kaivian.klibrary.requirement.impl.AbstractRequirement;
import io.github.kaivian.klibrary.service.ServiceProvider;
import org.bukkit.GameMode;

import java.util.Locale;

/**
 * Checks whether a player is in a specific game mode.
 *
 * <p><b>YAML:</b></p>
 * <pre>{@code
 * type: gamemode
 * gamemode: SURVIVAL    # SURVIVAL, CREATIVE, ADVENTURE, SPECTATOR
 * }</pre>
 */
public class GamemodeRequirement extends AbstractRequirement {

    private final GameMode requiredMode;

    /**
     * Constructs a new {@code GamemodeRequirement}.
     *
     * @param services     the service provider
     * @param requiredMode the required game mode
     */
    public GamemodeRequirement(ServiceProvider services, GameMode requiredMode) {
        super(services);
        this.requiredMode = requiredMode;
    }

    /**
     * Constructs a new {@code GamemodeRequirement} from a string name.
     *
     * @param services     the service provider
     * @param gamemodeName the game mode name (case-insensitive)
     * @throws IllegalArgumentException if the game mode name is invalid
     */
    public GamemodeRequirement(ServiceProvider services, String gamemodeName) {
        super(services);
        try {
            this.requiredMode = GameMode.valueOf(gamemodeName.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid gamemode: " + gamemodeName);
        }
    }

    /** {@inheritDoc} */
    @Override
    public RequirementResult evaluate(ExecutionContext context) {
        return context.getPlayer().map(player ->
                player.getGameMode() == requiredMode
                        ? met()
                        : notMet("Player is in " + player.getGameMode()
                        + " but required: " + requiredMode)
        ).orElse(notMet("No player in context"));
    }

    /** @return the required game mode */
    public GameMode getRequiredMode() { return requiredMode; }
}
