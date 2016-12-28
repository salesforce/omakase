/*
 * Copyright (c) 2015, salesforce.com, inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 *    Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *    following disclaimer.
 *
 *    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 *    the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 *    Neither the name of salesforce.com, inc. nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package ${package};

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.KeywordValue;
import com.salesforce.omakase.ast.declaration.PropertyValue;
import com.salesforce.omakase.ast.declaration.Term;
import com.salesforce.omakase.util.Values;

import java.util.Map;
import java.util.Optional;

/**
 * Enum of all recognized CSS keywords.
 * <p>
 * THIS FILE IS GENERATED. DO NOT EDIT DIRECTLY.
 * <p>
 * See ${generator} for instructions on updating.
 */
@SuppressWarnings("SpellCheckingInspection")
public enum Keyword {
    <#list keywords as keyword>
    /** CSS keyword named '${keyword}' */
    ${keyword?upper_case?replace("-","_")}("${keyword}"),

    </#list>
    ;

    /** reverse lookup map */
    private static final Map<String, Keyword> map;
    static {
        Builder<String, Keyword> builder = ImmutableMap.builder();
        for (Keyword kw : Keyword.values()) {
            builder.put(kw.toString(), kw);
        }
        map = builder.build();
    }

    private final String keyword;

    Keyword(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Gets whether this keyword is the only value of the given {@link Declaration}.
     * <p/>
     * Example:
     * <pre>
     * {@code Keyword.NONE.isOnlyValueIn(theDeclaration);}
     * </pre>
     *
     * @param declaration
     *     Check if this {@link Declaration}'s value only consists of this keyword.
     *
     * @return True if this keyword is the only value in the {@link Declaration}.
     */
    public boolean isOnlyValueIn(Declaration declaration) {
        return isOnlyValueIn(declaration.propertyValue());
    }

    /**
     * Gets whether the given {@link PropertyValue} only consists of one {@link Term} which is a {@link KeywordValue} with this
     * {@link Keyword}.
     * <p/>
     * Example:
     * <pre>
     * {@code Keyword.NONE.isOnlyValueIn(thePropertyValue);}
     * </pre>
     *
     * @param value
     *     The {@link PropertyValue} to check.
     *
     * @return True if this keyword is the only value in the {@link PropertyValue}.
     */
    public boolean isOnlyValueIn(PropertyValue value) {
        Optional<KeywordValue> keywordValue = Values.asKeyword(value);
        return keywordValue.isPresent() && keywordValue.get().keyword().equals(keyword);
    }

    @Override
    public String toString() {
        return keyword;
    }

    /**
    * Gets the keyword associated with the given name.
    *
    * @param name
    *     Name of the keyword.
    *
    * @return The matching {@link Keyword}, or null if not found.
    */
    public static Keyword lookup(String name) {
        return map.get(name);
    }
}
