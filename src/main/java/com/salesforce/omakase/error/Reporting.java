/**
 * ADD LICENSE
 */
package com.salesforce.omakase.error;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.salesforce.omakase.plugin.validator.SyntaxValidator;

/**
 * Specifies {@link ErrorLevel} overrides for specific error ids.
 * 
 * <p>
 * Each error id is a string, as specified by the {@link SyntaxValidator} reporting the error, usually in the form of
 * "x.y", e.g., "omakase.pseudoElementPosition".
 * 
 * <p>
 * Specifying an override makes it easier for some {@link SyntaxValidator}s to be ignored for say, known noncompliant or
 * legacy CSS files.
 * 
 * @author nmcwilliams
 */
public class Reporting {
    private final Map<String, ErrorLevel> overrides = Maps.newHashMap();

    /** use the {@link #customized()} constructor method instead. */
    private Reporting() {}

    /**
     * Specifies that the errors with the given id should be reporting at the given {@link ErrorLevel}.
     * 
     * @param id
     *            The error id.
     * @param level
     *            Report errors with the given id at this level.
     * @return this, for chaining.
     */
    public Reporting on(String id, ErrorLevel level) {
        checkNotNull(id, "id cannot be null");
        checkNotNull(level, "level cannot be null");
        overrides.put(id, level);
        return this;
    }

    /**
     * Gets the override for the given id.
     * 
     * @param id
     *            The error id.
     * @return The override, or {@link Optional#absent()} if no override is specified.
     */
    public Optional<ErrorLevel> getErrorLevelOverride(String id) {
        return Optional.fromNullable(overrides.get(id));
    }

    /**
     * Creates a new {@link Reporting} instance. This allows you to override the error level of various validation and
     * parsing errors.
     * 
     * @return The new {@link Reporting} instance.
     */
    public static Reporting customized() {
        return new Reporting();
    }
}
