package io.github.kaivian.klibrary.action.api;

/**
 * Represents the outcome of an {@link Action} execution.
 *
 * <p>This sealed interface provides three possible outcomes:</p>
 * <ul>
 *   <li>{@link #success()} — Action completed successfully</li>
 *   <li>{@link #failure(String)} — Action failed with a reason</li>
 *   <li>{@link #skipped(String)} — Action was intentionally skipped</li>
 * </ul>
 *
 * <p>Using a result type instead of exceptions allows the execution pipeline
 * to react to outcomes without catching exceptions, making the control flow
 * explicit and predictable.</p>
 *
 * <p><b>Pattern matching example:</b></p>
 * <pre>{@code
 * ActionResult result = action.execute(context);
 * switch (result) {
 *     case ActionResult.Success s -> logger.info("Action succeeded");
 *     case ActionResult.Failure f -> logger.warning("Failed: " + f.reason());
 *     case ActionResult.Skipped s -> logger.info("Skipped: " + s.reason());
 * }
 * }</pre>
 */
public sealed interface ActionResult {

    /**
     * Creates a successful result.
     *
     * @return a {@link Success} instance
     */
    static ActionResult success() {
        return Success.INSTANCE;
    }

    /**
     * Creates a failure result with the given reason.
     *
     * @param reason a human-readable explanation of why the action failed
     * @return a {@link Failure} instance
     */
    static ActionResult failure(String reason) {
        return new Failure(reason);
    }

    /**
     * Creates a skipped result with the given reason.
     *
     * @param reason a human-readable explanation of why the action was skipped
     * @return a {@link Skipped} instance
     */
    static ActionResult skipped(String reason) {
        return new Skipped(reason);
    }

    /**
     * Checks whether this result represents a successful execution.
     *
     * @return {@code true} if this is a {@link Success}
     */
    default boolean isSuccess() {
        return this instanceof Success;
    }

    /**
     * Checks whether this result represents a failure.
     *
     * @return {@code true} if this is a {@link Failure}
     */
    default boolean isFailure() {
        return this instanceof Failure;
    }

    /**
     * Checks whether this result represents a skipped action.
     *
     * @return {@code true} if this is a {@link Skipped}
     */
    default boolean isSkipped() {
        return this instanceof Skipped;
    }

    /**
     * Represents a successful action execution.
     */
    record Success() implements ActionResult {
        private static final Success INSTANCE = new Success();
    }

    /**
     * Represents a failed action execution.
     *
     * @param reason a human-readable explanation of the failure
     */
    record Failure(String reason) implements ActionResult {
    }

    /**
     * Represents an intentionally skipped action execution.
     *
     * <p>This is used when an action determines it should not run
     * (e.g., missing dependency, condition not met) but this is not
     * considered an error.</p>
     *
     * @param reason a human-readable explanation of why it was skipped
     */
    record Skipped(String reason) implements ActionResult {
    }
}
