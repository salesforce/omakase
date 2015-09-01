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
import com.salesforce.omakase.data.Keyword;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.data.Property;
import com.salesforce.omakase.util.Equivalents;
import com.salesforce.omakase.util.Values;

import java.util.Set;

/**
 * Flexbox support.
 * <p/>
 * Handles special needs of <code>display:flex</code> and <code>display:inline-flex</code>
 * <p/>
 * <pre>
 * Useful links:
 * https://developer.mozilla.org/en-US/docs/Web/Guide/CSS/Flexible_boxes
 * https://msdn.microsoft.com/en-us/library/dn265027(v=vs.85).aspx
 * http://caniuse.com/#search=flexbox
 * http://www.flexboxin5.com/
 * </pre>
 *
 * @author nmcwilliams
 */
final class HandleFlexValue extends AbstractHandler<KeywordValue, Declaration> {
    // general known issues:
    // 1: If the newer or older spec webkit units are present the other one won't be added.

    @Override
    protected boolean applicable(KeywordValue instance, SupportMatrix support) {
        // must be display:flex or display:inline-flex

        if (!instance.declaration().isProperty(Property.DISPLAY)) return false;

        if (instance.group().size() != 1) return false; // keyword should be by itself
        Optional<Keyword> kw = instance.asKeyword();
        return kw.isPresent() && (kw.get() == Keyword.FLEX || kw.get() == Keyword.INLINE_FLEX);
    }

    @Override
    protected Declaration subject(KeywordValue instance) {
        return instance.declaration();
    }

    @Override
    protected Set<Prefix> required(KeywordValue instance, SupportMatrix support) {
        return support.prefixesForKeyword(instance.asKeyword().get());
    }

    @Override
    protected Multimap<Prefix, ? extends Declaration> equivalents(KeywordValue instance) {
        // find all of the variants that are equivalent
        Equivalents.EquivalentWalker<Declaration, KeywordValue> walker = new Equivalents.Base<Declaration, KeywordValue>() {
            @Override
            public KeywordValue locate(Declaration peer, KeywordValue unprefixed) {
                if (!peer.isProperty(Property.DISPLAY)) return null;

                Optional<KeywordValue> keywordValue = Values.asKeyword(peer.propertyValue());
                if (!keywordValue.isPresent()) return null;

                String kw = keywordValue.get().keyword();

                if (!unprefixed.name().contains("inline")) {
                    if (kw.equals("-webkit-flex") || kw.equals("-ms-flexbox") ||
                        kw.equals("-moz-box") || kw.equals("-webkit-box")) {
                        return keywordValue.get();
                    }
                } else {
                    if (kw.equals("-webkit-inline-flex") || kw.equals("-ms-inline-flexbox") ||
                        kw.equals("-moz-inline-box") || kw.equals("-webkit-inline-box")) {
                        return keywordValue.get();
                    }
                }
                return null;
            }
        };

        return Equivalents.prefixes(subject(instance), instance, walker);
    }

    @Override
    protected void copy(Declaration original, Prefix prefix, SupportMatrix support) {
        // info based on caniuse.com
        // display: -webkit-box; /* 2009; chrome 4-20, saf 3.1-6, ios saf 3.2-6.1, android 2.1-4.3 */
        // display: -moz-box; /* 2009; ff 2-21 */
        // display: -ms-flexbox; /* 2012; ie10, ie10 mob */
        // display: -webkit-flex; /* standard; chrome 21-28, saf 6.1-8, ios saf 7.0-8.4 */
        // display: flex; /* standard */
        // op 15, 16 need webkit, but ignoring this

        if (PrefixBehaviors.FLEX_2009.matches(support, prefix)) {
            Declaration copy = replaceKeyword(original.copy(), prefix, "box");
            original.prepend(copy);
        }

        if (PrefixBehaviors.FLEX_2012.matches(support, prefix)) {
            Declaration copy = replaceKeyword(original.copy(), prefix, "flexbox");
            original.prepend(copy);
        }

        if (PrefixBehaviors.FLEX_FINAL.matches(support, prefix)) {
            Declaration copy = replaceKeyword(original.copy(), prefix, "flex");
            original.prepend(copy);
        }
    }

    private Declaration replaceKeyword(Declaration declaration, Prefix prefix, String name) {
        KeywordValue keywordValue = Values.asKeyword(declaration.propertyValue()).get();

        StringBuilder builder = new StringBuilder(prefix.toString());
        if (keywordValue.keyword().contains("inline")) {
            builder.append("inline-");
        }
        builder.append(name);

        keywordValue.keyword(builder.toString());
        return declaration;
    }
}
