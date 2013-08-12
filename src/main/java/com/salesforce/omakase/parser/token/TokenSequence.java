/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.token;

import java.util.List;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface TokenSequence extends Iterable<MemberToken> {
    String description();

    List<MemberToken> tokens();
}
