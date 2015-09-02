/*
 * Copyright (C) 2015 salesforce.com, inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.salesforce.omakase.plugin.prefixer;

import com.google.common.base.Optional;
import com.google.common.collect.Multimap;
import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.KeywordValue;
import com.salesforce.omakase.ast.declaration.PropertyName;
import com.salesforce.omakase.data.Keyword;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.data.Property;
import com.salesforce.omakase.util.Equivalents;
import com.salesforce.omakase.util.Values;

import java.util.EnumSet;
import java.util.Set;

/**
 * Flexbox support.
 * <p/>
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
