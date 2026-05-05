package io.github.kaivian.klibrary.requirement.impl;

import io.github.kaivian.klibrary.action.api.Action;
import io.github.kaivian.klibrary.action.impl.ActionPipeline;
import io.github.kaivian.klibrary.context.ExecutionContext;
import io.github.kaivian.klibrary.requirement.api.Requirement;
import io.github.kaivian.klibrary.requirement.api.RequirementResult;
import io.github.kaivian.klibrary.service.ServiceProvider;

import java.util.Collections;
import java.util.List;

/**
 * Abstract base class for requirement implementations.
 *
 * <p>Provides common utilities, service access, and support for
 * accept/deny action handlers. When a requirement is evaluated,
 * the appropriate handler actions are automatically executed.</p>
 *
 * <p><b>Features:</b></p>
 * <ul>
 *   <li>{@link #met()} / {@link #notMet(String)} — result factory methods</li>
 *   <li>Accept/deny action handlers — executed after evaluation</li>
 *   <li>Service provider access for dependency-gated requirements</li>
 * </ul>
 */
public abstract class AbstractRequirement implements Requirement {

    /**
     * The service provider for accessing services.
     */
    protected final ServiceProvider services;

    private List<Action> acceptActions = Collections.emptyList();
    private List<Action> denyActions = Collections.emptyList();

    /**
     * Constructs a new {@code AbstractRequirement}.
     *
     * @param services the service provider
     */
    protected AbstractRequirement(ServiceProvider services) {
        this.services = services;
    }

    /**
     * Creates a result indicating the requirement is met.
     *
     * @return a met result
     */
    protected RequirementResult met() {
        return RequirementResult.met();
    }

    /**
     * Creates a result indicating the requirement is not met.
     *
     * @param reason explanation of why the requirement is not met
     * @return a not-met result
     */
    protected RequirementResult notMet(String reason) {
        return RequirementResult.notMet(reason);
    }

    /**
     * Sets the actions to execute when the requirement is met.
     *
     * @param acceptActions the accept handler actions
     * @return this requirement for chaining
     */
    public AbstractRequirement setAcceptActions(List<Action> acceptActions) {
        this.acceptActions = acceptActions != null ? List.copyOf(acceptActions) : Collections.emptyList();
        return this;
    }

    /**
     * Sets the actions to execute when the requirement is not met.
     *
     * @param denyActions the deny handler actions
     * @return this requirement for chaining
     */
    public AbstractRequirement setDenyActions(List<Action> denyActions) {
        this.denyActions = denyActions != null ? List.copyOf(denyActions) : Collections.emptyList();
        return this;
    }

    /**
     * Returns the accept handler actions.
     *
     * @return an unmodifiable list of accept actions
     */
    public List<Action> getAcceptActions() {
        return acceptActions;
    }

    /**
     * Returns the deny handler actions.
     *
     * @return an unmodifiable list of deny actions
     */
    public List<Action> getDenyActions() {
        return denyActions;
    }

    /**
     * Evaluates the requirement and executes the appropriate handler actions.
     *
     * <p>This method calls {@link #evaluate(ExecutionContext)} and then
     * runs the accept or deny actions based on the result.</p>
     *
     * @param context the execution context
     * @return the evaluation result
     */
    public RequirementResult evaluateAndHandle(ExecutionContext context) {
        RequirementResult result = evaluate(context);

        if (result.isMet() && !acceptActions.isEmpty()) {
            ActionPipeline.of(acceptActions).execute(context);
        } else if (!result.isMet() && !denyActions.isEmpty()) {
            ActionPipeline.of(denyActions).execute(context);
        }

        return result;
    }
}
