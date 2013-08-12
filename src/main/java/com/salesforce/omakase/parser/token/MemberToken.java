/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.token;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public final class MemberToken {
    private final Token token;
    private final boolean optional;

    private MemberToken(Token token, boolean optional) {
        this.token = token;
        this.optional = optional;
    }

    public Token token() {
        return token;
    }

    public boolean isOptional() {
        return optional;
    }

    public static MemberToken optional(Token token) {
        return new MemberToken(token, true);
    }

    public static MemberToken required(Token token) {
        return new MemberToken(token, false);
    }
}
