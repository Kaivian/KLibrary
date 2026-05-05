package io.github.kaivian.klibrary.action.impl.actions;

import io.github.kaivian.klibrary.action.api.ActionResult;
import io.github.kaivian.klibrary.action.impl.AbstractAction;
import io.github.kaivian.klibrary.context.ExecutionContext;
import io.github.kaivian.klibrary.service.ServiceProvider;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;

import java.util.Map;

/**
 * Unified command action supporting both player and console execution.
 *
 * <p>The executor mode is configurable, allowing the same action type to
 * dispatch commands as either the player or the console.</p>
 *
 * <p><b>YAML configuration:</b></p>
 * <pre>{@code
 * type: command
 * command: "give {player_name} diamond 1"
 * executor: CONSOLE    # or PLAYER
 * }</pre>
 */
public class CommandAction extends AbstractAction {

    /**
     * The command executor mode.
     */
    public enum Executor {
        /** Execute as the player */
        PLAYER,
        /** Execute as the console */
        CONSOLE
    }

    private final String command;
    private final Executor executor;

    /**
     * Constructs a new {@code CommandAction}.
     *
     * @param services the service provider
     * @param command  the command to execute (without leading slash)
     * @param executor the executor mode (PLAYER or CONSOLE)
     */
    public CommandAction(ServiceProvider services, String command, Executor executor) {
        super(services);
        this.command = command;
        this.executor = executor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActionResult execute(ExecutionContext context) {
        String resolved = command;

        // Apply custom placeholders
        for (Map.Entry<String, String> entry : context.getPlaceholders().entrySet()) {
            resolved = resolved.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        // Apply PlaceholderAPI if available
        if (services.getDependencyService().hasPlaceholderAPI() && context.getPlayer().isPresent()) {
            try {
                resolved = PlaceholderAPI.setPlaceholders(context.getPlayer().get(), resolved);
            } catch (Exception ignored) {
            }
        }

        try {
            return switch (executor) {
                case CONSOLE -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), resolved);
                    yield success();
                }
                case PLAYER -> {
                    String finalResolved = resolved;
                    yield requirePlayer(context).map(player -> {
                        player.performCommand(finalResolved);
                        return success();
                    }).orElse(failure("No player in context for CommandAction (PLAYER mode)"));
                }
            };
        } catch (Exception e) {
            return failure("Command execution failed: " + e.getMessage());
        }
    }

    /** @return the command template */
    public String getCommand() { return command; }

    /** @return the executor mode */
    public Executor getExecutor() { return executor; }
}
