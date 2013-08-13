/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.token;

import java.util.List;

/**
 * An explicit sequence of tokens comprised of optional and required {@link MemberToken}s.
 * 
 * @author nmcwilliams
 */
public interface TokenSequence extends Iterable<MemberToken> {
    /**
     * Gets the description of this {@link TokenSequence}.
     * 
     * @return The description.
     */
    String description();

    /**
     * Gets the list of {@link MemberToken}s in this sequence.
     * 
     * @return The list of {@link MemberToken}s.
     */
    List<MemberToken> tokens();
}
