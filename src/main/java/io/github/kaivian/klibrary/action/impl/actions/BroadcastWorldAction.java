package io.github.kaivian.klibrary.action.impl.actions;

import io.github.kaivian.klibrary.action.api.ActionResult;
import io.github.kaivian.klibrary.action.impl.AbstractAction;
import io.github.kaivian.klibrary.context.ExecutionContext;
import io.github.kaivian.klibrary.service.ServiceProvider;
import org.bukkit.entity.Player;

/**
 * Broadcasts a MiniMessage-formatted message to all players in the same world
 * as the target player.
 *
 * <p><b>YAML configuration:</b></p>
 * <pre>{@code
 * type: broadcast_world
 * message: "<yellow>[World] <white>An event is starting!"
 * }</pre>
 */
public class BroadcastWorldAction extends AbstractAction {

    private final String message;

    /**
     * Constructs a new {@code BroadcastWorldAction}.
     *
     * @param services the service provider
     * @param message  the MiniMessage-formatted message to broadcast
     */
    public BroadcastWorldAction(ServiceProvider services, String message) {
        super(services);
        this.message = message;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActionResult execute(ExecutionContext context) {
        return requirePlayer(context).map(target -> {
            for (Player player : target.getWorld().getPlayers()) {
                services.getMessageService().send(player, message, context.getPlaceholders());
            }
            return success();
        }).orElse(failure("No player in context for BroadcastWorldAction"));
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
