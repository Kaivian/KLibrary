package io.github.kaivian.klibrary.requirement.impl.requirements;

import io.github.kaivian.klibrary.context.ExecutionContext;
import io.github.kaivian.klibrary.requirement.api.RequirementResult;
import io.github.kaivian.klibrary.requirement.impl.AbstractRequirement;
import io.github.kaivian.klibrary.service.ServiceProvider;
import me.clip.placeholderapi.PlaceholderAPI;

import java.util.logging.Logger;

/**
 * Evaluates a dynamic condition via PlaceholderAPI.
 *
 * <p>Resolves a placeholder for the player and compares it against an
 * expected value using a configurable operator.</p>
 *
 * <p>If PlaceholderAPI is not installed, this requirement automatically passes.</p>
 *
 * <p><b>YAML:</b></p>
 * <pre>{@code
 * type: placeholder
 * placeholder: "%player_health%"
 * operator: ">="      # =, !=, >, <, >=, <=
 * value: "10"
 * }</pre>
 */
public class PlaceholderRequirement extends AbstractRequirement {

    private static final Logger LOGGER = Logger.getLogger(PlaceholderRequirement.class.getName());

    /**
     * Supported comparison operators.
     */
    public enum Operator {
        /** Equal */
        EQUALS("="),
        /** Not equal */
        NOT_EQUALS("!="),
        /** Greater than */
        GREATER(">"),
        /** Less than */
        LESS("<"),
        /** Greater than or equal */
        GREATER_EQUALS(">="),
        /** Less than or equal */
        LESS_EQUALS("<=");

        private final String symbol;

        Operator(String symbol) {
            this.symbol = symbol;
        }

        /**
         * Parses an operator from its symbol string.
         *
         * @param symbol the operator symbol
         * @return the parsed operator
         * @throws IllegalArgumentException if the symbol is invalid
         */
        public static Operator fromSymbol(String symbol) {
            for (Operator op : values()) {
                if (op.symbol.equals(symbol)) return op;
            }
            throw new IllegalArgumentException("Invalid operator: " + symbol);
        }

        /**
         * Returns the symbol representation.
         *
         * @return the operator symbol
         */
        public String getSymbol() { return symbol; }
    }

    private final String placeholder;
    private final Operator operator;
    private final String expectedValue;

    /**
     * Constructs a new {@code PlaceholderRequirement}.
     *
     * @param services      the service provider
     * @param placeholder   the PlaceholderAPI placeholder to evaluate
     * @param operator      the comparison operator
     * @param expectedValue the expected value to compare against
     */
    public PlaceholderRequirement(ServiceProvider services, String placeholder,
                                  Operator operator, String expectedValue) {
        super(services);
        this.placeholder = placeholder;
        this.operator = operator;
        this.expectedValue = expectedValue;
    }

    /** {@inheritDoc} */
    @Override
    public RequirementResult evaluate(ExecutionContext context) {
        if (!services.getDependencyService().hasPlaceholderAPI()) {
            return met(); // Fail-open when PAPI missing
        }

        return context.getPlayer().map(player -> {
            try {
                String resolved = PlaceholderAPI.setPlaceholders(player, placeholder);
                boolean result = compare(resolved, expectedValue, operator);
                return result
                        ? met()
                        : notMet("Placeholder check failed: " + placeholder
                        + " (" + resolved + ") " + operator.getSymbol()
                        + " " + expectedValue);
            } catch (Exception e) {
                LOGGER.warning("PlaceholderRequirement evaluation failed: " + e.getMessage());
                return met(); // Fail-open on error
            }
        }).orElse(notMet("No player in context"));
    }

    /**
     * Compares two values using the specified operator.
     * Attempts numeric comparison first, falls back to string comparison.
     */
    private boolean compare(String actual, String expected, Operator op) {
        // Try numeric comparison
        try {
            double actualNum = Double.parseDouble(actual);
            double expectedNum = Double.parseDouble(expected);
            return switch (op) {
                case EQUALS -> actualNum == expectedNum;
                case NOT_EQUALS -> actualNum != expectedNum;
                case GREATER -> actualNum > expectedNum;
                case LESS -> actualNum < expectedNum;
                case GREATER_EQUALS -> actualNum >= expectedNum;
                case LESS_EQUALS -> actualNum <= expectedNum;
            };
        } catch (NumberFormatException ignored) {
        }

        // Fall back to string comparison
        return switch (op) {
            case EQUALS -> actual.equals(expected);
            case NOT_EQUALS -> !actual.equals(expected);
            default -> false; // Relational operators not supported for strings
        };
    }

    /** @return the placeholder expression */
    public String getPlaceholder() { return placeholder; }

    /** @return the comparison operator */
    public Operator getOperator() { return operator; }

    /** @return the expected value */
    public String getExpectedValue() { return expectedValue; }
}
