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

import java.util.Set;

/**
 * Flexbox support.
 * <p/>
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

        if (PrefixBehaviors.FLEX_2012.matches(support, prefix)) {
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
