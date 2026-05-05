package io.github.kaivian.klibrary.requirement.api;

import java.util.Optional;

/**
 * Represents the outcome of a {@link Requirement} evaluation.
 *
 * <p>Each result contains:</p>
 * <ul>
 *   <li>{@code success} — whether the requirement condition was satisfied</li>
 *   <li>{@code reason} — an optional explanation (useful for deny messages)</li>
 * </ul>
 *
 * <p><b>Usage example:</b></p>
 * <pre>{@code
 * RequirementResult result = requirement.evaluate(context);
 * if (!result.isMet()) {
 *     result.getReason().ifPresent(reason ->
 *         player.sendRichMessage("<red>" + reason));
 * }
 * }</pre>
 *
 * @param success whether the requirement is satisfied
 * @param reason  an optional human-readable explanation
 */
public record RequirementResult(boolean success, String reason) {

    /**
     * Creates a result indicating the requirement is met (no reason needed).
     *
     * @return a met result
     */
    public static RequirementResult met() {
        return new RequirementResult(true, null);
    }

    /**
     * Creates a result indicating the requirement is not met.
     *
     * @param reason a human-readable explanation of why it was not met
     * @return a not-met result with reason
     */
    public static RequirementResult notMet(String reason) {
        return new RequirementResult(false, reason);
    }

    /**
     * Checks whether the requirement is met.
     *
     * @return {@code true} if met
     */
    public boolean isMet() {
        return success;
    }

    /**
     * Returns the reason for the result, if present.
     *
     * @return an {@link Optional} containing the reason, or empty
     */
    public Optional<String> getReason() {
        return Optional.ofNullable(reason);
    }
}
