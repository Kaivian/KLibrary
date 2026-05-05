package io.github.kaivian.klibrary.action.impl.actions;

import io.github.kaivian.klibrary.action.api.ActionResult;
import io.github.kaivian.klibrary.action.impl.AbstractAction;
import io.github.kaivian.klibrary.context.ExecutionContext;
import io.github.kaivian.klibrary.service.ServiceProvider;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;

import java.util.Map;

/**
 * Executes a command as the console.
 *
 * <p>Placeholders in the command are resolved via PlaceholderAPI (if available)
 * and custom context placeholders before dispatch.</p>
 *
 * <p><b>YAML configuration:</b></p>
 * <pre>{@code
 * type: console_command
 * command: "give {player_name} diamond 1"
 * }</pre>
 */
public class ConsoleCommandAction extends AbstractAction {

    private final String command;

    /**
     * Constructs a new {@code ConsoleCommandAction}.
     *
     * @param services the service provider
     * @param command  the command to execute (without leading slash)
     */
    public ConsoleCommandAction(ServiceProvider services, String command) {
        super(services);
        this.command = command;
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

        // Apply PlaceholderAPI if available and player is present
        if (services.getDependencyService().hasPlaceholderAPI() && context.getPlayer().isPresent()) {
            try {
                resolved = PlaceholderAPI.setPlaceholders(context.getPlayer().get(), resolved);
            } catch (Exception e) {
                // Continue with unresolved placeholders
            }
        }

        try {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), resolved);
            return success();
        } catch (Exception e) {
            return failure("Console command failed: " + e.getMessage());
        }
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
