package io.github.kaivian.klibrary.requirement.impl;

import io.github.kaivian.klibrary.context.ExecutionContext;
import io.github.kaivian.klibrary.requirement.api.Requirement;
import io.github.kaivian.klibrary.requirement.api.RequirementResult;

import java.util.List;

/**
 * Composite requirement supporting AND/OR logic for grouping requirements.
 *
 * <p><b>AND mode:</b> All requirements must be met.</p>
 * <p><b>OR mode:</b> At least one requirement must be met.</p>
 *
 * <p><b>YAML configuration:</b></p>
 * <pre>{@code
 * requirements:
 *   mode: AND     # or OR
 *   checks:
 *     - type: permission
 *       permission: "example.vip"
 *     - type: has_money
 *       amount: 100
 * }</pre>
 */
public class RequirementGroup implements Requirement {

    /**
     * The grouping mode for requirement evaluation.
     */
    public enum GroupMode {
        /** All requirements must be met */
        AND,
        /** At least one requirement must be met */
        OR
    }

    private final List<Requirement> requirements;
    private final GroupMode mode;

    /**
     * Constructs a new {@code RequirementGroup}.
     *
     * @param requirements the requirements in this group
     * @param mode         the grouping mode (AND or OR)
     */
    public RequirementGroup(List<Requirement> requirements, GroupMode mode) {
        this.requirements = List.copyOf(requirements);
        this.mode = mode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RequirementResult evaluate(ExecutionContext context) {
        if (requirements.isEmpty()) {
            return RequirementResult.met();
        }

        return switch (mode) {
            case AND -> evaluateAnd(context);
            case OR -> evaluateOr(context);
        };
    }

    /**
     * Evaluates requirements in AND mode — all must pass.
     */
    private RequirementResult evaluateAnd(ExecutionContext context) {
        for (Requirement req : requirements) {
            RequirementResult result = req.evaluate(context);
            if (!result.isMet()) {
                return result;
            }
        }
        return RequirementResult.met();
    }

    /**
     * Evaluates requirements in OR mode — at least one must pass.
     */
    private RequirementResult evaluateOr(ExecutionContext context) {
        StringBuilder reasons = new StringBuilder();
        for (Requirement req : requirements) {
            RequirementResult result = req.evaluate(context);
            if (result.isMet()) {
                return RequirementResult.met();
            }
            result.getReason().ifPresent(r -> {
                if (!reasons.isEmpty()) reasons.append("; ");
                reasons.append(r);
            });
        }
        return RequirementResult.notMet("None of the OR conditions were met: " + reasons);
    }

    /** @return the requirements in this group */
    public List<Requirement> getRequirements() { return requirements; }

    /** @return the grouping mode */
    public GroupMode getMode() { return mode; }
}
