package io.github.kaivian.klibrary.action.impl;

import io.github.kaivian.klibrary.action.api.Action;
import io.github.kaivian.klibrary.action.api.ActionResult;
import io.github.kaivian.klibrary.context.ExecutionContext;
import io.github.kaivian.klibrary.service.ServiceProvider;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * Abstract base class for action implementations.
 *
 * <p>Provides common utilities and service access for all built-in actions.
 * Extending this class is optional — actions only need to implement the
 * {@link Action} interface — but it reduces boilerplate for common patterns.</p>
 *
 * <p><b>Provided utilities:</b></p>
 * <ul>
 *   <li>{@link #success()} / {@link #failure(String)} / {@link #skipped(String)} — result factory methods</li>
 *   <li>{@link #requirePlayer(ExecutionContext)} — extracts the player or returns a failure result</li>
 *   <li>{@link #services} — direct access to the service provider</li>
 * </ul>
 */
public abstract class AbstractAction implements Action {

    /**
     * The service provider for accessing services (messaging, economy, etc.).
     */
    protected final ServiceProvider services;

    /**
     * Constructs a new {@code AbstractAction} with the given service provider.
     *
     * @param services the service provider
     */
    protected AbstractAction(ServiceProvider services) {
        this.services = services;
    }

    /**
     * Creates a successful result.
     *
     * @return {@link ActionResult#success()}
     */
    protected ActionResult success() {
        return ActionResult.success();
    }

    /**
     * Creates a failure result.
     *
     * @param reason the failure reason
     * @return {@link ActionResult#failure(String)}
     */
    protected ActionResult failure(String reason) {
        return ActionResult.failure(reason);
    }

    /**
     * Creates a skipped result.
     *
     * @param reason the skip reason
     * @return {@link ActionResult#skipped(String)}
     */
    protected ActionResult skipped(String reason) {
        return ActionResult.skipped(reason);
    }

    /**
     * Extracts the player from the context, or returns empty if not present.
     *
     * @param context the execution context
     * @return the player, if present
     */
    protected Optional<Player> requirePlayer(ExecutionContext context) {
        return context.getPlayer();
    }
}
