package io.github.kaivian.klibrary.action.impl;

import io.github.kaivian.klibrary.action.api.Action;
import io.github.kaivian.klibrary.action.api.ActionResult;
import io.github.kaivian.klibrary.context.ExecutionContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Executes a sequence of {@link Action}s within an execution context.
 *
 * <p>The pipeline runs actions in order and collects their results.
 * Two execution modes are supported:</p>
 * <ul>
 *   <li><b>Stop-on-failure</b> — execution halts at the first failure</li>
 *   <li><b>Continue-on-failure</b> — all actions run regardless of individual outcomes</li>
 * </ul>
 *
 * <p><b>Example:</b></p>
 * <pre>{@code
 * ActionPipeline pipeline = ActionPipeline.of(action1, action2, action3)
 *     .stopOnFailure(true);
 * List<ActionResult> results = pipeline.execute(context);
 * }</pre>
 *
 * @see Action
 * @see ActionResult
 */
public class ActionPipeline implements Action {

    private static final Logger LOGGER = Logger.getLogger(ActionPipeline.class.getName());

    private final List<Action> actions;
    private boolean stopOnFailure = false;

    /**
     * Constructs a pipeline with the given actions.
     *
     * @param actions the actions to execute in order
     */
    public ActionPipeline(List<Action> actions) {
        this.actions = new ArrayList<>(actions);
    }

    /**
     * Creates a pipeline from varargs actions.
     *
     * @param actions the actions to include
     * @return a new pipeline
     */
    public static ActionPipeline of(Action... actions) {
        return new ActionPipeline(List.of(actions));
    }

    /**
     * Creates a pipeline from a list of actions.
     *
     * @param actions the actions to include
     * @return a new pipeline
     */
    public static ActionPipeline of(List<Action> actions) {
        return new ActionPipeline(actions);
    }

    /**
     * Sets whether the pipeline should stop at the first failure.
     *
     * @param stopOnFailure {@code true} to halt on failure
     * @return this pipeline for chaining
     */
    public ActionPipeline stopOnFailure(boolean stopOnFailure) {
        this.stopOnFailure = stopOnFailure;
        return this;
    }

    /**
     * Executes the pipeline, returning the overall result.
     *
     * <p>Returns {@link ActionResult#success()} if all actions succeed.
     * If any action fails and {@code stopOnFailure} is enabled, returns
     * the first failure result. In continue mode, returns the last failure
     * if any occurred.</p>
     *
     * @param context the execution context
     * @return the aggregate result
     */
    @Override
    public ActionResult execute(ExecutionContext context) {
        List<ActionResult> results = executeAll(context);
        return results.stream()
                .filter(ActionResult::isFailure)
                .findFirst()
                .orElse(ActionResult.success());
    }

    /**
     * Executes all actions and returns their individual results.
     *
     * @param context the execution context
     * @return a list of results, one per action
     */
    public List<ActionResult> executeAll(ExecutionContext context) {
        if (actions.isEmpty()) {
            return Collections.emptyList();
        }

        List<ActionResult> results = new ArrayList<>(actions.size());

        for (Action action : actions) {
            try {
                ActionResult result = action.execute(context);
                results.add(result);

                if (result.isFailure()) {
                    LOGGER.fine("Action failed: " + ((ActionResult.Failure) result).reason());
                    if (stopOnFailure) {
                        break;
                    }
                }
            } catch (Exception e) {
                LOGGER.warning("Unexpected exception in action pipeline: " + e.getMessage());
                ActionResult errorResult = ActionResult.failure("Exception: " + e.getMessage());
                results.add(errorResult);
                if (stopOnFailure) {
                    break;
                }
            }
        }

        return Collections.unmodifiableList(results);
    }

    /**
     * Returns the actions in this pipeline.
     *
     * @return an unmodifiable list of actions
     */
    public List<Action> getActions() {
        return Collections.unmodifiableList(actions);
    }

    /**
     * Returns whether this pipeline stops on first failure.
     *
     * @return {@code true} if stop-on-failure is enabled
     */
    public boolean isStopOnFailure() {
        return stopOnFailure;
    }
}
