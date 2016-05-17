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

import com.google.common.base.Optional;
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

import java.util.Set;

/**
 * Flexbox support.
 * <p>
 * Handles the justify-content property.
 *
 * @author nmcwilliams
 */
final class HandleFlexJustifyContent extends AbstractHandler<Declaration, Declaration> {
    @Override
    protected boolean applicable(Declaration instance, SupportMatrix support) {
        return instance.isProperty(Property.JUSTIFY_CONTENT);
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
                if (peer.isPrefixed() && (peer.isPropertyIgnorePrefix(Property.JUSTIFY_CONTENT) ||
                    peer.isPropertyIgnorePrefix("box-pack") ||
                    peer.isPropertyIgnorePrefix("flex-pack"))) {
                    return peer;
                }
                return null;
            }
        };
        return Equivalents.prefixes(subject(instance), instance, walker);
    }

    @Override
    protected void copy(Declaration original, Prefix prefix, SupportMatrix support) {
        // -webkit-box-pack:start; /* 2009 */
        // -moz-box-pack:start; /* 2009 */
        // -ms-flex-pack:start; /* 2012 */
        // -webkit-justify-content:flex-start; /* standard */
        // justify-content:flex-start; /* standard */

        if (PrefixBehaviors.FLEX_2009.matches(support, prefix)) {
            PropertyName newName = PropertyName.of("box-pack").prefix(prefix);
            Declaration copy = original.copy().propertyName(newName);

            // some keyword values are changed
            Optional<KeywordValue> kwValue = Values.asKeyword(copy.propertyValue());
            if (kwValue.isPresent()) {
                fixKeywordName(kwValue.get(), false);
            }

            original.prepend(copy);
        }

        if (PrefixBehaviors.FLEX_2011.matches(support, prefix)) {
            PropertyName newName = PropertyName.of("flex-pack").prefix(prefix);
            Declaration copy = original.copy().propertyName(newName);

            // some keyword values are changed
            Optional<KeywordValue> kwValue = Values.asKeyword(copy.propertyValue());
            if (kwValue.isPresent()) {
                fixKeywordName(kwValue.get(), true);
            }

            original.prepend(copy);
        }

        if (PrefixBehaviors.FLEX_FINAL.matches(support, prefix)) {
            Declaration copy = original.copy();
            copy.propertyName().prefix(prefix);
            original.prepend(copy);
        }
    }

    private void fixKeywordName(KeywordValue kwValue, boolean supportSpaceAround) {
        Optional<Keyword> kw = kwValue.asKeyword();
        if (kw.isPresent()) {
            if (kw.get() == Keyword.FLEX_START) {
                kwValue.keyword("start");
            } else if (kw.get() == Keyword.FLEX_END) {
                kwValue.keyword("end");
            } else if (kw.get() == Keyword.SPACE_BETWEEN) {
                kwValue.keyword("justify");
            } else if (kw.get() == Keyword.SPACE_AROUND) {
                if (supportSpaceAround) {
                    kwValue.keyword("distribute");
                } else {
                    kwValue.destroy();
                }
            }
        }
    }
}
