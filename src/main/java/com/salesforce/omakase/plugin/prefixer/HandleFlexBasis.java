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
 * Handles the flex-basis property.
 *
 * @author nmcwilliams
 */
final class HandleFlexBasis extends HandleProperty {
    @Override
    protected boolean applicable(Declaration instance, SupportMatrix support) {
        return instance.isProperty(Property.FLEX_BASIS);
    }

    @Override
    protected Multimap<Prefix, Declaration> equivalents(final Declaration instance) {
        Equivalents.EquivalentWalker<Declaration, Declaration> walker = new Equivalents.Base<Declaration, Declaration>() {
            @Override
            public Declaration locate(Declaration peer, Declaration unprefixed) {
                if (peer.isPrefixed() && (peer.isPropertyIgnorePrefix(Property.FLEX_BASIS) ||
                    peer.isPropertyIgnorePrefix("flex-preferred-size"))) {
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
            copied.propertyName(PropertyName.of("flex-preferred-size").prefix(prefix));
        } else {
            super.prefix(copied, prefix, support);
        }
    }
}
