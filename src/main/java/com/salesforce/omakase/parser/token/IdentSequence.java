/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.token;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class IdentSequence implements TokenSequence {
    private final List<MemberToken> tokens = ImmutableList.<MemberToken>builder()
        .add(MemberToken.optional(Tokens.HYPHEN))
        .add(MemberToken.required(Tokens.NMSTART))
        .add(MemberToken.optional(Tokens.NMCHAR))
        .build();

    @Override
    public String description() {
        // TODO Auto-generated method stub
        return "TODO";
    }

    @Override
    public List<MemberToken> tokens() {
        return tokens;
    }

    @Override
    public Iterator<MemberToken> iterator() {
        return tokens.iterator();
    }

}
