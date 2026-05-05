package io.github.kaivian.klibrary.action.impl.actions;

import io.github.kaivian.klibrary.action.api.ActionResult;
import io.github.kaivian.klibrary.action.impl.AbstractAction;
import io.github.kaivian.klibrary.context.ExecutionContext;
import io.github.kaivian.klibrary.service.ServiceProvider;

/**
 * Makes the target player execute a command.
 *
 * <p><b>YAML configuration:</b></p>
 * <pre>{@code
 * type: player_command
 * command: "spawn"
 * }</pre>
 */
public class PlayerCommandAction extends AbstractAction {

    private final String command;

    /**
     * Constructs a new {@code PlayerCommandAction}.
     *
     * @param services the service provider
     * @param command  the command to execute (without leading slash)
     */
    public PlayerCommandAction(ServiceProvider services, String command) {
        super(services);
        this.command = command;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActionResult execute(ExecutionContext context) {
        return requirePlayer(context).map(player -> {
            try {
                player.performCommand(command);
                return success();
            } catch (Exception e) {
                return failure("Player command failed: " + e.getMessage());
            }
        }).orElse(failure("No player in context for PlayerCommandAction"));
    }

    /**
     * Returns the command template.
     *
     * @return the command string
     */
    public String getCommand() {
        return command;
    }
}
