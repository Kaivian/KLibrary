package io.github.kaivian.klibrary.action.api;

import io.github.kaivian.klibrary.context.ExecutionContext;

/**
 * Represents an executable action within the KLibrary action engine.
 *
 * <p>Actions are the fundamental unit of work in the system. Each action
 * receives an {@link ExecutionContext} containing the player, sender,
 * placeholders, and metadata, and returns an {@link ActionResult} indicating
 * the outcome.</p>
 *
 * <p>Actions should <b>never throw exceptions</b> for expected failure
 * conditions. Instead, they return {@link ActionResult#failure(String)}
 * or {@link ActionResult#skipped(String)} to communicate the outcome
 * to the execution pipeline.</p>
 *
 * <p><b>Implementation example:</b></p>
 * <pre>{@code
 * public class MyCustomAction implements Action {
 *     @Override
 *     public ActionResult execute(ExecutionContext context) {
 *         return context.getPlayer()
 *             .map(player -> {
 *                 player.sendRichMessage("<green>Custom action!");
 *                 return ActionResult.success();
 *             })
 *             .orElse(ActionResult.failure("No player in context"));
 *     }
 * }
 * }</pre>
 *
 * @see ActionResult
 * @see ExecutionContext
 * @see ActionFactory
 * @see ActionRegistry
 */
@FunctionalInterface
public interface Action {

    /**
     * Executes this action within the given context.
     *
     * @param context the execution context containing player, sender, and metadata
     * @return the result of the execution; never {@code null}
     */
    ActionResult execute(ExecutionContext context);
}
