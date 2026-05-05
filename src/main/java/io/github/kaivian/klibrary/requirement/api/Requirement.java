package io.github.kaivian.klibrary.requirement.api;

import io.github.kaivian.klibrary.context.ExecutionContext;

/**
 * Represents a condition that must be evaluated before actions can execute.
 *
 * <p>Requirements are the gating mechanism in the action/requirement engine.
 * Each requirement receives an {@link ExecutionContext} and returns a
 * {@link RequirementResult} indicating whether the condition is met.</p>
 *
 * <p>Requirements should <b>never throw exceptions</b> for expected conditions.
 * Instead, they return a result with {@code met = false} and an explanatory
 * reason message.</p>
 *
 * <p><b>Implementation example:</b></p>
 * <pre>{@code
 * public class MyRequirement implements Requirement {
 *     @Override
 *     public RequirementResult evaluate(ExecutionContext context) {
 *         return context.getPlayer()
 *             .map(player -> player.getLevel() >= 10
 *                 ? RequirementResult.met()
 *                 : RequirementResult.notMet("Player level too low"))
 *             .orElse(RequirementResult.notMet("No player in context"));
 *     }
 * }
 * }</pre>
 *
 * @see RequirementResult
 * @see ExecutionContext
 * @see RequirementFactory
 * @see RequirementRegistry
 */
@FunctionalInterface
public interface Requirement {

    /**
     * Evaluates this requirement within the given context.
     *
     * @param context the execution context containing player, sender, and metadata
     * @return the evaluation result; never {@code null}
     */
    RequirementResult evaluate(ExecutionContext context);
}
