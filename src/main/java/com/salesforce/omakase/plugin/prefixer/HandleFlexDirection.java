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

package com.salesforce.omakase.plugin.prefixer;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Multimap;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.KeywordValue;
import com.salesforce.omakase.ast.declaration.PropertyName;
import com.salesforce.omakase.data.Keyword;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.data.Property;
import com.salesforce.omakase.util.Equivalents;
import com.salesforce.omakase.util.SupportMatrix;
import com.salesforce.omakase.util.Values;

/**
 * Flexbox support.
 * <p>
 * Handles the flex-direction property.
 *
 * @author nmcwilliams
 */
final class HandleFlexDirection extends AbstractHandler<Declaration, Declaration> {
    private static final EnumSet<Keyword> RECOGNIZED = EnumSet.of(
        Keyword.ROW, Keyword.ROW_REVERSE, Keyword.COLUMN, Keyword.COLUMN_REVERSE);

    @Override
    protected boolean applicable(Declaration instance, SupportMatrix support) {
        return instance.isProperty(Property.FLEX_DIRECTION);
    }

    @Override
    protected Declaration subject(Declaration instance) {
        return instance;
    }

    @Override
    protected Set<Prefix> required(Declaration instance, SupportMatrix support) {
        return support.prefixesForProperty(instance.propertyName().asProperty().get());
    }

    @Override
    protected Multimap<Prefix, ? extends Declaration> equivalents(Declaration instance) {
        Equivalents.EquivalentWalker<Declaration, Declaration> walker = new Equivalents.Base<Declaration, Declaration>() {
            @Override
            public Declaration locate(Declaration peer, Declaration unprefixed) {
                if (peer.isPrefixed() && (peer.isPropertyIgnorePrefix(Property.FLEX_DIRECTION) ||
                    peer.isPropertyIgnorePrefix("box-direction") ||
                    peer.isPropertyIgnorePrefix("box-orient"))) {
                    return peer;
                }
                return null;
            }
        };
        return Equivalents.prefixes(subject(instance), instance, walker);
    }

    @Override
    protected void copy(Declaration original, Prefix prefix, SupportMatrix support) {
        // for 2009 spec we use two properties:
        // box-orient (horizontal/normal) and box-direction (normal/reverse).
        // if one of the these two declarations are present then the other will NOT be added!

        if (PrefixBehaviors.FLEX_2009.matches(support, prefix)) {
            Optional<Keyword> kw = Values.asKeywordConstant(original.propertyValue());
            if (kw.isPresent() && RECOGNIZED.contains(kw.get())) { // don't add stuff for values like "initial" or "unset"
                Keyword originalKeyword = kw.get();

                // box-direction property
                PropertyName newName = PropertyName.of("box-direction").prefix(prefix);
                Declaration copy = original.copy().propertyName(newName);

                // change the keyword name
                KeywordValue kwValue = Values.asKeyword(copy.propertyValue()).get();
                if (originalKeyword == Keyword.ROW || originalKeyword == Keyword.COLUMN) {
                    kwValue.keyword("normal");
                } else if (originalKeyword == Keyword.ROW_REVERSE || originalKeyword == Keyword.COLUMN_REVERSE) {
                    kwValue.keyword("reverse");
                }

                original.prepend(copy);

                // box-orient property
                PropertyName orientProp = PropertyName.of("box-orient").prefix(prefix);
                if (originalKeyword == Keyword.ROW || originalKeyword == Keyword.ROW_REVERSE) {
                    copy.prepend(new Declaration(orientProp, KeywordValue.of("horizontal")));
                } else if (originalKeyword == Keyword.COLUMN || originalKeyword == Keyword.COLUMN_REVERSE) {
                    copy.prepend(new Declaration(orientProp, KeywordValue.of("vertical")));
                }
            }
        }

        if (PrefixBehaviors.FLEX_FINAL_PLUS.matches(support, prefix)) {
            Declaration copy = original.copy();
            copy.propertyName().prefix(prefix);
            original.prepend(copy);
        }
    }
}
