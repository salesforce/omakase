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

import com.google.common.collect.Multimap;
import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.PropertyName;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.data.Property;
import com.salesforce.omakase.util.Equivalents;

/**
 * Flexbox support.
 * <p/>
 * Handles the flex-grow property.
 *
 * @author nmcwilliams
 */
final class HandleFlexGrow extends HandleProperty {

    @Override
    protected boolean applicable(Declaration instance, SupportMatrix support) {
        return instance.isProperty(Property.FLEX_GROW);
    }

    @Override
    protected Multimap<Prefix, Declaration> equivalents(final Declaration instance) {
        Equivalents.EquivalentWalker<Declaration, Declaration> walker = new Equivalents.Base<Declaration, Declaration>() {
            @Override
            public Declaration locate(Declaration peer, Declaration unprefixed) {
                if (peer.isPrefixed() && (peer.isPropertyIgnorePrefix(Property.FLEX_GROW) ||
                    peer.isPropertyIgnorePrefix("flex-positive"))) {
                    return peer;
                }
                return null;
            }
        };
        return Equivalents.prefixes(subject(instance), instance, walker);
    }

    @Override
    protected void copy(Declaration original, Prefix prefix, SupportMatrix support) {
        if (PrefixBehaviors.FLEX_FINAL_HYBRID.matches(support, prefix)) {
            super.copy(original, prefix, support);
        }
    }

    @Override
    protected void prefix(Declaration copied, Prefix prefix, SupportMatrix support) {
        if (prefix == Prefix.MS) {
            copied.propertyName(PropertyName.of("flex-positive").prefix(prefix));
        } else {
            super.prefix(copied, prefix, support);
        }
    }
}
