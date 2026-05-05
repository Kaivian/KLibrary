package io.github.kaivian.klibrary.action.impl.actions;

import io.github.kaivian.klibrary.action.api.ActionResult;
import io.github.kaivian.klibrary.action.impl.AbstractAction;
import io.github.kaivian.klibrary.context.ExecutionContext;
import io.github.kaivian.klibrary.service.ServiceProvider;

/**
 * Makes the target player send a chat message.
 *
 * <p><b>YAML configuration:</b></p>
 * <pre>{@code
 * type: chat
 * message: "Hello everyone!"
 * }</pre>
 */
public class ChatAction extends AbstractAction {

    private final String message;

    /**
     * Constructs a new {@code ChatAction}.
     *
     * @param services the service provider
     * @param message  the chat message to send
     */
    public ChatAction(ServiceProvider services, String message) {
        super(services);
        this.message = message;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActionResult execute(ExecutionContext context) {
        return requirePlayer(context).map(player -> {
            player.chat(message);
            return success();
        }).orElse(failure("No player in context for ChatAction"));
    }

    /**
     * Returns the chat message.
     *
     * @return the message string
     */
    public String getMessage() {
        return message;
    }
}
