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
import com.salesforce.omakase.ast.declaration.NumericalValue;
import com.salesforce.omakase.ast.declaration.PropertyName;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.data.Property;
import com.salesforce.omakase.util.Equivalents;
import com.salesforce.omakase.util.Values;

import java.util.Set;

/**
 * Flexbox support.
 * <p/>
 * Handles the order property.
 *
 * @author nmcwilliams
 */
final class HandleFlexOrder extends AbstractHandler<Declaration, Declaration> {
    @Override
    protected boolean applicable(Declaration instance, SupportMatrix support) {
        return instance.isProperty(Property.ORDER);
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
                if (peer.isPrefixed() && (peer.isPropertyIgnorePrefix(Property.ORDER) ||
                    peer.isPropertyIgnorePrefix("flex-order") ||
                    peer.isPropertyIgnorePrefix("box-ordinal-group"))) {
                    return peer;
                }
                return null;
            }
        };
        return Equivalents.prefixes(subject(instance), instance, walker);
    }

    @Override
    protected void copy(Declaration original, Prefix prefix, SupportMatrix support) {
        // -webkit-box-ordinal-group: 1; /* 2009; chrome 4-20, saf 3.1-6, ios saf 3.2-6.1, android 2.1-4.3 */
        // -moz-box-ordinal-group: 1; /* 2009; ff 2-21 */
        // -ms-flex-order: 0; /* 2012; ie10, ie10 mob */
        // -webkit-order: 0; /* standard; chrome 21-28, saf 6.1-8, ios saf 7.0-8.4 */
        // order: 0; /* standard */

        if (PrefixBehaviors.FLEX_2009.matches(support, prefix)) {
            PropertyName newName = PropertyName.of("box-ordinal-group").prefix(prefix);
            Declaration copy = original.copy().propertyName(newName);

            // add +1 to the value
            Optional<NumericalValue> numerical = Values.asNumerical(copy.propertyValue());
            if (numerical.isPresent()) {
                int i = numerical.get().intValue();
                numerical.get().value(i + 1);
            }

            original.prepend(copy);
        }

        if (PrefixBehaviors.FLEX_2011.matches(support, prefix)) {
            PropertyName newName = PropertyName.of("flex-order").prefix(prefix);
            Declaration copy = original.copy().propertyName(newName);
            original.prepend(copy);
        }

        if (PrefixBehaviors.FLEX_FINAL.matches(support, prefix)) {
            Declaration copy = original.copy();
            copy.propertyName().prefix(prefix);
            original.prepend(copy);
        }
    }
}
