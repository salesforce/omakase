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
import com.salesforce.omakase.ast.declaration.NumericalValue;
import com.salesforce.omakase.ast.declaration.PropertyName;
import com.salesforce.omakase.ast.declaration.PropertyValueMember;
import com.salesforce.omakase.data.Keyword;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.data.Property;
import com.salesforce.omakase.util.Equivalents;
import com.salesforce.omakase.util.Values;

import java.util.Set;

/**
 * Flexbox support.
 * <p/>
 * Handles the special flex shorthand property.
 *
 * @author nmcwilliams
 */
final class HandleFlexProp extends AbstractHandler<Declaration, Declaration> {
    @Override
    protected boolean applicable(Declaration instance, SupportMatrix support) {
        return instance.isProperty(Property.FLEX);
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
    protected Multimap<Prefix, ? extends Declaration> equivalents(final Declaration instance) {
        Equivalents.EquivalentWalker<Declaration, Declaration> walker = new Equivalents.Base<Declaration, Declaration>() {
            @Override
            public Declaration locate(Declaration peer, Declaration unprefixed) {
                if (peer.isPrefixed() && (peer.isPropertyIgnorePrefix(Property.FLEX) ||
                    peer.isPropertyIgnorePrefix("box-flex"))) {
                    return peer;
                }
                return null;
            }
        };
        return Equivalents.prefixes(subject(instance), instance, walker);
    }

    @Override
    protected void copy(Declaration original, Prefix prefix, SupportMatrix support) {
        // lots of variations here, see https://developer.mozilla.org/en-US/docs/Web/CSS/flex#Syntax
        // what a confusing mess.

        if (PrefixBehaviors.FLEX_2009.matches(support, prefix)) {
            PropertyName newName = PropertyName.of("box-flex").prefix(prefix);
            Declaration copy = original.copy().propertyName(newName);
            boolean appendCopy = true;

            // change values if required
            Optional<KeywordValue> kv = Values.asKeyword(copy.propertyValue());
            if (kv.isPresent()) {
                Optional<Keyword> keyword = kv.get().asKeyword();
                if (keyword.isPresent() && keyword.get() == Keyword.NONE) {
                    // flex: none = flex: 0 0 auto
                    // 2009 spec doesn't support this shorthand or value of none/auto,
                    // so change the value to the first part which is flex-grow.
                    // not going to bother with magically adding the other props.
                    kv.get().replaceWith(NumericalValue.of(0));
                } else if (keyword.isPresent() && keyword.get() == Keyword.AUTO) {
                    // flex: auto = flex: 1 1 auto (see above comments)
                    kv.get().replaceWith(NumericalValue.of(1));
                }
            } else if (copy.propertyValue().members().size() == 1) {
                // if there is only one value and it is a united value then it is the flex-basis,
                // so don't add the 2009 spec copies as those would change flex-grow.
                Optional<NumericalValue> numerical = Values.asNumerical(copy.propertyValue());
                if (numerical.isPresent() && numerical.get().unit().isPresent()) {
                    appendCopy = false;
                }
            } else {
                // if there are multiple values, just take the first one which should be flex-grow
                Optional<PropertyValueMember> first = copy.propertyValue().members().first();
                if (first.isPresent()) {
                    copy.propertyValue().members().replaceExistingWith(first.get());
                }
            }

            if (appendCopy) {
                original.prepend(copy);
            }
        }

        if (PrefixBehaviors.FLEX_FINAL_PLUS.matches(support, prefix)) {
            Declaration copy = original.copy();
            copy.propertyName().prefix(prefix);
            original.prepend(copy);
        }
    }
}
