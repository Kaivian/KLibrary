package io.github.kaivian.klibrary.action.impl.actions;

import io.github.kaivian.klibrary.action.api.ActionResult;
import io.github.kaivian.klibrary.action.impl.AbstractAction;
import io.github.kaivian.klibrary.context.ExecutionContext;
import io.github.kaivian.klibrary.service.ServiceProvider;

/**
 * Sends an action bar message to the target player.
 *
 * <p>The message supports MiniMessage formatting and PlaceholderAPI.</p>
 *
 * <p><b>YAML configuration:</b></p>
 * <pre>{@code
 * type: actionbar
 * message: "<gradient:green:blue>+100 Coins!"
 * }</pre>
 */
public class ActionBarAction extends AbstractAction {

    private final String message;

    /**
     * Constructs a new {@code ActionBarAction}.
     *
     * @param services the service provider
     * @param message  the MiniMessage-formatted action bar text
     */
    public ActionBarAction(ServiceProvider services, String message) {
        super(services);
        this.message = message;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActionResult execute(ExecutionContext context) {
        return requirePlayer(context).map(player -> {
            services.getMessageService().sendActionBar(player, message, context.getPlaceholders());
            return success();
        }).orElse(failure("No player in context for ActionBarAction"));
    }

    /**
     * Returns the action bar message.
     *
     * @return the MiniMessage string
     */
    public String getMessage() {
        return message;
    }
}
