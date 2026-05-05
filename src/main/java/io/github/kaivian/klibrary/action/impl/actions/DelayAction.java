package io.github.kaivian.klibrary.action.impl.actions;

import io.github.kaivian.klibrary.action.api.Action;
import io.github.kaivian.klibrary.action.api.ActionResult;
import io.github.kaivian.klibrary.action.impl.AbstractAction;
import io.github.kaivian.klibrary.action.impl.ActionPipeline;
import io.github.kaivian.klibrary.context.ExecutionContext;
import io.github.kaivian.klibrary.service.ServiceProvider;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.logging.Logger;

/**
 * Wraps child actions and executes them after a configurable delay.
 *
 * <p>Uses Bukkit's {@code BukkitScheduler.runTaskLater()} for tick-based,
 * main-thread-safe delayed execution. This ensures compatibility with
 * the Bukkit API which is not thread-safe.</p>
 *
 * <p><b>YAML configuration:</b></p>
 * <pre>{@code
 * type: delay
 * ticks: 40    # 2 seconds (20 ticks = 1 second)
 * actions:
 *   - type: message
 *     message: "<yellow>Delayed message!"
 *   - type: sound
 *     sound: ENTITY_EXPERIENCE_ORB_PICKUP
 * }</pre>
 */
public class DelayAction extends AbstractAction {

    private static final Logger LOGGER = Logger.getLogger(DelayAction.class.getName());

    private final long delayTicks;
    private final List<Action> childActions;

    /**
     * Constructs a new {@code DelayAction}.
     *
     * @param services     the service provider
     * @param delayTicks   the delay in ticks before executing child actions
     * @param childActions the actions to execute after the delay
     */
    public DelayAction(ServiceProvider services, long delayTicks, List<Action> childActions) {
        super(services);
        if (delayTicks < 0) {
            throw new IllegalArgumentException("Delay ticks cannot be negative");
        }
        this.delayTicks = delayTicks;
        this.childActions = List.copyOf(childActions);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Schedules the child actions for delayed execution. Returns
     * {@link ActionResult#success()} immediately — the delayed actions
     * run asynchronously on the main thread after the specified ticks.</p>
     */
    @Override
    public ActionResult execute(ExecutionContext context) {
        Plugin plugin = context.getPlugin().orElse(services.getPlugin());

        try {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                ActionPipeline pipeline = ActionPipeline.of(childActions);
                pipeline.execute(context);
            }, delayTicks);
            return success();
        } catch (Exception e) {
            LOGGER.warning("Failed to schedule delayed action: " + e.getMessage());
            return failure("Failed to schedule delay: " + e.getMessage());
        }
    }

    /** @return the delay in ticks */
    public long getDelayTicks() { return delayTicks; }

    /** @return the child actions */
    public List<Action> getChildActions() { return childActions; }
}
