/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.token;

import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.parser.Stream;

/**
 * For Enums that are members of {@link Syntax} units and are associated with a specific {@link Token} to be parsed
 * from a {@link Stream}. In other words, by adding this interface to an Enum it allows it to be easily parsed to the
 * correct Enum constant using {@link Stream#optionalFromEnum(Class)}.
 * 
 * @param <E>
 *            The Enum
 * @author nmcwilliams
 */
public interface TokenEnum<E extends Enum<E>> {
    /**
     * Gets the token represented by the enum constant.
     * 
     * @return The token represented by the enum constant.
     */
    Token token();
}
