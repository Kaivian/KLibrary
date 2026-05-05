package io.github.kaivian.klibrary.action.impl.actions;

import io.github.kaivian.klibrary.action.api.Action;
import io.github.kaivian.klibrary.action.api.ActionResult;
import io.github.kaivian.klibrary.action.impl.AbstractAction;
import io.github.kaivian.klibrary.action.impl.ActionPipeline;
import io.github.kaivian.klibrary.context.ExecutionContext;
import io.github.kaivian.klibrary.requirement.api.Requirement;
import io.github.kaivian.klibrary.requirement.api.RequirementResult;
import io.github.kaivian.klibrary.service.ServiceProvider;

import java.util.Collections;
import java.util.List;

/**
 * Executes actions conditionally based on requirement evaluation.
 *
 * <p>This action evaluates a list of requirements. If all requirements are met,
 * the "then" actions execute. Otherwise, the "else" actions execute (if any).</p>
 *
 * <p><b>YAML configuration:</b></p>
 * <pre>{@code
 * type: conditional
 * requirements:
 *   - type: permission
 *     permission: "example.vip"
 * then:
 *   - type: message
 *     message: "<green>VIP access granted!"
 * else:
 *   - type: message
 *     message: "<red>You need VIP access."
 * }</pre>
 */
public class ConditionalAction extends AbstractAction {

    private final List<Requirement> requirements;
    private final List<Action> thenActions;
    private final List<Action> elseActions;

    /**
     * Constructs a new {@code ConditionalAction}.
     *
     * @param services     the service provider
     * @param requirements the conditions to evaluate
     * @param thenActions  the actions to execute if conditions are met
     * @param elseActions  the actions to execute if conditions are not met
     */
    public ConditionalAction(ServiceProvider services,
                             List<Requirement> requirements,
                             List<Action> thenActions,
                             List<Action> elseActions) {
        super(services);
        this.requirements = List.copyOf(requirements);
        this.thenActions = List.copyOf(thenActions);
        this.elseActions = elseActions != null ? List.copyOf(elseActions) : Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActionResult execute(ExecutionContext context) {
        boolean allMet = requirements.stream()
                .map(req -> req.evaluate(context))
                .allMatch(RequirementResult::isMet);

        List<Action> actionsToRun = allMet ? thenActions : elseActions;

        if (actionsToRun.isEmpty()) {
            return allMet ? success() : skipped("Conditions not met and no else actions defined");
        }

        return ActionPipeline.of(actionsToRun).execute(context);
    }

    /** @return the requirements */
    public List<Requirement> getRequirements() { return requirements; }

    /** @return the then-branch actions */
    public List<Action> getThenActions() { return thenActions; }

    /** @return the else-branch actions */
    public List<Action> getElseActions() { return elseActions; }
}
