package io.github.kaivian.klibrary.action.impl.actions;

import io.github.kaivian.klibrary.action.api.ActionResult;
import io.github.kaivian.klibrary.action.impl.AbstractAction;
import io.github.kaivian.klibrary.context.ExecutionContext;
import io.github.kaivian.klibrary.service.ServiceProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Broadcasts a MiniMessage-formatted message to all online players.
 *
 * <p>The triggering player (if present) is used as the PlaceholderAPI context
 * for resolving player-specific placeholders in the message.</p>
 *
 * <p><b>YAML configuration:</b></p>
 * <pre>{@code
 * type: broadcast
 * message: "<gold>[Server] <white>Welcome {player_name}!"
 * }</pre>
 */
public class BroadcastAction extends AbstractAction {

    private final String message;

    /**
     * Constructs a new {@code BroadcastAction}.
     *
     * @param services the service provider
     * @param message  the MiniMessage-formatted message to broadcast
     */
    public BroadcastAction(ServiceProvider services, String message) {
        super(services);
        this.message = message;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActionResult execute(ExecutionContext context) {
        Player source = context.getPlayer().orElse(null);
        for (Player player : Bukkit.getOnlinePlayers()) {
            services.getMessageService().send(player, message, context.getPlaceholders());
        }
        return success();
    }

    /**
     * Returns the broadcast message template.
     *
     * @return the MiniMessage string
     */
    public String getMessage() {
        return message;
    }
}
