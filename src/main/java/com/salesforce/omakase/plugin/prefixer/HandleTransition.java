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
import com.salesforce.omakase.util.SupportMatrix;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.KeywordValue;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.data.Property;
import com.salesforce.omakase.util.Values;

import java.util.EnumSet;

/**
 * Handles the special needs of the <code>transition</code> and <code>transition-property</code> properties.
 * <p/>
 * These declarations are special because there may be property names in the declaration value that need prefixes (e.g.,
 * <code>transition: transform 3s;</code>. These property names in the value may need to be prefixed even if the transition
 * property itself doesn't need a prefix.
 * <p/>
 * XXX it would be nice if this could also handle removing prefixes and not adding if they already exist!
 *
 * @author nmcwilliams
 */
final class HandleTransition extends HandleProperty {
    @Override
    public boolean handle(Declaration instance, boolean rearrange, boolean prune, SupportMatrix support) {
        if (applicable(instance, support)) {
            boolean handled = super.handle(instance, rearrange, prune, support);
            if (!handled) {
                // the "transition" property may not need a prefix, but one of the values still might
                prefixValues(instance, support);
            }
            return true;
        }

        return false;
    }

    @Override
    protected boolean applicable(Declaration instance, SupportMatrix support) {
        // must be the transition property
        Optional<Property> p = instance.propertyName().asProperty();
        return p.isPresent() && (p.get() == Property.TRANSITION || p.get() == Property.TRANSITION_PROPERTY);
    }

    @Override
    protected void prefix(Declaration copied, Prefix prefix, SupportMatrix support) {
        super.prefix(copied, prefix, support); // prefix the property as normal
        prefixValues(copied, prefix, support); // special-case, also prefix prefixable property names in the value
    }

    /**
     * Prefixes property names in the declaration value using the given prefix, if applicable.
     *
     * @param instance
     *     The declaration.
     * @param prefix
     *     The prefix to apply, if necessary.
     * @param support
     *     The support matrix.
     */
    private void prefixValues(Declaration instance, Prefix prefix, SupportMatrix support) {
        for (KeywordValue kw : Values.filter(KeywordValue.class, instance.propertyValue())) {
            Property property = Property.lookup(kw.keyword());
            if (property != null && support.requiresPrefixForProperty(prefix, property)) {
                kw.keyword(prefix + kw.keyword());
            }
        }
    }

    /**
     * Determines if prefixes are required for property names in the declaration value, and if so prepends prefixed copies of the
     * declaration as necessary. This should only be used when the transition property itself is not prefixed.
     *
     * @param instance
     *     The declaration.
     * @param support
     *     The support matrix.
     */
    private void prefixValues(Declaration instance, SupportMatrix support) {
        // find all necessary prefixes
        EnumSet<Prefix> prefixes = EnumSet.noneOf(Prefix.class);
        for (KeywordValue kw : Values.filter(KeywordValue.class, instance.propertyValue())) {
            // check if the keyword is a property
            Property keywordAsProperty = Property.lookup(kw.keyword());
            if (keywordAsProperty != null) {
                prefixes.addAll(support.prefixesForProperty(keywordAsProperty));
            }
        }

        // add a prefixed copy for each required prefix
        for (Prefix prefix : prefixes) {
            Declaration copy = instance.copy();
            prefixValues(copy, prefix, support);
            instance.prepend(copy);
        }
    }
}

