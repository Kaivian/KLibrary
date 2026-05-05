package io.github.kaivian.klibrary.action.impl.actions;

import io.github.kaivian.klibrary.action.api.ActionResult;
import io.github.kaivian.klibrary.action.impl.AbstractAction;
import io.github.kaivian.klibrary.context.ExecutionContext;
import io.github.kaivian.klibrary.service.ServiceProvider;

/**
 * Sends a MiniMessage-formatted message to the target player.
 *
 * <p>Messages are parsed through the full resolution pipeline:
 * custom placeholders → PlaceholderAPI → MiniMessage.</p>
 *
 * <p><b>YAML configuration:</b></p>
 * <pre>{@code
 * type: message
 * message: "<green>Hello, {player_name}!"
 * }</pre>
 */
public class MessageAction extends AbstractAction {

    private final String message;

    /**
     * Constructs a new {@code MessageAction}.
     *
     * @param services the service provider
     * @param message  the MiniMessage-formatted message
     */
    public MessageAction(ServiceProvider services, String message) {
        super(services);
        this.message = message;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActionResult execute(ExecutionContext context) {
        return requirePlayer(context).map(player -> {
            services.getMessageService().send(player, message, context.getPlaceholders());
            return success();
        }).orElse(failure("No player in context for MessageAction"));
    }

    /**
     * Returns the message template.
     *
     * @return the MiniMessage string
     */
    public String getMessage() {
        return message;
    }
}
