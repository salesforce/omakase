/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.token;

import com.google.common.base.CharMatcher;

/**
 * An object wrapper over {@link CharMatcher}s.
 *
 * @author nmcwilliams
 */
public interface Token {
    /**
     * Gets the wrapped {@link CharMatcher}.
     *
     * @return the wrapped {@link CharMatcher}.
     */
    CharMatcher matcher();

    /**
     * Whether the given character matchers this {@link Token}.
     *
     * @param c
     *     Compare to this character.
     *
     * @return true if this token matches the given character.
     */
    boolean matches(Character c);

    /**
     * Gets a description of the token. This is used in error-reporting to indicate what was expected.
     *
     * @return The description.
     */
    String description();

    /**
     * A Utility to create a new {@link CompoundToken}, combining this {@link Token} with another one. This is useful for OR
     * character comparisons.
     *
     * @param other
     *     The other {@link Token}.
     *
     * @return A {@link CompoundToken} instance.
     */
    Token or(Token other);
}
