/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.token;

/**
 * A wrapper around a {@link Token}, used within a {@link TokenSequence}s.
 * 
 * @author nmcwilliams
 */
public final class MemberToken {
    private final Token token;
    private final boolean optional;

    /** use constructor functions instead */
    private MemberToken(Token token, boolean optional) {
        this.token = token;
        this.optional = optional;
    }

    /**
     * Gets the {@link Token}.
     * 
     * @return The {@link Token}.
     */
    public Token token() {
        return token;
    }

    /**
     * Gets whether this member is optional.
     * 
     * @return True if this member is optional.
     */
    public boolean isOptional() {
        return optional;
    }

    /**
     * Constructs an optional {@link MemberToken}.
     * 
     * @param token
     *            The {@link Token}.
     * @return The {@link MemberToken} instance.
     */
    public static MemberToken optional(Token token) {
        return new MemberToken(token, true);
    }

    /**
     * Constructs an optional {@link MemberToken}.
     * 
     * @param token
     *            The {@link Token}.
     * @return The {@link MemberToken} instance.
     */
    public static MemberToken required(Token token) {
        return new MemberToken(token, false);
    }
}
